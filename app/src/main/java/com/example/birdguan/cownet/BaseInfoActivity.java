package com.example.birdguan.cownet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class BaseInfoActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baseinfo);
        TextView textView_femaleInfo = findViewById(R.id.textView_femaleinfo);
        TextView textView_beefInfo = findViewById(R.id.textView_beefinfo);
        TextView textView_maleInfo = findViewById(R.id.textView_maleinfo);
        TextView textView_calfInfo = findViewById(R.id.textView_calfinfo);
        //母牛信息
        textView_femaleInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_femalInfo = new Intent(BaseInfoActivity.this, InfoOverviewActivity.class);
                intent_femalInfo.putExtra("cowType",1 );
                startActivity(intent_femalInfo);
            }
        });
        //育肥牛信息
        textView_beefInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_beef = new Intent(BaseInfoActivity.this, InfoOverviewActivity.class);
                intent_beef.putExtra("cowType", 2);
                startActivity(intent_beef);
            }
        });
        //种公牛信息
        textView_maleInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_maleInfo = new Intent(BaseInfoActivity.this, InfoOverviewActivity.class);
                intent_maleInfo.putExtra("cowType", 3);
                startActivity(intent_maleInfo);
            }
        });
        //犊牛信息
        textView_calfInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_calfInfo = new Intent(BaseInfoActivity.this, InfoOverviewActivity.class);
                intent_calfInfo.putExtra("cowType", 4);
                startActivity(intent_calfInfo);
            }
        });
    }
}
