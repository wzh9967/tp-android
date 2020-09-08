package com.tokenbank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.tokenbank.R;

import com.tokenbank.base.BlockChainData;
import com.tokenbank.base.TBController;
import com.tokenbank.config.Constant;
import com.tokenbank.fragment.BaseFragment;
import com.tokenbank.fragment.PKFragment;
import com.tokenbank.fragment.WordsFragment;
import com.tokenbank.view.TitleBar;


//添加keystore导入
public class ImportWalletActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;
    private TextView mTvWords;
    private TextView mTvPrivateKey;
    private ViewPager mViewPager;
    private ImportWalletAdapter mAdapter;
    private final static String FROM = "From";
    private final static String BLOCK_ID = "BlockId";
    private BlockChainData.Block mBlock;
    private int mFlag = 1;
    private int mBlockChainId;

    public final static String TAG = "ImportWalletActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        if (getIntent() != null) {
            mFlag = getIntent().getIntExtra(FROM, 1);
        }
        ChooseWalletBlockActivity.navToActivity(ImportWalletActivity.this, Constant.CHOOSE_BLOCK_REQUEST_CODE);
    }

    @Override
    public void onClick(View view) {
        if (view == mTvWords) {
            mViewPager.setCurrentItem(0);
        } else if (view == mTvPrivateKey) {
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.CHOOSE_BLOCK_REQUEST_CODE) {
                if(data == null) {
                    this.finish();
                    return;
                }
                mBlock = data.getParcelableExtra(Constant.BLOCK_KEY);
                if (mBlock == null) {
                    this.finish();
                } else {
                    if (mBlock.hid == TBController.SWT_INDEX) {
                        Intent intent = new Intent();
                        intent.putExtra(PKFragment.BLOCK, mBlock);
                        FragmentContainerActivity.start(ImportWalletActivity.this,
                                PKFragment.class, intent);
                        this.finish();
                    } else {
                        finish();
                    }
                }
            }
        } else {
            this.finish();
        }

    }


    public static void startImportWalletActivity(Context context, int blockChainId) {

        Intent intent = new Intent(context, ImportWalletActivity.class);
        intent.putExtra(BLOCK_ID, blockChainId);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startImportWalletActivity(Context context) {
        Intent intent = new Intent(context, ImportWalletActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    class ImportWalletAdapter extends FragmentPagerAdapter {

        public BaseFragment[] mFragments = new BaseFragment[]{
                WordsFragment.newInstance(mFlag, mBlock),
                PKFragment.newInstance(mFlag, mBlock)
        };

        public ImportWalletAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }
}
