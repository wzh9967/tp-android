package com.tokenbank.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.utils.Util;


public class OrderDetailDialog extends BaseDialog implements View.OnClickListener {
    private final static String TAG = "PKDialog";

    public interface onConfirmOrderListener {
        void onConfirmOrder();
    }

    private onConfirmOrderListener mOnConfirmOrderListener;

    private ImageView mImgClose;
    private TextView mTvReceiverAddress;
    private TextView mTvSenderAddress;
    private TextView mTvGasInToken;
    private TextView mTvGasInfo;
    private TextView mTvTokenCount;
    private TextView mTvTokenName;
    private TextView mTvConfirm;

    private String mSenderAddress;
    private String mReceiverAddress;
    private String mTokenName;
    private String mGasLimit;
    private double mTokenCount;
    private Double mGasPrice;
    private int mDecimal;


    public OrderDetailDialog(@NonNull Context context, onConfirmOrderListener onConfirmOrderListener, String senderAddress,
                             String receiverAddress, int Decimal ,Double gasPrice, String gasLimit, double tokencount, String tokenName) {
        super(context, R.style.DialogStyle);
        mOnConfirmOrderListener = onConfirmOrderListener;
        this.mSenderAddress = senderAddress;
        this.mReceiverAddress = receiverAddress;
        this.mGasPrice = gasPrice;
        this.mTokenName = tokenName;
        this.mGasLimit = gasLimit;
        this.mDecimal = Decimal;
        this.mTokenCount = tokencount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.layout_dialog_confirmorder);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = -1;
        lp.height = -2;
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(lp);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view == mTvConfirm) {
            if (mOnConfirmOrderListener != null) {
                mOnConfirmOrderListener.onConfirmOrder();
                dismiss();
            }
        } else if (view == mImgClose) {
            dismiss();
        }
    }

    private void initView() {
        mImgClose = findViewById(R.id.img_close);
        mImgClose.setOnClickListener(this);
        mTvReceiverAddress = findViewById(R.id.tv_receiver_address);
        mTvReceiverAddress.setText(mReceiverAddress);
        mTvSenderAddress = findViewById(R.id.tv_sender_address);
        mTvSenderAddress.setText(mSenderAddress);
        mTvGasInToken = findViewById(R.id.tv_gas_intoken);
        mTvGasInToken.setText(Util.calculateGasInToken(mDecimal,mGasLimit, mGasPrice)+"moab");
        mTvGasInfo = findViewById(R.id.tv_gas_info);
        mTvGasInfo.setText("≈ " + mGasPrice + " * " + mGasLimit+"/10^"+mDecimal);
        mTvTokenCount = findViewById(R.id.tv_token_count);
        mTvTokenCount.setText(Util.formatDoubleToStr(5, mTokenCount));
        mTvTokenName = findViewById(R.id.tv_token_name);
        mTvTokenName.setText(mTokenName);
        mTvConfirm = findViewById(R.id.tv_confirm);
        mTvConfirm.setOnClickListener(this);
    }
}
