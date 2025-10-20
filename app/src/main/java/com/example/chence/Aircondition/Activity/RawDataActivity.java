package com.example.chence.Aircondition.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chence.Aircondition.R;
import com.example.chence.Aircondition.DataUI.DataUi;
import com.example.chence.Aircondition.Service.SerialService;
import com.example.chence.Aircondition.util.BitUtil;

import java.util.ArrayList;

/**
 * Created by cchen on 2016/6/21.
 */
public class RawDataActivity extends Activity implements DataUi, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = RawDataActivity.class.getSimpleName();
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            SerialService msgService = ((SerialService.MsgBinder) service).getService();
            msgService.addUi(RawDataActivity.this);
        }
    };
    private LinearLayout root_view;
    private Button btn_pause;
    private boolean pause;
    private CheckBox chk_bin;
    private CheckBox chk_decimal;
    private CheckBox chk_hex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_data);
        initViews();
        bindService(new Intent().setClass(RawDataActivity.this, SerialService.class), conn, Context.BIND_AUTO_CREATE);
    }

    private void initViews() {
        root_view = (LinearLayout) findViewById(R.id.root_view);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause = !pause;
                btn_pause.setText(!pause ? "pause" : "resume");
            }
        });

        chk_bin = (CheckBox) findViewById(R.id.chk_bin);
        chk_bin.setOnCheckedChangeListener(this);
        chk_hex = (CheckBox) findViewById(R.id.chk_hex);
        chk_hex.setOnCheckedChangeListener(this);
        chk_decimal = (CheckBox) findViewById(R.id.chk_decimal);
        chk_decimal.setOnCheckedChangeListener(this);

        chk_hex.setChecked(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void onNewData(byte[] bytes) {
//        Log.d(TAG," onNewData " + Arrays.toString(bytes));

        if (pause) return;
        final TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (byte b : bytes) {
//            String hexStringFromInt = BitUtil.getHexStringFromInt(b);
            if (chk_hex.isChecked()) {
                sb.append(String.format("%02x", b));
            } else if (chk_bin.isChecked()) {
                sb.append(BitUtil.byteToBit(b));
            } else if (chk_decimal.isChecked()) {
                sb.append(String.format("%d", b));
            }
            sb.append(" ");
        }
        sb.append("]");

        textView.setText(sb.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout layout_content = (LinearLayout) root_view.findViewById(R.id.layout_content);
                if (layout_content.getChildCount() > 50) layout_content.removeAllViews();
                layout_content.addView(textView);
            }
        });
    }

    @Override
    public void onFrameGet(ArrayList<Byte> dataList) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.chk_bin:
                if (isChecked) {
                    chk_hex.setChecked(false);
                    chk_decimal.setChecked(false);
                }
                break;
            case R.id.chk_hex:
                if (isChecked) {
                    chk_bin.setChecked(false);
                    chk_decimal.setChecked(false);
                }
                break;
            case R.id.chk_decimal:
                if (isChecked) {
                    chk_hex.setChecked(false);
                    chk_bin.setChecked(false);
                }
                break;
        }
        if (!chk_decimal.isChecked() && !chk_hex.isChecked() && !chk_bin.isChecked()) {
            chk_hex.setChecked(true);
        }

        if (isChecked) {
            LinearLayout layout_content = (LinearLayout) root_view.findViewById(R.id.layout_content);
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            view.setBackgroundResource(R.color.colorPrimaryDark);
            layout_content.addView(view);
        }
    }
}
