package com.michelle.blt.bluetoothchat.bean;

import android.graphics.Bitmap;

/**
 * Created by michelle on 2017/5/16.
 */

public class ChatInfo {
    public static final int TAG_LEFT = 0;
    public static final int TAG_RIGHT = 1;
    private int tag;
    private String name;
    private String content;
    public  byte[] drawble;

    public ChatInfo(int tag, String name, String content,byte[] drable) {
        this.tag = tag;
        this.name = name;
        this.content = content;
        this.drawble=drable;

    }

    public byte[] getDrawble() {
        return drawble;
    }

    public void setDrawble(byte[] drawble) {
        this.drawble = drawble;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
