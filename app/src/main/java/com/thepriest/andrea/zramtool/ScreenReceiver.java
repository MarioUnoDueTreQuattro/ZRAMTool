package com.thepriest.andrea.zramtool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {
    public static boolean bScreenOn = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            ZRAMToolApp.bScreenIsOn = false;
            bScreenOn = false;
            if (ZRAMToolApp.bLog) ZRAMToolApp.mLogHelper.appendLog("ZRAMToolApp.bScreenIsOn=false;");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            ZRAMToolApp.bScreenIsOn = true;
            bScreenOn = true;
            if (ZRAMToolApp.bLog) ZRAMToolApp.mLogHelper.appendLog("ZRAMToolApp.bScreenIsOn=true;");
        }
    }
}