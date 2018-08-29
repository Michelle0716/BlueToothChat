package com.michelle.blt.bluetoothchat.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 蓝牙连接工具类，有多种连接方式，进行遍历合适的一种
 * Created by Michelle on 2018/8/23.
 */

public class BlueToothConnectUtil {

    public BlueToothConnectUtil instance;
    /**
     * uuid是固定的，串口终端有自己的uuid,不同设备交互有对应的系统uuid
     */
   // public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String SPP_UUID = "fa87c0d0-afac-11de-8a39-0800200c9a66";
    static final UUID SPPUUID = UUID.fromString(SPP_UUID);

    public BlueToothConnectUtil setBlue(){
        if(instance==null){
            instance=new BlueToothConnectUtil();
        }
        return  instance;
    }


    BluetoothSocket createSecureSocketWithChannel(BluetoothDevice mDevice, int channel) {
        BluetoothSocket socket = null;
        Class<? extends BluetoothDevice> cls = BluetoothDevice.class;
        Method m = null;
        try {
            m = cls.getMethod("createRfcommSocket", int.class);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
        if (m != null) {
            try {
                socket = (BluetoothSocket) m.invoke(mDevice, channel);
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        return socket;
    }

    BluetoothSocket createInsecureSocketWithChannel(BluetoothDevice mDevice,int channel) {
        BluetoothSocket socket = null;
        Class<? extends BluetoothDevice> cls = BluetoothDevice.class;
        Method m = null;
        try {
            m = cls.getMethod("createInsecureRfcommSocket", int.class);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
        if (m != null) {
            try {
                socket = (BluetoothSocket) m.invoke(mDevice, channel);
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }


        return socket;
    }


    /**
     * 传入蓝牙设备，遍历，创建socket连接
     * @param mDevice
     * @return
     */
    BluetoothSocket createSecureSocket(BluetoothDevice mDevice) {
        BluetoothSocket socket = null;
        try {
            socket = mDevice.createRfcommSocketToServiceRecord(SPPUUID);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    BluetoothSocket createInsecureSocket(BluetoothDevice mDevice) {
        BluetoothSocket socket = null;
        Class<? extends BluetoothDevice> cls = BluetoothDevice.class;
        Method m = null;

        try {
            m = cls.getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (m != null) {
            try {
                socket = (BluetoothSocket) m.invoke(mDevice, SPPUUID);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }



    public  BluetoothSocket setSocket(BluetoothDevice device){
        BluetoothSocket tmp = null;
        int version = getSdkVersion();
        Log.d("SdkVersion: ",  String.valueOf(version));
        if ( version >= 3.1 )
        {
            int mode=0;
            while (mode<5){
                switch (mode++){
                    case 0:
                        tmp = createSecureSocketWithChannel(device,6);
                        break;
                    case 1:
                        tmp = createInsecureSocket(device);
                        break;
                    case 2:
                        tmp = createInsecureSocketWithChannel(device,6);
                        break;
                    case 3:
                        tmp = createSecureSocket(device);
                        break;
                }
                if (tmp!=null){
                    mode = 5;

                }
            }
            if (tmp==null){
                try {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tmp;

      }


    private int getSdkVersion() {
        int version = 0;
        version = Integer.parseInt(android.os.Build.VERSION.SDK);
        return version;
    }

}
