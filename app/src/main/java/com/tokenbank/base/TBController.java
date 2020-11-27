package com.tokenbank.base;

import android.util.Log;

import com.tokenbank.config.AppConfig;
import com.tokenbank.net.query.QueryDataFromNet;
import com.tokenbank.net.query.QueryTransaction;
import com.tokenbank.wallet.FstServer;
import com.tokenbank.wallet.FstWallet;

import java.util.ArrayList;
import java.util.List;
public class TBController {


    //关联抽象接口和具体实现
    private final static String TAG = "TBController";
    public final static int SWT_INDEX = 2;
    private FstWallet mFstWallet;
    private static TBController sInstance = new TBController();
    private  List<Integer> mSupportType = new ArrayList<>();
    private QueryDataFromNet mQueryTransaction;

    private TBController() {

    }

    public static TBController getInstance() {
        return sInstance;
    }

    public void init() {
        mSupportType.add(SWT_INDEX);
        mFstWallet = mFstWallet.getInstance();
        mFstWallet.init(AppConfig.getContext());
        Log.d(TAG, "init: "+ FstServer.getInstance().getNode());
        mFstWallet.initStorm3Provider(FstServer.getInstance().getNode());
        mQueryTransaction = new QueryTransaction();
    }

    public FstWallet getFstWallet() {
        return mFstWallet;
    }

    public QueryDataFromNet getmQueryTransaction(){
        return mQueryTransaction;
    }


    public List<Integer> getSupportType() {
        return mSupportType;
    }

}
