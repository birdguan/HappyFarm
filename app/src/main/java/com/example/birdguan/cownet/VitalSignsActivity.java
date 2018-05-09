package com.example.birdguan.cownet;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.birdguan.cownet.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class VitalSignsActivity extends Activity{
    private List<String> list_terminalNumber = new ArrayList<String>();
    private List<String> list_earNumber = new ArrayList<String>();
    private List<String> list_vitalSigns = new ArrayList<String>();
    private List<String> list_url = new ArrayList<String>();
    private List<Bitmap> list_image = new ArrayList<Bitmap>();
    private List<Drawable> drawableImages = new ArrayList<Drawable>();
    private ListView listView_vatalSigns;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitalsigns);
        setCustomActionBar();
        listView_vatalSigns = findViewById(R.id.listView_vitalSigns);
        String url_vitalSigns = "http://106.15.53.134:60006/LivestockSystem2018_APP/appCattleBasicCowInfo/appGetCattleBasicCowInfo";
        SharedPreferences lognInfo = getSharedPreferences("login", MODE_PRIVATE);
        String user_id = lognInfo.getString("userID", "");
        String role_id = lognInfo.getString("roleID", "");

//        //测试数据
//        user_id = "817df6584b24487fa2dbc19450869639";
//        role_id = "牧主";

        Map<String, String> map_detailedInfo = new HashMap<>();
        map_detailedInfo.put("USER_ID", user_id);
        map_detailedInfo.put("ROLE_ID", role_id);
        OkHttpManager.postAsync(url_vitalSigns, map_detailedInfo, new OkHttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.d("Debug", "获取牛基本信息失败");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                try {
                    Log.d("Debug", result);
                    JSONObject jsonObject_detailedInfo = new JSONObject(result);
                    if (jsonObject_detailedInfo.getString("message").equals("01")) {
                        Log.d("Debug", "获取牛基本信息成功");
                        JSONArray jsonArray_inner = jsonObject_detailedInfo.getJSONArray("cattleBasicCowInfoList");
                        list_terminalNumber.clear();
                        list_earNumber.clear();
                        list_vitalSigns.clear();
//                        DownloadImageTask downloadImageTask = new DownloadImageTask();
                        String[] url = new String[jsonArray_inner.length()];
                        for (int i = 0; i < jsonArray_inner.length(); i++) {

                            JSONObject jsonObject_inner = jsonArray_inner.getJSONObject(i);
                            Log.d("Debug", "jsonObject: " + jsonObject_inner);
                            list_vitalSigns.add(jsonObject_inner.getString("REPRODUCTIVE_STATUS"));
                            list_terminalNumber.add(jsonObject_inner.getString("TERMINAL_NUMBER"));
                            list_earNumber.add(jsonObject_inner.getString("EAR_MARKINGS"));

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
                    showitem.put("vitalSigns", list_vitalSigns.get(i));
                    showitem.put("imageURL", list_url.get(i));
                    listitem.add(showitem);
                }
                SimpleAdapter simpleAdapter_detailedInfo = new SimpleAdapter(getApplicationContext(),
                        listitem,
                        R.layout.custom_listview_vitalsigns,
                        new String[]{"terminalNumber", "earNumber", "vitalSigns", "imageURL"},
                        new int[]{R.id.textView_vitalSignsTerminalInput, R.id.textView_vitalSignsEarNumberInput, R.id.textView_vitalSignsStatusInput, R.id.imageView_vitalSignsIcon});
                simpleAdapter_detailedInfo.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if (view instanceof ImageView && data instanceof String) {
                            Picasso.with(VitalSignsActivity.this).load((String) data).into((ImageView) view);
                            return true;
                        }
                        if (view instanceof TextView && data instanceof String){
                            if (data.equals("受伤")){
                                ((TextView) view).setTextColor(R.color.red);
                            }
                        }
                        return false;
                    }
                });
                listView_vatalSigns.setDivider(null);
                listView_vatalSigns.setAdapter(simpleAdapter_detailedInfo);
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(VitalSignsActivity.this, MenuActivity.class);
//        startActivity(intent);
//    }

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
        textView.setText("生命体征");
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
                Intent intent = new Intent(VitalSignsActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

//    protected class DownloadImageTask extends AsyncTask<String, Void, List<Bitmap>> {
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
//                        R.layout.custom_listview_vitalsigns,
//                        new String[]{"icon"},
//                        new int[]{R.id.imageView_vitalSignsIcon});
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
//                listView_vatalSigns.setAdapter(simpleAdapter_image);
//            }
//        }
//    }
}



