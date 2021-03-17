package com.steelmate.androidbleadvertisesettings;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.le.ScanFilter;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xt on 2019/11/21 14:48
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);

        BleAdvertisingModel.getInstance().init(this, BleAdvertisingModel.ADVERTISE_SERVICE_UUID, BleAdvertisingModel.ADVERTISER_SERVICE_DATA_UUID, BleAdvertisingModel.MANUFACTURER_ID);

        AppUtils.registerAppStatusChangedListener(new Utils.OnAppStatusChangedListener() {
            @Override
            public void onForeground(Activity activity) {
                List<ScanFilter.Builder> builderList = new ArrayList<>();
                builderList.add(new ScanFilter.Builder().setServiceUuid(BleAdvertisingModel.getInstance().getServiceUuid()));
                BleAdvertisingModel.getInstance().startBleScan(builderList);
            }

            @Override
            public void onBackground(Activity activity) {
                BleAdvertisingModel.getInstance().stopScan();
            }
        });
    }
}
