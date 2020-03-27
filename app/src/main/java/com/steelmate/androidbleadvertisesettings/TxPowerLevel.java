package com.steelmate.androidbleadvertisesettings;

/**
 * @author xt on 2019/11/21 16:02
 */
public class TxPowerLevel {
    private int    txPowerLevel;
    private String txPowerLevelDesc;

    public TxPowerLevel(int txPowerLevel, String txPowerLevelDesc) {
        this.txPowerLevel = txPowerLevel;
        this.txPowerLevelDesc = txPowerLevelDesc;
    }

    public int getTxPowerLevel() {
        return txPowerLevel;
    }

    public void setTxPowerLevel(int txPowerLevel) {
        this.txPowerLevel = txPowerLevel;
    }

    public String getTxPowerLevelDesc() {
        return txPowerLevelDesc;
    }

    public void setTxPowerLevelDesc(String txPowerLevelDesc) {
        this.txPowerLevelDesc = txPowerLevelDesc;
    }

    @Override
    public String toString() {
        return "发送功率"+txPowerLevelDesc;
    }
}
