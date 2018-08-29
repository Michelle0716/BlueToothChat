package com.michelle.blt.bluetoothchat.bean;

/**
 * Created by michelle on 2017/5/10.
 */

public class BlueTooth {
    /**
     * 显示蓝牙信息
     */
    public static final int TAG_NORMAL = 0;
    /**
     * 显示msg/title之类的文字提醒
     */
    public static final int TAG_TOAST = 1;
    private int tag;
    private String name;
    private String mac;
    /**
     * 蓝牙强度
     */
    private String rssi;

    public BlueTooth(String name , int tag) {
        this.tag = tag;
        this.name = name;
    }
    public BlueTooth(String name, String mac, String rssi) {
        tag = TAG_NORMAL;
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
}
