package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.fragment.BaseFragment;
import com.tokenbank.fragment.PKFragment;
import com.tokenbank.fragment.WordsFragment;
import com.tokenbank.view.TitleBar;

public class ImportWalletActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;
    private TextView mTvWords;
    private TextView mTvPrivateKey;
    private ViewPager mViewPager;
    private ImportWalletAdapter mAdapter;
    private int flag;

    public final static String TAG = "ImportWalletActivity";
    public final static String IMPORT_FLAG = "flag";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);

        if (getIntent() != null) {
            flag = getIntent().getIntExtra(IMPORT_FLAG, -1);
        }
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view == mTvWords) {
            mViewPager.setCurrentItem(0);
        } else if (view == mTvPrivateKey) {
            mViewPager.setCurrentItem(1);
        }
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_import_wallet));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                ImportWalletActivity.this.finish();
            }
        });

        mTvWords = findViewById(R.id.tv_word);
        mTvWords.setOnClickListener(this);
        mTvPrivateKey = findViewById(R.id.tv_privatekey);
        mTvPrivateKey.setOnClickListener(this);
        mViewPager = findViewById(R.id.viewpager_import_wallet);
        mAdapter = new ImportWalletAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    mTvWords.setTextColor(getResources().getColor(R.color.common_blue));
                    mTvPrivateKey.setTextColor(getResources().getColor(R.color.common_black_fontcolor));
                } else if (position == 1) {
                    mTvWords.setTextColor(getResources().getColor(R.color.common_black_fontcolor));
                    mTvPrivateKey.setTextColor(getResources().getColor(R.color.common_blue));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(0);
        mTvWords.setTextColor(getResources().getColor(R.color.common_blue));
        mTvPrivateKey.setTextColor(getResources().getColor(R.color.common_black_fontcolor));
    }

    /**
     * 判断跳转方式（从原生 还是 Dapp）
     * @param context context
     * @param Flag 0 为Dapp ； 1 为原生 ；
     */
    public static void startImportWalletActivity(Context context,int Flag) {
        Intent intent = new Intent(context, ImportWalletActivity.class);
        intent.putExtra(IMPORT_FLAG, Flag);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    class ImportWalletAdapter extends FragmentPagerAdapter {

        public BaseFragment[] mFragments = new BaseFragment[]{
                WordsFragment.newInstance(),
                PKFragment.newInstance()
        };

        public ImportWalletAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt(IMPORT_FLAG, flag);
            mFragments[position].setArguments(bundle);
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }
}
