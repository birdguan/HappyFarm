package com.example.birdguan.cownet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.birdguan.cownet.utils.OkHttpManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class MsgManagerActivity extends Activity {
    private List<String> list_theme = new ArrayList<String>();
    private List<String> list_info = new ArrayList<String>();
    private List<String> list_name = new ArrayList<String>();
    private List<String> list_data = new ArrayList<String>();
    private ListView listView_infoManager;
    private int size_all;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msgmanager);
        setCustomActionBar();
        listView_infoManager = findViewById(R.id.listView_infoManager);
        MenuActivity menuActivity = new MenuActivity();
        menuActivity.hasRead = true;
        final SharedPreferences sharedPreferences_infoManager = getSharedPreferences("MSG",MODE_PRIVATE);
        size_all = sharedPreferences_infoManager.getInt("sizeAll", 0);
        for (int i = 0; i < size_all; i++){
            list_theme.add(sharedPreferences_infoManager.getString("themeAll_" + i, null));
            list_info.add(sharedPreferences_infoManager.getString("infoAll_" + i, null));
            list_name.add(sharedPreferences_infoManager.getString("nameAll_" + i, null));
            list_data.add(sharedPreferences_infoManager.getString("dataAll_" + i, null));
        }

        List<Map<String, Object>> list_infoitem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < list_theme.size(); i++){
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("theme", list_theme.get(i));
            showitem.put("info", list_info.get(i));
            showitem.put("name", list_name.get(i));
            showitem.put("data", list_data.get(i));
            list_infoitem.add(showitem);
        }
        SimpleAdapter simpleAdapter_infoManager = new SimpleAdapter(getApplicationContext(),
                list_infoitem,
                R.layout.custom_listview_infomanager,
                new String[]{"theme", "info", "name", "data"},
                new int[]{R.id.textView_infoTheme, R.id.textView_info, R.id.textView_infoName, R.id.textView_infoData}
                );
        listView_infoManager.setAdapter(simpleAdapter_infoManager);
        listView_infoManager.setDivider(null);
        //长按删除
        listView_infoManager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.textView_info);
                String msgtoDelete = textView.getText().toString();
                Log.d("Debug", "要删除的信息: " + msgtoDelete);
                SharedPreferences sharedPreferences_deleteMsg = getSharedPreferences("MSG", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences_deleteMsg.edit();
                editor.putString("msgtoDelete", msgtoDelete);
                editor.apply();
                initPopupWindow(view.findViewById(R.id.textView_infoTheme));
                return true;
            }
        });
        listView_infoManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView_theme = view.findViewById(R.id.textView_infoTheme);
                TextView textView_info = view.findViewById(R.id.textView_info);
                TextView textView_name = view.findViewById(R.id.textView_infoName);
                TextView textView_data = view.findViewById(R.id.textView_infoData);
                Intent intent_detailedMsg = new Intent(MsgManagerActivity.this, DetailedMsgActivity.class);
                intent_detailedMsg.putExtra("theme", textView_theme.getText().toString());
                intent_detailedMsg.putExtra("info", textView_info.getText().toString());
                intent_detailedMsg.putExtra("name", textView_name.getText().toString());
                intent_detailedMsg.putExtra("data", textView_data.getText().toString());
                startActivity(intent_detailedMsg);
            }
        });


    }

    private void initPopupWindow(View v){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_popupwindow_deletemsg, null, false);
        Button button_deleteMsg = view.findViewById(R.id.button_deleteMsg);
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
        popupWindow.showAsDropDown(v,0,0);
        //删除按钮
        button_deleteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences_deleteMsg = getSharedPreferences("MSG", MODE_PRIVATE);
                String msgtoDelete = sharedPreferences_deleteMsg.getString("msgtoDelete", null);
                SharedPreferences sharedPreferences_infoManager = getSharedPreferences("MSG",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences_infoManager.edit();
                size_all = sharedPreferences_infoManager.getInt("sizeAll", 0);
                for (int i = 0; i < size_all; i++){
                    list_theme.add(sharedPreferences_infoManager.getString("themeAll_" + i, null));
                    list_info.add(sharedPreferences_infoManager.getString("infoAll_" + i, null));
                    list_name.add(sharedPreferences_infoManager.getString("nameAll_" + i, null));
                    list_data.add(sharedPreferences_infoManager.getString("dataAll_" + i, null));
                }
                Log.d("Debug", "删除前消息：" + list_info);
                Log.d("Debug", "取出的要删除的信息： " + msgtoDelete);
                int index = list_info.indexOf(msgtoDelete);
                list_theme.remove(index);
                list_info.remove(index);
                list_name.remove(index);
                list_data.remove(index);
                Log.d("Debug", "删除后消息： " + list_info);

                //保存所有数据

                size_all--;
                editor.putInt("sizeAll", size_all);
                for (int i = 0; i < size_all; i++){
                    editor.remove("themeAll_" + i);
                    editor.remove("infoAll_" + i);
                    editor.remove("nameAll_" + i);
                    editor.remove("dataAll_" + i);
                    editor.putString("themeAll_" + i, list_theme.get(i));
                    editor.putString("infoAll_" + i, list_info.get(i));
                    editor.putString("nameAll_" + i, list_name.get(i));
                    editor.putString("dataAll_" + i, list_data.get(i));

                }
                editor.apply();

                //弹框提示删除成功
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(MsgManagerActivity.this);
                normalDialog.setMessage("删除成功");
                normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //更新消息显示
                        Intent intent = new Intent(MsgManagerActivity.this, MsgManagerActivity.class);
                        startActivity(intent);
                    }
                });
                normalDialog.show();
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
                Intent intent = new Intent(MsgManagerActivity.this, MenuActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MsgManagerActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void setCustomActionBar(){
        android.app.ActionBar.LayoutParams layoutParams = new android.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this).inflate(R.layout.custom_actionbar, null);
        TextView textView = mActionBarView.findViewById(R.id.textView_title);
        textView.setText("消息管理");
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
                Intent intent = new Intent(MsgManagerActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
