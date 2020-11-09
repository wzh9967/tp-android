
package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.tokenbank.R;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.base.WCallback;
import com.tokenbank.config.Constant;
import com.tokenbank.dialog.OrderDetailDialog;
import com.tokenbank.dialog.PwdDialog;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.utils.ViewUtil;
import com.tokenbank.view.TitleBar;

/**
 * 交易表单页面
 */
public class TokenTransferActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "TokenTransferActivity";
    private TitleBar mTitleBar;
    private TextView mTvToken;
    private TextView mTvGas;
    private EditText mEdtWalletAddress, mEdtTransferNum, mEdtTransferRemark;
    private SeekBar seekBar;
    private Button mBtnNext;
    private Double mGasPrice;
    private Double SettingGasPrice;
    private BaseWalletUtil mWalletUtil;
    private WalletInfoManager.WData mWalletData;
    private String mContractAddress ="";
    private String mOriginAddress = "";
    private String mReceiveAddress = "";
    private String mTokenSymbol = "";
    private String mGasLimit;
    private String mValue = "";
    private String mAmount;
    private String mTokenName;
    private boolean defaultToken;
    private int mDecimal;
    private final static String CONTRACT_ADDRESS_KEY = "Contact_Address";
    private final static String RECEIVE_ADDRESS_KEY = "Receive_Address";
    private final static String TOKEN_SYMBOL_KEY = "Token_Symbol";
    private final static String TOKEN_DECIMAL = "Token_Decimal";
    private final static String TOKEN_NAME = "Token_Name";
    private final static String TOEKN_GAS = "Token_Gas";
    private final static String TOEKN_AMOUNT = "Token_Amount";
    private final static String VALUE = "value";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_token);
        if (getIntent() != null) {
            mOriginAddress = getIntent().getStringExtra(RECEIVE_ADDRESS_KEY);
            mContractAddress = getIntent().getStringExtra(CONTRACT_ADDRESS_KEY);
            mTokenSymbol = getIntent().getStringExtra(TOKEN_SYMBOL_KEY);
            mDecimal = getIntent().getIntExtra(TOKEN_DECIMAL, 0);
            mAmount = getIntent().getStringExtra(TOEKN_AMOUNT);
            mValue = getIntent().getStringExtra(VALUE);
            mTokenName = getIntent().getStringExtra(TOKEN_NAME);
        }
        initData();
    }

    private void initData(){
        mWalletData = WalletInfoManager.getInstance().getCurrentWallet();
        if (mWalletData == null) {
            this.finish();
            return;
        }
        mWalletUtil = TBController.getInstance().getWalletUtil();
        defaultToken = true;
        if(!mContractAddress.equals("")){
            this.mDecimal = Integer.parseInt(mWalletUtil.getDataByContract(mContractAddress,"decimal"));
            this.mGasLimit = Constant.Erc20gasLimit;
        } else {
            this.mDecimal = Constant.DefaultDecimal;
            this.mGasLimit = Constant.gasLimit;
        }
        getGasPrice();
        initView();
    }
    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(mTokenName +" "+ getString(R.string.titleBar_transfer));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                TokenTransferActivity.this.finish();
            }
        });
        mEdtWalletAddress = findViewById(R.id.edt_wallet_address);
        mEdtWalletAddress.setText(mOriginAddress);
        mEdtTransferNum = findViewById(R.id.edt_transfer_num);
        mTvGas = findViewById(R.id.tv_transfer_gas);
        mEdtTransferNum.setHint(mAmount+"("+getString(R.string.tip_max)+")");
        if(!mValue.equals("")){
            mEdtTransferNum.setText(mValue);
        }
        mEdtTransferRemark = findViewById(R.id.edt_transfer_remark);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            mContractAddress = data.getStringExtra(CONTRACT_ADDRESS_KEY);
            mTokenSymbol = data.getStringExtra(TOKEN_SYMBOL_KEY);
            mDecimal = data.getIntExtra(TOKEN_DECIMAL, 0);
            defaultToken = TextUtils.equals(Constant.TokenSymbol, mTokenSymbol);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (paramCheck()) {
                    OrderDetailDialog orderDetailDialog = new OrderDetailDialog(TokenTransferActivity.this,
                            new OrderDetailDialog.onConfirmOrderListener() {
                                @Override
                                public void onConfirmOrder() {
                                    verifyPwd();
                                }
                            }, mWalletData.waddress, mEdtWalletAddress.getText().toString(),
                            mDecimal ,SettingGasPrice, mGasLimit, Util.parseDouble(mEdtTransferNum.getText().toString()), 0, mTokenSymbol, defaultToken);
                    orderDetailDialog.show();
                }
                break;
        }
    }

    private void verifyPwd() {
        PwdDialog pwdDialog = new PwdDialog(TokenTransferActivity.this, new PwdDialog.PwdResult() {
            @Override
            public void authPwd(String tag, boolean result) {
                if (TextUtils.equals(tag, "transaction")) {
                    if (result) {
                        pwdRight();
                    } else {
                        ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_order_password_incorrect));
                    }
                }
            }
        }, mWalletData.whash, "transaction");
        pwdDialog.show();
    }

    private void pwdRight() {
        updateBtnToTranferingState();
        TokenTransfer();
    }

    private void TokenTransfer() {
        if(mContractAddress.equals("")){
            sendTransaction();
        } else {
            sendErc20Transaction();
        }
    }
    private void getGasPrice(){
        mWalletUtil.getGasPrice(new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if(ret == 0){
                    mGasPrice = extra.getDouble("GasPrice",0.0);
                    SettingGasPrice = mGasPrice;
                    setGasSeekBar();
                }
            }
        });
    }

    private void setGasSeekBar(){
        mTvGas.setText(Util.calculateGasInToken(mDecimal,mGasLimit, mGasPrice)+" "+Constant.TokenSymbol);
        seekBar = findViewById(R.id.seekBar2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SettingGasPrice = mGasPrice * (100 + progress) / 100.0;
                mTvGas.setText(Util.calculateGasInToken(mDecimal, mGasLimit, SettingGasPrice) + " " + Constant.TokenSymbol);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void sendTransaction() {
        String address = mWalletData.waddress ;
        String secret = mWalletData.wpk ;
        String to = mEdtWalletAddress.getText().toString();
        String note = mEdtTransferRemark.getText().toString();
        String value = mEdtTransferNum.getText().toString();
        GsonUtil data = new GsonUtil("{}");
        data.putString("address",address);
        data.putString("to",to);
        data.putString("secret",secret);
        data.putString("value",Util.fromValue(mDecimal,value));
        data.putString("gasLimit",mGasLimit);
        data.putDouble("gasPrice",SettingGasPrice);
        data.putString("data",note);
        mWalletUtil.sendTransaction(data,new WCallback(){
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if(ret == 0){
                    String hash = extra.getString("hash", "");
                    Log.d("Transaction", "onGetWResult: hash = "+hash);
                    ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_transfer_success));
                    resetTranferBtn();
                    TransactionDetailsActivity.startTransactionDetailActivity(TokenTransferActivity.this, hash,true);
                    TokenTransferActivity.this.finish();
                }else {
                    String err = extra.getString("err", "");
                    Log.d("Transaction", "onGetWResult: err"+err);
                    resetTranferBtn();
                    ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_transfer_failed)+ret);
                }
            }
        });
    }

    private void sendErc20Transaction(){
        String address = mWalletData.waddress ;
        String secret = mWalletData.wpk ;
        String to = mEdtWalletAddress.getText().toString();
        String note = mEdtTransferRemark.getText().toString();
        String value = mEdtTransferNum.getText().toString();
        String contract = mContractAddress;
        GsonUtil data = new GsonUtil("{}");
        data.putString("address",address);
        data.putString("contract",contract);
        data.putString("address",address);
        data.putString("to",to);
        data.putString("secret",secret);
        data.putString("value",Util.fromValue(mDecimal,value));
        data.putString("gasLimit",mGasLimit);
        data.putString("data",note);
        data.putDouble("gasPrice",SettingGasPrice);
        mWalletUtil.sendErc20Transaction(data,new WCallback(){
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if(ret == 0){
                    String hash = extra.getString("hash", "");
                    Log.d("sendErc20Transaction", "onGetWResult: hash = "+hash);
                    resetTranferBtn();
                    TransactionDetailsActivity.startTransactionDetailActivity(TokenTransferActivity.this, hash,true);
                    ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_transfer_success));
                    TokenTransferActivity.this.finish();
                } else {
                    String err = extra.getString("err", "");
                    // ret = -1 发送交易错误     ret = -2 签名错误
                    Log.d("sendErc20Transaction", "onGetWResult: err"+err);
                    resetTranferBtn();
                    ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_transfer_failed)+ret);
                }
            }
        });
    }

    private boolean paramCheck() {
        String address = mEdtWalletAddress.getText().toString();
        String num = mEdtTransferNum.getText().toString();
        if (TextUtils.isEmpty(address)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_no_wallet_address), "OK");
            return false;
        }

        if (TextUtils.equals(address, mWalletData.waddress)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_receive_address_incorrect), "OK");
            return false;
        }

        if (!mWalletUtil.checkWalletAddress(address)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_address_format_incorrect), "OK");
            return false;
        }


        if ((TextUtils.isEmpty(num) || Util.parseDouble(num) <= 0.0f)) {
            ViewUtil.showSysAlertDialog(this, getString(R.string.dialog_content_amount_incorrect), "OK");
            return false;
        }

        if(Util.parseDouble(mAmount) <=Util.parseDouble(num)){
            ViewUtil.showSysAlertDialog(this, getString(R.string.toast_insufficient_balance), "OK");
            return false;
        }
        return true;
    }

    private void updateBtnToTranferingState() {
        mBtnNext.setEnabled(false);
        mBtnNext.setText(getString(R.string.btn_transferring));
    }

    private void resetTranferBtn() {
        mBtnNext.setEnabled(true);
        mBtnNext.setText(getString(R.string.btn_next));
    }

    /**
     * 启动Activity
     *
     * @param context
     */
    public static void startTokenTransferActivity(Context context, String receiveAddress, String contactAddress,
                                                  String mAmount, String tokenName,String tokenSymbol, int decimal, double gas,String value) {
        Intent intent = new Intent(context, TokenTransferActivity.class);
        intent.putExtra(CONTRACT_ADDRESS_KEY, contactAddress);
        intent.putExtra(RECEIVE_ADDRESS_KEY, receiveAddress);
        intent.putExtra(TOKEN_SYMBOL_KEY, tokenSymbol);
        intent.putExtra(TOKEN_DECIMAL, decimal);
        intent.putExtra(TOKEN_NAME, tokenName);
        intent.putExtra(TOEKN_GAS, gas);
        intent.putExtra(TOEKN_AMOUNT, mAmount);
        intent.putExtra(VALUE, value);
        context.startActivity(intent);
    }
}
