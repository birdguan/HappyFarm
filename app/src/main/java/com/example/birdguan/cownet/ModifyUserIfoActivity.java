package com.example.birdguan.cownet;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.common.RequestParameters;
import com.alibaba.sdk.android.oss.common.utils.HttpUtil;
import com.example.birdguan.cownet.utils.OkHttpManager;
import com.example.birdguan.cownet.utils.OssUtils;
import com.mob.tools.utils.Data;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.annotation.HttpResponse;
import org.xutils.http.request.HttpRequest;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Request;

public class ModifyUserIfoActivity extends Activity implements AdapterView.OnItemSelectedListener{
    public static final int TAKE_PHOTO = 1;
    public static final int CHOSE_PHOTO = 2;
    private Uri uri_imageView;
    private Bitmap bitmap;
    private List<String> list_gender = new ArrayList<String>();
    private EditText editText_modifyUserName;
    private EditText editText_modifyPhoneNUmber;
    private EditText editText_modifyAddress;
    private TextView textView_modifyGender;
    private ImageView imageView_modifyUserIcon;
    private TextView textView_userRole;
    private Uri cameraImageUri;
    private String imagePath;
    private File imageFile = null;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyuserinfo);
        setCustomActionBar();
        x.Ext.init(getApplication());
        mProgressDialog = new ProgressDialog(ModifyUserIfoActivity.this);
        list_gender.add(new String("男"));
        list_gender.add(new String("女"));
        final Spinner spinner_uerGender = findViewById(R.id.spinner_userGender);
        editText_modifyUserName = findViewById(R.id.editText_modifuUserInfoName);
        editText_modifyAddress = findViewById(R.id.editTextModifyUserInfoAddress);
        editText_modifyPhoneNUmber = findViewById(R.id.editText_modifyUserInfoPhoneNumber);
        textView_modifyGender = findViewById(R.id.textView_spinnerGender);
        imageView_modifyUserIcon = findViewById(R.id.imageView_usericon);
        textView_userRole = findViewById(R.id.textView_userid);
        SharedPreferences lognInfo = getSharedPreferences("login",MODE_PRIVATE);
        String userIconUrl = lognInfo.getString("userIconUrl", "");
        textView_userRole.setText(lognInfo.getString("roleID", ""));
        Picasso.with(getApplicationContext()).load(userIconUrl).into(imageView_modifyUserIcon);
        Intent intent = getIntent();
        editText_modifyUserName.setText(intent.getStringExtra("name"));
        editText_modifyPhoneNUmber.setText(intent.getStringExtra("phoneNumber"));
        editText_modifyAddress.setText(intent.getStringExtra("address"));
        //性别spinner数据及布局
        ArrayAdapter<String> adapter_userGender = new ArrayAdapter<String>(this, R.layout.custom_spinner_gender, list_gender);
        adapter_userGender.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner_uerGender.setAdapter(adapter_userGender);
        spinner_uerGender.setOnItemSelectedListener(this);
        if (intent.getStringExtra("gender").equals("男")){
            spinner_uerGender.setSelection(0);
        }else{
            spinner_uerGender.setSelection(1);
        }

        //点击头像修改用户头像
        ImageView imageView_modifyUserIcon = findViewById(R.id.imageView_usericon);
        imageView_modifyUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "点击用户头像");
                //弹出选择相机拍摄还是相册选取的popupwindow
                showPopupwindow();
            }
        });

        //“保存”按钮
        Button button_saveModify = findViewById(R.id.button_saveModify);
        button_saveModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "/n-------------------------------------");
                Log.d("Debug", "/n开始保存用户修改信息");


