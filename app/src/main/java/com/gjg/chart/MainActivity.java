package com.gjg.chart;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.gjg.chart.aty.HistogramActivity;
import com.gjg.chart.aty.LineActivity;
import com.gjg.chart.aty.PieActivity;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private RelativeLayout rl_histogram;
    private RelativeLayout rl_line;
    private RelativeLayout rl_pie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView() {
        rl_histogram = (RelativeLayout) findViewById(R.id.rl_histogram);
        rl_line = (RelativeLayout) findViewById(R.id.rl_line);
        rl_pie = (RelativeLayout) findViewById(R.id.rl_pie);

        rl_histogram.setOnClickListener(this);
        rl_line.setOnClickListener(this);
        rl_pie.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.rl_histogram:
                startActivity(new Intent(this, HistogramActivity.class));
                break;
            case R.id.rl_line:
                startActivity(new Intent(this, LineActivity.class));
                break;
            case R.id.rl_pie:
                startActivity(new Intent(this, PieActivity.class));
                break;
        }
    }
}
