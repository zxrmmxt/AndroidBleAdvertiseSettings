package com.steelmate.androidbleadvertisesettings;

/**
 * @author xt on 2019/11/21 16:24
 */
public class AdvertiseMode {
    private int advertiseMode;
    private String advertiseModeDesc;

    public AdvertiseMode(int advertiseMode, String advertiseModeDesc) {
        this.advertiseMode = advertiseMode;
        this.advertiseModeDesc = advertiseModeDesc;
    }

    public int getAdvertiseMode() {
        return advertiseMode;
    }

    public void setAdvertiseMode(int advertiseMode) {
        this.advertiseMode = advertiseMode;
    }

    public String getAdvertiseModeDesc() {
        return advertiseModeDesc;
    }

    public void setAdvertiseModeDesc(String advertiseModeDesc) {
        this.advertiseModeDesc = advertiseModeDesc;
    }

    @Override
    public String toString() {
        return "发送频率"+advertiseModeDesc;
    }
}
