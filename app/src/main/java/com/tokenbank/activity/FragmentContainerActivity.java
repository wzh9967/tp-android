package com.tokenbank.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.tokenbank.R;
import com.tokenbank.base.SysApplication;
import com.tokenbank.fragment.BaseFragment;

public class FragmentContainerActivity extends BaseActivity {

    private static final String TAG = "FragmentContainerActivity";

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        SysApplication.addActivity(this);
        Log.d(TAG, "onCreate: 跳转到了本容器");
        try {
            String frag = getIntent().getStringExtra("__fragment__");
            Log.d(TAG, "onCreate: fragment = "+frag);
            Fragment fragment = (Fragment) Class.forName(frag).newInstance();
            Log.d(TAG, "onCreate: fragment = "+fragment);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } catch (Throwable e) {
            e.printStackTrace();
            finish();
        }
    }

    @SuppressLint("LongLogTag")
    public static void start(Context ctx, Class<? extends BaseFragment> fragment,Intent intent) {
        Log.d(TAG, "start: 执行start");
        start(ctx, fragment.getName(),intent);
    }

    @SuppressLint("LongLogTag")
    public static void start(Context ctx, String fragment, Intent intent) {
        Log.d(TAG, "start: 执行 跳转"+fragment);
        ctx.startActivity(new Intent(intent)
                .setClass(ctx, FragmentContainerActivity.class)
                .putExtra("__fragment__", fragment)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }
}
