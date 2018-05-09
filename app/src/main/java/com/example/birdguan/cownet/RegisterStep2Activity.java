package com.example.birdguan.cownet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LongDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.birdguan.cownet.utils.OkHttpManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class RegisterStep2Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    String[] dictionaries;
    List<String> dictionaryID = new ArrayList<String>();
    List<String> dictionaryName = new ArrayList<String>();
    List<String> list_dictionariesData = new ArrayList<String>();
    Spinner spinner_province;
    Spinner spinner_city;
    Spinner spinner_county;
    Spinner spinner_town;
    Spinner spinner_village;
    TextView textView_spinner;
    String ID;
    private int count = 0;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_step2);
        spinner_province = findViewById(R.id.spinner_province);
        spinner_city = findViewById(R.id.spinner_city);
        spinner_county = findViewById(R.id.spinner_county);
        spinner_town = findViewById(R.id.spinner_town);
        spinner_village = findViewById(R.id.spinner_village);
        textView_spinner = findViewById(R.id.textView_spinner);

        //第一次，获取省级ID
        Log.d("Debug","获取省级ID");
        getDictionaries("1");
        //spinner数据及布局
        ArrayAdapter<String> adapter_province = new ArrayAdapter<String>(getApplicationContext(), R.layout.example_spinner, dictionaryName);
        adapter_province.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner_province.setAdapter(adapter_province);
        spinner_province.setOnItemSelectedListener(this);
        count = 1;

        Button button_register = findViewById(R.id.register_button);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_register = "http://106.15.53.134:60008/LivestockSystem2018_APP/appUser/registerAppUser";
                SharedPreferences sharedPreferences_newUserData = getSharedPreferences("register", MODE_PRIVATE);
                Map<String, String> map_register = new HashMap<>();
                map_register.put("USERNAME", sharedPreferences_newUserData.getString("newUserPhoneNumber",null));
                map_register.put("PASSWORD", sharedPreferences_newUserData.getString("newUserPwd",null));
                map_register.put("NAME",sharedPreferences_newUserData.getString("newUserName",null));
                map_register.put("ROLE_ID", sharedPreferences_newUserData.getString("newUserRole", null));
                map_register.put("AREA_ID", sharedPreferences_newUserData.getString("dictionaryID", null));
                OkHttpManager.postAsync(url_register, map_register, new OkHttpManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        Log.d("Debug", "注册连接失败");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        try {
                            JSONObject jsonObject_register = new JSONObject(result);
                            if(jsonObject_register.getString("message").equals("01")){
                                Log.d("Debug", "注册成功");

                                //弹出提示注册成功消息框，点击确认返回登陆界面
                                Dialog dialog = new AlertDialog.Builder(RegisterStep2Activity.this)
                                        .setMessage("注册成功")
                                        .setCancelable(false)
                                        .setPositiveButton("返回登录", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent_toLogin = new Intent(RegisterStep2Activity.this, LoginActivity.class);
                                                startActivity(intent_toLogin);
                                            }
                                        }).create();
                                dialog.show();

                            }
                        }catch (Exception e){
                            Log.d("Debug", "解析失败");
                        }
                    }
                });
            }
        });
    }

    private void getDictionaries(String ID){
        Log.d("Debug", "获取数据");
        String  url_dictionaries = "http://106.15.53.134:60008/LivestockSystem2018_APP/appUser/backProvince";
        Map<String, String> map_register = new HashMap<>();
        map_register.put("PARENT_ID", ID);
        OkHttpManager.postAsync(url_dictionaries, map_register, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("Debug","获取行政区域连接失败");
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    JSONObject jsonObject_province = new JSONObject(result);
                    if (jsonObject_province.getString("message").equals("01")) {
                        JSONArray jsonArray_province = jsonObject_province.getJSONArray("dictionariesList");
                        dictionaryName.clear();
                        dictionaryID.clear();
                        dictionaryID.add("");
                        dictionaryName.add("");
                        for (int i = 0; i < jsonArray_province.length();i++){
                            Log.d("Debug",jsonArray_province.getJSONObject(i).getString("DICTIONARIES_ID"));
                            dictionaryID.add(jsonArray_province.getJSONObject(i).getString("DICTIONARIES_ID"));
                            Log.d("Debug",jsonArray_province.getJSONObject(i).getString("NAME"));
                            dictionaryName.add(jsonArray_province.getJSONObject(i).getString("NAME"));
                        }
                        Log.d("Debug", "数据获取成功");
                    }else{
                        Toast.makeText(RegisterStep2Activity.this, "网络错误", Toast.LENGTH_SHORT);
                    }
                }catch (Exception e){
                    Toast.makeText(RegisterStep2Activity.this, "解析失败", Toast.LENGTH_SHORT);
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        switch (parent.getId()){
//            case R.id.spinner_province:
//                //第二次，获取市级ID
//                Log.d("Debug","获取市级ID");
//                ID = dictionaryID.get(dictionaryName.indexOf(textView_spinner.getText().toString()));
//                getDictionaries(ID);
//                //spinner数据及布局
//                ArrayAdapter<String> adapter_city = new ArrayAdapter<String>(this, R.layout.example_spinner, list_dictionariesData);
//                adapter_city.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//                spinner_city.setAdapter(adapter_city);
//                spinner_city.setOnItemSelectedListener(this);
//                spinner_province.setEnabled(false);
//                break;
//            case R.id.spinner_city:
//                //第三次，获取县级ID
//                ID = dictionaryID.get(dictionaryName.indexOf(textView_spinner.getText().toString()));
//                getDictionaries(ID);
//                //spinner数据及布局
//                ArrayAdapter<String> adapter_county = new ArrayAdapter<String>(this, R.layout.example_spinner, list_dictionariesData);
//                adapter_county.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//                spinner_county.setAdapter(adapter_county);
//                spinner_county.setOnItemSelectedListener(this);
//                spinner_city.setEnabled(false);
//                break;
//            case R.id.spinner_county:
//                //第四次，获取镇级ID
//                ID = dictionaryID.get(dictionaryName.indexOf(textView_spinner.getText().toString()));
//                getDictionaries(ID);
//                //spinner数据及布局
//                ArrayAdapter<String> adapter_town = new ArrayAdapter<String>(this, R.layout.example_spinner, list_dictionariesData);
//                adapter_town.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//                spinner_town.setAdapter(adapter_town);
//                spinner_town.setOnItemSelectedListener(this);
//                spinner_county.setEnabled(false);
//                break;
//            case R.id.spinner_town:
//                //第五次，获取村级ID
//                ID = dictionaryID.get(dictionaryName.indexOf(textView_spinner.getText().toString()));
//                getDictionaries(ID);
//                //spinner数据及布局
//                ArrayAdapter<String> adapter_village = new ArrayAdapter<String>(this, R.layout.example_spinner, list_dictionariesData);
//                adapter_village.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//                spinner_village.setAdapter(adapter_village);
//                spinner_village.setOnItemSelectedListener(this);
//        }
//        switch (count){
//            case 1:
//                //第二次，获取市级ID
//                Log.d("Debug","获取市级ID");
//                ID = dictionaryID.get(dictionaryName.indexOf(textView_spinner.getText().toString()));
//                getDictionaries(ID);
//                //spinner数据及布局
//                ArrayAdapter<String> adapter_city = new ArrayAdapter<String>(this, R.layout.example_spinner, list_dictionariesData);
//                adapter_city.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//                spinner_city.setAdapter(adapter_city);
//                spinner_city.setOnItemSelectedListener(this);
//                spinner_province.setEnabled(false);
//                count = 2;
//                break;
//            case 2:
//                //第三次，获取县级ID
//                ID = dictionaryID.get(dictionaryName.indexOf(textView_spinner.getText().toString()));
//                getDictionaries(ID);
//                //spinner数据及布局
//                ArrayAdapter<String> adapter_county = new ArrayAdapter<String>(this, R.layout.example_spinner, list_dictionariesData);
//                adapter_county.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//                spinner_county.setAdapter(adapter_county);
//                spinner_county.setOnItemSelectedListener(this);
//                spinner_city.setEnabled(false);
//                count = 3;
//                break;
//            case 3:
//                //第四次，获取镇级ID
//                ID = dictionaryID.get(dictionaryName.indexOf(textView_spinner.getText().toString()));
//                getDictionaries(ID);
//                //spinner数据及布局
//                ArrayAdapter<String> adapter_town = new ArrayAdapter<String>(this, R.layout.example_spinner, list_dictionariesData);
//                adapter_town.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//                spinner_town.setAdapter(adapter_town);
//                spinner_town.setOnItemSelectedListener(this);
//                spinner_county.setEnabled(false);
//                count = 4;
//                break;
//            case 4:
//                //第五次，获取村级ID
//                ID = dictionaryID.get(dictionaryName.indexOf(textView_spinner.getText().toString()));
//                getDictionaries(ID);
//                //spinner数据及布局
//                ArrayAdapter<String> adapter_village = new ArrayAdapter<String>(this, R.layout.example_spinner, list_dictionariesData);
//                adapter_village.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
//                spinner_village.setAdapter(adapter_village);
//                spinner_village.setOnItemSelectedListener(this);
//                spinner_town.setEnabled(false);
//                break;
//            default:
//                break;
//
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
