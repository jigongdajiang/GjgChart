package com.gjg.chart.aty;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gjg.chart.R;
import com.gjg.chart.histogram.HistogramView;

/**
 * @author gaojigong
 * @version V1.0
 * @Description:
 * @date 17/5/22
 */
public class HistogramActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    private HistogramView histogramView;
    private SeekBar seekBar;
    private TextView tv_money;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);
        histogramView = (HistogramView) findViewById(R.id.hv);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(499000);
        tv_money = (TextView) findViewById(R.id.tv_money);
        seekBar.setOnSeekBarChangeListener(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(99000);
                histogramView.initData(new float[]{0.08f, 0.07f, 0.06f, 0.05f});
                histogramView.start(2);
            }
        },2000);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tv_money.setText(String.valueOf(1000 + progress * ((500000 - 1000) / seekBar.getMax())));
        histogramView.changeProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
