package com.example.birdguan.cownet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.birdguan.cownet.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class NewDetailedInfoActivity extends Activity {
    private List<String> list_terminalNumber = new ArrayList<String>();
    private List<String> list_earNumber = new ArrayList<String>();
    private List<String> list_variety = new ArrayList<String>();
    private List<String> list_gender = new ArrayList<String>();
    private List<String> list_streakColor = new ArrayList<String>();
    private List<String> list_monthAge = new ArrayList<String>();
    private List<String> list_sate = new ArrayList<String>();
    private List<String> list_master = new ArrayList<String>();
    private List<String> list_url = new ArrayList<String>();
    private List<Bitmap> list_image = new ArrayList<Bitmap>();
    private List<String> list_feedRecordTime = new ArrayList<>();
    private List<String> list_feedCount = new ArrayList<>();
    private List<String> list_feedCowID = new ArrayList<>();
    private List<Integer> list_slightExerciseCounts = new ArrayList<Integer>();
    private List<Integer> list_middleExerciseCounts = new ArrayList<Integer>();
    private List<Integer> list_strenuousExerciseCounts = new ArrayList<Integer>();
    private List<String> list_estrusRecordTime = new ArrayList<String>();
    private List<Drawable> drawableImages = new ArrayList<Drawable>();
    private ListView listView_detailedInfo;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detiledinfo_new);
        setCustomActionBar();
        listView_detailedInfo = findViewById(R.id.listView_detailedInfo);

        final String url_detailedInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appCattleBasicCowInfo/appGetCattleBasicCowInfo";
        final SharedPreferences lognInfo = getSharedPreferences("login", MODE_PRIVATE);
        String user_id = lognInfo.getString("userID", "");
        String role_id = lognInfo.getString("roleID", "");

