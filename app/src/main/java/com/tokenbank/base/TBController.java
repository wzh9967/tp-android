package com.tokenbank.base;
import java.util.ArrayList;
import java.util.List;

//后期要作一些修改
public class TBController {


    //关联抽象接口和具体实现
    private final static String TAG = "TBController";


    public final static int SWT_INDEX = 2;

    private BaseWalletUtil mWalletUtil;

    private BaseWalletUtil mSwtWalletUtil;
    private TestWalletBlockchain mNullWalletUtil;

    private static TBController sInstance = new TBController();
    private  List<Integer> mSupportType = new ArrayList<>();

    private TBController() {

    }

    public static TBController getInstance() {
        return sInstance;
    }

    public void init() {
        mSupportType.add(SWT_INDEX);

        mSwtWalletUtil = new SWTWalletBlockchain();
        mSwtWalletUtil.init();

        mNullWalletUtil = new TestWalletBlockchain();
    }

    public BaseWalletUtil getWalletUtil() {
        mWalletUtil = mSwtWalletUtil;
        return mWalletUtil;
    }

    //delete
    public List<Integer> getSupportType() {
        return mSupportType;
    }

}
