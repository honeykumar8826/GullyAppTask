package com.videoMaker.application;

import android.app.Application;

import com.videoMaker.prefManager.SharedPreference;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreference.initShared(this);
    }
}
