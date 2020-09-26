package com.tokenbank.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.tokenbank.config.Constant;
import com.tokenbank.utils.NetUtil;

/**
 * 用来检测节点的连接性
 */
public class MoacServer {
    private static final String STATUS = "OPEN";
    private static String server = Constant.moc_node;
    private static Boolean local_sign = true;
    private static MoacServer instance;
    private static Context mContext;
    static SharedPreferences sharedPreferences;
    private MoacServer() {
    }
    public static MoacServer getInstance(Context context) {
        if (!NetUtil.isNetworkAvailable(context)) {
        }
        if (instance == null) {
            instance = new MoacServer();
        }
        return instance;
    }





}
