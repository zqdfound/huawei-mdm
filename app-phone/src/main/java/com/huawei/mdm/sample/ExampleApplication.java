package com.huawei.mdm.sample;

import android.app.Application;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

public class ExampleApplication  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       // Log.i("极光","极光初始化");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}