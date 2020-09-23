package com.tokenbank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.tokenbank.R;
import com.tokenbank.config.Constant;
import com.tokenbank.fragment.PKFragment;
import com.tokenbank.fragment.WordsFragment;

public class ImportWalletActivity extends BaseActivity {

    private final static String FROM = "From";
    private int mFlag = 1;

    public final static String TAG = "ImportWalletActivity";

    //跳至chooseWalletImportWay选择点击方式，根据结果跳至其他导入方式
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);
        if (getIntent() != null) {
            mFlag = getIntent().getIntExtra(FROM, 1);
        }
        //选择区块链
        //ChooseWalletBlockActivity.navToActivity(ImportWalletActivity.this, Constant.CHOOSE_BLOCK_REQUEST_CODE);
        //选择导入方式
        ChooseWalletImportWay.navToActivity(ImportWalletActivity.this, Constant.CHOOSE_IMPORTWAY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.CHOOSE_IMPORTWAY_CODE) {
                int postion = data.getIntExtra("position", 0);
                Intent intent = new Intent();
                Log.d(TAG, "onActivityResult: 执行跳转");
                Log.d(TAG, "onActivityResult: posion = "+postion);
                switch (postion) {
                    case 0 :
                        Log.d(TAG, "onActivityResult: 跳转到 密钥导入");
                        FragmentContainerActivity.start(ImportWalletActivity.this, PKFragment.class,intent);
                        break;
                    case 1 :
                        Log.d(TAG, "onActivityResult: 跳转到 助记词导入");
                        FragmentContainerActivity.start(ImportWalletActivity.this, WordsFragment.class,intent);
                        break;
                }
                this.finish();
            } else {
                finish();
            }
        }
    }

    public static void startImportWalletActivity(Context context) {
        Intent intent = new Intent(context, ImportWalletActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
