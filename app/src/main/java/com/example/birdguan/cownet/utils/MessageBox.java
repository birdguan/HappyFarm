package com.example.birdguan.cownet.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Gwg on 2016/8/30.
 */
public class MessageBox {

    public static void Show(Context context, String text) {
        MessageBox.Show(context, text, "提示");
    }

    public static void Show(Context context, String text, Boolean isCancelable) {
        MessageBox.Show(context, text, "提示", isCancelable, null);
    }

    public static void Show(Context context, String text, String Title) {
        MessageBox.Show(context, text, Title);
    }

    public static void Show(Context context, String text, String Title,
                            Boolean isCancelable, final DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(isCancelable)
                .setTitle(Title)
                .setMessage(text)
                .setPositiveButton("确定", listener);
        builder.create().show();
    }
}
