package com.michelle.blt.bluetoothchat.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rdc.zzh.bluetoothchat.R;

/**
 * Created by michelle on 2018/5/15.
 * 文字信息提醒，如：以下是已配对设备
 *
 */

public class ToastHolder extends RecyclerView.ViewHolder{
    private TextView tvToast;
    public ToastHolder(View itemView) {
        super(itemView);
        tvToast = (TextView)itemView.findViewById(R.id.tv_toast);
    }

    public TextView getTvToast() {
        return tvToast;
    }

    public void setTvToast(TextView tvToast) {
        this.tvToast = tvToast;
    }
}