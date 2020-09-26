
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
import android.widget.TextView;

import com.android.jccdex.app.base.JCallback;
import com.android.jccdex.app.moac.MoacWallet;
import com.android.jccdex.app.util.JCCJson;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class TokenTransferActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = "TokenTransferActivity";
    private TitleBar mTitleBar;
    private TextView mTvToken;
    private TextView mTvGas;
    private EditText mEdtWalletAddress, mEdtTransferNum, mEdtTransferRemark;
    private Button mBtnNext;
    private double mGasPrice = 0.0f;
    private BaseWalletUtil mWalletUtil;
    private WalletInfoManager.WData mWalletData; //当前使用哪个钱包转账
    private double mGas;
    private String mContractAddress;
    private String mOriginAddress;
    private String mReceiveAddress;
    private String mTokenSymbol;
    private double mAmount;
    private boolean defaultToken;
    private int mDecimal = 0;
    private int mBlockChain;
    private String gasPrice;
    private final static String CONTRACT_ADDRESS_KEY = "Contact_Address";
    private final static String RECEIVE_ADDRESS_KEY = "Receive_Address";
    private final static String TOKEN_SYMBOL_KEY = "Token_Symbol";
    private final static String TOKEN_DECIMAL = "Token_Decimal";
    private final static String TOEKN_GAS = "Token_Gas";
    private final static String TOEKN_AMOUNT = "Token_Amount";

    private MoacWallet mMoacWallet;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_token);
        mMoacWallet = MoacWallet.getInstance();
        mMoacWallet.init(this);
        String moacNode = Constant.moc_node;
        mMoacWallet.initChain3Provider(moacNode);
        if (getIntent() != null) {
            mOriginAddress = getIntent().getStringExtra(RECEIVE_ADDRESS_KEY);
            mContractAddress = getIntent().getStringExtra(CONTRACT_ADDRESS_KEY);
            mTokenSymbol = getIntent().getStringExtra(TOKEN_SYMBOL_KEY);
            mDecimal = getIntent().getIntExtra(TOKEN_DECIMAL, 0);
            mGas = getIntent().getDoubleExtra(TOEKN_GAS, 0);
            mAmount = getIntent().getDoubleExtra(TOEKN_AMOUNT, 0.0f);
        }

        mWalletData = WalletInfoManager.getInstance().getCurrentWallet();
        if (mWalletData == null) {
            this.finish();
            return;
        }

        mWalletUtil = TBController.getInstance().getWalletUtil();
        defaultToken = true;
        initView();
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_transfer));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                TokenTransferActivity.this.finish();
            }
        });
        mEdtWalletAddress = findViewById(R.id.edt_wallet_address);
        mEdtTransferNum = findViewById(R.id.edt_transfer_num);
        mGas = mWalletUtil.getRecommendGas(mGas);

        mTvGas = findViewById(R.id.tv_transfer_gas);
        mTvGas.setOnClickListener(this);
        mMoacWallet.gasPrice(new JCallback() {
            @Override
            public void completion(JCCJson jccJson) {
                String gas = jccJson.getString("gasPrice");
                if(gas != null) {
                    mGasPrice = Double.parseDouble(gas);
                    Log.d(TAG, "completion: mGasPrice = "+mGasPrice);
                    //?
                    mGas = 0;
                    mWalletUtil.calculateGasInToken(mGas, mGasPrice, defaultToken, new WCallback() {
                        @Override
                        public void onGetWResult(int ret, GsonUtil extra) {
                            mTvGas.setText(extra.getString("gas", ""));
                        }
                    });
                }
            }
        });
        mWalletUtil.translateAddress(mOriginAddress, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                mReceiveAddress = extra.getString("receive_address", "");
                mEdtWalletAddress.setText(mReceiveAddress);
            }
        });
        DecimalFormat df = new DecimalFormat("#.00000000");
        mEdtTransferNum.setText(mAmount > 0.0f ? df.format(mAmount).toString() : "");
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
            defaultToken = TextUtils.equals(mWalletUtil.getDefaultTokenSymbol(), mTokenSymbol);
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
                            mGasPrice, mGas, Util.parseDouble(mEdtTransferNum.getText().toString()), mBlockChain, mTokenSymbol, defaultToken);
                    orderDetailDialog.show();
                }
                break;
            case R.id.tv_transfer_gas:
                mWalletUtil.gasSetting(TokenTransferActivity.this, mGasPrice, defaultToken, new WCallback() {
                    @Override
                    public void onGetWResult(int ret, GsonUtil extra) {
                        if (ret == 0) {
                            String gas = extra.getString("gas", "");
                            mGasPrice = extra.getDouble("gasPrice", 0.0f);
                            gas = gas +"1";
                            mTvGas.setText(gas);
                        }
                    }
                });
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



    }


    private void gasPrice(){
        mMoacWallet.gasPrice(new JCallback() {
            @Override
            public void completion(JCCJson jccJson) {
                String gasPrice = jccJson.getString("gasPrice");
                if(!gasPrice.equals("")){

                }
            }
        });
    }

    //update
    private void signedTransaction(String senderAddress, String receiverAddress,
                                   double value) throws JSONException {

        JSONObject transaction = new JSONObject();
        transaction.put("from", senderAddress);
        transaction.put("value", value);
        transaction.put("to", receiverAddress);
        transaction.put("gas", "");
        transaction.put("gasPrice", "");

        String secret = "";

        mMoacWallet.sign(transaction, secret, new JCallback() {
            @Override
            public void completion(JCCJson jccJson) {
                String rawTransaction = jccJson.getString("rawTransaction");
                if(!rawTransaction.equals("")) {
                    sendSignedTransaction(rawTransaction);
                } else {
                    resetTranferBtn();
                    ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_transfer_failed) + 6);
                }
            }
        });
    }

    private void sendSignedTransaction(String rawTransaction) {
        if (TextUtils.isEmpty(rawTransaction)) {
            resetTranferBtn();
            ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_transfer_failed) + 3);
            return;
        }
        mMoacWallet.sendSignedTransaction(rawTransaction, new JCallback() {
            @Override
            public void completion(JCCJson jccJson) {
                String hash = jccJson.getString("hash");
                if(!hash.equals("")){
                    resetTranferBtn();
                    ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_transfer_success));
                    TokenTransferActivity.this.finish();
                } else {
                    resetTranferBtn();
                    ToastUtil.toast(TokenTransferActivity.this, getString(R.string.toast_transfer_failed) + 4);
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
                                                  double mAmount, String tokenSymbol, int decimal, double gas) {
        Intent intent = new Intent(context, TokenTransferActivity.class);
        intent.putExtra(CONTRACT_ADDRESS_KEY, contactAddress);
        intent.putExtra(RECEIVE_ADDRESS_KEY, receiveAddress);
        intent.putExtra(TOKEN_SYMBOL_KEY, tokenSymbol);
        intent.putExtra(TOKEN_DECIMAL, decimal);
        intent.putExtra(TOEKN_GAS, gas);
        intent.putExtra(TOEKN_AMOUNT, mAmount);
        context.startActivity(intent);
    }
}
