package com.jennifer.andy.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private Button mBtnStart;
    private List<String> mStringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        initData();
        initView();
    }

    private void findView() {
        mRecyclerView = findViewById(R.id.recycler);
        mEditText = findViewById(R.id.et_number);
        mBtnStart = findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(MainActivity.this, "请输入滚动的位置", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(number) > mStringList.size() - 1) {
                    Toast.makeText(MainActivity.this, "请输入正确的范围0到100", Toast.LENGTH_SHORT).show();
                } else {
                    mRecyclerView.smoothScrollToPosition(Integer.parseInt(number));
                }
            }
        });
    }

    private void initView() {
//        mRecyclerView.setLayoutManager(new LinearLayoutManagerWithScrollTop(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new SimpleTextAdapter(this, mStringList));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void initData() {
        mStringList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mStringList.add("这是第" + i + "条数据");
        }

    }
}
