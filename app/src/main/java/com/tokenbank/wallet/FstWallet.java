package com.tokenbank.wallet;

import android.content.Context;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.tokenbank.base.WalletUtil;
import com.tokenbank.base.WCallback;
import com.tokenbank.utils.GsonUtil;


/**
 * 用于和链进行交互 （Storm3.js 的函数调用）
 */
public class FstWallet implements WalletUtil {

    private static final String TAG = "FstWallet";
    private static BridgeWebView mWebview;
    private static FstWallet instance = new FstWallet();

    public static FstWallet getInstance() {
        return instance;
    }

    /**
     * 初始化 BridgeWebView
     * @param context
     */
    public void init(Context context,String node,WCallback callback) {
        mWebview = new BridgeWebView(context);
        mWebview.loadUrl(node);
        GsonUtil params = new GsonUtil("{}");
        params.putString("node",node);

        JSUtil.getInstance().callJS("init", params, callback);
    }

    /**
     * 初始化合约
     * @param contract 合约地址
     * @param address 钱包地址
     * @param node 节点
     */
    public void initContract(String contract,String address,String node) {
        GsonUtil params = new GsonUtil("{}");
        params.putString("node",node);
        params.putString("contract",contract);
        params.putString("address",address);
        JSUtil.getInstance().callJS("initContract", params, null);
    }

    /**
     * 创建一个钱包，返回密钥，地址，助记词
     * @param callback {"secret":"","address":"","words":""}
     */
    @Override
    public void createWallet(WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        JSUtil.getInstance().callJS("createWallet", params, callback);
    }

    /**
     * 确认地址是否可用
     * @param address 地址
     * @param callback {"isAddress":""}
     */
    @Override
    public void isValidAddress(String address, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("address",address);
        JSUtil.getInstance().callJS("isValidAddress", params, callback);

    }

    /**
     * 确认密钥是否可用
     * @param secret 密钥
     * @param callback {"isSecret":""}
     */
    @Override
    public void isValidSecret(String secret, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("secret",secret);
        JSUtil.getInstance().callJS("isValidSecret", params, callback);
    }

    /**
     * 导入密钥
     * @param secret 密钥
     * @param password 密码
     * @param callback {"secret":"","address":""}
     */
    @Override
    public void importSecret(String secret,String password, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("secret",secret);
        params.putString("password",password);
        JSUtil.getInstance().callJS("importSecret", params, callback);
    }

    /**
     * 导入助记词
     * @param words 密钥
     * @param password 密码
     * @param callback {"secret":"","address":""}
     */
    @Override
    public void importWords(String words,String password, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("words",words);
        params.putString("password",password);
        JSUtil.getInstance().callJS("importWords", params, callback);

    }

    /**
     * 将地址转换为Iban
     * @param address 地址
     * @param callback {"Iban":""}
     */
    @Override
    public void toIban(String address, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("address",address);
        JSUtil.getInstance().callJS("toIban", params, callback);
    }

    /**
     * 将Iban转换为地址
     * @param iban Iban地址
     * @param callback {"address":""}
     */
    @Override
    public void fromIban(String iban, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("iban",iban);
        JSUtil.getInstance().callJS("fromIban", params, callback);

    }


    /**
     * 获取余额
     * @param address 地址
     * @param callback {"balance":""}
     */
    @Override
    public void getBalance(String address, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("address",address);
        JSUtil.getInstance().callJS("getBalance", params, callback);
    }

    /**
     * 发送erc20交易
     * @param data {"address":"","to":"","secret":"","value":"","gasLimit":“”,"gasPrice":"","data":"","contract":""}
     * @param callback {"hash":""}
     */
    @Override
    public void sendErc20Transaction(GsonUtil data, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        JSUtil.getInstance().callJS("sendErc20Transaction", data, callback);

    }

    /**
     * 发送erc20交易
     * @param data {"address":"","to":"","secret":"","value":"","gasLimit":“”,"gasPrice":"","data":""}
     * @param callback {"hash":""}
     */
    @Override
    public void sendTransaction(GsonUtil data, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        JSUtil.getInstance().callJS("sendTransaction", data, callback);
    }

    /**
     * 获取对应Erc20币的余额
     * @param Contract Erc20地址
     * @param address 钱包地址
     * @param callback {"balance":""}
     */
    @Override
    public void getErc20Balance(String Contract, String address, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("contract",Contract);
        params.putString("address",address);
        JSUtil.getInstance().callJS("getErc20Balance", params, callback);

    }

    /**
     * 获取链上的GasPrice
     * @param callback {"GasPrice":""}
     */
    @Override
    public void getGasPrice(WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        JSUtil.getInstance().callJS("getGasPrice", params, callback);
    }

    /**
     * 获取交易详情
     * @param hash 交易hash
     * @param callback {"data":"{}"}
     */
    @Override
    public void getTransactionDetail(String hash, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("hash",hash);
        JSUtil.getInstance().callJS("getTransactionDetail", params, callback);

    }

    /**
     * 获取交易详情
     * @param hash 交易hash
     * @param callback {"data":"{}"}
     */
    @Override
    public void getTransactionReceipt(String hash, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil params = new GsonUtil("{}");
        params.putString("hash",hash);
        JSUtil.getInstance().callJS("getTransactionReceipt", params, callback);
    }

    /**
     * 签名交易
     * @param data
     * @param callback
     */
    @Override
    public void SignTransaction(GsonUtil data, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        JSUtil.getInstance().callJS("SignTransaction", data, callback);
    }

    private boolean checkInit(WCallback callback) {
        return JSUtil.getInstance().checkInit(callback);
    }
}
