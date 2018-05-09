package com.example.birdguan.cownet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mob.MobSDK;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterStep1Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private String newUserRole;
    private String newUserPhoneNumber;
    private String newUserName;
    private String newUserPwd;
    private String newUserConfirmPwd;
    private List<String> newUserRoleData = new ArrayList<String>();
    private boolean isCodeRight;

    EventHandler smsHandler;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_step1);

        MobSDK.init(this,"25316ad1c4333", "7a1780cc6ac1f145eac1ada981245048");
        newUserRoleData.add(new String("牧主"));
        newUserRoleData.add(new String("兽医"));
        newUserRoleData.add(new String("农牧"));



        final Spinner spinner_newUserRole = findViewById(R.id.spinner_newUserRole);
        final EditText editText_newUserPhoneNumber = findViewById(R.id.editText_newUserPhoneNumber);
        final EditText editText_newUserCode = findViewById(R.id.editText_newUserCode);
        final EditText editText_newUserName = findViewById(R.id.editText_newUserName);
        final EditText editText_newUserPwd = findViewById(R.id.editText_newUserPwd);
        final EditText editText_newUserConfirmedPwd = findViewById(R.id.editText_newUserConfirmedPwd);


        //spinner数据及布局
        ArrayAdapter<String> adapter_newUserRole = new ArrayAdapter<String>(this, R.layout.example_spinner, newUserRoleData);
        adapter_newUserRole.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner_newUserRole.setAdapter(adapter_newUserRole);
        spinner_newUserRole.setOnItemSelectedListener(this);


        //“获取验证码”按钮
        Button button_getNewUserCode = findViewById(R.id.button_getnewUserCode);
        button_getNewUserCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserPhoneNumber = editText_newUserPhoneNumber.getText().toString();
                if (newUserPhoneNumber.isEmpty()){
                    editText_newUserPhoneNumber.requestFocus();
                    editText_newUserPhoneNumber.setError("请输入手机号");
                }else {
                    SMSSDK.getVerificationCode("86", newUserPhoneNumber);
                    //弹窗提示用户接收验证码
//                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
//                    normalDialog.setMessage("请您等待接收验证码");
//                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //消息提示用，无操作
//                        }
//                    });
//                    normalDialog.show();
                }
            }
        });



        //“下一步”按键
        Button button_netStep = findViewById(R.id.button_register_nextstep);
        button_netStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean bSuccess;
                String newUserPhoneNumber = editText_newUserPhoneNumber.getText().toString();
                SharedPreferences sharedPreferences_newUserData = getSharedPreferences("register", MODE_PRIVATE);
                SharedPreferences.Editor editor_newUserData = sharedPreferences_newUserData.edit();

                //测试输入数据是否有空缺，两次密码是否一致
