//package com.example.birdguan.cownet;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.text.LoginFilter;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.alibaba.fastjson.TypeReference;
//import com.example.birdguan.cownet.model.UserData;
//import com.example.birdguan.cownet.utils.OkHttpManager;
//
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import okhttp3.Request;
//
//
//
//public class LoginActivity extends AppCompatActivity {
//    final String TAG = "info";
//    private EditText mUserNo;
//    private EditText mPassword;
//    private CheckBox mRemember;
//    private TextView mTextview_appversion;
//    private Button mSignInButton;
//    private ProgressDialog mProgressDialog;
//    private static boolean loginSuccessFlag = false;
//    private JSONObject jsonObject_Login;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        mProgressDialog = new ProgressDialog(LoginActivity.this);
//
//        mUserNo = findViewById(R.id.userno);
//        mPassword = findViewById(R.id.password);
//        mRemember = findViewById(R.id.rememberMe);
//        mSignInButton = findViewById(R.id.signin_button);
//        mTextview_appversion = findViewById(R.id.appversion);
//
//
//
//        mTextview_appversion.setText("系统版本V" + "0.1");
//
//        SharedPreferences loginInfo = getSharedPreferences("login", MODE_PRIVATE);
//        Boolean remenberMe = loginInfo.getBoolean("rememberme", false);
//        mRemember.setChecked(remenberMe);
//
//        if (remenberMe){
//            mUserNo.setText(loginInfo.getString("user",""));
//            mPassword.setText(loginInfo.getString("passwd", ""));
//        }
//
//        mSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    Login();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                //无验证登录测试
////                Intent intent_menu = new Intent(LoginActivity.this, MenuActivity.class);
////                startActivity(intent_menu);
//            }
//        });
//
//
//
////        Button mRegisterButton = findViewById(R.id.button_register_newUser);
////        Log.d(TAG, "onCreate: "+mRegisterButton);
////        mRegisterButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(LoginActivity.this, RegisterStep1Activity.class);
////                startActivity(intent);
////            }
////        });
//
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        loginSuccessFlag = false;
//    }
//
//    private void Login() throws JSONException {
//        String url_Login = "http://xyz.viphk.ngrok.org/LivestockSystem2018_APP/appUser/appLogin";
//        Map<String, String> map_Login = new HashMap<>();
//        map_Login.put("USERNAME", mUserNo.getText().toString());
//        map_Login.put("PASSWORD", mPassword.getText().toString());
//        OkHttpManager.postAsync(url_Login, map_Login, new OkHttpManager.DataCallBack() {
//            @Override
//            public void requestFailure(Request request, IOException e) {
//                Log.d("Debug", "获取登录信息连接失败");
//            }
//            @Override
//            public void requestSuccess(String result) throws Exception {
//                try {
//                    Log.d("Debug", result);
//                    jsonObject_Login = new JSONObject(result);
//                    if (jsonObject_Login.getString("message").equals("success")){
//                        Log.d("Debug", "登陆信息验证成功");
//                        loginSuccessFlag = true;
//                    }else {
//                        loginSuccessFlag = false;
//                        //警告框提示用户名或密码错误
//                        AlertDialog.Builder normalDialog = new AlertDialog.Builder(LoginActivity.this);
//                        normalDialog.setMessage("用户名或密码错误");
//                        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //消息提示用，无操作
//                            }
//                        });
//                        normalDialog.show();
//                    }
//
//
//
//                }catch (Exception e){
//                    Log.d("Debug", e.toString());
//                }
//            }
//        });
//
//        //测试后门
//        if(mUserNo.getText().toString().equals("admin") && mPassword.getText().toString().equals("admin")){
//            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
//            startActivity(intent);
//        }
//        if (loginSuccessFlag){
//            Intent intent_loginSuccess = new Intent(LoginActivity.this, MenuActivity.class);
//            intent_loginSuccess.putExtra("name", jsonObject_Login.getString("NAME"));
//            intent_loginSuccess.putExtra("role_id", jsonObject_Login.getString("ROLE_ID"));
//            startActivity(intent_loginSuccess);
//        }
//    }
//
//
//}


package com.example.birdguan.cownet;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.birdguan.cownet.utils.AppUpdateUtils;
import com.example.birdguan.cownet.utils.MessageBox;
import com.example.birdguan.cownet.utils.OkHttpManager;


