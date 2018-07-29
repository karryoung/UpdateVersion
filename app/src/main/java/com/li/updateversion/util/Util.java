package com.li.updateversion.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.li.updateversion.R;

/**
 * 辅助类
 */
public class Util {
    public static final String UPDATE_IS_RIGHT = "update_is_right";// 当有新版本时，发送广播所需要的action

    // 获取系统versionCode
    public static int getVersionCode(Context context) {
        int version = -1;
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (Exception e) {
            LogUtil.e(Util.class, "getVersionCode", e);
        }
        return version;
    }

    // 获取系统版本号
    public static String getVersionName(Context context) {
        String version = "";
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (Exception e) {
            LogUtil.e(Util.class, "getVersionName", e);
        }
        return version;
    }

    // 如果返回false
    public static boolean judgeInternet(Context context) {// 是否连接网络
        try {

            if (!isNetworkConnected(context)) {
                ToastManager.showToast(context, context.getString(R.string.nerwork_error_retry));
                return false;
            }

        } catch (Exception e) {
            LogUtil.e(Util.class, "judgeInternet(Context context)", e);
        }
        return true;
    }

    // 判断当前网络是否连接
    public static boolean isNetworkConnected(Context context) {
        try {

            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager
                        .getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }

        } catch (Exception e) {
            LogUtil.e(Util.class, "isNetworkConnected(Context context)", e);
        }
        return false;
    }
}
