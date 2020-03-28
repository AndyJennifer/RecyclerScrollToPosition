package com.jennifer.andy.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jennifer.andy.simple.widget.AdjustLinearLayoutManager;
import com.jennifer.andy.simple.widget.AdjustLinearSmoothScroller;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RadioGroup mRadioGroup;
    private SeekBar mSeekPosition;
    private SeekBar mSeekTime;
    private TextView mTvInchTime;
    private TextView mTvPosition;
    private Button mBtnStart;

    private List<String> mStringList;
    private AdjustLinearLayoutManager mLayoutManager;
    private int mScrollType = LinearSmoothScroller.SNAP_TO_ANY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        initData();
    }

    private void findView() {

        mRecyclerView = findViewById(R.id.recycler);
        mRadioGroup = findViewById(R.id.radio_group);
        mSeekPosition = findViewById(R.id.sb_position);
        mSeekTime = findViewById(R.id.seek_bar);
        mTvInchTime = findViewById(R.id.tv_time);
        mTvPosition = findViewById(R.id.tv_position);
        mBtnStart = findViewById(R.id.btn_start);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mLayoutManager = new AdjustLinearLayoutManager(MainActivity.this);
                switch (checkedId) {
                    case R.id.rb_normal://default
                        mScrollType = LinearSmoothScroller.SNAP_TO_ANY;
                        break;
                    case R.id.rb_top://top
                        mScrollType = LinearSmoothScroller.SNAP_TO_START;
                        break;
                    case R.id.rb_bottom://bottom
                        mScrollType = LinearSmoothScroller.SNAP_TO_END;
                        break;

                }
                mLayoutManager.setScrollType(mScrollType);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(new SimpleTextAdapter(MainActivity.this, mStringList));
                mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
            }
        });

        mSeekPosition.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvPosition.setText(getString(R.string.to_position, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLayoutManager.setMillisecondsPerInch(progress);
                mTvInchTime.setText(getString(R.string.ms_per_inch, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mSeekPosition.getProgress();
                int perInchTime = mSeekTime.getProgress();
                if (position <= 0) {
                    Toast.makeText(MainActivity.this, "Position cannot be zero", Toast.LENGTH_SHORT).show();
                } else if (perInchTime <= 10) {
                    Toast.makeText(MainActivity.this, "Time cannot be less than ten milliseconds", Toast.LENGTH_SHORT).show();
                } else {
                    mRecyclerView.smoothScrollToPosition(position);
                }
            }
        });


    }


    private void initData() {
        mStringList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mStringList.add("this is data " + i);
        }
        mLayoutManager = new AdjustLinearLayoutManager(this);
        mLayoutManager.setScrollType(mScrollType);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new SimpleTextAdapter(this, mStringList));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mSeekPosition.setMax(mStringList.size());
        mSeekTime.setProgress((int) AdjustLinearSmoothScroller.DEFAULT_MILLISECONDS_PER_INCH);
        mTvPosition.setText(getString(R.string.to_position, 0));
    }

}
