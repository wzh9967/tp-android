package com.tokenbank.base;

import android.content.Context;

import com.tokenbank.utils.GsonUtil;

import java.math.BigDecimal;


public class TestWalletBlockchain implements BaseWalletUtil {
    @Override
    public void init() {

    }

    @Override
    public void initStorm3(String url, WCallback wCallback) {

    }

    @Override
    public void createWallet(String walletPassword, WCallback callback) {

    }


    @Override
    public void importWallet(String privateKey, int type, WCallback callback) {
    }

    @Override
    public void toIban(String address, WCallback callback) {

    }

    @Override
    public void fromIban(String ibanAddress, WCallback callback) {

    }



    @Override
    public boolean isValidAddress(String address, WCallback callback) {
        return false;
    }

    @Override
    public boolean isValidSecret(String secret, WCallback callback) {
        return false;
    }

    @Override
    public void generateReceiveAddress(String walletAddress, double amount, String token, WCallback callback) {

    }

    @Override
    public String calculateGasInToken(int decimal, String gasLimit, Double gasPrice) {
        return null;
    }

    @Override
    public boolean checkWalletAddress(String receiveAddress) {
        return false;
    }

    @Override
    public boolean checkWalletPk(String privateKey) {
        return false;
    }



    @Override
    public void queryTransactionList(int pagesize,String address, WCallback callback) {

    }

    @Override
    public String toValue(int decimal, String originValue) {
        return null;
    }

    @Override
    public String fromValue(int decimal, String Value) {
        return null;
    }

    @Override
    public String getTransactionSearchUrl(String hash) {
        return null;
    }

    @Override
    public void queryErc20TransactionList(int PageSize,int decimal ,String contract, String address, WCallback callback) {

    }

    @Override
    public void sendErc20Transaction(GsonUtil data, WCallback callback) {

    }

    @Override
    public void sendTransaction(GsonUtil data, WCallback callback) {

    }


    @Override
    public void getErc20GasPrice(String Contract, WCallback callback) {
    }

    @Override
    public void getErc20Balance(String Contract, String address, WCallback callback) {

    }

    @Override
    public void getBalance(String address, WCallback wCallback) {

    }

    @Override
    public void getGasPrice(WCallback wCallback) {

    }


    @Override
    public void importSecret(String secret, WCallback wCallback) {

    }

    @Override
    public void importWords(String words, WCallback wCallback) {

    }



    @Override
    public void Test(WCallback callback) {

    }

    @Override
    public String getDataByContract(String contract, String key) {
        return null;
    }

    @Override
    public void getTransactionDetail(String hash, WCallback wCallback) {

    }

    @Override
    public void getTransactionReceipt(String hash, WCallback wCallback) {

    }

    @Override
    public GsonUtil ConvertJson(GsonUtil json) {
        return null;
    }

    @Override
    public GsonUtil loadTransferTokens(Context context) {
        return new GsonUtil("{}");
    }
}