//                if (imagePath != null){
//                    Log.d("Debug", "Uri不为空");
//                    String filename = imagePath.split("/")[imagePath.split("/").length - 1];
//                    Log.d("Debug", "filename: " + filename);
                SharedPreferences sharedPreferences_oestus = getSharedPreferences("login", MODE_PRIVATE);
                String user_id = sharedPreferences_oestus.getString("userID","");
                Log.d("Debug", "imageFile: " + imageFile);
                if (imageFile != null) {
                    String url_uploadUserIcon = "http://106.15.53.134:60006/LivestockSystem2018_APP/appShepherd/appEditPhoto";
                    RequestParams params = new RequestParams(url_uploadUserIcon);
                    params.addBodyParameter("USER_ID", user_id);
                    params.addBodyParameter("tp", imageFile);
                    Callback.Cancelable cancelable = x.http().post(params, new Callback.ProgressCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("Debug", "图片文件上传成功");
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Log.d("Debug", "图片文件上传失败");

                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {
                            showProgress(false);
                            AlertDialog.Builder normalDialog = new AlertDialog.Builder(ModifyUserIfoActivity.this);
                            normalDialog.setMessage("修改成功");
                            normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ModifyUserIfoActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                }
                            });
                            normalDialog.show();
                        }

                        @Override
                        public void onWaiting() {
                            showProgress(true);
                        }

                        @Override
                        public void onStarted() {

                        }

                        @Override
                        public void onLoading(long total, long current, boolean isDownloading) {

                        }
                    });
                }
//                    Map<String, File> map_uploadUserIcon = new HashMap<String, File>();
//                    map_uploadUserIcon.put("tp", imageFile);
//                    map_uploadUserIcon.put("USER_ID", user_id);
//                    OssUtils.uploadAsync(imagePath)
//                    String url_uploadUserIcon = "http://106.15.53.134:60006/LivestockSystem2018_APP/appShepherd/appEditPhoto";
//
//                    OkHttpManager.postAsync(url_uploadUserIcon, map_uploadUserIcon, new OkHttpManager.DataCallBack() {
//                        @Override
//                        public void requestFailure(Request request, IOException e) {
//                            Log.d("Debug", "修改用户头像连接失败");
//                        }
//
//                        @Override
//                        public void requestSuccess(String result) throws Exception {
//                            Log.d("Debug", "修改用户头像结果：" + result);
//                            try {
//                                JSONObject jsonObject_uploadUserIcon = new JSONObject(result);
//                                if (jsonObject_uploadUserIcon.getString("message").equals("01")) {
//                                    Log.d("Debug", "修改用户照片成功");
//                                }
//                            }catch (Exception e){
//                                Log.d("Debug", "修改用户头像时：" + e.toString());
//                            }
////
////                        }
////                    });
////                }else {
////                    Log.d("Debug", "Uri为空");
////                }
                Log.d("Debug", "\n\nimagePath: " + imagePath);
                SharedPreferences loginInfo = getSharedPreferences("login",MODE_PRIVATE);
