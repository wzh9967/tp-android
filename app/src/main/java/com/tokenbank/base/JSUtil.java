package com.tokenbank.base;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.tokenbank.config.AppConfig;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;
import com.tokenbank.view.TBWebView;

import java.util.HashMap;


public class JSUtil {

    private final static String TAG = "JSUtil";

    private TBWebView mWebView;
    private boolean isInit = false;
    private int mCallID = 0;
    private HashMap<Integer, WCallback> mCallbackHashMap = new HashMap<>();
    private static JSUtil instance = new JSUtil();
    private JSUtil() {

    }

    public static JSUtil getInstance() {
        return instance;
    }

    public void init() {
        mWebView = new TBWebView(AppConfig.getContext());
        mWebView.addJavascriptInterface(this, "client");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (TextUtils.equals(Constant.base_web3_url, url)) {
                    isInit = false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (TextUtils.equals(Constant.base_web3_url, url)) {
                    isInit = true;
                }
            }
        });
        loadJs();
    }
    private void loadJs() {
        mWebView.loadUrl(Constant.base_web3_url);
    }
    //原生(out) -> js -> 原生（）
    public void callJS(String optCallback, GsonUtil json, WCallback walletOptCallback) {
        if (optCallback == null || optCallback.length() <= 0 || json == null) {
            return;
        }
        if(!isInit) {
            return;
        }
        mCallID++;
        json.putInt("callid", mCallID);
        json.putString("url",MoacServer.getInstance().getNode());
        mCallbackHashMap.put(mCallID, walletOptCallback);

        String jsonParams = json.toString();

        try {
            mWebView.loadUrl("javascript:void(function(){try{"
                    + optCallback + "('" + jsonParams + "');"
                    + "}catch(e){console.error('callJS error, function:' + " + optCallback + " + ', msg:' + e.toString());" + generateCatchCallback(mCallID, "opt error code 1001") + "}}())");
        } catch (Throwable e) {
            TLog.e(TAG, "操作失败");
        }
    }

    private String generateCatchCallback(int callId, String errorMsg) {

        String callbackStr = "var result = new Object();result.ret = -1;result.callid = " + callId +
                ";result.extra='" + errorMsg + "';client.notifyWeb3Result(JSON.stringify(result));";
        return callbackStr;
    }


    public boolean checkInit(WCallback callback) {
        if (!isInit) {
            if (callback != null) {
                GsonUtil reason = new GsonUtil("{}");
                reason.putString("reason", "page not init, make sure call loadJs and page loadData success");
                callback.onGetWResult(-1, reason);
            }
            return false;
        } else {
            return true;
        }
    }

    //原生调用JS的回调处理    原生 -> js -> 原生（in）
    @JavascriptInterface
    public void notifyWeb3Result(final String result) {
        AppConfig.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    GsonUtil json = new GsonUtil(result);
                    WCallback web3ResultCallback = mCallbackHashMap.get(json.getInt("callid", -1));
                    mCallbackHashMap.remove(json.getInt("callid", -1));
                    if (web3ResultCallback != null) {
                        web3ResultCallback.onGetWResult(json.getInt("ret", -1), json.getObject("extra", "{}"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @JavascriptInterface
    public void callHandler(String methodName, String params, String callbackId) {
        GsonUtil result = new GsonUtil("{}");
        switch (methodName){
            case "getNativeInfo" : getNativeInfo(params,callbackId);break;
            case "test" : test(callbackId);break;
            case "sendNodeToJs" : sendNodeToJs(callbackId);break;
        }
    }

    private void sendNodeToJs(String callbackId) {
        //获取当前node
        String node = MoacServer.getInstance().getNode();
        Log.d(TAG, "sendNodeToJs:  node = "+node);
        GsonUtil result = new GsonUtil("{}");
        result.putString("node",node);
        notifyNativeResult(result,callbackId);
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