import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LoginActivity extends Activity{
//    private UserLoginTask mAuthTask = null;
    private String userPhoneNumber;
    private String userID;
    private String userRoleID;
    private String userName;
    private String userAddress;

    // UI references.
    private EditText mUserNoView;
    private EditText mPasswordView;
    private CheckBox mRememberView;

    private ProgressDialog mProgressDialog;
    Boolean bSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setCustomActionBar();

        mProgressDialog = new ProgressDialog(LoginActivity.this);

        mUserNoView = (EditText) findViewById(R.id.userno);
        mPasswordView = (EditText) findViewById(R.id.password);
        mRememberView = (CheckBox)findViewById(R.id.rememberMe);

        SharedPreferences lognInfo = getSharedPreferences("login",MODE_PRIVATE);
        Boolean rememberMe = lognInfo.getBoolean("rememberme",false);
        mRememberView.setChecked(rememberMe);
        if(rememberMe) {
            mUserNoView.setText(lognInfo.getString("user", ""));
            mPasswordView.setText(lognInfo.getString("passwd", ""));
        }

        final Button signInButton = (Button) findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        ((TextView) findViewById(R.id.appversion)).setText("系统版本 V" + AppUpdateUtils.getVerName(this));

        RequestPermissions();

        AppUpdateUtils.checkVersion(this, false);

    }


    @TargetApi(23)
    private void RequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, 1);

        }
    }

    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        mUserNoView.setError(null);
        mPasswordView.setError(null);

        final String userno = mUserNoView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final Boolean rememberMe = mRememberView.isChecked();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userno)) {
            mUserNoView.setError(getString(R.string.error_invalid_userno));
            focusView = mUserNoView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
//            mAuthTask = new UserLoginTask(userno, password,mRememberView.isChecked());
//            mAuthTask.execute((Void) null);
            String url_Login = "http://106.15.53.134:60006/LivestockSystem2018_APP/appUser/appLogin";
            Map<String, String> map_login = new HashMap<>();
            map_login.put("USERNAME", userno);
            map_login.put("PASSWORD", password);
            OkHttpManager.postAsync(url_Login, map_login, new OkHttpManager.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {

                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(LoginActivity.this);
                    normalDialog.setMessage("网络错误");
                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //消息提示用，无操作
                        }
                    });
                    normalDialog.show();
                    Log.d("Debug", "获取登录信息连接失败");
                    showProgress(false);
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    Log.d("Debug", result);
                    JSONObject jsonObject_login = new JSONObject(result);
                    if(jsonObject_login.getString("message").equals("success")){
                        Log.d("Debug", "获取登陆信息成功");
                        userPhoneNumber = jsonObject_login.getString("PHONE");
                        userID = jsonObject_login.getString("USER_ID");
                        userName = jsonObject_login.getString("NAME");
                        userRoleID = jsonObject_login.getString("ROLE_ID");
                        SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user", userno);
                        editor.putString("passwd", password);
                        editor.putBoolean("rememberme", rememberMe);
                        editor.putString("userID", userID);
                        editor.putString("roleID", userRoleID);
                        editor.apply();
                        showProgress(false);
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        intent.putExtra("userID", userID);
                        intent.putExtra("roleID", userRoleID);
                        startActivity(intent);
                    }else {
                        AlertDialog.Builder normalDialog = new AlertDialog.Builder(LoginActivity.this);
                        normalDialog.setMessage("用户名或密码错误");
                        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //消息提示用，无操作
                            }
                        });
                        normalDialog.show();
                        showProgress(false);

                    }
                }
            });
        }
    }

    private void showProgress(final boolean show) {
        if (show) {
            mProgressDialog.setTitle("请稍候");
            mProgressDialog.setMessage("系统登录中...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        android.app.ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#448936")));
    }

    private void setCustomActionBar(){
        android.app.ActionBar.LayoutParams layoutParams = new android.app.ActionBar.LayoutParams(android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.custom_actionbar, null);
        TextView textView = mActionBarView.findViewById(R.id.textView_title);
        textView.setText("登录");
        android.app.ActionBar actionBar = getActionBar();
        actionBar.setCustomView(mActionBarView, layoutParams);
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        ImageView imageView = mActionBarView.findViewById(R.id.imageView_back);
        imageView.setVisibility(View.INVISIBLE);
    }

//    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mUserNo;
//        private final String mPassword;
//        private final boolean mRememberMe;
//
//        private String mErrorInfo;
//
//        UserLoginTask(String userno, String password,boolean rememberMe) {
//            mUserNo = userno;
//            mPassword = password;
//            mRememberMe = rememberMe;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            try {
//                String url_Login = "http://106.15.53.134:60006/LivestockSystem2018_APP/appUser/appLogin";
//                Map<String, String> map_login = new HashMap<>();
//                map_login.put("USERNAME", mUserNo);
//                map_login.put("PASSWORD", mPassword);
//                OkHttpManager.postAsync(url_Login, map_login, new OkHttpManager.DataCallBack() {
//                    @Override
//                    public void requestFailure(Request request, IOException e) {
//                        Log.d("Debug", "获取登录信息失败");
//                    }
//
//                    @Override
//                    public void requestSuccess(String result) throws Exception {
//                        Log.d("Debug", result);
//                        JSONObject jsonObject_login = new JSONObject(result);
//                        if(jsonObject_login.getString("message").equals("success")){
//                            Log.d("Debug", "获取登陆信息成功");
//                            userPhoneNumber = jsonObject_login.getString("PHONE");
//                            userID = jsonObject_login.getString("USER_ID");
//                            userName = jsonObject_login.getString("NAME");
//                            userRoleID = jsonObject_login.getString("ROLE_ID");
//                            bSuccess = true;
//                        }else {
//                            bSuccess = false;
//
//                        }
//                    }
//                });
//
//                //测试
////                if (mUserNoView.getText().toString().equals("admin") && mPasswordView.getText().toString().equals("admin")){
////                    bSuccess = true;
////                }
//
//            } catch (Exception e) {
//                Log.d("Debug", e.toString());
//            }
//
//            return bSuccess;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            Log.d("Debug", "OnPost: " + success.toString());
//            mAuthTask = null;
//            showProgress(false);
//            if (success) {
//                finish();
//                SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("user", mUserNo);
//                editor.putString("passwd", mPassword);
//                editor.putBoolean("rememberme", mRememberMe);
//                editor.putString("userID", userID);
//                editor.putString("roleID", userRoleID);
//                editor.apply();
//                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
//                intent.putExtra("userID", userID);
//                intent.putExtra("roleID", userRoleID);
//                startActivity(intent);
//            } else {
////                MessageBox.Show(LoginActivity.this, mErrorInfo);
//                AlertDialog.Builder normalDialog = new AlertDialog.Builder(LoginActivity.this);
//                normalDialog.setMessage("用户名或密码错误");
//                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //消息提示用，无操作
//                    }
//                });
//                normalDialog.show();
////                Toast.makeText(getApplicationContext(),mErrorInfo,Toast.LENGTH_SHORT).show();
//                mPasswordView.requestFocus();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTask = null;
//            showProgress(false);
//        }
//    }
}