//        //测试数据
//        //user_id = "0223018f802c4b159a2b87311d366793";
//        user_id = "817df6584b24487fa2dbc19450869639";
//        role_id = "牧主";

        Map<String, String> map_detailedInfo = new HashMap<>();
        map_detailedInfo.put("USER_ID", user_id);
        map_detailedInfo.put("ROLE_ID", role_id);
        OkHttpManager.postAsync(url_detailedInfo, map_detailedInfo, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("Debug", "获取牛基本信息失败");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    Log.d("Debug", "result: " + result);
                    JSONObject jsonObject_detailedInfo = new JSONObject(result);
                    if (jsonObject_detailedInfo.getString("message").equals("01")) {
                        Log.d("Debug", "获取牛基本信息成功");
                        JSONArray jsonArray_inner = jsonObject_detailedInfo.getJSONArray("cattleBasicCowInfoList");
                        list_terminalNumber.clear();
                        list_earNumber.clear();
                        list_variety.clear();
                        list_gender.clear();
                        list_streakColor.clear();
                        list_monthAge.clear();
                        list_sate.clear();
                        list_master.clear();
                        list_image.clear();
//                        DownloadImageTask downloadImageTask = new DownloadImageTask();
                        String[] url = new String[jsonArray_inner.length()];
                        for (int i = 0; i < jsonArray_inner.length(); i++) {
                            JSONObject jsonObject_inner = jsonArray_inner.getJSONObject(i);
                            list_terminalNumber.add(jsonObject_inner.getString("TERMINAL_NUMBER"));
                            list_earNumber.add(jsonObject_inner.getString("EAR_MARKINGS"));
                            list_variety.add(jsonObject_inner.getString("VARIETY"));
                            list_gender.add(jsonObject_inner.getString("GENDER"));
                            list_streakColor.add(jsonObject_inner.getString("COLOR"));
                            list_monthAge.add(jsonObject_inner.getString("MONTH_OLD"));
                            list_sate.add(jsonObject_inner.getString("LIVESTOCK_STATUS"));
                            list_master.add(jsonObject_inner.getString("PARTNER_NAME"));
                            //获取图片地址
                            String image_description = jsonObject_inner.getString("DESCRIPTION");
                            Log.d("Debug", image_description);
                            String url_file = "http://106.15.53.134:60006/LivestockSystem2018_APP/uploadFiles/uploadImgs/";
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(url_file);
                            stringBuilder.append(image_description);
                            String url_image = stringBuilder.toString();
                            Log.d("Debug", url_image);
                            list_url.add(url_image);
                            url[i] = url_image;

                        }
//                        downloadImageTask.execute(url);

                    }
                } catch (JSONException e) {
                    Log.d("Debug", e.toString());
                }

                List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < list_terminalNumber.size(); i++) {
                    Map<String, Object> showitem = new HashMap<String, Object>();
                    showitem.put("terminalNumber", list_terminalNumber.get(i));
                    showitem.put("earNumber", list_earNumber.get(i));
                    showitem.put("variety", list_variety.get(i));
                    showitem.put("gender", list_gender.get(i));
                    showitem.put("streakColor", list_streakColor.get(i));
                    showitem.put("monthAge", list_monthAge.get(i));
                    showitem.put("status", list_sate.get(i));
                    showitem.put("master", list_master.get(i));
                    showitem.put("imageURL", list_url.get(i));

                    listitem.add(showitem);
                }
                SimpleAdapter simpleAdapter_detailedInfo = new SimpleAdapter(getApplicationContext(),
                        listitem,
                        R.layout.custom_listview_newdetailedinfo,
                        new String[]{"terminalNumber", "earNumber", "variety", "gender", "streakColor", "monthAge", "status", "master", "imageURL"},
                        new int[]{R.id.textView_detailedInfoTerminalInput, R.id.textView_detailedInfoEarNumberInput, R.id.textView_detailedInfoVarietyInput, R.id.textView_detailedInfoGenderInput, R.id.textView_detailedInfoStreakColorInput,
                                R.id.textView_detailedInfoMonthAgeInput, R.id.textView_detailedInfoStateInput, R.id.textView_detailedInfoMasterInput, R.id.imageView_detailedInfoIcon});
                simpleAdapter_detailedInfo.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if (view instanceof ImageView && data instanceof String) {
                            Picasso.with(getApplicationContext()).load((String) data).into((ImageView) view);
                            return true;
                        }
                        return false;
                    }
                });
                listView_detailedInfo.setDivider(null);
                listView_detailedInfo.setAdapter(simpleAdapter_detailedInfo);

            }
        });

        listView_detailedInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.textView_detailedInfoTerminalInput);
                String terminalNumber = textView.getText().toString();
                Log.d("Debug", "选中的终端号: " + terminalNumber);
                SharedPreferences sharedPreferences = getSharedPreferences("cowInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("terminalNumber", terminalNumber);
                editor.apply();
                initPopupWindow(view.findViewById(R.id.imageView_detailedInfoIcon));
            }
        });
    }

    private void initPopupWindow(View v){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_popupwindow_detailedinfo, null, false);
        Button button_gotoFeedInfo = view.findViewById(R.id.button_popupwindow_feed);
        Button button_gotoEstrusInfo = view.findViewById(R.id.button_popupwindow_estrus);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.showAsDropDown(v,-200,0);

        //获取进食信息按钮
        button_gotoFeedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sharedPreferences = getSharedPreferences("cowInfo",MODE_PRIVATE);
