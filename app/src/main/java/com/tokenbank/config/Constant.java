package com.tokenbank.config;


import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;

import java.lang.reflect.GenericArrayType;

public class Constant {

    public final static String wallet_prefs_prefix = "wallet_pref_";

    public final static String wallet_def_file = "wallet_default_pref";
    public final static String wid = "wid";
    public final static String wtype = "wtype";
    public final static String wname = "wname";
    public final static String waddress = "waddress";
    public final static String whash = "whash";
    public final static String wpk = "wpk";
    public final static String baked = "baked";
    public final static String tips = "tips";
    public final static String words = "words";

    //node
    public final static String node_def_file = "node_default_pref";
    public final static String NodeName = "NodeName";
    public final static String NodeUrl = "NodeUrl";
    //common sp
    public final static String common_prefs = "common_prefs";
    public final static String asset_visible_key = "asset_visible";

    //什么是私钥
    public final static String privatekey_intro_url = "";


    public final static String sys_prefs = "sys_prefs";
    public final static String init_keys = "init_keys";

    //本地web3文件地址
    public final static String base_web3_url = "file:///android_asset/fst_storm3.html";
    //联盟链节点地址和浏览器地址
    public static String moc_node = "http://101.200.174.239:7545";
    public final static String web_node = "http://consortium.moacchain.net";
    //帮助
    public final static String help_url = "";

    //隐私策略
    public final static String privilege_url = "";

    //服务协议
    public final static String service_term_url = "file:///android_asset/TestJsNativeBridge.html";

    //交易查询
    public final static String swt_transaction_search_url = "http://state.jingtum.com/#!/tx/";

    public final static String JC_EXCHANGE_SERVER = "https://e9joixcvsdvi4sf.jccdex.cn";


    public final static String MOC_EXCHANGE_SERVER = "http://dao.moacchain.net/api/v1/wallets/";

    public final static String MOC_ERC20EXCHANGE_SERVER = moc_node+"/api/v1/wallets/";
    //jt
    public final static String jt_base_url = "https://api.jingtum.com";

    //activity requestCode
    public final static int CHOOSE_BLOCK_REQUEST_CODE = 1001;
    public final static int CHOOSE_IMPORTWAY_CODE = 1002;
    public final static String BLOCK_KEY = "BLOCK";
    public final static String gasLimit = "22000";
    public final static String Erc20gasLimit = "70000";
    public final static String CustomNodeName = "自定义节点";
    public final static String TokenSymbol = "mfc";
    public final static int DefaultDecimal = 18;
}
