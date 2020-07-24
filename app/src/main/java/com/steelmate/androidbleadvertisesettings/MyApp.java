package com.steelmate.androidbleadvertisesettings;

import android.app.Activity;
import android.app.Application;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

/**
 * @author xt on 2019/11/21 14:48
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCommonContextUtils.init(this);

        BleAdvertisingModel.getInstance().init(this, BleAdvertisingModel.ADVERTISE_SERVICE_UUID, BleAdvertisingModel.ADVERTISER_SERVICE_DATA_UUID, BleAdvertisingModel.MANUFACTURER_ID);

        AppUtils.registerAppStatusChangedListener(new Utils.OnAppStatusChangedListener() {
            @Override
            public void onForeground(Activity activity) {
                BleAdvertisingModel.getInstance().startScan();
            }

            @Override
            public void onBackground(Activity activity) {
                BleAdvertisingModel.getInstance().stopScan();
            }
        });
    }
}
