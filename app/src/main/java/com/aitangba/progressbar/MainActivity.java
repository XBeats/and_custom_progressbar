package com.aitangba.progressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LINE_WIDTH = 20; //px

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressbarView progressbar = (ProgressbarView) findViewById(R.id.progressbar);
        progressbar.setLineWidth(LINE_WIDTH);
        progressbar.setStepDistance(4);
        progressbar.setRefreshTime(15);
        progressbar.setAngle(60);

        final TextView descText = (TextView) findViewById(R.id.tv_desc);
        final TextView progressText = (TextView) findViewById(R.id.tv_progress);
        SeekBar seekBarProgress = (SeekBar) findViewById(R.id.seekBar_progress);
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                descText.setText(progress + "%");
                progressbar.setProgress(progress);
                progressText.setText("当前进度" + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarProgress.setProgress(50);

        final TextView stepDistanceText = (TextView) findViewById(R.id.tv_step_distance);
        SeekBar stepDistanceSeekBar = (SeekBar) findViewById(R.id.seekBar_step_distance);
        stepDistanceSeekBar.setMax(2 * LINE_WIDTH);
        stepDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressbar.setStepDistance(progress);
                stepDistanceText.setText("移动间距" + progress + "px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        stepDistanceSeekBar.setProgress(4);

        final TextView refreshTimeText = (TextView) findViewById(R.id.tv_refresh_time);
        SeekBar refreshTimeSeekBar = (SeekBar) findViewById(R.id.seekBar_refresh_time);
        refreshTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressbar.setRefreshTime(progress);
                refreshTimeText.setText("刷新频率" + progress + "ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        refreshTimeSeekBar.setProgress(15);

        final TextView angleText = (TextView) findViewById(R.id.tv_angle);
        SeekBar angleSeekBar = (SeekBar) findViewById(R.id.seekBar_angle);
        angleSeekBar.setMax(89);
        angleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressbar.setAngle(progress);
                angleText.setText("倾斜角度" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        angleSeekBar.setProgress(60);
    }
}
