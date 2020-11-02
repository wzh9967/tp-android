package com.tokenbank.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.tokenbank.R;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.SysApplication;
import com.tokenbank.base.WCallback;
import com.tokenbank.base.TBController;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.QRUtils;
import com.tokenbank.utils.ToastUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.view.TitleBar;

import java.math.BigInteger;
import java.util.function.LongToDoubleFunction;


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
    private String mHash;
    private int mDecimal;
    private GsonUtil transactionData;
    private BaseWalletUtil mWalletUtil;

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        SysApplication.addActivity(this);
        if (getIntent() != null) {
            String data = getIntent().getStringExtra("ITEM");
            transactionData = new GsonUtil(data);
        }
        mHash = transactionData.getString("transactionHash", "");
        if (TextUtils.isEmpty(mHash)) {
            ToastUtil.toast(TransactionDetailsActivity.this, getString(R.string.toast_illegal_parameters));
            this.finish();
            return;
        }

        mWalletUtil = TBController.getInstance().getWalletUtil();
        if (mWalletUtil == null) {
            this.finish();
            return;
        }
        initView();
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
        mDecimal = mWalletUtil.getDefaultDecimal();
        updateData(transactionData);
    }

    private void updateData(GsonUtil transactionInfo) {
        String bl_symbol = Constant.TokenSymbol;
        String value = transactionInfo.getString("value","");
        String contract = transactionInfo.getString("contract","");
        Double gasPrice = transactionInfo.getDouble("gasPrice",0.0);
        String gasUsed = transactionInfo.getString("gasUsed","");
        if(!contract.equals("")){
            bl_symbol = mWalletUtil.getDataByContract(contract,"bl_symbol");
        }
        String gasFee = mWalletUtil.calculateGasInToken(mDecimal,gasUsed,gasPrice);
        String toAddress = transactionInfo.getString("to", "");
        mTvGas.setText(gasFee + Constant.TokenSymbol);
        mTvSender.setText(transactionInfo.getString("from", ""));
        mTvReceiver.setText(toAddress);
        mTvInfo.setText(transactionInfo.getString("input", ""));
        mTvTransactionId.setText(transactionInfo.getString("transactionHash", ""));
        mTvBlockId.setText(transactionInfo.getString("blockNumber", ""));
        mTvTransactionTime.setText(transactionInfo.getString("timestamp", ""));
        int status = transactionInfo.getInt("txreceipt_status", 5);
        if (status == 1) {
            //success
            mTvTransactionStatus.setText(getString(R.string.content_trading_success));
        } else if (status == 2) {
            //pending
            mTvTransactionStatus.setText(getString(R.string.content_trading_pending));
        } else if (status == 0) {
            //fail
            mTvTransactionStatus.setText(getString(R.string.content_trading_failure));
        } else {
            mTvTransactionStatus.setText(getString(R.string.content_trading_unknown));
        }
        mTvCount.setText(value+" ");
        mTvSymbol.setText(bl_symbol);
        createQRCode(mWalletUtil.getTransactionSearchUrl(mTvTransactionId.getText().toString()));
    }


    public static void startTransactionDetailActivity(Context context, GsonUtil data) {
        Intent intent = new Intent(context, TransactionDetailsActivity.class);
        intent.putExtra("ITEM", data.toString());
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
}
