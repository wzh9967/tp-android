package com.tokenbank.base;
import com.android.jccdex.app.moac.MoacWallet;
import com.tokenbank.config.AppConfig;
import com.tokenbank.config.Constant;

import java.util.ArrayList;
import java.util.List;

//后期要作一些修改
public class TBController {


    //关联抽象接口和具体实现
    private final static String TAG = "TBController";


    public final static int SWT_INDEX = 2;

    private BaseWalletUtil mWalletUtil;
    private BaseWalletUtil mMocWalletUtil;
    private TestWalletBlockchain mNullWalletUtil;
    private MoacWallet mMoacWalletUtil;
    private MoacWallet mMoacWallet;
    private static TBController sInstance = new TBController();
    private  List<Integer> mSupportType = new ArrayList<>();

    private TBController() {

    }

    public static TBController getInstance() {
        return sInstance;
    }

    public void init() {
        mSupportType.add(SWT_INDEX);
        mMocWalletUtil = new MoacWalletBlockchain();
        mMocWalletUtil.init();
        mNullWalletUtil = new TestWalletBlockchain();
        mMoacWallet = MoacWallet.getInstance();
        mMoacWallet.init(AppConfig.getContext());
        mMoacWallet.initChain3Provider(MoacServer.getInstance().getNode());
    }

    public BaseWalletUtil getWalletUtil() {
        mWalletUtil = mMocWalletUtil;
        return mWalletUtil;
    }

    public MoacWallet getMoacWallet() {
        mMoacWalletUtil = mMoacWallet;
        return mMoacWalletUtil;
    }

    //delete
    public List<Integer> getSupportType() {
        return mSupportType;
    }

}
