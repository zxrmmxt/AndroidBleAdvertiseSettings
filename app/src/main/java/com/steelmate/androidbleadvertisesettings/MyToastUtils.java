package com.steelmate.androidbleadvertisesettings;

import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author xt on 2019/11/22 15:18
 */
public class MyToastUtils {
    private static           Handler sToastThreadHandler;
    private static final Object  synObj = new Object();

    private static Handler getToastThreadHandler() {
        if (sToastThreadHandler == null) {
            sToastThreadHandler = AppCommonThreadUtils.getThreadHandler();
        }
        return sToastThreadHandler;
    }

    public static void showShortToast(final CharSequence text) {
        getToastThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    if (!TextUtils.isEmpty(text)) {
                        Toast.makeText(AppCommonContextUtils.getApp(), text, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
