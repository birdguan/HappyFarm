package com.example.birdguan.cownet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.birdguan.cownet.utils.OkHttpManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class InfoOverviewActivity extends AppCompatActivity{
    String url_overViewInfo;
    List<String> list_overviewInfoTerminal = new ArrayList<String>();
    List<String> list_earmarkings = new ArrayList<String>();
    List<String> list_variety = new ArrayList<String>();
    List<String> list_monthOld = new ArrayList<String>();
    int dataNumber = 0;
    ListView listView_overviewInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_overview);

        //获取存储的用户ID和角色ID
        SharedPreferences sharedPreferences_queryFemaleInfo = getSharedPreferences("login",MODE_PRIVATE);
        String user_id = sharedPreferences_queryFemaleInfo.getString("user", "");
        String role_id = sharedPreferences_queryFemaleInfo.getString("role","");

        //根据不同的牛类别查询不同的URL
        Intent intent = getIntent();
        int cowType = intent.getIntExtra("cowType" , 0);
        switch (cowType){
            //母牛信息
            case 1:
               url_overViewInfo = "http://106.15.53.134:60008/LivestockSystem2018_APP/appCattleBasicCowInfo/appGetCattleBasicCowInfo";
                break;
            //育肥牛信息
            case 2:
                //url_beefInfo = "";
                break;
            //种公牛信息
            case 3:
                //url_maleInfo = "";
                break;
            //犊牛信息
            case 4:
                //url_calfInfo = "";
                break;
            default:
                break;

        }



        //测试数据
        String url_overViewInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appCattleBasicCowInfo/appGetCattleBasicCowInfo";
        user_id = "0223018f802c4b159a2b87311d366793";
        role_id = "牧主";

        //获取各类别牛的信息
        Map<String, String> map_overViewInfo = new HashMap<>();
        map_overViewInfo.put("USER_ID",user_id);
        map_overViewInfo.put("ROLE_ID",role_id);
        OkHttpManager.postAsync(url_overViewInfo, map_overViewInfo, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("Debug","获取信息连接失败");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                try{
                    Log.d("Debug",result);
                    JSONObject jsonObject_overviewInfo = new JSONObject(result);
                    if (jsonObject_overviewInfo.getString("message").equals("01")){
                        Log.d("Debug","获取信息数据成功");
                        JSONArray jsonArray_overviewInfo = jsonObject_overviewInfo.getJSONArray("cattleBasicCowInfoList");
                        list_overviewInfoTerminal.clear();
                        list_earmarkings.clear();
                        list_variety.clear();
                        list_monthOld.clear();
                        for (int i = 0; i<jsonArray_overviewInfo.length(); i++){
                            JSONObject jsonObject_inner = jsonArray_overviewInfo.getJSONObject(i);
                            String terminal = jsonObject_inner.getString("TERMINAL_NUMBER");
                            Log.d("Debug",terminal);
                            list_overviewInfoTerminal.add(terminal);
                            String ear_ranking = jsonObject_inner.getString("EAR_MARKINGS");
                            list_earmarkings.add(ear_ranking);
                            String variety = jsonObject_inner.getString("VARIETY");
                            list_variety.add(variety);
                            list_monthOld.add(jsonObject_inner.getString("MONTH_OLD"));
                        }
                        Log.d("Debug","解析成功");

                        //构建信息概览List的Adapter
                        Log.d("Debug","构建信息概览List的Adapter");
                        List<Map<String, Object>> list_overviewInfo = new ArrayList<Map<String,Object>>();
                        for(int i = 0; i<list_overviewInfoTerminal.size();i++){
                            Map<String,Object> showitem = new HashMap<String, Object>();
                            showitem.put("overviewInfoTermianl", list_overviewInfoTerminal.get(i));
                            Log.d("Debug","termial: " + list_overviewInfoTerminal.get(i));
                            showitem.put("overviewInfoEarMarking",list_earmarkings.get(i));
                            showitem.put("overviewInfoVariety",list_variety.get(i));
                            showitem.put("overviewInfoMonthOld",list_monthOld.get(i));
                            list_overviewInfo.add(showitem);
                        }
                        SimpleAdapter simpleAdapter_overviewInfo = new SimpleAdapter(getApplicationContext(), list_overviewInfo,R.layout.custom_infolist,
                                new String[]{"overviewInfoTermianl","overviewInfoEarMarking","overviewInfoVariety","overviewInfoMonthOld"},
                                new int[]{R.id.textView_overviewTerminal, R.id.textView_overViewEarMarking, R.id.textView_overview_variety,R.id.textView_overviewmonthold});
                        listView_overviewInfo = findViewById(R.id.infoList);
                        listView_overviewInfo.setAdapter(simpleAdapter_overviewInfo);
                        listView_overviewInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(InfoOverviewActivity.this, DetailedInfoActivity.class);
                                TextView textView = view.findViewById(R.id.textView_overviewTerminal);
                                String terminal = textView.getText().toString();
                                Log.d("Debug", terminal);
                                intent.putExtra("terminaltoQuery", terminal);
                                intent.putExtra("flag", "Query");
                                startActivity(intent);
                            }
                        });


                    }else {
                        Log.d("Debug","获取信息失败");
                    }
                }catch (JSONException e){
                    Log.d("Debug",e.toString());
                }
            }
        });



        //查询按钮
        Button button_query = findViewById(R.id.button_query);
        button_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText_terminaltoQuery = findViewById(R.id.editText_terminaltoQuery);
                if (!editText_terminaltoQuery.getText().toString().equals("")) {
                    Intent intent_detailedInfo = new Intent(InfoOverviewActivity.this, DetailedInfoActivity.class);
                    intent_detailedInfo.putExtra("terminaltoQuery",editText_terminaltoQuery.getText().toString());
                    intent_detailedInfo.putExtra("flag", "Query");
                    startActivity(intent_detailedInfo);
                }
            }
        });

//        //“新增”按钮
//        Button button_addInfo = findViewById(R.id.button_addInfo);
//        button_addInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent_addInfo = new Intent(InfoOverviewActivity.this, DetailedInfoActivity.class);
//                intent_addInfo.putExtra("flag", "Add");
//                startActivity(intent_addInfo);
//            }
//        });
    }
//    //列表点击跳转
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(InfoOverviewActivity.this, DetailedInfoActivity.class);
//        TextView textView = view.findViewById(R.id.textView_overviewTerminal);
//        String terminal = textView.getText().toString();
//        Log.d("Debug", terminal);
//        intent.putExtra("terminaltoQuery", terminal);
//        startActivity(intent);
//
//    }



}
