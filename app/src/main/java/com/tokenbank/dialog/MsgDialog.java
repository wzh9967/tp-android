package com.tokenbank.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tokenbank.R;
import com.tokenbank.config.AppConfig;
import com.tokenbank.utils.ViewUtil;


public class MsgDialog extends BaseDialog {

    private TextView mTvMsg;
    private String mMsg;

    private boolean isHook = true;

    public MsgDialog(@NonNull Context context, String msg) {
        super(context, R.style.DialogStyle);
        mMsg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y = ViewUtil.dip2px(getContext(), 70);
        lp.gravity = Gravity.TOP;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setDimAmount(0f);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {
        mTvMsg = (TextView) findViewById(R.id.tv_content);
        mTvMsg.setText(mMsg);

        //3秒后关闭
        AppConfig.postDelayOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 3000);
    }

    /**
     * 是否需要验证密码，默认true
     *
     * @param isVerifyPwd
     * @return
     */
    public com.tokenbank.dialog.MsgDialog setIsHook(boolean isVerifyPwd) {
        this.isHook = isVerifyPwd;
        return this;
    }
}
