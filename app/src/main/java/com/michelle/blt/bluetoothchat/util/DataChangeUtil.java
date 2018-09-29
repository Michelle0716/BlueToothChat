package com.michelle.blt.bluetoothchat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rdc.zzh.bluetoothchat.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by centerm on 2018/9/29.
 */

public class DataChangeUtil {

    //图片转为二进制数据
    public static byte[] bitmabToBytes(Context context,int pic){
        //将图片转化为位图
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), pic);
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos= new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //将字节数组输出流转化为字节数组byte[]
            byte[] imagedata = baos.toByteArray();
            return imagedata;
        }catch (Exception e){
        }finally {
            try {
                bitmap.recycle();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }


    public static byte[] Bitmap2Bytes(Context context, int pic) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), pic);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }



    //  byte[] → Bitmap

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

}
