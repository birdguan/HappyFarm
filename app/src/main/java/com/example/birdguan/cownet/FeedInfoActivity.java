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
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.example.birdguan.cownet.R;
import com.example.birdguan.cownet.utils.OkHttpManager;


import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class FeedInfoActivity extends Activity {


    ListView listView_feedInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedinfo);
        setCustomActionBar();
        Intent intent = getIntent();

        listView_feedInfo = findViewById(R.id.listView_feedInfo);
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < intent.getStringArrayListExtra("feedRecordTime").size(); i++){
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("feed_record_time", intent.getStringArrayListExtra("feedRecordTime").get(i));
            showitem.put("feed_eat_count", intent.getStringArrayListExtra("feedEatCount").get(i));
            listitem.add(showitem);
        }
        SimpleAdapter simpleAdapter_feedData = new SimpleAdapter(getApplicationContext(), listitem, R.layout.custom_listview_feedinfo,
                new String[]{"feed_record_time", "feed_eat_count"},
                new int[] {R.id.textView_listView_recordTime, R.id.textView_listView_feedCount});
        listView_feedInfo.setAdapter(simpleAdapter_feedData);
        listView_feedInfo.setVisibility(View.VISIBLE);


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
        textView.setText("进食详情");
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
//                Intent intent = new Intent(FeedInfoActivity.this, FeedInfoOverviewActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
            }
        });
    }


}
