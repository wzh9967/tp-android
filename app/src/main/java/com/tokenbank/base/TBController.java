package com.tokenbank.base;



import com.tokenbank.net.query.QueryDataFromNet;
import com.tokenbank.net.query.QueryTransaction;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.wallet.FstServer;
import com.tokenbank.wallet.FstWallet;

import java.util.ArrayList;
import java.util.List;

import static com.tokenbank.config.AppConfig.getContext;

public class TBController {


    //关联抽象接口和具体实现
    private final static String TAG = "TBController";
    public final static int SWT_INDEX = 2;
    private FstWallet mFstWallet;
    private WalletUtil walletUtil;
    private static TBController sInstance = new TBController();
    private  List<Integer> mSupportType = new ArrayList<>();
    private QueryDataFromNet mQueryTransaction;
    private Boolean isValidNode = null;
    private TBController() {

    }

    public static TBController getInstance() {
        return sInstance;
    }

    public void init() {
        mSupportType.add(SWT_INDEX);
        mFstWallet = mFstWallet.getInstance();;
        mFstWallet.init(getContext(), FstServer.getInstance().getNode(), new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                if(ret != 0){
                    //节点无效 初始化失败
                    isValidNode = false;
                }
            }
        });
        mQueryTransaction = new QueryTransaction();
    }

    public WalletUtil getFstWallet() {
        walletUtil = mFstWallet;
        return walletUtil;
    }

    public QueryDataFromNet getmQueryTransaction(){

        return mQueryTransaction;

    }

    public Boolean getNodeStatus(){
        return isValidNode;
    }

    public List<Integer> getSupportType() {

        return mSupportType;

    }

}
