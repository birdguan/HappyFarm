package com.example.birdguan.cownet;

import android.animation.Animator;
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
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Request;

public class NewEstrusActivity extends Activity {
    private List<Integer> list_slightExerciseCounts = new ArrayList<Integer>();
    private List<Integer> list_middleExerciseCounts = new ArrayList<Integer>();
    private List<Integer> list_strenuousExerciseCounts = new ArrayList<Integer>();
    private List<String> list_estrusRecordTime = new ArrayList<String>();
    LineChartView lineChartView;
    LinearLayout linearLayout_estrusLegend;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estrus_new);
        setCustomActionBar();
        lineChartView = new LineChartView(getApplicationContext());
        lineChartView = findViewById(R.id.chart_estrus);
        linearLayout_estrusLegend = findViewById(R.id.linearLayout_estrusLegend);
        Intent intent = getIntent();
        list_estrusRecordTime = intent.getStringArrayListExtra("estrusRecordTime");
        list_slightExerciseCounts = intent.getIntegerArrayListExtra("estrusSlightCount");
        list_middleExerciseCounts = intent.getIntegerArrayListExtra("estrusMiddleCount");
        list_strenuousExerciseCounts = intent.getIntegerArrayListExtra("estrusStrenuousCount");
        if (list_estrusRecordTime.size() == 0) {
            linearLayout_estrusLegend.setVisibility(View.INVISIBLE);
            lineChartView.setVisibility(View.INVISIBLE);
        }else {

            List<PointValue> list_estrus_slight = new ArrayList<PointValue>();
            List<PointValue> list_estrus_middle = new ArrayList<PointValue>();
            List<PointValue> list_strenuous = new ArrayList<PointValue>();
            for (int i = 0; i < list_estrusRecordTime.size(); i++) {
                PointValue pointValue_slight = new PointValue();
                pointValue_slight.set(i + 1, list_slightExerciseCounts.get(i));
                pointValue_slight.setLabel(list_estrusRecordTime.get(i) + ": 轻微运动" + list_slightExerciseCounts.get(i) + "次");
                list_estrus_slight.add(pointValue_slight);

                PointValue pointValue_middle = new PointValue();
                pointValue_middle.set(i + 1, list_middleExerciseCounts.get(i));
                pointValue_middle.setLabel(list_estrusRecordTime.get(i) + ": 中度运动" + list_middleExerciseCounts.get(i) + "次");
                list_estrus_middle.add(pointValue_middle);

                PointValue pointValue_strenuous = new PointValue();
                pointValue_strenuous.set(i + 1, list_strenuousExerciseCounts.get(i));
                pointValue_strenuous.setLabel(list_estrusRecordTime.get(i) + ": 剧烈运动" + list_strenuousExerciseCounts.get(i) + "次");
                list_strenuous.add(pointValue_strenuous);

            }
            //轻微运动线
            Line line_slight = new Line(list_estrus_slight).setColor(Color.GREEN).setCubic(true);
            line_slight.setStrokeWidth(1);
            line_slight.setPointRadius(3);
            line_slight.setFilled(true);
            line_slight.setHasLabelsOnlyForSelected(true);

            //中度运动线
            Line line_middle = new Line(list_estrus_middle).setColor(Color.BLUE).setCubic(true);
            line_middle.setStrokeWidth(1);
            line_middle.setPointRadius(3);
            line_middle.setFilled(true);
            line_middle.setHasLabelsOnlyForSelected(true);

            //剧烈运动线
            Line line_strenuous = new Line(list_strenuous).setColor(Color.RED).setCubic(true);
            line_strenuous.setStrokeWidth(1);
            line_strenuous.setPointRadius(3);
            line_strenuous.setFilled(true);
            line_strenuous.setHasLabelsOnlyForSelected(true);

            List<Line> list_line = new ArrayList<Line>();
            list_line.add(line_slight);
            list_line.add(line_middle);
            list_line.add(line_strenuous);
            LineChartData data = new LineChartData();
            Axis axisX = new Axis();
            axisX.setName("时间");
            Axis axisY = new Axis();
            axisY.setName("次数");
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
            data.setLines(list_line);
            lineChartView.setVisibility(View.VISIBLE);
            lineChartView.setInteractive(true);
            lineChartView.setScrollEnabled(true);
            lineChartView.setValueSelectionEnabled(true);
            lineChartView.startDataAnimation(800);
            lineChartView.setLineChartData(data);
            linearLayout_estrusLegend.setVisibility(View.VISIBLE);
        }
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
        textView.setText("运动详情");
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
//                Intent intent = new Intent(NewEstrusActivity.this, EstrusActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
            }
        });
    }
}
