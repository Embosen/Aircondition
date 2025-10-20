package com.example.chence.Aircondition.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.chence.Aircondition.DataUI.DataUi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import android_serialport_api.SerialPort;
import com.example.chence.Aircondition.util.BitUtil;
import com.example.chence.Aircondition.DataUI.Data;

/**
 * Created by cchen on 2016/6/18.
 */
public class SerialService extends Service {
    private static final String TAG = SerialService.class.getSimpleName() + " cchen";
    public static boolean continueRead = false;
    private static final int GOOD_SIZE = 26;
    private static final int HEAD_0 = 0xAA;
    private static final int HEAD_1 = 0x55;
    private static final int HEAD_2 = 0xFB;
    ArrayList<DataUi> uis = new ArrayList<DataUi>();

    public SerialPort serialPort;

    public void addUi(DataUi ui) {
        uis.add(ui);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind ...");
        initLocalPort();
        return new MsgBinder();
    }

    private void initLocalPort() {
        try {
            File sdCardDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String SerialPortDevStr="/dev/ttyS3";
            try {
                File fileReader = new File(sdCardDir, "SerialPortDev.txt");
                if (fileReader.exists()) {
                    InputStream inStream = new FileInputStream(fileReader);
                    InputStreamReader inputReader = new InputStreamReader(inStream);
                    BufferedReader bufferedReader = new BufferedReader(inputReader);
                    SerialPortDevStr = bufferedReader.readLine();
                    inStream.close();
                }
                else
                {
                    File SaveFile=new File(sdCardDir,"SerialPortDev.txt");
                    FileOutputStream outputStream=new FileOutputStream(SaveFile,true);

                    String Str=SerialPortDevStr;

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    bufferedWriter.write(Str);
                    Log.d("writefile", "SerialPortDev.txt: "+Str);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                }
            }
            catch(java.io.IOException e){
                Log.d(TAG, "Can't Read File  " +e.getMessage());
            }


            serialPort = new SerialPort(new File(SerialPortDevStr), 9600, 0);
            new DataThread(serialPort).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public SerialService getService() {
            return SerialService.this;
        }

    }

    private class DataThread extends Thread {
        private final SerialPort mPort;

        public DataThread(SerialPort serialPort) {
            mPort = serialPort;
            continueRead = true;
        }

        @Override
        public void run() {
            super.run();

            // 定义一个包的最大长度
            int maxLength = 2048;
            byte[] buffer = new byte[maxLength];
            // 每次收到实际长度
            int available = 0;
            // 当前已经收到包的总长度
            int currentLength = 0;
            // 协议头长度4个字节（开始符1，开始符2，长度，标志字FB）
            int headerLength = 4;

            if (mPort == null) return;
            InputStream mInputStream = mPort.getInputStream();
            while (!isInterrupted()) {
                try {
                    available = mInputStream.available();
                    if (available > 0) {
                        // 防止超出数组最大长度导致溢出
                        if (available > maxLength - currentLength) {
                            available = maxLength - currentLength;
                        }
                        mInputStream.read(buffer, currentLength, available);

                        currentLength += available;

                        byte[] raw = new byte[currentLength];
                        System.arraycopy(buffer, 0, raw, 0, currentLength);
                        if (uis != null) {
                            for (DataUi ui : uis) {
                                if (ui != null)
                                    ui.onNewData(raw);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                int cursor = 0;

                // 如果当前收到包大于头的长度，则解析当前包
                while (currentLength >= headerLength) {

                    // 证明当前的cursor并没有指向头
                    if (buffer[cursor] != -86) {
                        currentLength = currentLength - 1;
                        ++cursor;
                        continue;
                    } else if (buffer[cursor + 1] != 85) {
                        currentLength = currentLength - 1;
                        ++cursor;
                        continue;
                    } else if (buffer[cursor + 3] != -5) {
                        currentLength = currentLength - 1;
                        ++cursor;
                        continue;

                    }

                    //此处cursor已经指向了头，然后看看内容长度是多少
                    int contentLength = buffer[cursor + 2];

                    // 如果内容包的长度大于最大内容长度或者小于等于0，则说明这个包有问题，丢弃
                    if (contentLength <= 0 || contentLength > maxLength - 7) {
                        currentLength = 0;
                        break;
                    }


                    // 如果当前获取到长度小于整个包的长度，则跳出循环等待继续接收数据
                    int factPackLen = contentLength + 7;
                    if (currentLength < factPackLen) {
                        break;
                    }

                    // 一个完整数据帧即产生，然后解析它
                    onDataReceived(buffer, cursor, factPackLen);

                    //将当前的cursor向后移动一个完整数据帧长度，同时将收到的数据包长度减少完整数据帧长度
                    currentLength -= factPackLen;
                    cursor += factPackLen;
                }

                // 残留字节移到缓冲区首
                if (currentLength > 0 && cursor > 0) {
                    System.arraycopy(buffer, cursor, buffer, 0, currentLength);
                }
            }
        }


        protected void onDataReceived(final byte[] buffer, final int index, final int packlen) {
            byte[] pakage = new byte[packlen];
            System.arraycopy(buffer, index, pakage, 0, packlen);
            //此时pakage就是一帧完整的数据包，该包以0xAA 0x55 0x 0xFB开头，最终以0xAA 0x55结束
            //在此处编写解析与刷新界面函数吧
            ArrayList<Byte> objects = new ArrayList<>();
            for (byte tempB : pakage) {
                objects.add(tempB);
            }
            if (uis != null) {
                for (DataUi ui : uis) {
                    if (ui != null)
                        ui.onFrameGet(objects);
                }
            }
        }
    }



    ArrayList<Byte> dataList = new ArrayList<Byte>();


    @Override
    public void onDestroy() {
        super.onDestroy();
        continueRead = false;
        uis.clear();
    }
}
