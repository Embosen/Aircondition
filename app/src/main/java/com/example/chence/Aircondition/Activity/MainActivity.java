package com.example.chence.Aircondition.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.chence.Aircondition.*;
import com.example.chence.Aircondition.DataUI.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import kiyun.controllib.AlarmCtrl;
import kiyun.controllib.DataCtrl;
import kiyun.controllib.ToggleCtrl;
import kiyun.controllib.InstructionCtrl;
import com.example.chence.Aircondition.DataUI.DataUi;
import com.example.chence.Aircondition.Service.SerialService;


public class MainActivity extends AppCompatActivity  implements DataUi{

    private static final String TAG = "Test cc";

    private DataCtrl FunData,CurTemperature,SetTemperature;
    private ToggleCtrl BeOpenClose,RefOrBlow,CompRunOrClose,FunRunOrClose;

    private InstructionCtrl InstructCtrl;

    private DataCtrl CurCoverityData;
    private DataCtrl AllCoverityData;
    public static float m_AllCoverityData;

    private float m_PreAllCoverityData;
    private static int clickCount;

    SerialService GlobalmsgService;

    ServiceConnection conn = new ServiceConnection() {


        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            SerialService msgService = ((SerialService.MsgBinder) service).getService();
            GlobalmsgService=msgService;
            msgService.addUi(MainActivity.this);
        }
    };

    @Override
    public void onNewData(byte[] bytes) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FunData = (DataCtrl)findViewById(R.id.FunData);
        CurTemperature = (DataCtrl)findViewById(R.id.CurTemperature);
        SetTemperature = (DataCtrl)findViewById(R.id.SetTemperature);

        BeOpenClose = (ToggleCtrl)findViewById(R.id.BeOpenClose);
        RefOrBlow = (ToggleCtrl)findViewById(R.id.RefOrBlow);
        CompRunOrClose = (ToggleCtrl)findViewById(R.id.CompRunOrClose);
        FunRunOrClose = (ToggleCtrl)findViewById(R.id.FunRunOrClose);


        CurCoverityData = (DataCtrl) findViewById(R.id.CurCoverityData);
        AllCoverityData = (DataCtrl) findViewById(R.id.AllCoverityData);

        CurCoverityData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " click count " + clickCount);
                clickCount++;
                clickHandler.sendEmptyMessageDelayed(0, 1000);
                if (clickCount == 3) {
                    startActivity(new Intent().setClass(MainActivity.this, RawDataActivity.class));
                    clickCount = 0;
                }
            }
        });

        m_IniFileAndDevHandler.postDelayed(IniFileAndDevHandlerRun, 1000);
        m_TimerFileSaveHandler.postDelayed(TimerFileSaveHandlerRun, 2000);



    };

    Handler clickHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                clickCount = 0;
            }
        }
    };

    private Handler handler=new Handler(){
        public void handleMessage(Message Msg)
        {
            switch(Msg.what)
            {
                case 0:
                    Data m_Data=(Data)Msg.obj;
                    SetUI(m_Data);
                    Log.d(TAG, "onDataReceived good  " + m_Data.toString());
            }

        }

    };

    Handler m_TimerFileSaveHandler=new Handler();
    Runnable TimerFileSaveHandlerRun=new Runnable() {
        @Override
        public void run() {
            if(m_AllCoverityData > m_PreAllCoverityData)
            {
                SaveToCoverityFile(m_AllCoverityData);
                m_PreAllCoverityData=m_AllCoverityData;
            }

            m_TimerFileSaveHandler.postDelayed(TimerFileSaveHandlerRun, 500);
        }
    };

    Handler m_IniFileAndDevHandler=new Handler();
    Runnable IniFileAndDevHandlerRun=new Runnable() {
        @Override
        public void run() {
            readFromOldCoverity();
            bindService(new Intent().setClass(MainActivity.this, SerialService.class), conn, Context.BIND_AUTO_CREATE);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SaveToCoverityFile(m_AllCoverityData);
        unbindService(conn);
        GlobalmsgService.serialPort.close();
        stopService(new Intent().setClass(this, SerialService.class));
        System.exit(0);
    }

    public void onFrameGet(final ArrayList<Byte> frame) {
        Data m_Data=new Data(frame);
        Message message=new Message();
        message.what=0;
        message.obj=m_Data;
        handler.sendMessage(message);

    }

    public void SetUI(Data m_Data)
    {
        FunData.setData(m_Data.FunData);
        CurTemperature.setData(m_Data.CurTemperature);
        SetTemperature.setData(m_Data.SetTemperature);

        BeOpenClose.setToggle(m_Data.BeOpenClose);
        RefOrBlow.setToggle(m_Data.RefOrBlow);
        CompRunOrClose.setToggle(m_Data.CompRunOrClose);
        FunRunOrClose.setToggle(m_Data.FunRunOrClose);

        CurCoverityData.setData(m_Data.CoverageData);
        m_Data.AllCoverageData=(int)m_AllCoverityData;
        m_AllCoverityData=m_Data.GetAllCoverityData();
        AllCoverityData.setData(m_AllCoverityData);
    }

    public void OnClickClearCoverityFileData(View v) {
        File sdCardDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File fileReader = new File(sdCardDir, "log.txt");
        if(fileReader.exists())
        {
            fileReader.delete();

            m_AllCoverityData=0;
            AllCoverityData.setData(m_AllCoverityData);
            SaveToCoverityFile(m_AllCoverityData);
        }
    }

    public void OnClickSaveCoverityFileData(View v) {
        SaveToCoverityFile(m_AllCoverityData);
    }

    public static void SaveToCoverityFile(float m_tempAllCoverityData)   {

        File sdCardDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        File SaveFile=new File(sdCardDir,"log.txt");
        try {
            FileOutputStream outputStream=new FileOutputStream(SaveFile,true);

            String Str=String.valueOf(m_tempAllCoverityData);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(Str);
            bufferedWriter.newLine();
            Log.d("writefile", "write CoverityFile: "+Str);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

        }
        catch(java.io.IOException e)
        {
            Log.d(TAG, "Can't Writer File : " +e.getMessage());
        }
    }

    void readFromOldCoverity()
    {
        File sdCardDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        try {
            File file = new File(sdCardDir, "log.txt");
            if (file.exists()) {

                FileReader fileReader1=new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader1);
                boolean FindInFile = false;

                while(true) {
                    String line = bufferedReader.readLine();
                    Log.d("readfile", "Read File  " + line);
                    if (line != null) {
                        m_AllCoverityData = Float.parseFloat(line);
                        AllCoverityData.setData(m_AllCoverityData);
                    } else {
                        break;
                    }
                }
            }
            else //覆盖率文件不存在，
            {
                m_AllCoverityData=0;
                AllCoverityData.setData(m_AllCoverityData);
            }

            m_PreAllCoverityData=m_AllCoverityData;
        }
        catch(java.io.IOException e){
            Log.d(TAG, "Can't Read File  " +e.getMessage());
        }
    }
}
