package com.example.chence.Aircondition.DataUI;

import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by cchen on 2016/6/18.
 */
public interface DataUi {
    public void onNewData(byte[] bytes);

    void onFrameGet(ArrayList<Byte> dataList);
}