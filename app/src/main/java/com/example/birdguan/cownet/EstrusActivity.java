package com.example.birdguan.cownet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import okhttp3.Request;

public class EstrusActivity extends Activity {
    private int select_year;
    private int select_month;
    private int select_day;
    private int current_year;
    private int current_month;
    private int current_day;
    private int date_type;
    private String select_date;
    private EditText editText_estrusStartDate;
    private EditText editText_estrusEndDate;
    private EditText editText_estrusTernimalNumber;
    private List<Integer> list_slightExerciseCounts = new ArrayList<Integer>();
    private List<Integer> list_middleExerciseCounts = new ArrayList<Integer>();
    private List<Integer> list_strenuousExerciseCounts = new ArrayList<Integer>();
    private List<String> list_estrusRecordTime = new ArrayList<String>();
    private ListView listView_estrusOverview;
    int estrusListNum = 50;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estrus);
        setCustomActionBar();
        editText_estrusStartDate = findViewById(R.id.editText_estrusStartData);
        editText_estrusEndDate = findViewById(R.id.editText_estrusEndData);
        editText_estrusTernimalNumber = findViewById(R.id.editText_estrusterminaltoQuery);
        listView_estrusOverview = findViewById(R.id.listview_estrus);


        //上方查询信息
        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH);
        current_day = calendar.get(Calendar.DAY_OF_MONTH);
        //“获取起始时间”按钮
        Button button_getStartDate = findViewById(R.id.button_estrusStartData);
        button_getStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_type = 1;
                new DatePickerDialog(EstrusActivity.this, onDateSetListener, current_year, current_month, current_day).show();
            }
        });
        //“获取结束时间”按钮
        Button button_getEndDate = findViewById(R.id.button_estrusEndData);
        button_getEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_type = 2;
                new DatePickerDialog(EstrusActivity.this, onDateSetListener, current_year, current_month, current_day).show();
            }
        });

        //“查询”按钮
        Button button_queryEstrus = findViewById(R.id.button_estrusQuery);
        button_queryEstrus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String url_queryEstrusInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetGpsSensorData";
                Map<String, String> map_queryEstrusInfo = new HashMap<>();
                //按照查询个数查询（查询100条）
                if (editText_estrusStartDate.getText().toString().equals("") && editText_estrusEndDate.getText().toString().equals("")) {
                    map_queryEstrusInfo.put("sensorType", "FQ2");
                    map_queryEstrusInfo.put("terminal_number", editText_estrusTernimalNumber.getText().toString());
                    map_queryEstrusInfo.put("numbers", "100");
                } else {
                    //按照查询时间查询
                    map_queryEstrusInfo.put("sensorType", "FQ1");
                    map_queryEstrusInfo.put("terminal_number", editText_estrusTernimalNumber.getText().toString());
                    map_queryEstrusInfo.put("startDate", editText_estrusStartDate.getText().toString());
                    map_queryEstrusInfo.put("endDate", editText_estrusEndDate.getText().toString());
                }
                list_slightExerciseCounts.clear();
                list_middleExerciseCounts.clear();
                list_strenuousExerciseCounts.clear();
                list_estrusRecordTime.clear();
                OkHttpManager.postAsync(url_queryEstrusInfo, map_queryEstrusInfo, new OkHttpManager.DataCallBack() {
                    @Override
                    public void requestFailure(Request request, IOException e) {
                        Log.d("Debug", "获取发情数据连接失败");
                    }

                    @Override
                    public void requestSuccess(String result) throws Exception {
                        Log.d("Debug", result);
                        try{
                            JSONObject jsonObject_estrus = new JSONObject(result);
                            if (jsonObject_estrus.getString("message").equals("01")) {
                                Log.d("Debug", "获取发情数据成功");
                                int ertrusNumber = jsonObject_estrus.getInt("numbers");
                                JSONArray jsonArray_inner = jsonObject_estrus.getJSONArray("estrus");
                                for (int i = 0; i < ertrusNumber; i++) {
                                    JSONObject jsonObject_inner = jsonArray_inner.getJSONObject(i);
                                    list_slightExerciseCounts.add(jsonObject_inner.getInt("samples_number"));
                                    list_middleExerciseCounts.add(jsonObject_inner.getInt("exercises_number"));
                                    list_strenuousExerciseCounts.add(jsonObject_inner.getInt("average_value"));
                                    list_estrusRecordTime.add(jsonObject_inner.getString("record_time"));
                                }
                            }
                        }catch (Exception e){
                            Log.d("Debug", "获取发情数据时: " + e.toString());
                        }
                        Intent intent_detailedEstrusInfo = new Intent(EstrusActivity.this, NewEstrusActivity.class);
                        intent_detailedEstrusInfo.putStringArrayListExtra("estrusRecordTime", (ArrayList<String>) list_estrusRecordTime);
                        intent_detailedEstrusInfo.putIntegerArrayListExtra("estrusSlightCount", (ArrayList<Integer>) list_slightExerciseCounts);
                        intent_detailedEstrusInfo.putIntegerArrayListExtra("estrusMiddleCount", (ArrayList<Integer>) list_middleExerciseCounts);
                        intent_detailedEstrusInfo.putIntegerArrayListExtra("estrusStrenuousCount", (ArrayList<Integer>) list_strenuousExerciseCounts);
                        startActivity(intent_detailedEstrusInfo);
                    }
                });
            }
        });

        //下方显示发情信息
        final Intent intent = getIntent();
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < intent.getStringArrayListExtra("estrusTerminal").size(); i++){
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("terminal_number", intent.getIntegerArrayListExtra("estrusTerminal").get(i));
            showitem.put("samples_number", intent.getIntegerArrayListExtra("estrusSlightCount").get(i));
            showitem.put("exercise_value", intent.getIntegerArrayListExtra("estrusMiddleCount").get(i));
            showitem.put("average_value", intent.getIntegerArrayListExtra("estrusStrenuousCount").get(i));
            listitem.add(showitem);
        }
        SimpleAdapter simpleAdapter_estrusOverview = new SimpleAdapter(getApplicationContext(),
                listitem,
                R.layout.custom_listview_estrus,
                new String[]{"terminal_number", "samples_number", "exercise_value", "average_value"},
                new int[]{R.id.textView_estrusTerminal, R.id.textView_estrusSlightCounts, R.id.textView_estrusMiddleCounts, R.id.textView_estrusStrenuousCounts});
                listView_estrusOverview.setAdapter(simpleAdapter_estrusOverview);
                listView_estrusOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String, String> map_QueryFeedInfo = new HashMap<>();
                    String url_QueryFeedInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetGpsSensorData";
                    map_QueryFeedInfo.put("sensorType", "FQ2");
                    TextView textView_estrusTerminalNumber = view.findViewById(R.id.textView_estrusTerminal);
                    map_QueryFeedInfo.put("terminal_number", textView_estrusTerminalNumber.getText().toString());
                    map_QueryFeedInfo.put("numbers", "100");
                    OkHttpManager.postAsync(url_QueryFeedInfo, map_QueryFeedInfo, new OkHttpManager.DataCallBack() {
                        @Override
                        public void requestFailure(Request request, IOException e) {
                            Log.d("Debug", "获取发情信息连接失败");
                        }

                        @Override
                        public void requestSuccess(String result) throws Exception {
                            Log.d("Debug", result);
                            try {
                                JSONObject jsonObject_QueryFeedInfo = new JSONObject(result);
                                if (jsonObject_QueryFeedInfo.getString("message").equals("01")){
                                    Log.d("Debug", "获取发情数据成功");
                                    int feedDateNum = jsonObject_QueryFeedInfo.getInt("numbers");
                                    JSONArray jsonArray_inner = jsonObject_QueryFeedInfo.getJSONArray("estrus");
                                    list_estrusRecordTime.clear();
                                    list_slightExerciseCounts.clear();
                                    list_middleExerciseCounts.clear();
                                    list_strenuousExerciseCounts.clear();
                                    for (int i = 0; i < feedDateNum; i++){
                                        list_estrusRecordTime.add(jsonArray_inner.getJSONObject(i).getString("record_time"));
                                        list_slightExerciseCounts.add(jsonArray_inner.getJSONObject(i).getInt("samples_number"));
                                        list_middleExerciseCounts.add(jsonArray_inner.getJSONObject(i).getInt("exercises_number"));
                                        list_strenuousExerciseCounts.add(jsonArray_inner.getJSONObject(i).getInt("average_value"));
                                    }
                                }else {
                                    Log.d("Debug", "获取发情数据失败");
                                }
                            }catch (Exception e){
                                Log.d("Debug", e.toString());
                            }
                            Intent intent_detailedEstrusInfo = new Intent(EstrusActivity.this, NewEstrusActivity.class);
                            intent_detailedEstrusInfo.putStringArrayListExtra("estrusRecordTime", (ArrayList<String>) list_estrusRecordTime);
                            intent_detailedEstrusInfo.putIntegerArrayListExtra("estrusSlightCount", (ArrayList<Integer>) list_slightExerciseCounts);
                            intent_detailedEstrusInfo.putIntegerArrayListExtra("estrusMiddleCount", (ArrayList<Integer>) list_middleExerciseCounts);
                            intent_detailedEstrusInfo.putIntegerArrayListExtra("estrusStrenuousCount", (ArrayList<Integer>) list_strenuousExerciseCounts);
                            startActivity(intent_detailedEstrusInfo);

                        }


                    });
            }
        });
    }

    //安卓原生日历控件回调，选择日期，将选择的日期显示在相应的EditText
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
                    editText_estrusStartDate.setText(select_date);
                    break;
                case 2:
                    editText_estrusEndDate.setText(select_date);
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
        textView.setText("发情预警");
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
                Intent intent = new Intent(EstrusActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}



