package com.steelmate.androidbleadvertisesettings;

/**
 * @author xt on 2019/11/21 16:25
 */
public class ScanMode {
    private int    scanMode;
    private String scanModeDesc;

    public ScanMode(int scanMode, String scanModeDesc) {
        this.scanMode = scanMode;
        this.scanModeDesc = scanModeDesc;
    }

    public int getScanMode() {
        return scanMode;
    }

    public void setScanMode(int scanMode) {
        this.scanMode = scanMode;
    }

    public String getScanModeDesc() {
        return scanModeDesc;
    }

    public void setScanModeDesc(String scanModeDesc) {
        this.scanModeDesc = scanModeDesc;
    }

    @Override
    public String toString() {
        return "接收频率"+scanModeDesc;
    }
}
