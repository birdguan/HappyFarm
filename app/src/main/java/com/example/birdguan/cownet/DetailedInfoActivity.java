package com.example.birdguan.cownet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.birdguan.cownet.utils.OkHttpManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

public class DetailedInfoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detialedinfo);
        final EditText editText_ternimalNumbers = findViewById(R.id.editText_ternimalNumber);
        final EditText editText_earNumber = findViewById(R.id.editText_earNumber);
        final EditText editText_variety = findViewById(R.id.editText_type);
        final EditText editText_sex = findViewById(R.id.editText_sex);
        final EditText editText_huase = findViewById(R.id.editText_huase);
        final EditText editText_weight = findViewById(R.id.editText_weight);
        final EditText editText_origin = findViewById(R.id.editText_laiyuan);
        final EditText editText_yueling = findViewById(R.id.editText_yueling);
        final EditText editText_tixing = findViewById(R.id.editText_tixing);
        final EditText editText_birthData = findViewById(R.id.editText_birthData);
        final EditText editText_leibie = findViewById(R.id.editText_leibie);
        final EditText editText_xumuzhuangtai = findViewById(R.id.editText_xumuzhuangtai);
        final EditText editText_xumujieduan = findViewById(R.id.editText_xumujieduan);
        final EditText editText_hezuohu = findViewById(R.id.editText_hezuohu);
        final EditText editText_fanzhizhuangtai = findViewById(R.id.editText_fanzhizhuangtai);
        final EditText editText_chaungjianshijian = findViewById(R.id.editText_chuangjianshijian);
        final EditText editText_muchangmingcheng = findViewById(R.id.editText_muchangmingcheng);
        EditText editText_muchangdizhi = findViewById(R.id.editText_muchangdizhi);
        EditText editText_state = findViewById(R.id.editText_state);
        Intent intent = getIntent();
        if (intent.getStringExtra("flag").equals("Query")){
            String terminaltoQuery = intent.getStringExtra("terminaltoQuery");
            Log.d("Debug",terminaltoQuery);
            String url_queryDetailedInfo = "http://106.15.53.134:60006/LivestockSystem2018_APP/appCattleBasicCowInfo/appGetCattleBasicCowInfoByTerminalNumber";
            Map<String,String> map_detaiedInfo = new HashMap<String, String>();
            map_detaiedInfo.put("terminal_number",terminaltoQuery);
            OkHttpManager.postAsync(url_queryDetailedInfo, map_detaiedInfo, new OkHttpManager.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    Log.d("Debug","获取详细信息连接失败");
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    try {
                        JSONObject jsonObject_detailedInfo = new JSONObject(result);
                        Log.d("Debug",jsonObject_detailedInfo.toString());
                        if (jsonObject_detailedInfo.getString("message").equals("01")){
                            Log.d("Debug","获取详细信息数据成功");
                            JSONArray jsonArray_inner = jsonObject_detailedInfo.getJSONArray("cattleBasicCowInfo");
                            JSONObject jsonObject_inner= jsonArray_inner.getJSONObject(0);
                            editText_ternimalNumbers.setText(jsonObject_inner.getString("TERMINAL_NUMBER"));
                            editText_earNumber.setText(jsonObject_inner.getString("EAR_MARKINGS"));
                            editText_variety.setText(jsonObject_inner.getString("VARIETY"));
                            editText_sex.setText(jsonObject_inner.getString("GENDER"));
                            editText_huase.setText(jsonObject_inner.getString("COLOR"));
                            editText_weight.setText(jsonObject_inner.getString("WEIGHT"));
                            editText_origin.setText(jsonObject_inner.getString("SOURCE"));
                            editText_yueling.setText(jsonObject_inner.getString("MONTH_OLD"));
                            editText_tixing.setText(jsonObject_inner.getString("BODY_TYPE"));
                            editText_birthData.setText(jsonObject_inner.getString("BIRTHDAY"));
                            editText_leibie.setText(jsonObject_inner.getString("CATEGORY"));
                            editText_xumuzhuangtai.setText(jsonObject_inner.getString("LIVESTOCK_STATUS"));
                            editText_xumujieduan.setText(jsonObject_inner.getString("LIVESTOCK_STAGE"));
                            editText_hezuohu.setText(jsonObject_inner.getString("PARTNER_NAME"));
                            editText_fanzhizhuangtai.setText(jsonObject_inner.getString("REPRODUCTIVE_STATUS"));
                            editText_chaungjianshijian.setText(jsonObject_inner.getString("CREATE_TIME"));
//                        editText_muchangmingcheng.setText(jsonObject_inner.getString(""));
                        }

                    }catch (Exception e){
                        Log.d("Debug",e.toString());
                    }
                }
            });
        }


        //“保存”按钮
        Button button_save = findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(DetailedInfoActivity.this);
                normalDialog.setMessage("保存成功！");
                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //消息提示用，无操作
                    }
                });
                normalDialog.show();
            }
        });

        //“定位轨迹”按钮
        Button button_getLocation = findViewById(R.id.button_getLocation);
        button_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_map = new Intent(DetailedInfoActivity.this, MapActivity.class);
                intent_map.putExtra("isInerFlag",true);
                startActivity(intent_map);
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
}
