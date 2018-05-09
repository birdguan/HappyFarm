package com.example.birdguan.cownet.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.birdguan.cownet.R;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class AppUpdateUtils {

    private static final String UPDATE_URL = "http://120.27.96.188/static/HappyFarmUpdate.json";

    private static Activity mActivity;
    private static boolean mIsShowDialog;

    public static void checkVersion(Activity activity, boolean isShowDialog) {
        mActivity = activity;
        mIsShowDialog = isShowDialog;

        OkHttpManager.getAsync(UPDATE_URL, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
            }

            @Override
            public void requestSuccess(String result) {
                //Log.d("debug", result);
                appUpdate(result);
            }
        });
    }

    private static void appUpdate(String result) {
        Map<String, String> map = JSON.parseObject(result, new TypeReference<Map<String, String>>() {
        });

        int lastForce = Integer.valueOf(map.get("lastForce"));
        int updateFlag = Integer.valueOf(map.get("updateFlag"));
        String upgradeinfo = map.get("upgradeinfo");
        String url = map.get("updateurl");

        Integer localVersion = getVersionCode(mActivity);
        Log.d("Debug", "当前版本号：" + localVersion);
        Log.d("Debug", "服务器版本号: " + Integer.valueOf(map.get("serverVersionCode")));
        if (localVersion < Integer.valueOf(map.get("serverVersionCode"))) {
            if (updateFlag == 1) {
                // 官方推荐升级
                if (localVersion < lastForce) {
                    //强制升级
                    forceUpdate(upgradeinfo, url);
                } else {
                    //正常升级
                    normalUpdate(upgradeinfo, url);
                }
            } else if (updateFlag == 2) {
                // 官方强制升级
                forceUpdate(upgradeinfo, url);
            }
        } else {
            if (mIsShowDialog) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("提示");
                builder.setMessage("已经是最新版本。");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }

            clearUpdateFile();
        }
    }

    //强制更新
    private static void forceUpdate(String upgradeinfo, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("版本更新");
        builder.setMessage(upgradeinfo);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startUpdate(url);
            }
        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        }).setCancelable(false).create().show();
    }

    //正常升级，用户可以选择是否取消升级
    private static void normalUpdate(String upgradeinfo, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("版本更新");
        builder.setMessage(upgradeinfo);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startUpdate(url);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private static void startUpdate(String url) {
        final ProgressDialog progressDlg = new ProgressDialog(mActivity);
        progressDlg.setTitle("系统更新");
        progressDlg.setMessage("安装文件下载中...");
        progressDlg.setIcon(R.mipmap.ic_newlaucher);
        progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDlg.setCancelable(false);
        progressDlg.show();

        String fileName = url.split("/")[url.split("/").length - 1];

        File updateDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            updateDir = Environment.getExternalStorageDirectory();
        } else {
            updateDir = mActivity.getFilesDir();
        }
        final File localFile = new File(updateDir, fileName);

        OkHttpManager.downloadAsync(url, new ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean done) {
                Log.d("debug", "currentBytes=" + currentBytes + ",contentLength=" + contentLength + ",done=" + done);

                int progress = (int) (currentBytes * 100 / contentLength);
                progressDlg.setProgress(progress);

                if (done) {
                    //安装程序
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    installIntent.setDataAndType(Uri.fromFile(localFile), "application/vnd.android.package-archive");

                    //启动安装程序
                    mActivity.startActivity(installIntent);
                    progressDlg.dismiss();
                }

            }
        }, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("debug", "downloadFile onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    if (response.code() != 200) {
                        progressDlg.dismiss();

                        final String message = response.message();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MessageBox.Show(mActivity, "下载失败：" + message, "错误", false, null);
                            }
                        });

                        return;
                    }

                    InputStream is = response.body().byteStream();

                    FileOutputStream fos = new FileOutputStream(localFile);
                    int len = 0;
                    byte[] buffer = new byte[2048];
                    while (-1 != (len = is.read(buffer))) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    is.close();
                }
            }
        });

    }

    static class myFileNameFilter {
        public static FilenameFilter filter(final String regex) {

            return new FilenameFilter() {
                private Pattern pattern = Pattern.compile(regex);

                @Override
                public boolean accept(File dir, String name) {
                    return pattern.matcher(name).matches();
                }
            };
        }
    }

    private static void clearUpdateFile() {
        File updateDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            updateDir = Environment.getExternalStorageDirectory();
        } else {
            updateDir = mActivity.getFilesDir();
        }

        final File[] files = updateDir.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().startsWith("qlw") && file.getName().endsWith("apk")) {
                Log.d("debug", "删除升级包 :" + file.getAbsolutePath());
                file.delete();
            }
        }
    }

    //versionCode:用于本地app和后台的app提供的版本进行对比，用于更新功能实现。
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    //versionName:用于展现给客户看的版本信息。
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
