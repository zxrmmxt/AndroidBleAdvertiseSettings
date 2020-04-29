package com.steelmate.androidbleadvertisesettings;

import android.app.Application;
import android.os.Build;

/**
 * @author xt on 2019/11/21 14:48
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCommonContextUtils.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BleAdvertisingModel.getInstance().init(BleAdvertisingModel.ADVERTISE_SERVICE_UUID, BleAdvertisingModel.ADVERTISER_SERVICE_DATA_UUID, BleAdvertisingModel.MANUFACTURER_ID);
        }
    }
}
