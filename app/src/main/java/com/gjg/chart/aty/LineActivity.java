package com.gjg.chart.aty;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.gjg.chart.R;
import com.gjg.chart.line.LineChartView;
import com.gjg.chart.line.LineData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author gaojigong
 * @version V1.0
 * @Description:
 * @date 17/5/22
 */
public class LineActivity extends AppCompatActivity {
    private LineChartView lineChart;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        lineChart = (LineChartView) findViewById(R.id.lineChart);
        refreshDataMouth();
        lineChart.setOnTabSelectedListener(new LineChartView.onTabSelectedListener() {
            @Override
            public void onTabSelected(int position, String xValue, List<Float> yData) {
                Toast.makeText(LineActivity.this,"position="+position + "-- xValue="+xValue + "--yData="+yData.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void refreshDataMouth() {
        //准备X轴的数据
        List<String> xValues = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            xValues.add((i + 1) + "");
        }
        //准备折线数据集合
        List<LineData> lineDatas = new ArrayList<>();
        LineData lineData1 = new LineData();
        lineData1.setLineColor(Color.parseColor("#FF4657"));
        List<Float> lineData1Values = new ArrayList<>();
        for(int i = 0;i<xValues.size();i++){
            if(i<2){
                lineData1Values.add(0.0f);
            }else{
                Random r = new Random();
                Float z = r.nextFloat() * 100;
                lineData1Values.add(z);
            }
        }
        lineData1.setLineValues(lineData1Values);
        lineDatas.add(lineData1);

        LineData lineData2 = new LineData();
        lineData2.setLineColor(Color.parseColor("#F6AB00"));
        List<Float> lineData2Values = new ArrayList<>();
        for(int i = 0;i<xValues.size();i++){
            if(i<2){
                lineData2Values.add(0.0f);
            }else{
                Random r = new Random();
                Float z = r.nextFloat() * 100;
                lineData2Values.add(z);
            }
        }
        lineData2.setLineValues(lineData2Values);
        lineDatas.add(lineData2);

        LineData lineData3 = new LineData();
        lineData3.setLineColor(Color.parseColor("#37B2F3"));
        List<Float> lineData3Values = new ArrayList<>();
        for(int i = 0;i<xValues.size();i++) {
            if (i < 2) {
                lineData3Values.add(0.0f);
            } else {
                Random r = new Random();
                Float z = r.nextFloat() * 100;
                lineData3Values.add(z);
            }
        }
        lineData3.setLineValues(lineData3Values);
        lineDatas.add(lineData3);

        LineData lineData4 = new LineData();
        lineData4.setLineColor(Color.parseColor("#1AAA1F"));
        List<Float> lineData4Values = new ArrayList<>();
        for(int i = 0;i<xValues.size();i++){
            if(i<2){
                lineData4Values.add(0.0f);
            }else{
                Random r = new Random();
                Float z = r.nextFloat() * 100;
                lineData4Values.add(z);
            }
        }
        lineData4.setLineValues(lineData4Values);
        lineDatas.add(lineData4);

        //设置折线数据
        try {
            lineChart.setDataResource(xValues,lineDatas,8,"月");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void refreshDataDay() {
        List<String> xValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            xValues.add((i + 1) + "");
        }
        xValues.add("昨日");
        xValues.add("今日");
        List<LineData> lineDatas = new ArrayList<>();
        LineData lineData1 = new LineData();
        lineData1.setLineColor(Color.parseColor("#FF4657"));
        List<Float> lineData1Values = new ArrayList<>();
        for(int i = 0;i<xValues.size();i++){
            Random r = new Random();
            Float z = r.nextFloat() * 1000000000;
            lineData1Values.add(z);
        }
        lineData1.setLineValues(lineData1Values);
        lineDatas.add(lineData1);

        LineData lineData2 = new LineData();
        lineData2.setLineColor(Color.parseColor("#F6AB00"));
        List<Float> lineData2Values = new ArrayList<>();
        for(int i = 0;i<xValues.size();i++){
            Random r = new Random();
            Float z = r.nextFloat() * 100;
            lineData2Values.add(z);
        }
        lineData2.setLineValues(lineData2Values);
        lineDatas.add(lineData2);

        LineData lineData3 = new LineData();
        lineData3.setLineColor(Color.parseColor("#37B2F3"));
        List<Float> lineData3Values = new ArrayList<>();
        for(int i = 0;i<xValues.size();i++){
            Random r = new Random();
            Float z = r.nextFloat() * 100;
            lineData3Values.add(z);
        }
        lineData3.setLineValues(lineData3Values);
        lineDatas.add(lineData3);

        LineData lineData4 = new LineData();
        lineData4.setLineColor(Color.parseColor("#1AAA1F"));
        List<Float> lineData4Values = new ArrayList<>();
        for(int i = 0;i<xValues.size();i++){
            Random r = new Random();
            Float z = r.nextFloat() * 100;
            lineData4Values.add(z);
        }
        lineData4.setLineValues(lineData4Values);
        lineDatas.add(lineData4);

        try {
            lineChart.setDataResource(xValues,lineDatas,3,"日");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void day(View v){
        refreshDataDay();
    }
    public void mouth(View v){
        refreshDataMouth();
    }
}
