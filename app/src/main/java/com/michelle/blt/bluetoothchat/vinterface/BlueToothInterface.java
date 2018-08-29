package com.michelle.blt.bluetoothchat.vinterface;

import android.bluetooth.BluetoothDevice;

/**
 * Created by michelle on 2017/5/15. 蓝牙搜索到设备或搜索结束
 */

public interface BlueToothInterface {
    void getBlutToothDevices(BluetoothDevice device , int rssi);
    void searchFinish();
}
