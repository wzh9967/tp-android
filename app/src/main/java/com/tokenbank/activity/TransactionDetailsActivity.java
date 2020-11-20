package com.tokenbank.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.tokenbank.R;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.QRUtils;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.view.TitleBar;

import java.math.BigInteger;

/**
 *  交易详情，其数据通过Hash从链上直接获取。
 */
public class TransactionDetailsActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar mTitleBar;
    private TextView mTvTransactionStatus;
    private TextView mTvCount;
    private TextView mTvSymbol;
    private TextView mTvSender;
    private TextView mTvReceiver;
    private TextView mTvGas;
    private TextView mTvInfo;
    private TextView mTvTransactionId;
    private TextView mTvBlockId;
    private TextView mTvTransactionTime;
    private TextView mTvCopyUrl;
    private ImageView mImgTransactionQrCode;
    private String bl_symbol;
    private String mHash;
    private int mDecimal;
    private Double mGasPrice;
    private String mGasUsed;
    private String mTimestamp;
    private String mValue;
    private boolean isTransaction = true;
    private GsonUtil transactionData;
    private int DelayMills = 1;
    private BaseWalletUtil mWalletUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        initData();
    }

    private void initData() {
        mWalletUtil = TBController.getInstance().getWalletUtil();
        if (mWalletUtil == null) {
            this.finish();
            return;
        }
        if (getIntent() != null) {
            String Hash = getIntent().getStringExtra("hash");
            isTransaction = getIntent().getBooleanExtra("isTransaction",true);
            mHash = Hash;
        }
        if (TextUtils.isEmpty(mHash)) {
            ToastUtil.toast(TransactionDetailsActivity.this, getString(R.string.toast_illegal_parameters));
            this.finish();
            return;
        }
        //通过hash 从链上获取交易细节
        mWalletUtil.getTransactionDetail(mHash, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if(ret == 0){
                    GsonUtil payment = extra.getObject("data");
                    if(payment == null){
                        transactionData = payment;
                    } else {
                        transactionData = ConvertJson(payment);
                    }
                }
                initView();
            }
        });
    }

    private void initView() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setLeftDrawable(R.drawable.ic_back);
        mTitleBar.setTitle(getString(R.string.titleBar_transaction_details));
        mTitleBar.setTitleBarClickListener(new TitleBar.TitleBarListener() {
            @Override
            public void onLeftClick(View view) {
                onBackPressed();
            }
        });

        mTvTransactionStatus = findViewById(R.id.tv_transaction_status);
        mTvCount = findViewById(R.id.tv_transaction_count);
        mTvSymbol = findViewById(R.id.tv_symbol);
        mTvSender = findViewById(R.id.tv_send_address);
        mTvSender.setOnClickListener(this);

        mTvReceiver = findViewById(R.id.tv_receive_address);
        mTvReceiver.setOnClickListener(this);

        mTvGas = findViewById(R.id.tv_gas);
        mTvInfo = findViewById(R.id.tv_info);
        mTvTransactionId = findViewById(R.id.tv_transaction_id);
        mTvTransactionId.setOnClickListener(this);
        mTvBlockId = findViewById(R.id.tv_block);
        mTvTransactionTime = findViewById(R.id.tv_transaction_time);
        mTvCopyUrl = findViewById(R.id.tv_copy_transaction_url);
        mTvCopyUrl.setOnClickListener(this);
        mImgTransactionQrCode = findViewById(R.id.img_transaction_qrcode);
        mDecimal = Constant.DefaultDecimal;
        bl_symbol = Constant.TokenSymbol;
        if(transactionData !=null){
            updateData(transactionData);
        } else {
            setTransactionUnknown();
            mTvTransactionId.setText(mHash);
        }
    }

    private void updateData(GsonUtil transactionInfo) {
        String contract = transactionData.getString("contract","");
        if(contract != ""){
            mDecimal = Integer.parseInt(mWalletUtil.getDataByContract(contract,"decimal"));
            bl_symbol = mWalletUtil.getDataByContract(contract,"bl_symbol");
        }
        String toAddress = transactionInfo.getString("to", "");
        mTvSender.setText(transactionInfo.getString("from", ""));
        mTvReceiver.setText(toAddress);
        mTvInfo.setText(transactionInfo.getString("input", ""));
        mTvTransactionId.setText(transactionInfo.getString("transactionHash", ""));
        mTvBlockId.setText(transactionInfo.getString("blockNumber", ""));

        //从交易跳转过来，需要时间确认交易
        if(isTransaction){
            DelayMills = 10000;
            mTvTransactionStatus.setText(getString(R.string.content_trading_pending));
        } else {
            mTvTransactionStatus.setText(getString(R.string.content_trading_waiting));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mWalletUtil.getTransactionReceipt(mHash, new WCallback() {
                    @Override
                    public void onGetWResult(int ret, GsonUtil extra) {
                        if(ret == 0){
                            GsonUtil payment = extra.getObject("data");
                            if (payment !=null){
                                boolean status = payment.getBoolean("status",false);
                                if (status) {
                                    mTimestamp = payment.getString("timestamp","");
                                    mGasUsed = payment.getString("gasUsed","");
                                    setTransactionSuccess();
                                } else {
                                    setTransactionFailed();
                                }
                            } else {
                                setTransactionUnknown();
                            }
                        } else {
                            mTvTransactionStatus.setText(getString(R.string.toast_transaction_info_failure));
                        }
                    }
                });
            }
        }, DelayMills);
        mTvSymbol.setText(bl_symbol);
        createQRCode(mWalletUtil.getTransactionSearchUrl(mTvTransactionId.getText().toString()));
    }

    public static void startTransactionDetailActivity(Context context, String hash,boolean isTransaction) {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        intent.putExtra("hash", hash);
        intent.putExtra("isTransaction",isTransaction);
        intent.addFlags(context instanceof BaseActivity ? 0 : Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void createQRCode(String transactionUrl) {
        try {
            Bitmap bitmap = QRUtils.createQRCode(transactionUrl, getResources().getDimensionPixelSize(R.dimen.dimen_qr_width));
            mImgTransactionQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void setTransactionSuccess(){
        mTvTransactionStatus.setText(getString(R.string.content_trading_success));
        mTvGas.setText(Util.calculateGasInToken(mDecimal,mGasUsed,mGasPrice));
        mTvTransactionTime.setText(Util.toDate(mTimestamp));
        mTvCount.setText(mValue);
    }
    private void setTransactionFailed(){
        mTvTransactionStatus.setText(getString(R.string.content_trading_failure));
    }
    private void setTransactionUnknown(){
        mTvTransactionStatus.setText(getString(R.string.content_trading_unknown));
        mTvCount.setText(mValue);
    }

    @Override
    public void onClick(View v) {
        if (v == mTvCopyUrl) {
            Util.clipboard(TransactionDetailsActivity.this, "",
                    mWalletUtil.getTransactionSearchUrl(mTvTransactionId.getText().toString()));
            ToastUtil.toast(TransactionDetailsActivity.this, getString(R.string.toast_url_copied));
        } else if (v == mTvSender) {
            Util.clipboard(TransactionDetailsActivity.this, "", mTvSender.getText().toString());
            ToastUtil.toast(TransactionDetailsActivity.this, getString(R.string.toast_send_address_copied))
            ;
        } else if (v == mTvReceiver) {
            Util.clipboard(TransactionDetailsActivity.this, "", mTvReceiver.getText().toString());
            ToastUtil.toast(TransactionDetailsActivity.this, getString(R.string.toast_receive_address_copied))
            ;
        } else if (v == mTvTransactionId) {
            WebBrowserActivity.startWebBrowserActivity(TransactionDetailsActivity.this, getString(R.string.titleBar_transaction_query),
                    mWalletUtil.getTransactionSearchUrl(mTvTransactionId.getText().toString()));
        }
    }

    public GsonUtil ConvertJson(GsonUtil payment){
        GsonUtil data = new GsonUtil("{}");
        data.putString("input",payment.getString("input",""));
        data.putString("from",payment.getString("from",""));
        data.putString("transactionHash",payment.getString("hash",""));
        data.putString("blockNumber", payment.getString("blockNumber",""));
        String input = payment.getString("input","");
        data.putString("input",input);
        mGasPrice = Double.valueOf(payment.getString("gasPrice",""));
        if(input.length() == 138 && input.startsWith("0xa9059cbb")){
            String value = new BigInteger(input.substring(74), 16).toString();
            String contract = payment.getString("to","");
            int Decimal = Integer.parseInt(mWalletUtil.getDataByContract(contract.toLowerCase(),"decimal"));
            mValue = Util.toValue(Decimal,value);

            data.putString("to", "0x"+input.substring(34,74));
            data.putString("isErc20","true");
            data.putString("contract",contract);
        }else{
            mValue = Util.toValue(Constant.DefaultDecimal,payment.getString("value", ""));
            data.putString("value",payment.getString("value", ""));
            data.putString("to", payment.getString("to", ""));
        }
        return data;
    }
}
