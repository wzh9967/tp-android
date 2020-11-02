package com.tokenbank.base;


import android.content.Context;

import com.tokenbank.utils.GsonUtil;

import java.math.BigInteger;


public interface BaseWalletUtil {

    void init();

    void initStorm3(String url,WCallback wCallback);

    void createWallet(String walletPassword, WCallback callback);

    void importWallet(String secret, int type, WCallback callback);

    void toIban(String address, WCallback callback);

    void fromIban(String ibanAddress, WCallback callback);

    void signedTransaction(GsonUtil data, WCallback callback);

    boolean isValidAddress(String address,WCallback callback);

    boolean isValidSecret(String secret,WCallback callback);

    void generateReceiveAddress(String walletAddress, double amount, String token, WCallback callback);

    String calculateGasInToken(int decimal,String gasLimit, Double gasPrice);

    void gasSetting(Context context, double gasPrice, boolean defaultToken, WCallback callback);

    double getRecommendGas(double gas);

    String getDefaultTokenSymbol();

    int getDefaultDecimal();

    void translateAddress(String sourceAddress, WCallback callback);

    boolean checkWalletAddress(String receiveAddress);

    boolean checkWalletPk(String privateKey);

    void queryTransactionList(int pagesize,String address, WCallback callback);

    String toValue(int decimal, String originValue);

    String fromValue(int decimal , String Value);

    GsonUtil loadTransferTokens(Context context);

    String getTransactionSearchUrl(String hash);

    void queryErc20TransactionList(int PageSize,int Decimal,String contract,String address, final WCallback callback);

    void sendErc20Transaction(GsonUtil data,WCallback callback);

    void sendTransaction(GsonUtil data,WCallback callback);

    void getErc20GasPrice(String Contract, WCallback callback);

    void getErc20Balance(String Contract,String address, WCallback callback);

    void getBalance(String address, WCallback wCallback);

    void getGasPrice(WCallback wCallback);

    void importSecret(String secret, WCallback wCallback);

    void importWords(String words, WCallback wCallback);

    String getGasLimit();

    void setGasLimit(String gasLimit);

    Double calculateGasFee();

    void Test(WCallback callback);

    String getDataByContract(String contract,String key);
}
