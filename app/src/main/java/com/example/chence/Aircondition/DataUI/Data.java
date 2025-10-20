package com.example.chence.Aircondition.DataUI;

import android.util.Log;


import java.util.ArrayList;

import kiyun.dataprocess.RawdataProcess;


/**
 * Created by ChenCe on 2017/12/6.
 */

public class Data extends RawdataProcess {
    public float FunData,CurTemperature,SetTemperature;
    public boolean BeOpenClose,RefOrBlow,CompRunOrClose,FunRunOrClose;
    public int CoverageData;
    public int AllCoverageData;

    public Data(ArrayList<Byte> frame) {
        int BeginIndex = 4;
        Byte l1,l2,l3,l4;
        l1= frame.get(BeginIndex + 0);
        if (l1 == 1) BeOpenClose = true;
        else BeOpenClose = false;

        l1= frame.get(BeginIndex + 1);
        if (l1 == 1) RefOrBlow = true;
        else RefOrBlow = false;

        l1= frame.get(BeginIndex + 2);
        FunData=l1;

        l1= frame.get(BeginIndex + 3);
        SetTemperature=l1;

        l1= frame.get(BeginIndex + 4);
        l2= frame.get(BeginIndex + 5);
        l3= frame.get(BeginIndex + 6);
        l4= frame.get(BeginIndex + 7);

        int data=get4ByteValueInt(l1,l2,l3,l4);
        CurTemperature=Float.intBitsToFloat(data);

        l1= frame.get(BeginIndex + 8);
        if (l1 == 1) CompRunOrClose = true;
        else CompRunOrClose = false;

        l1= frame.get(BeginIndex + 9);
        if (l1 == 1) FunRunOrClose = true;
        else FunRunOrClose = false;

        CoverageData=frame.get(BeginIndex + 10);
    }

    public float GetAllCoverityData(){
        if(CoverageData>AllCoverageData)
            AllCoverageData=CoverageData;

        return AllCoverageData;
    }
}

