package com.example.birdguan.cownet;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.birdguan.cownet.model.UserData;

/**
 * Created by Gwg on 2016/8/30.
 */
public class MyApplication extends Application {
    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;

    private static Context mContext;
    private static UserData loginUser;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (null == mBluetoothManager) {
            Log.d("debug", "BluetoothManager init error!");
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (null == mBluetoothAdapter) {
            Log.d("debug", "BluetoothManager init error!");
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, "该手机不支持蓝牙通讯！", Toast.LENGTH_SHORT).show();
        }

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
//        if (!mBluetoothAdapter.isEnabled()) {
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//        }

        //android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setLoginUser(UserData user) {
        loginUser = user;
    }

    public static UserData getLoginUser() {

        return loginUser;
    }

    public static boolean IsAuthorizedDevice() {
        return Build.MODEL.toUpperCase().equals("H941");
    }
}
