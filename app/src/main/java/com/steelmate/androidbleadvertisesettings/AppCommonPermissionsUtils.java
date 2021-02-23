package com.steelmate.androidbleadvertisesettings;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * created by XuTi on 2019/5/9 9:38
 */
public class AppCommonPermissionsUtils {
    public static boolean needRuntimePermissions() {
        return android.os.Build.VERSION.SDK_INT >= 23;
    }

    @NonNull
    private static String[] getNotGrantedPermissions(List<String> permissions) {
        ArrayList<String> per = new ArrayList<>();
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(Utils.getApp(), permission)) {
                per.add(permission);
            }
        }
        String[] p = new String[per.size()];
        return per.toArray(p);
    }

    public static boolean hasPermissions(List<String> permissions) {
        String[] per = getNotGrantedPermissions(permissions);
        if (per.length > 0) {
            return false;
        }
        return true;
    }

    public static boolean requestPermissions(Activity activity, List<String> permissions, int requestCode) {
        String[] per = getNotGrantedPermissions(permissions);
        if (per.length > 0) {
            ActivityCompat.requestPermissions(activity, per, requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean requestPermissions(Fragment fragment, List<String> permissions, int requestCode) {
        String[] per = getNotGrantedPermissions(permissions);
        if (per.length > 0) {
            fragment.requestPermissions(per, requestCode);
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param grantResults activity中onRequestPermissionsResult方法的参数
     * @return 是否被授权
     */
    public static boolean isPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
