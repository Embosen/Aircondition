package com.example.chence.Aircondition.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.chence.Aircondition.Activity.FullscreenActivity;
import com.example.chence.Aircondition.Activity.MainActivity;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("ACTION_BOOT_COMPLETED", Intent.ACTION_BOOT_COMPLETED);
        context.startActivity(new Intent().setClass(context, FullscreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
