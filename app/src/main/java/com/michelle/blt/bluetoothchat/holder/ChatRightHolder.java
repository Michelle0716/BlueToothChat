package com.michelle.blt.bluetoothchat.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.zzh.bluetoothchat.R;

/**
 * Created by michelle on 2017/5/16.
 */

public class ChatRightHolder extends RecyclerView.ViewHolder {
    private TextView tvContent;
    private TextView tvName;
    private ImageView imageView;
    public ChatRightHolder(View itemView) {
        super(itemView);
        tvContent = (TextView) itemView.findViewById(R.id.tv_right);
        tvName = (TextView) itemView.findViewById(R.id.tv_device);
        imageView=(ImageView)itemView.findViewById(R.id.image_right);

    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public void setTvContent(TextView tvContent) {
        this.tvContent = tvContent;
    }

    public TextView getTvName() {
        return tvName;
    }

    public void setTvName(TextView tvName) {
        this.tvName = tvName;
    }
}
