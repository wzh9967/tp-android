package com.tokenbank.base;


import com.tokenbank.utils.GsonUtil;

public interface WalletUtil {

    void createWallet(WCallback callback);

    void isValidAddress(String address, WCallback callback);

    void isValidSecret(String secret, WCallback callback);

    void importSecret(String secret,String password, WCallback callback);

    void importWords(String words, String password, WCallback callback);

    void toIban(String address, WCallback callback);

    void fromIban(String iban, WCallback callback);

    void getBalance(String address, WCallback callback);

    void sendErc20Transaction(GsonUtil data, WCallback callback);

    void sendTransaction(GsonUtil data, WCallback callback);

    void getErc20Balance(String Contract, String address, WCallback callback);

    void getGasPrice(WCallback wCallback);

    void getTransactionDetail(String hash, WCallback wCallback );

    void getTransactionReceipt(String hash, WCallback wCallback );

    void SignTransaction(GsonUtil data, WCallback wCallback );
}
