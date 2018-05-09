package com.example.birdguan.cownet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MonitDataActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitdata);

        //发情数据
        TextView textView_oestrusData = findViewById(R.id.textView_oestrusData);
        textView_oestrusData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_oestusData = new Intent(MonitDataActivity.this, EstrusActivity.class);
                startActivity(intent_oestusData);
            }
        });
    }
}