//                if (editText_newUserPhoneNumber.getText().toString().isEmpty()){
//                    editText_newUserPhoneNumber.requestFocus();
//                    editText_newUserPhoneNumber.setError("请输入手机号码");
//                    bSuccess = false;
//                }else if (editText_newUserPhoneNumber.getText().toString().length() != 11){
//                    editText_newUserPhoneNumber.requestFocus();
//                    editText_newUserPhoneNumber.setError("请输入11位手机号码");
//                } else {
//                    editor_newUserData.putString("newUserPhoneNumber", editText_newUserPhoneNumber.getText().toString());
//                    bSuccess = true;
//                }
//                if (editText_newUserCode.getText().toString().isEmpty()){
//                    editText_newUserCode.requestFocus();
//                    editText_newUserCode.setError("请输入验证码");
//                }
//                if (editText_newUserName.getText().toString().isEmpty()){
//                    editText_newUserName.requestFocus();
//                    editText_newUserName.setError("请输入姓名");
//                    bSuccess = false;
//                }else {
//                    editor_newUserData.putString("newUserName", editText_newUserName.getText().toString());
//                    bSuccess = true;
//                }
//                if (editText_newUserPwd.getText().toString().isEmpty()){
//                    editText_newUserPwd.requestFocus();
//                    editText_newUserPwd.setError("请输入密码");
//                    bSuccess = false;
//                }else {
//                    editor_newUserData.putString("newUserPwd", editText_newUserPwd.getText().toString());
//                    bSuccess = true;
//                }
//                if (editText_newUserConfirmedPwd.getText().toString().isEmpty()){
//                    editText_newUserConfirmedPwd.requestFocus();
//                    editText_newUserConfirmedPwd.setError("请确认密码");
//                    bSuccess = false;
//                }else {
//                    editor_newUserData.putString("newUserConfirmedPwd", editText_newUserConfirmedPwd.getText().toString());
//                    bSuccess = true;
//                }
//                if(! sharedPreferences_newUserData.getString("newUserPwd", "").equals(sharedPreferences_newUserData.getString("newUserConfirmedPwd", ""))){
//                    editText_newUserPwd.setText("");
//                    editText_newUserConfirmedPwd.setText("");
//                    editText_newUserPwd.findFocus();
//                    editText_newUserPwd.setError("两次输入的密码不一致");
//                    bSuccess = false;
//                }
//                SMSSDK.submitVerificationCode("86", newUserPhoneNumber, editText_newUserCode.getText().toString());
//                if (bSuccess && isCodeRight) {
//                    Intent intent_registerStep2 = new Intent(RegisterStep1Activity.this, RegisterStep2Activity.class);
//                    startActivity(intent_registerStep2);
//                }


                //无验证测试
                Intent intent_registerStep2 = new Intent(RegisterStep1Activity.this, RegisterStep2Activity.class);
                startActivity(intent_registerStep2);


            }
        });

        //验证短信验证码是否匹配
        smsHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.d("Debug", "smsHandler : " +  event + "," + result + "," + data.toString());
                result = SMSSDK.RESULT_COMPLETE;

                if (result == SMSSDK.RESULT_COMPLETE) {
                    switch (event) {
                        case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                            //获取验证码成功
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
                                    normalDialog.setMessage("验证码已发送");
                                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //消息提示用，无操作
                                        }
                                    });
                                    normalDialog.show();
                                }
                            });
                            break;
                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isCodeRight = true;
                                }
                            });
                            break;
                    }
                } else {
                    Throwable throwable = (Throwable) data;
                    Map<String, Object> map = JSON.parseObject(throwable.getMessage(), new TypeReference<Map<String, Object>>() {
                    });
                    int status = (Integer) map.get("status");
                    Log.d("Debug", "status is " + status);
                    switch (status) {
                        case 462:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
                                    normalDialog.setMessage("发送短信太频繁，请稍候再试！");
                                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //消息提示用，无操作
                                        }
                                    });
                                    normalDialog.show();
                                }
                            });
                            break;
                        case 467:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
                                    normalDialog.setMessage("五分钟内对APP发送的验证码校验超过三次！");
                                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //消息提示用，无操作
                                        }
                                    });
                                    normalDialog.show();
                                }
                            });
                            break;
                        case 468:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
                                    normalDialog.setMessage("不合法的验证码或者验证码已过期！");
                                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //消息提示用，无操作
                                        }
                                    });
                                    normalDialog.show();
                                }
                            });
                            break;
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(smsHandler);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner_newUserRole:
                TextView textView_newUserRole = findViewById(R.id.textView_spinner);
                SharedPreferences sharedPreferences_newUserData = getSharedPreferences("register", MODE_PRIVATE);
                SharedPreferences.Editor editor_newUserData = sharedPreferences_newUserData.edit();
                editor_newUserData.putString("newUserRole", textView_newUserRole.getText().toString());
                Log.d("Debug", "选择的角色：" + textView_newUserRole.getText().toString());
                Toast.makeText(RegisterStep1Activity.this,"您选择的是：" + textView_newUserRole.getText().toString(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
