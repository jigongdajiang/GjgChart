package com.gjg.chart.aty;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.gjg.chart.R;
import com.gjg.chart.pie.PieChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author gaojigong
 * @version V1.0
 * @Description:
 * @date 17/5/22
 */
public class PieActivity extends AppCompatActivity {
    private PieChartView piechart;
    private TextView txtData;
    private List<Double> values;
    private Random random;
    private List<Integer> colors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);
        piechart = (PieChartView) findViewById(R.id.piechart);
        txtData = (TextView) findViewById(R.id.txtData);
        //初始化颜色集合
        colors = new ArrayList<>();
        colors.add(android.R.color.holo_orange_light);
        colors.add(android.R.color.holo_blue_light);
        colors.add(android.R.color.holo_green_light);
        colors.add(android.R.color.holo_red_light);

        //准备数据集合
        values = new ArrayList<>();
        random = new Random();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshData();
                piechart.setOnItemChangedListener(new PieChartView.OnItemChangedListener() {
                    @Override
                    public void onItemChanged(int index, double value) {
                        txtData.setText("index: " +index+ " value: "+value );
                    }
                });
            }
        },3000);

    }

    private void refreshData() {
        values.clear();
        values.add(random.nextDouble()*100);
        values.add(random.nextDouble()*100);
        values.add(random.nextDouble()*100);
        values.add(random.nextDouble()*100);
        //设置数据和颜色集合
        piechart.setDatas(values,colors);
    }

    public void refresh(View v){
        refreshData();
    }
}
