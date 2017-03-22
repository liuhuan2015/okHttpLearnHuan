package com.huan.mooc_okhttp;

import android.util.Log;

/**
 * Created by huan on 2017/3/21.
 */

public class L {
    private static String TAG = "Mooc_okhttp";
    private static boolean debug = true;


    public static void e(String msg) {
        if (debug) {
            Log.e(TAG, msg);
        }
    }
}
