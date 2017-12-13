package com.example.robot.a321movie.Base;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by robot on 2017/12/11.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);//初始化 xUtils3
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