//        listView_estrusOverview = findViewById(R.id.listview_estrus);
//        Intent intent = getIntent();
//        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
//        for (int i = 0; i < intent.getStringArrayListExtra("estrusTerminal").size(); i++){
//            Map<String, Object> showitem = new HashMap<String, Object>();
//            showitem.put("estrusTerminal", intent.getStringArrayListExtra("estrusTerminal").get(i));
//            showitem.put("estrusSlightCount", intent.getStringArrayListExtra("estrusSlightCount").get(i));
//            showitem.put("estrusMiddleCount", intent.getStringArrayListExtra("estrusMiddleCount").get(i));
//            showitem.put("estrusStrenuousCount", intent.getStringArrayListExtra("estrusStrenuous").get(i));
//            listitem.add(showitem);
//        }
//        SimpleAdapter simpleAdapter_estrusOverview = new SimpleAdapter(getApplicationContext(),
//                listitem,
//                R.layout.custom_listview_estrus,
//                new String[]{"estrusTerminal", "estrusSlightCount", "estrusMiddleCount", "estrusStrenuousCount"},
//                new int[]{R.id.textView_estrusTerminal, R.id.textView_estrusSlightCounts, R.id.textView_estrusMiddleCounts, R.id.textView_estrusStrenuousCounts});
//        listView_estrusOverview.setAdapter(simpleAdapter_estrusOverview);



        //测试数据
