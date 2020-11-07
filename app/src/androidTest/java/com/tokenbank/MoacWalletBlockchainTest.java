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
    public MoacWallet moacWallet;
    private String transactionHash = "transactionHash";
    private final static int PageSize = 1;
    private final static String address = "0x981d4bc976c221b3b42270be6dcab72d37d2e0cd";
    private final static String secret = "0x1a0ad31a04ed4dbcec91a8a54c0d87187b50ab60e03139f404533332e9b31917";
    private final static String password = "Qq123456";
    private final static String rawTransaction = "Qq123456";
    private final static String rawErc20Transaction = "Qq123456";
    private final static String words = "chase spoon junior ability pepper twice mutual jungle trap exit family famous";
    private final static String Contract = "0xc19323c4c4298673b41c6847ba937b5e6d9d77db";
    @Before
    public void setUp() {
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
                sigal.countDown();
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
                sigal.countDown();
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
        moacWallet.isValidSecret(secret,new JCallback() {
            @Override
            public void completion(JCCJson json) {
                Boolean isValid = json.getBoolean("isValid");
                Assert.assertEquals(isValid,true);
                sigal.countDown();
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
                Assert.assertNotNull(json.getString("secret"));
                Assert.assertNotNull(json.getString("address"));
                sigal.countDown();
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
        moacWallet.importSecret(secret,new JCallback() {
            @Override
            public void completion(JCCJson json) {
                String secret1 = json.getString("secret");
                String address1 = json.getString("address");
                Assert.assertEquals(secret1,secret);
                Assert.assertEquals(address1,address);
                sigal.countDown();
            }
        });
        try {
            sigal.await();
        } catch (InterruptedException e) {

        }
    }
}
