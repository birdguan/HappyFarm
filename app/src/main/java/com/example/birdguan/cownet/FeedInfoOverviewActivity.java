package com.example.birdguan.cownet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.birdguan.cownet.utils.OkHttpManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class FeedInfoOverviewActivity extends Activity {
    private ListView listView_feedInfoOverview;
    private EditText editText_feedEndDate;
    private EditText editText_feedStartDate;
    private EditText editText_feedTernimalNumber;
    private int select_year;
    private int select_month;
    private int select_day;
    private int current_year;
    private int current_month;
    private int current_day;
    private int date_type;
    private String select_date;
    private List<String> list_feedRecordTime = new ArrayList<>();
    private List<String> list_feedCount = new ArrayList<>();
    private List<String> list_feedCowID = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedinfo_overview);
        setCustomActionBar();

        //在下方显示进食信息预览
        listView_feedInfoOverview = findViewById(R.id.listView_feedInfo);
        final Intent intent = getIntent();
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < intent.getStringArrayListExtra("feedTerminal").size(); i++){
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("feedTerminal", intent.getStringArrayListExtra("feedTerminal").get(i));
            showitem.put("feedEatCount", intent.getStringArrayListExtra("feedEatCount").get(i));
            listitem.add(showitem);
        }
        SimpleAdapter simpleAdapter_estrusOverview = new SimpleAdapter(getApplicationContext(),
                listitem,
                R.layout.custom_listview_feedinfooverview,
                new String[]{"feedTerminal", "feedEatCount"},
                new int[]{R.id.textView_customfeedOverviewTerminal, R.id.textView_customfeedOverviewEatCount});
        listView_feedInfoOverview.setAdapter(simpleAdapter_estrusOverview);
        listView_feedInfoOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> map_QueryFeedInfo = new HashMap<>();
                String url_QueryFeedInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetGpsSensorData";
                map_QueryFeedInfo.put("sensorType", "JS2");
                TextView textView_feedTerminalNumber = view.findViewById(R.id.textView_customfeedOverviewTerminal);
                map_QueryFeedInfo.put("terminal_number", textView_feedTerminalNumber.getText().toString());
                map_QueryFeedInfo.put("numbers", "100");
                OkHttpManager.postAsync(url_QueryFeedInfo, map_QueryFeedInfo, new OkHttpManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        Log.d("Debug", "获取进食信息连接失败");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        Log.d("Debug", result);
                        try {
                            JSONObject jsonObject_QueryFeedInfo = new JSONObject(result);
                            if (jsonObject_QueryFeedInfo.getString("message").equals("01")){
                                Log.d("Debug", "获取进食数据成功");
                                int feedDateNum = jsonObject_QueryFeedInfo.getInt("numbers");
                                JSONArray jsonArray_inner = jsonObject_QueryFeedInfo.getJSONArray("estrus");
                                list_feedRecordTime.clear();
                                list_feedCount.clear();
                                for (int i = 0; i < feedDateNum; i++){
                                    list_feedRecordTime.add(jsonArray_inner.getJSONObject(i).getString("record_time"));
                                    list_feedCount.add(jsonArray_inner.getJSONObject(i).getString("eat_count"));
                                    list_feedCowID.add(jsonArray_inner.getJSONObject(i).getString("cattleEatData_id"));
                                }
                            }else {
                                Log.d("Debug", "获取进食数据失败");
                            }
                        }catch (Exception e){
                            Log.d("Debug", e.toString());
                        }
                        Intent intent_detailedFeedInfo = new Intent(FeedInfoOverviewActivity.this, FeedInfoActivity.class);
                        intent_detailedFeedInfo.putStringArrayListExtra("feedRecordTime", (ArrayList<String>) list_feedRecordTime);
                        intent_detailedFeedInfo.putStringArrayListExtra("feedEatCount", (ArrayList<String>) list_feedCount);
                        startActivity(intent_detailedFeedInfo);

                    }


                });
            }
        });


        //上方填写查询信息
        editText_feedEndDate = findViewById(R.id.editText_feedEndData);
        editText_feedStartDate = findViewById(R.id.editText_feedStartData);
        editText_feedTernimalNumber = findViewById(R.id.editText_feedTerminalNumber);
        editText_feedTernimalNumber.setText(intent.getStringExtra("terminalNumber"));

        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH);
        current_day = calendar.get(Calendar.DAY_OF_MONTH);
        //“获取起始时间”按钮
        Button button_getStartDate = findViewById(R.id.button_feedStartData);
        button_getStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_type = 1;
                new DatePickerDialog(FeedInfoOverviewActivity.this, onDateSetListener, current_year, current_month, current_day).show();
            }
        });
        //“获取结束时间”按钮
        Button button_getEndDate = findViewById(R.id.button_feedEndData);
        button_getEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_type = 2;
                new DatePickerDialog(FeedInfoOverviewActivity.this, onDateSetListener, current_year, current_month, current_day).show();
            }
        });

        //“查询进食信息”按钮
        Button button_queryFeedInfo = findViewById(R.id.button_queryFeedInfo);
        button_queryFeedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_QueryFeedInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetGpsSensorData";
                Map<String, String> map_QueryFeedInfo = new HashMap<>();
                //按照个数查询(100个)
                if ((editText_feedStartDate.getText().toString().equals("") && editText_feedEndDate.getText().toString().equals(""))){
                    map_QueryFeedInfo.put("sensorType", "JS2");
                    map_QueryFeedInfo.put("terminal_number", editText_feedTernimalNumber.getText().toString());
                    map_QueryFeedInfo.put("numbers", "100");
                    OkHttpManager.postAsync(url_QueryFeedInfo, map_QueryFeedInfo, new OkHttpManager.DataCallBack() {
                        @Override
                        public void requestFailure(Request request, IOException e) {
                            Log.d("Debug", "获取进食信息连接失败");
                        }

                        @Override
                        public void requestSuccess(String result) throws Exception {
                            Log.d("Debug", result);
                            try {
                                JSONObject jsonObject_QueryFeedInfo = new JSONObject(result);
                                if (jsonObject_QueryFeedInfo.getString("message").equals("01")){
                                    Log.d("Debug", "获取进食数据成功");
                                    int feedDateNum = jsonObject_QueryFeedInfo.getInt("numbers");
                                    JSONArray jsonArray_inner = jsonObject_QueryFeedInfo.getJSONArray("estrus");
                                    list_feedRecordTime.clear();
                                    list_feedCount.clear();
                                    for (int i = 0; i < feedDateNum; i++){
                                        list_feedRecordTime.add(jsonArray_inner.getJSONObject(i).getString("record_time"));
                                        list_feedCount.add(jsonArray_inner.getJSONObject(i).getString("eat_count"));
                                        list_feedCowID.add(jsonArray_inner.getJSONObject(i).getString("cattleEatData_id"));
                                    }
                                }else {
                                    Log.d("Debug", "获取进食数据失败");
                                }
                            }catch (Exception e){
                                Log.d("Debug", e.toString());
                            }
                            Intent intent_detailedFeedInfo = new Intent(FeedInfoOverviewActivity.this, FeedInfoActivity.class);
                            intent_detailedFeedInfo.putStringArrayListExtra("feedRecordTime", (ArrayList<String>) list_feedRecordTime);
                            intent_detailedFeedInfo.putStringArrayListExtra("feedEatCount", (ArrayList<String>) list_feedCount);
                            startActivity(intent_detailedFeedInfo);

                        }


                    });
                }else {
                    //按照时间查询
                    map_QueryFeedInfo.put("sensorType", "JS1");
                    map_QueryFeedInfo.put("terminal_number", editText_feedTernimalNumber.getText().toString());
                    map_QueryFeedInfo.put("startDate", editText_feedStartDate.getText().toString());
                    map_QueryFeedInfo.put("endDate", editText_feedEndDate.getText().toString());
                    OkHttpManager.postAsync(url_QueryFeedInfo, map_QueryFeedInfo, new OkHttpManager.DataCallBack() {
                        @Override
                        public void requestFailure(Request request, IOException e) {
                            Log.d("Debug", "获取进食信息连接失败");
                        }

                        @Override
                        public void requestSuccess(String result) throws Exception {
                            Log.d("Debug", result);
                            try {
                                JSONObject jsonObject_QueryFeedInfo = new JSONObject(result);
                                if (jsonObject_QueryFeedInfo.getString("message").equals("01")){
                                    Log.d("Debug", "获取进食数据成功");
                                    int feedDateNum = jsonObject_QueryFeedInfo.getInt("numbers");
                                    JSONArray jsonArray_inner = jsonObject_QueryFeedInfo.getJSONArray("eatDatas");
                                    list_feedRecordTime.clear();
                                    list_feedCount.clear();
                                    list_feedRecordTime.clear();
                                    for (int i = 0; i < feedDateNum; i++){
                                        list_feedRecordTime.add(jsonArray_inner.getJSONObject(i).getString("record_time"));
                                        list_feedCount.add(jsonArray_inner.getJSONObject(i).getString("eat_count"));
                                        list_feedCowID.add(jsonArray_inner.getJSONObject(i).getString("cattleEatData_id"));
                                    }
                                }else {
                                    Log.d("Debug", "获取进食数据失败");
                                }
                            }catch (Exception e){
                                Log.d("Debug", e.toString());
                            }
                            Intent intent_detailedFeedInfo = new Intent(FeedInfoOverviewActivity.this, FeedInfoActivity.class);
                            intent_detailedFeedInfo.putStringArrayListExtra("feedRecordTime", (ArrayList<String>) list_feedRecordTime);
                            intent_detailedFeedInfo.putStringArrayListExtra("feedEatCount", (ArrayList<String>) list_feedCount);
                            startActivity(intent_detailedFeedInfo);
                        }
                    });
                }


            }
        });

    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            select_year = year;
            select_month = month;
            select_day = dayOfMonth;
            if (select_month + 1 < 10){
                if (select_day < 10){
                    select_date = new StringBuffer().append(select_year).append("-").append("0").
                            append(select_month + 1).append("-").append("0").append(select_day).toString();
                }else{
                    select_date = new StringBuffer().append(select_year).append("-").append("0").
                            append(select_month + 1).append("-").append(select_day).toString();
                }
            }else {
                if (select_day < 10){
                    select_date = new StringBuffer().append(select_year).append("-").
                            append(select_month + 1).append("-").append("0").append(select_day).toString();
                }else{
                    select_date = new StringBuffer().append(select_year).append("-").
                            append(select_month + 1).append("-").append(select_day).toString();
                }
            }
            switch (date_type){
                case 1:
                    editText_feedStartDate.setText(select_date);
                    break;
                case 2:
                    editText_feedEndDate.setText(select_date);
                    break;
            }
        }
    };

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

    private void setCustomActionBar(){
        android.app.ActionBar.LayoutParams layoutParams = new android.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.custom_actionbar, null);
        TextView textView = mActionBarView.findViewById(R.id.textView_title);
        textView.setText("进食信息");
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
//                Intent intent = new Intent(FeedInfoOverviewActivity.this, MenuActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
            }
        });
    }
}
