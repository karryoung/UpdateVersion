package com.li.updateversion.util;

import android.content.Context;
import android.content.Intent;

import com.li.updateversion.CheckUpdateActivity;
import com.li.updateversion.bean.VersionBean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 判断当前应用是否需要更新的辅助类
 */
public class CheckUpdataUtil {
    private Context context;
    private int localVersionCode = -1;// 本地版本号

    public CheckUpdataUtil(Context context) {
        this.context = context;
        localVersionCode = Util.getVersionCode(context);
    }

    public void IsUpdate() {
        try {
            new Thread() {
                @Override
                public void run() {
                    //版本更新的网络请求
                    String url = "";
                    //返回的结果，
                    String result = "";
                    LogUtil.i("--url_update--" + url);
                    LogUtil.i("--result_update--" + result);
                    if (result != null && !"".equals(result)) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            if (1 == jsonObject.getInt("Result")) {
                                JSONObject dataJson = jsonObject
                                        .getJSONObject("Data");
                                VersionBean versionBean = new VersionBean();
                                versionBean.versionCode = dataJson.getInt("VersionCode");
                                versionBean.versionName = dataJson.getString("VersionName");
                                versionBean.appAddress = dataJson.getString("AppAddress");
                                if (versionBean.versionCode > localVersionCode) {
                                    Intent in = new Intent(context, CheckUpdateActivity.class);
                                    in.putExtra("versionBean", versionBean);
                                    context.startActivity(in);
                                }
                            }
                        } catch (JSONException e) {
                            LogUtil.e(getClass(), "httpRequest()", e);
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            LogUtil.e(getClass(), "httpRequest", e);
        }
    }

}
