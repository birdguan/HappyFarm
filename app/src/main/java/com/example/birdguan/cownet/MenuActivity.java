package com.example.birdguan.cownet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.example.birdguan.cownet.utils.OkHttpManager;
import com.squareup.picasso.Picasso;
import com.vector.update_app.UpdateAppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class MenuActivity extends Activity{
    private Boolean hasMsg;
    public static Boolean hasRead = false;
    private TextView textView_userName;
    private TextView textView_userPhoneNumber;
    private TextView textView_userGender;
    private TextView textView_userAddress;
    private List<String> userTerminalNumber = new ArrayList<String>();
    private List<String> estrusTerminal = new ArrayList<String>();
    private List<Integer> estrusSlightCount = new ArrayList<Integer>();
    private List<Integer> estrusMiddleCount = new ArrayList<Integer>();
    private List<Integer> estrusStrenuousCount = new ArrayList<Integer>();
    private List<String> feedTerminal = new ArrayList<String>();
    private List<String> feedEatCount = new ArrayList<String>();



    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setCustomActionBar();
        LinearLayout linearLayout_baseInfo = findViewById(R.id.linearLayout_baseinfo);
        LinearLayout linearLayout_feedInfo = findViewById(R.id.linearLayout_feedInfo);
        LinearLayout linearLayout_vitalSigns = findViewById(R.id.linearLayout_vitalSigns);
        LinearLayout linearLayout_estrusWarning = findViewById(R.id.linearLayout_estrusWarning);
        LinearLayout linearLayout_msgManager = findViewById(R.id.linearLayout_msgmanager);
        textView_userName = findViewById(R.id.textView_userName);
        textView_userPhoneNumber = findViewById(R.id.textView_userPhoneNumber);
        textView_userGender = findViewById(R.id.textView_userGender);
        textView_userAddress = findViewById(R.id.textView_userAddress);
        final ImageView imageView = findViewById(R.id.imageView_menuusericon);
        final ImageView imageView_msgManager = findViewById(R.id.imageView_menu5);


        //获取该用户下所有的终端号
        estrusTerminal.clear();
        //获取存储的用户ID
        SharedPreferences sharedPreferences_oestus = getSharedPreferences("login", MODE_PRIVATE);
        String role_id = sharedPreferences_oestus.getString("roleID","");
        String user_id = sharedPreferences_oestus.getString("userID","");

        //获取该用户下的所有终端号
        //----------------------------------------------------------------------------------------------------------------------------------------------
        final String url_getTerminalNumber = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetTerminalNumberByUserID";
        final Map<String, String> map_getTerminalNumber = new HashMap<>();
        map_getTerminalNumber.put("USER_ID", user_id);
        map_getTerminalNumber.put("ROLE_ID", role_id);
        OkHttpManager.postAsync(url_getTerminalNumber, map_getTerminalNumber, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("Debug", "获取该用户下终端连接失败");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("Debug", "接口返回数据： " + result);
                try{
                    JSONObject jsonObject_getTerminalBumber = new JSONObject(result);
                    if (jsonObject_getTerminalBumber.getString("message").equals("01")){
                        JSONArray jsonArray_inner = jsonObject_getTerminalBumber.getJSONArray("terminal_list");
                        userTerminalNumber.clear();
                        for (int i = 0; i < jsonArray_inner.length(); i++){
                            Log.d("Debug", jsonArray_inner.getString(i));
                            userTerminalNumber.add(jsonArray_inner.getString(i));
                        }

                    }
                }catch (Exception e){
                    Log.d("Debug", e.toString());
                }



                //根据每个终端号去查询发情数据
                estrusTerminal.clear();
                estrusSlightCount.clear();
                estrusMiddleCount.clear();
                estrusStrenuousCount.clear();
                for (int i = 0; i < userTerminalNumber.size(); i++) {

                    String url_queryEstrus = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetGpsSensorData";
                    Map<String, String> map_queryEstrusByTerminal = new HashMap<>();
                    map_queryEstrusByTerminal.put("sensorType", "FQ2");
                    map_queryEstrusByTerminal.put("terminal_number", userTerminalNumber.get(i));
                    map_queryEstrusByTerminal.put("numbers", "1");
                    OkHttpManager.postAsync(url_queryEstrus, map_queryEstrusByTerminal, new OkHttpManager.DataCallBack() {
                        @Override
                        public void requestFailure(Request request, IOException e) {
                            Log.d("Debug", "获取特定终端的发情数据连接失败");
                        }

                        @Override
                        public void requestSuccess(String result) throws Exception {
                            Log.d("Debug", "获取特定终端发情数据结果：" + result);
                            try {
                                JSONObject jsonObject_queryEstrusBuTerminal = new JSONObject(result);
                                JSONArray jsonArray_inner = jsonObject_queryEstrusBuTerminal.getJSONArray("estrus");
                                JSONObject jsonObject_inner = jsonArray_inner.getJSONObject(0);
                                estrusTerminal.add(jsonObject_inner.getString("terminal_number"));
                                estrusSlightCount.add(jsonObject_inner.getInt("samples_number"));
                                estrusMiddleCount.add(jsonObject_inner.getInt("exercises_number"));
                                estrusStrenuousCount.add(jsonObject_inner.getInt("average_value"));
                            }catch (Exception e){
                                Log.d("Debug", "获取特定终端发情数据时：" + e.toString());
                            }
                            Log.d("Debug", "该用户下有发情数据终端号：" + estrusTerminal);
                            Log.d("Debug", "该用户下发情数据中度运动数据：" + estrusMiddleCount);


                        }
                    });
                }


                //根据每个终端号查进食数据
                feedTerminal.clear();
                feedEatCount.clear();
                for (int i = 0; i < userTerminalNumber.size(); i++) {
                    String url_queryFeedInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetGpsSensorData";
                    Map<String, String> map_queryEstrusByTerminal = new HashMap<>();
                    map_queryEstrusByTerminal.put("sensorType", "JS2");
                    map_queryEstrusByTerminal.put("terminal_number", userTerminalNumber.get(i));
                    map_queryEstrusByTerminal.put("numbers", "1");
                    OkHttpManager.postAsync(url_queryFeedInfo, map_queryEstrusByTerminal, new OkHttpManager.DataCallBack() {
                        @Override
                        public void requestFailure(Request request, IOException e) {
                            Log.d("Debug", "获取特定终端的进食数据连接失败");
                        }

                        @Override
                        public void requestSuccess(String result) throws Exception {
                            Log.d("Debug","获取特定终端进食数据结果: " + result);
                            try {
                                JSONObject jsonObject_queryEstrusBuTerminal = new JSONObject(result);
                                JSONArray jsonArray_inner = jsonObject_queryEstrusBuTerminal.getJSONArray("estrus");
                                JSONObject jsonObject_inner = jsonArray_inner.getJSONObject(0);
                                feedTerminal.add(jsonObject_inner.getString("terminal_number"));
                                feedEatCount.add(jsonObject_inner.getString("eat_count"));
                            }catch (Exception e){
                                Log.d("Debug", "获取特定终端进食数据时：" + e.toString());
                            }

                        }
                    });
                }

            }
        });
        //-----------------------------------------------------------------------------------------------------------------------------------



        //查询并更新用户头像、信息
        final SharedPreferences loginInfo = getSharedPreferences("login",MODE_PRIVATE);
        String url_queryUserInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appShepherd/appGetShepherdInfo";
        Map<String, String> map_queryUserInfo = new HashMap<>();
        map_queryUserInfo.put("USER_ID", user_id);
        map_queryUserInfo.put("ROLE_ID", role_id);
        OkHttpManager.postAsync(url_queryUserInfo, map_queryUserInfo, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("Debug", "获取用户信息连接失败");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("Debug", "result: " + result);
                try {
                    JSONObject jsonObject_queryUserInfo = new JSONObject(result);
                    if (jsonObject_queryUserInfo.getString("message").equals("01")) {
                        Log.d("Debug", "获取用户信息成功");
                        JSONArray jsonArray_inner = jsonObject_queryUserInfo.getJSONArray("shepherdList");
                        JSONObject jsonObject_inner = jsonArray_inner.getJSONObject(0);
                        String userName = "";
                        if (jsonObject_inner.has("NAME")){
                            userName = jsonObject_inner.getString("NAME");
                        }
                        Log.d("Debug", "用户姓名：" + userName);
                        String userPhoneNumber = "";
                        if (jsonObject_inner.has("PHONE")) {
                            userPhoneNumber = jsonObject_inner.getString("PHONE");
                        }
                        String userGender = "";
                        if (jsonObject_inner.has("GENDER")) {
                            userGender = jsonObject_inner.getString("GENDER");
                        }
                        String userAddress = "";
                        if (jsonObject_inner.has("ADDRESS")) {
                            userAddress = jsonObject_inner.getString("ADDRESS");
                        }
                        String userPhoto = "";
                        if (jsonObject_inner.has("PHOTO")) {
                            userPhoto = jsonObject_inner.getString("PHOTO");
                        }
                        String userPhotoUrl = "http://106.15.53.134:60006/LivestockSystem2018_APP/uploadFiles/uploadImgs/" + userPhoto;
                        Log.d("Debug", "用户头像地址: " + userPhotoUrl);
                        SharedPreferences.Editor editor = loginInfo.edit();
                        editor.putString("userIconUrl", userPhotoUrl);
                        editor.apply();
                        Picasso.with(getApplicationContext()).load(userPhotoUrl).into(imageView);
                        textView_userName.setText(userName);
                        textView_userPhoneNumber.setText(userPhoneNumber);
                        textView_userGender.setText(userGender);
                        textView_userAddress.setText(userAddress);
                    }
                }catch (Exception e){
                    Log.d("Debug", e.toString());
                }

            }
        });


        //查询是否有新消息,同时更新图标
        //测试
        hasMsg = false;

        String url_infoManager = "http://106.15.53.134:60006/LivestockSystem2018_APP/appGetNews/appGetNews";
        Map<String, String> map_infoManager = new HashMap<>();
        map_infoManager.put("USER_ID", user_id);
        map_infoManager.put("ROLE_ID", role_id);
        OkHttpManager.postAsync(url_infoManager, map_infoManager, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("Debug", "获取消息连接失败");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.d("Debug", "获取消息结果：" + result);
                try {
                    Log.d("Debug", result);
                    JSONObject jsonObject_info = new JSONObject(result);
                    if (jsonObject_info.getString("message").equals("01")){
                        //先获取消息
                        List<String> list_theme = new ArrayList<String>();
                        List<String> list_info = new ArrayList<String>();
                        List<String> list_name = new ArrayList<String>();
                        List<String> list_data = new ArrayList<String>();
                        List<String> list_theme_old = new ArrayList<String>();
                        List<String> list_info_old = new ArrayList<String>();
                        List<String> list_name_old = new ArrayList<String>();
                        List<String> list_data_old = new ArrayList<String>();
                        List<String> list_theme_all = new ArrayList<String>();
                        List<String> list_info_all = new ArrayList<String>();
                        List<String> list_name_all = new ArrayList<String>();
                        List<String> list_data_all = new ArrayList<String>();

                        //取出所有的消息
                        list_theme_all.clear();
                        list_info_all.clear();
                        list_name_all.clear();
                        list_data_all.clear();
                        SharedPreferences sharedPreferences_infoManager = getSharedPreferences("MSG", MODE_PRIVATE);
                        SharedPreferences.Editor editor= sharedPreferences_infoManager.edit();
                        int size_all = sharedPreferences_infoManager.getInt("sizeAll", 0);
                        Log.d("Debug", "所有信息共记" + size_all + " 条");
                        for (int i = 0; i < size_all; i++){
                            list_theme_all.add(sharedPreferences_infoManager.getString("themeAll_" + i, null));
                            list_info_all.add(sharedPreferences_infoManager.getString("infoAll_" + i, null));
                            list_name_all.add(sharedPreferences_infoManager.getString("nameAll_" + i, null));
                            list_data_all.add(sharedPreferences_infoManager.getString("dataAll_" + i, null));
                        }
                        Log.d("Debug", "取出的所有消息：" + list_data_all);



                        JSONArray jsonArray_inner = jsonObject_info.getJSONArray("newsList");
                        for (int i = 0; i < jsonArray_inner.length(); i++){
                            JSONObject jsonObject_inner = jsonArray_inner.getJSONObject(i);
                            list_theme.add(jsonObject_inner.getString("THEME"));
                            list_info.add(jsonObject_inner.getString("INFO"));
                            list_name.add(jsonObject_inner.getString("NAME"));
                            list_data.add(jsonObject_inner.getString("CREATE_TIME"));
                        }

                        //取出上次接收的消息
                        int size = sharedPreferences_infoManager.getInt("size", 0);
                        for (int i = 0; i < size; i++){
                            list_theme_old.add(sharedPreferences_infoManager.getString("theme_" + i, null));
                            list_info_old.add(sharedPreferences_infoManager.getString("info_" + i, null));
                            list_name_old.add(sharedPreferences_infoManager.getString("name_" + i, null));
                            list_data_old.add(sharedPreferences_infoManager.getString("data_" + i, null));
                        }
                        Log.d("Debug", "上次接收的消息: " + list_info_old);

                        //接收的消息和上次接收消息不一样，说明有新消息
                        for(int i = 0; i < list_info.size(); i++){
                            Boolean flag = true;
                            for (int j = 0; j < list_info_old.size(); j++){
                                if (list_info.get(i).equals(list_info_old.get(j))){
                                    flag = false;
                                }
                            }
                            if (flag){
                                size_all++;
                                imageView_msgManager.setImageResource(R.drawable.icon_infomanager_hasmsg);
                                list_theme_all.add(list_theme.get(i));
                                list_info_all.add(list_info.get(i));
                                list_name_all.add(list_name.get(i));
                                list_data_all.add(list_data.get(i));
                            }
                        }

                        //本次取出的消息保存为上次接收的消息
                        int size_old = list_theme.size();
                        editor.putInt("size", size_old);
                        for (int i = 0; i < size_old; i++){
                            editor.remove("theme_" + i);
                            editor.remove("info_" + i);
                            editor.remove("name_" + i);
                            editor.remove("data_" + i);
                            editor.putString("theme_" + i, list_theme.get(i));
                            editor.putString("info_" + i, list_info.get(i));
                            editor.putString("name_" + i, list_name.get(i));
                            editor.putString("data_" + i, list_data.get(i));

                        }
                        editor.apply();
                        //保存所有消息

                        editor.putInt("sizeAll", size_all);
                        for (int i = 0; i < size_all; i++){
                            editor.remove("themeAll_" + i);
                            editor.remove("infoAll_" + i);
                            editor.remove("nameAll_" + i);
                            editor.remove("dataAll_" + i);
                            editor.putString("themeAll_" + i, list_theme_all.get(i));
                            editor.putString("infoAll_" + i, list_info_all.get(i));
                            editor.putString("nameAll_" + i, list_name_all.get(i));
                            editor.putString("dataAll_" + i, list_data_all.get(i));

                        }
                        editor.apply();


                    }
                }catch (Exception e){
                    Log.d("Debuf", e.toString());
                }
            }
        });
        Log.d("Debug", hasRead.toString());

        if (hasMsg && !hasRead){
            imageView_msgManager.setImageResource(R.drawable.icon_infomanager_hasmsg);
        }else {
            imageView_msgManager.setImageResource(R.drawable.icon_infomanager);
        }



        //修改用户信息
        Button button_modifyUserInfo = findViewById(R.id.button_modifyUserInfo);
        button_modifyUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_modifyUserInfo = new Intent(MenuActivity.this, ModifyUserIfoActivity.class);
                intent_modifyUserInfo.putExtra("name", textView_userName.getText().toString());
                intent_modifyUserInfo.putExtra("gender", textView_userGender.getText().toString());
                intent_modifyUserInfo.putExtra("phoneNumber", textView_userPhoneNumber.getText().toString());
                intent_modifyUserInfo.putExtra("address", textView_userAddress.getText().toString());
                startActivity(intent_modifyUserInfo);
            }
        });
        //基本信息
        linearLayout_baseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_baseInfo = new Intent(MenuActivity.this, NewDetailedInfoActivity.class);
                startActivity(intent_baseInfo);
            }
        });

        //进食信息
        linearLayout_feedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_feedInfo = new Intent(MenuActivity.this, FeedInfoOverviewActivity.class);
                intent_feedInfo.putStringArrayListExtra("feedTerminal", (ArrayList<String>) feedTerminal);
                intent_feedInfo.putStringArrayListExtra("feedEatCount", (ArrayList<String>) feedEatCount);
                startActivity(intent_feedInfo);
            }
        });

        //生命体征
        linearLayout_vitalSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_vitalSigns = new Intent(MenuActivity.this, VitalSignsActivity.class);
                startActivity(intent_vitalSigns);
            }
        });

        //发情预警
        linearLayout_estrusWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_esdtrusWarning = new Intent(MenuActivity.this, EstrusActivity.class);
                intent_esdtrusWarning.putStringArrayListExtra("estrusTerminal", (ArrayList<String>) estrusTerminal);
                intent_esdtrusWarning.putIntegerArrayListExtra("estrusSlightCount", (ArrayList<Integer>) estrusSlightCount);
                intent_esdtrusWarning.putIntegerArrayListExtra("estrusMiddleCount", (ArrayList<Integer>) estrusMiddleCount);
                intent_esdtrusWarning.putIntegerArrayListExtra("estrusStrenuousCount", (ArrayList<Integer>) estrusStrenuousCount);
                Log.d("Debug", "发情数据传递的终端号：" + estrusTerminal);
                Log.d("Debug", "发情数据传递的中度运动数据：" + estrusMiddleCount);
                startActivity(intent_esdtrusWarning);
            }
        });

        //消息管理
        linearLayout_msgManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_msgManager.setImageResource(R.drawable.icon_infomanager);
                Intent intent_msgManager = new Intent(MenuActivity.this, MsgManagerActivity.class);
                startActivity(intent_msgManager);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    //自动更新
//    String url_update = "";
//    UpdateAppManager updateAppManager = new UpdateAppManager.Builder().setActivity(this).setUpdateUrl(url_update).setHttpManager(new ).build().update();

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
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setCustomActionBar(){
        android.app.ActionBar.LayoutParams layoutParams = new android.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.custom_actionbar, null);
        TextView textView = mActionBarView.findViewById(R.id.textView_title);
        textView.setText("目录");
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
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