//                String user_id = loginInfo.getString("userID", "");
                String role_id = loginInfo.getString("roleID", "");
                String url_modifyUserInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appShepherd/appEditShepherdInfo";
                Map<String, String> map_modifyUserInfo = new HashMap<String, String>();
                map_modifyUserInfo.put("user_id", user_id );
                map_modifyUserInfo.put("role_id", role_id);
                map_modifyUserInfo.put("name", editText_modifyUserName.getText().toString());
                map_modifyUserInfo.put("gender", textView_modifyGender.getText().toString());
                map_modifyUserInfo.put("phone", editText_modifyPhoneNUmber.getText().toString());
                map_modifyUserInfo.put("address", editText_modifyAddress.getText().toString());
                map_modifyUserInfo.put("ID_number", editText_modifyPhoneNUmber.getText().toString());
                OkHttpManager.postAsync(url_modifyUserInfo, map_modifyUserInfo, new OkHttpManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        Log.d("Debug", "修改合作户信息接口连接失败");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        try {
                            Log.d("Debug", result);
                            JSONObject jsonObject_modifyUserInfo = new JSONObject(result);
                            if (jsonObject_modifyUserInfo.getString("message").equals("01")){
                                Log.d("Debug", "修改成功");
                                if (imageFile == null){
                                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(ModifyUserIfoActivity.this);
                                    normalDialog.setMessage("修改成功");
                                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(ModifyUserIfoActivity.this, MenuActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    normalDialog.show();
                                }
                            }
                        }catch (Exception e){
                            Log.d("Debug", e.toString());
                        }
                    }
                });



            }
        });
    }
    private void showProgress(final boolean show) {
        if (show) {
            mProgressDialog.setTitle("请稍候");
            mProgressDialog.setMessage("图片上传中...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri_imageView);
//                        Matrix matrix = new Matrix();
//                        matrix.setScale(0.5f, 0.5f);
//                        bitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        imageView_modifyUserIcon.setImageBitmap(bitmap);
                        try {
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(imageFile));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                            bufferedOutputStream.flush();
                            bufferedOutputStream.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                    }catch (IOException e){
                        Log.d("Debug", e.toString());
                    }
//                    if (data != null) {
//                        Bundle bundle = data.getExtras();
//                        bitmap = (Bitmap) bundle.get("data");
//                    }else {
//                        imagePath = uri_imageView.getPath();
//                        Log.d("Debug", "imagePath: " + imagePath);
//
//                    }

//                    bitmap = BitmapFactory.decodeFile(imagePath);
//                    Matrix matrix = new Matrix();
//                    matrix.setScale(0.5f, 0.5f);
//                    bitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                    imageView_modifyUserIcon.setImageBitmap(bitmap);
                }
                break;
            case CHOSE_PHOTO:
                if (Build.VERSION.SDK_INT >= 19){
                    handleImageOnKitKat(data);
                }else{
                    handleImageBeforeKitKat(data);
                }
                bitmap = BitmapFactory.decodeFile(imagePath);
                Matrix matrix = new Matrix();
                matrix.setScale(0.5f, 0.5f);
                bitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imageView_modifyUserIcon.setImageBitmap(bitmap);
                imageFile = new File(imagePath);
                try {
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(imageFile));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
        }
    }

    // 4.4及以上系统使用这个方法处理图片 相册图片返回的不再是真实的Uri,而是分装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Log.d("Debug", "Uri: " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        uri_imageView = uri;
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        uri_imageView = uri;
        imagePath = getImagePath(uri, null);
    }


    private void showPopupwindow() {
        View parent = this.findViewById(android.R.id.content);
        final View popupView = View.inflate(this, R.layout.custom_camera_popupwindow, null);
        Button button_camera = popupView.findViewById(R.id.btn_camera_pop_camera);
        Button button_album = popupView.findViewById(R.id.btn_camera_pop_album);
        Button button_cancel = popupView.findViewById(R.id.btn_camera_pop_cancel);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);


        //“相机拍摄”按钮
        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "相机拍摄");
                File outputImage = new File(getExternalCacheDir(), "userIcon.jpg");
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT <24){
                    uri_imageView = Uri.fromFile(outputImage);
                }else {
                    //安卓7.0开始，使用本地的真实Uri路径不安全，使用FileProvider封装共享Uri
                    //参数二：fileprovider绝对路径
                    uri_imageView = FileProvider.getUriForFile(ModifyUserIfoActivity.this, "com.example.birdguan.cownet.fileprovider", outputImage);

                }
                imageFile = outputImage;
                popupWindow.dismiss();
                Log.d("Debug", "拍照");
                Intent intent_camera = new Intent("android.media.action.IMAGE_CAPTURE");
                intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, uri_imageView);
                startActivityForResult(intent_camera, TAKE_PHOTO);
            }
        });

        //“相册选取”按钮
        button_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "相册选取");
                if (ContextCompat.checkSelfPermission(ModifyUserIfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ModifyUserIfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else{
                    Intent intent_openAlbum =new Intent("android.intent.action.GET_CONTENT");
                    intent_openAlbum.setType("image/*");
                    startActivityForResult(intent_openAlbum, CHOSE_PHOTO);
                    popupWindow.dismiss();
                }
            }
        });

        //“取消”按钮
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "取消");
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(parent, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);

    }


    //gender spinner选择处理
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_userGender:
                textView_modifyGender = findViewById(R.id.textView_spinnerGender);
                SharedPreferences SharedPreferences_userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor_newUserData = SharedPreferences_userInfo.edit();
                editor_newUserData.putString("userGender", textView_modifyGender.getText().toString());
                Log.d("Debug", "选择的性别：" + textView_modifyGender.getText().toString());
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        android.app.ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#448936")));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ModifyUserIfoActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void setCustomActionBar(){
        android.app.ActionBar.LayoutParams layoutParams = new android.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.custom_actionbar, null);
        TextView textView = mActionBarView.findViewById(R.id.textView_title);
        textView.setText("修改信息");
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setCustomView(mActionBarView, layoutParams);
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        ImageView imageView = mActionBarView.findViewById(R.id.imageView_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                Intent intent = new Intent(ModifyUserIfoActivity.this, MenuActivity.class);
//                startActivity(intent);
            }
        });
    }

}
