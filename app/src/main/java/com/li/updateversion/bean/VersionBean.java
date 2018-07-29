package com.li.updateversion.bean;


import java.io.Serializable;

/**
 * 跟服务器交付时得到的版本数据的实体类
 */
public class VersionBean implements Serializable {
    public int versionCode;//版本号
    public String versionName;//版本名
    public String appAddress;//应用地址
}
