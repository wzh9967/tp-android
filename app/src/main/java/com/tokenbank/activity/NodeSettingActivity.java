package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tokenbank.R;
import com.tokenbank.view.TitleBar;

public class NodeSettingActivity extends BaseActivity implements View.OnClickListener, TitleBar.TitleBarClickListener{

    private static final String TAG = "NodeSettingActivity" ;
    private TitleBar mTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_node);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle(getString(R.string.title_SettingNode));
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setRightTextColor(R.color.white);
        mTitleBar.setTitleBarClickListener(this);
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLeftClick(View view) {

    }

    @Override
    public void onRightClick(View view) {

    }

    @Override
    public void onMiddleClick(View view) {

    }

    public static void startNodeSettingActivity(Context from) {
        Intent intent = new Intent(from, NodeSettingActivity.class);
        intent.addFlags(from instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }
}
