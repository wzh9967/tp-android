package com.tokenbank.web;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.webkit.JavascriptInterface;

import com.just.agentweb.AgentWeb;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.utils.DeviceUtil;
import com.tokenbank.utils.GsonUtil;
import com.zxing.activity.CaptureActivity;

import java.util.List;


/**
 * JS调用原生接口类
 */
public class JsNativeBridge {

    private final static String MSG_SUCCESS = "success";
    private final static long FIFTEEN = 15 * 60 * 1000L;

    private AgentWeb mAgentWeb;
    private Context mContext;
    private WalletInfoManager mWalletManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mFrom, mTo, mValue, mToken, mIssuer, mGas, mMemo;
    private IWebCallBack mWebCallBack;
    private WalletInfoManager.WData mCurrentWallet;

    public JsNativeBridge(AgentWeb agent, Context context, IWebCallBack callback) {
        this.mAgentWeb = agent;
        this.mContext = context;
        this.mWebCallBack = callback;
        this.mWalletManager = WalletInfoManager.getInstance();
    }

    @JavascriptInterface
    public void callHandler(String methodName, String params, String callbackId) {
        mCurrentWallet = mWalletManager.getCurrentWallet();
        GsonUtil result = new GsonUtil("{}");
        switch (methodName) {
            case "getAppInfo":
                String version = "";
                String name = "";
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo packageInfo = null;
                try {
                    packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
                    if (packageInfo != null) {
                        version = packageInfo.versionName;
                        name = mContext.getResources().getString(packageInfo.applicationInfo.labelRes);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                GsonUtil infoData = new GsonUtil("{}");
                infoData.putString("name", name);
                infoData.putString("system", "android");
                infoData.putString("version", version);
                infoData.putString("sys_version", Build.VERSION.SDK_INT + "");

                result.putBoolean("result", true);
                result.put("data", infoData);
                result.putString("msg", MSG_SUCCESS);
                this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                break;
            case "getDeviceId":
                String deviceId = DeviceUtil.generateDeviceUniqueId();
                this.mAgentWeb.getJsAccessEntrace().quickCallJs(callbackId, deviceId);
                break;
            case "getWallets":
                List<WalletInfoManager.WData> wallets = mWalletManager.getAllWallet();
                GsonUtil data1 = new GsonUtil("[]");
                for (int i = 0; i < wallets.size(); i++) {
                    GsonUtil wallet = new GsonUtil("{}");
                    String address = wallets.get(i).waddress;
                    String name1 = wallets.get(i).wname;
                    wallet.putString("name", name1);
                    wallet.putString("address", address);
                    data1.put(wallet);
                }
                result.putBoolean("result", true);
                result.put("data", data1);
                result.putString("msg", MSG_SUCCESS);
                this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                break;
            case "getCurrentWallet":
                String walletName = mCurrentWallet.wname;
                GsonUtil data = new GsonUtil("{}");
                data.putString("address", mCurrentWallet.waddress);
                data.putString("name", walletName);
                result.putBoolean("result", true);
                result.put("data", data);
                result.putString("msg", MSG_SUCCESS);
                this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                break;
            case "invokeQRScanner":
                CaptureActivity.startCaptureActivity(mContext, callbackId);
                break;
            case "back":
                if (mWebCallBack != null) {
                    mWebCallBack.onBack();
                }
                break;
            case "fullScreen":
                if (mWebCallBack != null) {
                    mWebCallBack.switchFullScreen(params);
                }
                break;
            case "close":
                if (mWebCallBack != null) {
                    mWebCallBack.onClose();
                }
                break;
            default:
                break;
        }

    }


    private Spanned formatHtml() {
        String paysH = "<font color=\"#3B6CA6\">" + mValue + " </font>";
        String paysCurH = "<font color=\"#021E38\">" + mToken + " </font>";
        return Html.fromHtml(paysH.concat(paysCurH));
    }
}
