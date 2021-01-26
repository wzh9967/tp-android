package com.tokenbank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.activity.MainActivity;
import com.tokenbank.activity.SplashActivity;
import com.tokenbank.activity.WebBrowserActivity;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.WalletUtil;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.FstWalletUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.wallet.FstWallet;
import com.tokenbank.wallet.WalletInfoManager;

import java.util.List;


public class PKFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = "PKFragment";
    private EditText mEdtWalletPrivateKey;
    private EditText mEdtWalletName;
    private EditText mEdtWalletPwd;
    private EditText mEdtWalletPwdRepeat;
    private EditText mEdtWalletPwdTips;
    private ImageView mImgboxTerms;
    private TextView mTvTerms;
    private TextView mTvImportWallet;
    private TextView mTvAboutPrivateKey;
    private WalletUtil mFstWallet;
    private Bundle bundle;
    private int flag;
    public static BaseFragment newInstance() {
        PKFragment fragment = new PKFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFstWallet =TBController.getInstance().getFstWallet();
        bundle = this.getArguments();
        flag = bundle.getInt("flag");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return ViewUtil.inflatView(inflater, container, R.layout.fragment_pk_importwallet, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    public void onClick(View view) {
        if (view == mTvTerms) {
            gotoServiceTermPage();
        } else if (view == mTvImportWallet) {
            checkPrivateKey();
            if (paramCheck()) {
                importWallet();
            }
        } else if (view == mTvAboutPrivateKey) {
            gotoPrivateKeyIntroPage();
        } else if (view == mImgboxTerms) {
            mImgboxTerms.setSelected(!mImgboxTerms.isSelected());
        }
    }

    private void initView(View view) {
        mEdtWalletPrivateKey = view.findViewById(R.id.edt_wallet_privatekey);
        mEdtWalletName = view.findViewById(R.id.edt_wallet_name);
        mEdtWalletPwd = view.findViewById(R.id.edt_wallet_pwd);
        mEdtWalletPwdRepeat = view.findViewById(R.id.edt_wallet_pwd_repeat);
        mEdtWalletPwdTips = view.findViewById(R.id.edt_pwd_tips);
        mImgboxTerms = view.findViewById(R.id.img_service_terms);
        mImgboxTerms.setOnClickListener(this);
        mTvTerms = view.findViewById(R.id.tv_service_terms);
        mTvTerms.setText(Html.fromHtml(getString(R.string.content_read_service)));
        mTvTerms.setOnClickListener(this);

        mTvImportWallet = view.findViewById(R.id.tv_import_wallet);
        mTvImportWallet.setOnClickListener(this);

        mTvAboutPrivateKey = view.findViewById(R.id.tv_about_privatekey);
        mTvAboutPrivateKey.setOnClickListener(this);

    }

    private void checkPrivateKey() {


    }

    private void gotoServiceTermPage() {
        WebBrowserActivity.startWebBrowserActivity(getActivity(), getString(R.string.titleBar_user_agreement), Constant.service_term_url);
    }

    private boolean paramCheck() {
        String walletPrivateKey = mEdtWalletPrivateKey.getText().toString();
        String walletPwd = mEdtWalletPwd.getText().toString();
        String walletPwdRepeat = mEdtWalletPwdRepeat.getText().toString();
        String walletName = mEdtWalletName.getText().toString();
        boolean readedTerms = mImgboxTerms.isSelected();

        if (TextUtils.isEmpty(walletName)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.enter_hint_wallet_name), "OK");
            return false;
        }

        if (TextUtils.isEmpty(walletPrivateKey)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_title_no_private_key), "OK");
            return false;
        }

        if (!FstWalletUtil.checkWalletPk(walletPrivateKey)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_title_key_format_incorrect), "OK");
            return false;
        }

        if (TextUtils.isEmpty(walletPwd)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_no_password), "OK");
            return false;
        }

        if (TextUtils.isEmpty(walletPwdRepeat)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_no_verify_password), "OK");
            return false;
        }

        if (!TextUtils.equals(walletPwdRepeat, walletPwd)) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_passwords_unmatch), "OK");
            return false;
        }

        if (walletPwd.length() < 8) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_short_password), "OK");
            return false;
        }

        if (!readedTerms) {
            ViewUtil.showSysAlertDialog(getActivity(), getString(R.string.dialog_content_no_read_service), "OK");
            return false;
        }

        return true;
    }

    private void importWallet() {
        final String secret = mEdtWalletPrivateKey.getText().toString();
        final String password = mEdtWalletPwd.getText().toString();
        mFstWallet.importSecret(secret, "",new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if(ret == 0){
                    String secret = extra.getString("secret","");
                    String address = extra.getString("address","");
                    if(secret ==null && address ==null){
                        ToastUtil.toast(getActivity(),getString(R.string.toast_import_wallet_failed));
                        return;
                    } else {
                        if (isWalletExsit(address)) {
                            ToastUtil.toast(getActivity(),getString(R.string.toast_wallet_exists));
                            return;
                        } else {
                            uploadWallet(mEdtWalletName.getText().toString(), FileUtil.getStringContent(password),
                                    secret, address);
                        }
                    }
                }
            }
        });
    }

    private void uploadWallet(final String name, final String hash, final String privateKey,
                              final String address) {
        long walletId = System.currentTimeMillis();
        storeWallet(walletId, name, address, hash, privateKey);
        if(flag == -1){
            gotoMainActivity();
        } else {
            SplashActivity.instance.finish();
            getActivity().finish();
            ToastUtil.toast(getActivity(),getString(R.string.wallet_import_success));
        }
    }

    private void gotoMainActivity() {
        // 添加资产时，进入创建钱包
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        SplashActivity.instance.finish();
        getActivity().finish();
    }

    private void storeWallet(long walletId, String walletName, String address, String walletHash, String privatekey) {
        WalletInfoManager.WData wallet = new WalletInfoManager.WData();
        wallet.wid = walletId;
        wallet.wname = walletName;
        wallet.waddress = address;
        wallet.whash = walletHash;
        wallet.wpk = privatekey;
        wallet.words = "";
        wallet.isBaked = true;
        WalletInfoManager.getInstance().insertWallet(wallet);
    }

    private void gotoPrivateKeyIntroPage() {
        WebBrowserActivity.startWebBrowserActivity(getActivity(), getString(R.string.titleBar_private_key), Constant.privatekey_intro_url);
    }

    private boolean isWalletExsit(String address) {
        List<WalletInfoManager.WData> allWallet = WalletInfoManager.getInstance().getAllWallet();
        if (allWallet == null || allWallet.size() <= 0) {
            return false;
        }
        for (WalletInfoManager.WData walletData : allWallet) {
            if (TextUtils.equals(walletData.waddress, address)) {
                return true;
            }
        }
        return false;
    }
}