//        user_id = "0223018f802c4b159a2b87311d366793";
//        role_id = "牧主";

//        Map<String, String> map_oestus = new HashMap<>();
//        map_oestus.put("ROLE_ID",role_id);
//        map_oestus.put("USER_ID",user_id);
//        map_oestus.put("SENSOR_TYPE","FQ");
//        map_oestus.put("NUMBERS","20");
//        OkHttpManager.postAsync(url_estrus, map_oestus, new OkHttpManager.DataCallBack() {
//            @Override
//            public void requestFailure(Request request, IOException e) {
//                Log.d("Debug","获取发情数据连接失败");
//            }
//
//            @Override
//            public void requestSuccess(String result) throws Exception {
//                try {
//                    JSONObject jsonObject_oestus = new JSONObject(result);
//                    if (jsonObject_oestus.getString("message").equals("01")){
//                        Log.d("Debug","获取发情数据成功");
//                        estrusListNum = Integer.parseInt(jsonObject_oestus.getString("numbers"));
//                        JSONArray jsonArray_estrus = jsonObject_oestus.getJSONArray("coordinate");
//                        estrusTerminal.clear();
//                        estrusExerciseNum.clear();
//                        estrusTime.clear();
//                        for(int i = 0; i < estrusListNum; i++){
//                            estrusTerminal.add(jsonArray_estrus.getJSONObject(i).getString("TERMINAL_NUMBER"));
//                            estrusExerciseNum.add(jsonArray_estrus.getJSONObject(i).getString("EXERCISES_NUMBER"));
//                            estrusTime.add(jsonArray_estrus.getJSONObject(i).getString("RECORD_TIME"));
//                        }
//
//                        //构建List的Adapter
//                        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
//                        for (int i = 0; i<estrusListNum;i++){
//                            Map<String, Object> showitem = new HashMap<String, Object>();
//                            showitem.put("estrus_terminal", estrusTerminal.get(i));
//                            showitem.put("estrus_exerciseNumber",estrusExerciseNum.get(i));
//                            showitem.put("estrus_time",estrusTime.get(i));
//                            listitem.add(showitem);
//                        }
//                        SimpleAdapter simpleAdapter_estrus = new SimpleAdapter(getApplicationContext(), listitem, R.layout.custom_listview_estrus,
//                                new String[]{"estrus_terminal","estrus_exerciseNumber","estrus_time"},
//                                new int[]{R.id.textView_estrusTerminal, R.id.textView_estrusNumber, R.id.textView_estrusTime});
//                        ListView listView_estrus = findViewById(R.id.listview_estrus);
//                        listView_estrus.setAdapter(simpleAdapter_estrus);
//                    }else {
//                        Log.d("Debug","获取失败");
//                    }
//                }catch (Exception e){
//                    Log.d("Debug",e.toString());
//                }
//            }
//        });




