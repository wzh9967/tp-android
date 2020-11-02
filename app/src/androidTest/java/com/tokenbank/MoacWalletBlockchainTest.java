package com.tokenbank;

import android.support.test.runner.AndroidJUnit4;

import com.android.jccdex.app.base.JCallback;
import com.android.jccdex.app.moac.MoacWallet;
import com.android.jccdex.app.util.JCCJson;
import com.tokenbank.base.BaseWalletUtil;
import com.tokenbank.base.TBController;
import com.tokenbank.base.WCallback;
import com.tokenbank.utils.GsonUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class MoacWalletBlockchainTest {
    public BaseWalletUtil mWalletUtil;
    public MoacWallet moacWallet;
    private String transactionHash = "transactionHash";
    private final static int PageSize = 1;
    private final static String address = "0x981d4bc976c221b3b42270be6dcab72d37d2e0cd";
    private final static String secret = "0x1a0ad31a04ed4dbcec91a8a54c0d87187b50ab60e03139f404533332e9b31917";
    private final static String password = "Qq123456";
    private final static String rawTransaction = "Qq123456";
    private final static String rawErc20Transaction = "Qq123456";
    private final static String words = "Qq123456";
    private final static String Contract = "Qq123456";
    @Before
    public void setUp() {
        mWalletUtil = TBController.getInstance().getWalletUtil();
        moacWallet = TBController.getInstance().getMoacWallet();
    }

    @Test
    public void createWalletTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        moacWallet.createWallet(new JCallback() {
            @Override
            public void completion(JCCJson json) {
                Assert.assertNotNull(json.getString("secret"));
                Assert.assertNotNull(json.getString("address"));
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void isValidAddressTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        moacWallet.isValidAddress(address,new JCallback() {
            @Override
            public void completion(JCCJson json) {
                Boolean isValid = json.getBoolean("isValid");
                Assert.assertEquals(isValid,true);
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void isValidSecretTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        moacWallet.isValidSecret(address,new JCallback() {
            @Override
            public void completion(JCCJson json) {
                Boolean isValid = json.getBoolean("isValid");
                Assert.assertEquals(isValid,true);
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }


    @Test
    public void importWordsTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        moacWallet.importWords(words,new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String secret1 = json.getString("secret");
                String address1 = json.getString("address");
                Assert.assertEquals(secret1,secret);
                Assert.assertEquals(address1,address);
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void importSecretTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        moacWallet.importSecret(address,new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String secret1 = json.getString("secret");
                String address1 = json.getString("address");
                Assert.assertEquals(secret1,secret);
                Assert.assertEquals(address1,address);
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }


    @Test
    public void sendTransactionTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        String address = "0x981d4bc976c221b3b42270be6dcab72d37d2e0cd";
        String to = "0xb4860ef01adae0f0714776d1c35e7ad4b0937b79";
        String secret = "0x1a0ad31a04ed4dbcec91a8a54c0d87187b50ab60e03139f404533332e9b31917";
        String note = "123456";
        GsonUtil data = new GsonUtil("{}");
        data.putString("address",address);
        data.putString("to",to);
        data.putString("secret",secret);
        data.putString("value","100");
        data.putInt("gas",22000);
        data.putString("gasPrice","100000000");
        data.putString("data",note);
        mWalletUtil.sendTransaction(data,new WCallback(){
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                Assert.assertEquals(0, ret);
                Assert.assertNotNull(extra.getString("hash",""));
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void gasPriceTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        mWalletUtil.getGasPrice(new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                Assert.assertEquals(0, ret);
                Assert.assertNotNull(extra.getString("GasPrice","1000000000"));
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }



    @Test
    public void getBalanceTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        mWalletUtil.getBalance(address,new WCallback(){
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                Assert.assertEquals(0, ret);
                Assert.assertNotEquals(extra.getString("balance",""),"");
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void queryTransactionListTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        mWalletUtil.queryTransactionList(PageSize,address, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                Assert.assertEquals(0, ret);
                GsonUtil transactionRecord = extra.getArray("data", "[]");
                Assert.assertEquals(transactionHash, transactionRecord.getString("transactionHash", ""));
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }


    @Test
    public void queryErc20TransactionListTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        mWalletUtil.queryErc20TransactionList(PageSize,18,Contract,address, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                Assert.assertEquals(0, ret);
                GsonUtil transactionRecord = extra.getArray("data", "[]");
                Assert.assertEquals(transactionHash, transactionRecord.getString("transactionHash", ""));
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }
    @Test
    public void sendErc20TransactionTest(){
        final CountDownLatch sigal = new CountDownLatch(1);
        //String from, String to, double Value,String Contract,String secret
        String address = "0x981d4bc976c221b3b42270be6dcab72d37d2e0cd";
        String contract = "0xba753eb6cc555c867e4e7a554f3e13018a9c075b";
        String to = "0xb4860ef01adae0f0714776d1c35e7ad4b0937b79";
        String secret = "0x1a0ad31a04ed4dbcec91a8a54c0d87187b50ab60e03139f404533332e9b31917";
        GsonUtil data = new GsonUtil("{}");
        data.putString("address",address);
        data.putString("contract",contract);
        data.putString("to",to);
        data.putString("secret",secret);
        data.putInt("value",100000000);
        data.putInt("gasLimit",99000);
        data.putString("gasPrice","1000000000");
        mWalletUtil.sendErc20Transaction(data,new WCallback(){
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                Assert.assertEquals(0, ret);
                Assert.assertNotNull(extra.getString("hash",""));
            }
        });
    }
    @Test
    public void getErc20GasPrice(){
        final CountDownLatch sigal = new CountDownLatch(1);
        mWalletUtil.getErc20GasPrice(address, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                Assert.assertEquals(0, ret);
                Assert.assertNotNull(extra.getString("GasPrice",""));
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }
    @Test
    public void getErc20Balance(){
        final CountDownLatch sigal = new CountDownLatch(1);
        mWalletUtil.getErc20Balance(Contract, address, new WCallback() {
            @Override
            public void onGetWResult(int ret, GsonUtil extra) {
                Assert.assertEquals(0, ret);
                Assert.assertNotEquals(extra.getString("balance",""),"");
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }
}
