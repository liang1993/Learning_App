package com.app.l.learningapp.utils;

import android.util.Log;

/**
 * Created by liang on 15/7/28.
 * 封装Log的方法
 */
public class MyLog {

    private final static String LOGTAG = "MyLog";

    public static void d(String string) {
        Log.d(LOGTAG, string);
    }

    public static void i(String string) {
        Log.i(LOGTAG, string);
    }

    public static void w(String string) {
        Log.w(LOGTAG, string);
    }

    public static void e(String string) {
        Log.e(LOGTAG, string);
    }

}
