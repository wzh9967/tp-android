package com.tokenbank.base;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.tokenbank.config.AppConfig;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;
import com.tokenbank.view.TBWebView;


//该JSNativeBridge具体会提供给H5使用，挂载在加载H5的webView上
public class JSNativeBridge {
    private static final String TAG = "JSNativeBridge";
    public TBWebView mWebView;
    private Context mContext;//后面需要，在init添加初始化


    private static JSNativeBridge instance = new JSNativeBridge();

    public JSNativeBridge() { }
    public static JSNativeBridge getInstance() {
        return instance;
    }
    public void init(TBWebView mWebView) {
        this.mWebView = mWebView;
    }



    @JavascriptInterface
    public void callHandler(String methodName, String params, String callbackId) {
        GsonUtil result = new GsonUtil("{}");
        switch (methodName){
            case "getNativeInfo" : getNativeInfo(params,callbackId);
            case "test" : test(callbackId);
            //添加各种函数供H5调用，但是不知道要调用什么













        }

    }
    private void test(final String callbackId) {
        GsonUtil result = new GsonUtil("{}");
        result.putString("message","honor9x");
        Log.d(TAG, "js说: "+callbackId);
        notifyNativeResult(result,callbackId);
    }

    private void getNativeInfo(final String jsonParams, final String callbackId) {
        //从json中获取参数
        //GsonUtil json = new GsonUtil(jsonParams);
        //do something

        //构建结果json
        GsonUtil result = new GsonUtil("{}");
        //callJS返回
        notifyNativeResult(result,callbackId);
    }

    //将原生结果返还给js    JS  ->  原生(out)  ->JS
    private void notifyNativeResult(final GsonUtil result, final String callbackId) {
        if ( result == null) {
            return;
        }
        Log.d(TAG, "notifyNativeResult: callback = "+callbackId);
        AppConfig.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //回调返回
                    mWebView.loadUrl("javascript:" + callbackId + "('" + result.toString() + "')");
                } catch (Throwable e) {
                    TLog.e(TAG, "操作失败"+e);
                }
            }
        });
    }
}
