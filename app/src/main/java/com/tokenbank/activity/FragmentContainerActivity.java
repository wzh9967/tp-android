package com.tokenbank.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.tokenbank.R;
import com.tokenbank.fragment.BaseFragment;

public class FragmentContainerActivity extends BaseActivity {

    private static final String TAG = "FragmentContainerActivity";

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        try {
            String frag = getIntent().getStringExtra("__fragment__");
            Fragment fragment = (Fragment) Class.forName(frag).newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } catch (Throwable e) {
            e.printStackTrace();
            finish();
        }
    }

    @SuppressLint("LongLogTag")
    public static void start(Context ctx, Class<? extends BaseFragment> fragment,Intent intent) {
        start(ctx, fragment.getName(),intent);
    }

    @SuppressLint("LongLogTag")
    public static void start(Context ctx, String fragment, Intent intent) {
        ctx.startActivity(new Intent(intent)
                .setClass(ctx, FragmentContainerActivity.class)
                .putExtra("__fragment__", fragment)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }
}