//                Intent intent_gotoFeedInfo = new Intent(NewDetailedInfoActivity.this, FeedInfoActivity.class);
//                intent_gotoFeedInfo.putExtra("terminalNumber", sharedPreferences.getString("terminalNumber", ""));
//                startActivity(intent_gotoFeedInfo);

                SharedPreferences sharedPreferences = getSharedPreferences("cowInfo",MODE_PRIVATE);
                Map<String, String> map_QueryFeedInfo = new HashMap<>();
                String url_QueryFeedInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetGpsSensorData";
                map_QueryFeedInfo.put("sensorType", "JS2");
                map_QueryFeedInfo.put("terminal_number",sharedPreferences.getString("terminalNumber", ""));
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
                        Intent intent_detailedFeedInfo = new Intent(NewDetailedInfoActivity.this, FeedInfoActivity.class);
                        intent_detailedFeedInfo.putStringArrayListExtra("feedRecordTime", (ArrayList<String>) list_feedRecordTime);
                        intent_detailedFeedInfo.putStringArrayListExtra("feedEatCount", (ArrayList<String>) list_feedCount);
                        startActivity(intent_detailedFeedInfo);

                    }


                });
            }
        });
        //获取发情信息按钮
        button_gotoEstrusInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("cowInfo",MODE_PRIVATE);
                String url_queryEstrusInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appSensor/appGetGpsSensorData";
                Map<String, String> map_queryEstrusInfo = new HashMap<>();
                //按照查询个数查询（查询100条）
                map_queryEstrusInfo.put("sensorType", "FQ2");
                map_queryEstrusInfo.put("terminal_number", sharedPreferences.getString("terminalNumber", ""));
                map_queryEstrusInfo.put("numbers", "100");
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
                                list_slightExerciseCounts.clear();
                                list_middleExerciseCounts.clear();
                                list_strenuousExerciseCounts.clear();
                                list_estrusRecordTime.clear();
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
                        Intent intent_detailedEstrusInfo = new Intent(NewDetailedInfoActivity.this, NewEstrusActivity.class);
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
        textView.setText("基本信息");
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
                Intent intent = new Intent(NewDetailedInfoActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


//        Log.d("Debug", "获取图片");
//        final List<Map<String, Object>> imageitem = new ArrayList<Map<String, Object>>();
//        for(int i = 0; i < list_url.size(); i++) {
//            try {
//                URL url_image = new URL(list_url.get(i));
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url_image.openConnection();
//                InputStream inputStream = httpURLConnection.getInputStream();
//                Map<String, Object> showitem = new HashMap<String, Object>();
//                showitem.put("icon", BitmapFactory.decodeStream(inputStream));
//                imageitem.add(showitem);
//                SimpleAdapter simpleAdapter_image = new SimpleAdapter(getApplicationContext(),
//                        imageitem,
//                        R.layout.custom_listview_newdetailedinfo,
//                        new String[]{"icon"},
//                        new int[]{R.id.imageView_detailedInfoIcon});
//                simpleAdapter_image.setViewBinder(new SimpleAdapter.ViewBinder() {
//                    @Override
//                    public boolean setViewValue(View view, Object data, String textRepresentation) {
//                        if (view instanceof ImageView && data instanceof Bitmap) {
//                            ImageView imageView = (ImageView) view;
//                            imageView.setImageBitmap((Bitmap) data);
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    }
//                });
//                listView_detailedInfo.setAdapter(simpleAdapter_image);
//            } catch (MalformedURLException e) {
//                Log.d("Debug", e.toString());
//            } catch (IOException e) {
//                Log.d("Debug", e.toString());
//            }
//        }
//    protected class DownloadImageTask extends AsyncTask<String, Void, List<Bitmap>>{
//
//        @Override
//        protected List<Bitmap> doInBackground(String... strings) {
//            List<Bitmap> list_bitmap = new ArrayList<Bitmap>();
//            for (int i = 0; i < strings.length; i++) {
//                String url = strings[i];
//                Bitmap cowImage = null;
//                try {
//                    InputStream inputStream = new URL(url).openStream();
//                    cowImage = BitmapFactory.decodeStream(inputStream);
//                    list_image.add(cowImage);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return list_bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(List<Bitmap> bitmaps) {
//            super.onPostExecute(bitmaps);
//            List<Map<String, Object>> imageitem = new ArrayList<Map<String, Object>>();
//            for(int i = 0; i < list_image.size(); i++) {
//                Map<String, Object> showitem = new HashMap<String, Object>();
//                showitem.put("icon", list_image.get(i));
//                imageitem.add(showitem);
//                SimpleAdapter simpleAdapter_image = new SimpleAdapter(getApplicationContext(),
//                        imageitem,
//                        R.layout.custom_listview_newdetailedinfo,
//                        new String[]{"icon"},
//                        new int[]{R.id.imageView_detailedInfoIcon});
//                simpleAdapter_image.setViewBinder(new SimpleAdapter.ViewBinder() {
//                    @Override
//                    public boolean setViewValue(View view, Object data, String textRepresentation) {
//                        if (view instanceof ImageView && data instanceof Bitmap) {
//                            ImageView imageView = (ImageView) view;
//                            imageView.setImageBitmap((Bitmap) data);
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    }
//                });
//                listView_detailedInfo.setAdapter(simpleAdapter_image);
//            }
//        }
//    }
}
