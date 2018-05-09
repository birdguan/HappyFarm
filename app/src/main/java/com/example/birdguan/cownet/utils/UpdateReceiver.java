package com.example.birdguan.cownet.utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.birdguan.cownet.MyApplication;


public class UpdateReceiver extends BroadcastReceiver {
    public static final String UPDATE_BROADCAST = "com.zjfsoft.petapp.LOCAL_BROADCAST_UPDATE";

    private AlertDialog.Builder mDialog;
    private boolean isShowDialog;

    public UpdateReceiver() {
    }

    public UpdateReceiver(boolean isShowDialog) {
        super();
        this.isShowDialog = isShowDialog;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(MyApplication.getContext(), "onReceive " + intent.getStringExtra("version"), Toast.LENGTH_SHORT).show();
    }
}