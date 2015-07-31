package com.app.l.learningapp;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by liang on 15/7/31.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        //Fresco初始化
        Fresco.initialize(this);
    }
}
