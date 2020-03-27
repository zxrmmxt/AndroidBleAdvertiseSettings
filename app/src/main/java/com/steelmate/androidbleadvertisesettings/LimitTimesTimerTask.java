package com.steelmate.androidbleadvertisesettings;

import java.util.TimerTask;

/**
 * @author xt on 2019/6/12 14:36
 */
public class LimitTimesTimerTask extends TimerTask {
    private volatile int                         mTimes;
    private          LimitTimesTimerTaskCallback mLimitTimesTimerTaskCallback;

    public LimitTimesTimerTask(int times, LimitTimesTimerTaskCallback limitTimesTimerTaskCallback) {
        super();
        this.mTimes = times;
        mLimitTimesTimerTaskCallback = limitTimesTimerTaskCallback;
    }

    @Override
    public void run() {
        mTimes = mTimes - 1;
        if (mTimes < 0) {
            cancel();
            if (mLimitTimesTimerTaskCallback != null) {
                mLimitTimesTimerTaskCallback.onTimerTaskEnd();
            }
        } else {
            if (mLimitTimesTimerTaskCallback != null) {
                mLimitTimesTimerTaskCallback.onDoTimerTask();
            }
        }
    }

    public interface LimitTimesTimerTaskCallback {
        void onDoTimerTask();

        void onTimerTaskEnd();
    }

    public int getTimes() {
        return mTimes;
    }
}
