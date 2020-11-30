package com.tokenbank.config;


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
    public final static String CustomNodeName = "自定义节点";
    public final static String common_prefs = "common_prefs";
    public final static String asset_visible_key = "asset_visible";
    public final static String privatekey_intro_url = "";
    public final static String sys_prefs = "sys_prefs";
    public final static String init_keys = "init_keys";
    public static final String LOAD_URL = "load_url";
    //服务协议
    public final static String service_term_url = "";

    public final static String MOC_Hash_SERVER = "http://dao.moacchain.net/transaction/";

    //activity requestCode
    public final static int CHOOSE_BLOCK_REQUEST_CODE = 1001;
    public final static int CHOOSE_IMPORTWAY_CODE = 1002;
    public final static String BLOCK_KEY = "BLOCK";

    //本地web3文件地址
    public final static String base_web3_url = "file:///android_asset/fst_storm3.html";

    //图片保存路径
    public final static String photo_path = "/TokenPocket/dapp/img/";

    //帮助
    public final static String help_url = "";
    //隐私策略
    public final static String privilege_url = "";

    //社区链浏览器接口
    public final static String FST_EXCHANGE_SERVER = "http://dao.moacchain.net/api/v1/wallets/";

    //联盟链浏览器接口  http://consortium.moacchain.net

    //社区链节点地址(默认节点地址)
    public static String fst_node = "http://101.200.174.239:7545";

    //原生货币交易默认gas上限
    public final static String gasLimit = "22000";

    //ERC20交易默认交易上限
    public final static String Erc20gasLimit = "70000";

    //基础货币单位符号
    public final static String TokenSymbol = "mfc";

    //基础货币名称
    public final static String TokenName = "mfc币";

    //基础货币默认decimal
    public final static int DefaultDecimal = 18;
}
