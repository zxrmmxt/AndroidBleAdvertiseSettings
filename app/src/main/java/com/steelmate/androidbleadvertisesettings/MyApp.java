package com.steelmate.androidbleadvertisesettings;

import android.app.Application;

/**
 * @author xt on 2019/11/21 14:48
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCommonContextUtils.init(this);
    }
}
