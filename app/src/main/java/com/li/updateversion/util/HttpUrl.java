package com.li.updateversion.util;

import android.os.Environment;

/**
 * 跟服务器交互时的接口
 */
public class HttpUrl {
    // APK下载地址
    public static final String PATH = Environment.getExternalStorageDirectory()
            + "/" + "download";// 获得存储卡的路径+文件夹名称
}
