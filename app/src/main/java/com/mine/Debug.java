package com.mine;

import android.util.Log;

/**
 * Created by USER on 2016-11-15.
 */
public class Debug {
    public static void printMethodStack() {
        String methodeList = "- - - printMethodStack - - -\n";
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
            methodeList += ste.getMethodName() + "\n└";
        Log.d("d", methodeList);
    }
}
