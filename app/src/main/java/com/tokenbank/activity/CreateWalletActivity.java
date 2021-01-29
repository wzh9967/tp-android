package com.tokenbank.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.base.WalletUtil;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.wallet.WalletInfoManager;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;

public class CreateWalletActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "CreateWalletActivity";
    private TitleBar mTitleBar;
    private EditText mEdtWalletName, mEdtWalletPwd, mEdtWalletPwdConfirm, mEdtWalletTips;
    private ImageView mImgServiceTerms;
    private TextView mTvServiceTerms;
    private Button mBtnConfirm;
    private WalletUtil walletUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet_new);
        initView();
        walletUtil =TBController.getInstance().getFstWallet();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(R.string.btn_create_wallet);
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });
        mEdtWalletName = findViewById(R.id.edt_wallet_name);
        mEdtWalletPwd = findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwdConfirm = findViewById(R.id.edt_wallet_pwd_confirm);
        mEdtWalletTips = findViewById(R.id.edt_wallet_tips);

        mImgServiceTerms = findViewById(R.id.img_service_terms);
        mImgServiceTerms.setOnClickListener(this);
        mTvServiceTerms = findViewById(R.id.tv_service_terms);
        mTvServiceTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvServiceTerms.setOnClickListener(this);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                if (paramCheck()) {
                    String walletName = mEdtWalletName.getText().toString();
                    String walletPwd = mEdtWalletPwd.getText().toString();
                    createMocWallet(walletName, walletPwd);
                }
                break;
            case R.id.img_service_terms:
                mImgServiceTerms.setSelected(!mImgServiceTerms.isSelected());
                break;
            case R.id.tv_service_terms:
                gotoServiceTermPage();
                break;
        }
    }

    public static void navToActivity(Context context, int request) {
        if (!(context instanceof BaseActivity)) {
            return;
        }
        Intent intent = new Intent(context, CreateWalletActivity.class);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) context).startActivityForResult(intent, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private boolean paramCheck() {

        String walletName = mEdtWalletName.getText().toString();
        String walletPwd = mEdtWalletPwd.getText().toString();
        String walletPwdRepeat = mEdtWalletPwdConfirm.getText().toString();
        boolean readedTerms = mImgServiceTerms.isSelected();

        if (TextUtils.isEmpty(walletName)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_name), "OK");
            return false;
        }
        if (TextUtils.isEmpty(walletPwd)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_password), "OK");
            return false;
        }

        if (TextUtils.isEmpty(walletPwdRepeat)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_verify_password), "OK");
            return false;
        }

        if (!TextUtils.equals(walletPwdRepeat, walletPwd)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_passwords_unmatch), "OK");
            return false;
        }
        if (walletPwd.length() < 8) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_short_password), "OK");
            return false;
        }
        if (!readedTerms) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_read_service), "OK");
            return false;
        }

        return true;
    }

    private void createMocWallet(final String walletName, final String walletPwd) {
        setBtnStateToCreating();
        walletUtil.createWallet(new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if(ret == 0){
                    String secret = extra.getString("secret","");
                    String address = extra.getString("address","");
                    String words = extra.getString("words","");
                    if (secret != null && address != null && words != null) {
                        String hash = FileUtil.getStringContent(walletPwd);
                        recordWallet(walletName, hash, secret, words, mEdtWalletTips.getText().toString(),
                                address);
                    } else {
                        resetBtn();
                        ToastUtil.toast(CreateWalletActivity.this, getString(R.string.btn_create_wallet_fail));
                    }
                } else {
                    resetBtn();
                    ToastUtil.toast(CreateWalletActivity.this, getString(R.string.btn_create_wallet_fail));
                }
            }
        });
    }

    //save
    private void recordWallet(final String name, final String hash, final String sectet,
                              final String words, String tips, final String address) {
        long walletID = System.currentTimeMillis();
        storeWallet(walletID, name, address, hash, sectet, words, tips);
        ToastUtil.toast(CreateWalletActivity.this, getString(R.string.toast_wallet_created));
        gotoBakup();
    }

    private void storeWallet(long walletId, String walletName, String address, String walletHash, String privatekey, String words ,String tips) {
        WalletInfoManager.WData wallet = new WalletInfoManager.WData();
        wallet.wid = walletId;
        wallet.wname = walletName;
        wallet.waddress = address;
        wallet.whash = walletHash;
        wallet.wpk = privatekey;
        wallet.words = words;
        wallet.tips = tips;
        WalletInfoManager.getInstance().insertWallet(wallet);
    }

    //view
    private void resetBtn() {
        mBtnConfirm.setText(getString(R.string.btn_create_wallet_done));
        mBtnConfirm.setEnabled(true);
    }

    private void setBtnStateToCreating() {
        mBtnConfirm.setText(getString(R.string.btn_creating_wallet));
        mBtnConfirm.setEnabled(false);
    }

    private void gotoBakup() {
        WalletInfoManager.WData walletData = WalletInfoManager.getInstance().getCurrentWallet();
        //也可以改成密钥
        if (!TextUtils.isEmpty(walletData.words)) {
            StartBakupActivity.startBakupWalletStartActivity(CreateWalletActivity.this, walletData.waddress, 2);
        }
        SplashActivity.instance.finish();
        this.finish();
    }

    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(this, getString(R.string.titleBar_service_terms), Constant.service_term_url);
    }
}
