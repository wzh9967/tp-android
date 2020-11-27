package com.tokenbank.net.query;

import com.tokenbank.base.WCallback;
import com.tokenbank.net.api.mcrequest.ERC20TransactionRequest;
import com.tokenbank.net.api.mcrequest.MCTransactionListRequest;
import com.tokenbank.net.load.RequestPresenter;
import com.tokenbank.utils.FstWalletUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.Util;

import java.math.BigInteger;

public class QueryTransaction implements QueryDataFromNet{

    @Override
    public  void queryErc20TransactionList(int PageSize, int Decimal, final String contract, final String address, final WCallback callback) {
        if (!FstWalletUtil.checkInit(callback)) {
            return;
        }
        new RequestPresenter().loadJtData(new ERC20TransactionRequest(PageSize,contract,address), new RequestPresenter.RequestCallback() {
            @Override
            public void onRequesResult(int ret, GsonUtil json) {
                if (ret == 1) {
                    GsonUtil translatedData = new GsonUtil("{}");
                    GsonUtil dataList = new GsonUtil("[]");
                    GsonUtil payments = json.getArray("data", "[]");
                    int len = payments.getLength();
                    for (int i = 0; i < len; i++) {
                        GsonUtil payment = payments.getObject(i, "{}");
                        String to = payment.getString("to","");
                        if(to.equals(contract)){
                            GsonUtil item = new GsonUtil("{}");
                            item.putString("blockHash", payment.getString("blockHash", ""));
                            item.putString("blockNumber", payment.getString("blockNumber", ""));
                            item.putString("contract",to);
                            item.putString("gasUsed", payment.getString("gasUsed", ""));
                            item.putDouble("gasPrice",payment.getDouble("gasPrice",0.0f));
                            item.putDouble("status",payment.getDouble("status",0.0f));
                            item.putString("from", payment.getString("from", ""));
                            item.putString("timestamp", FstWalletUtil.DateFrom(payment.getString("timestamp", "")));
                            String hash = payment.getString("transactionHash", "");
                            item.putString("transactionHash", hash);
                            String input = payment.getString("input","");
                            String value = new BigInteger(input.substring(74), 16).toString();
                            item.putString("value", Util.toValue(Decimal, value));
                            item.putString("to", "0x"+input.substring(34,74));
                            item.putInt("txreceipt_status",payment.getInt("txreceipt_status",1));
                            dataList.add(item);
                        }
                    }
                    translatedData.put("data", dataList);
                    callback.onGetWResult(0, translatedData);
                } else {
                    callback.onGetWResult(-1, new GsonUtil("{}"));
                }
            }
        });
    }


    @Override
    public  void queryTransactionList(int pagesize, final String address, final WCallback callback) {
        if (!FstWalletUtil.checkInit(callback)) {
            return;
        }
        new RequestPresenter().loadJtData(new MCTransactionListRequest(pagesize,address), new RequestPresenter.RequestCallback() {
            @Override
            public void onRequesResult(int ret, GsonUtil json) {
                if (ret == 1) {
                    GsonUtil translatedData = FstWalletUtil.ConvertJson(json);
                    callback.onGetWResult(0, translatedData);
                } else {
                    callback.onGetWResult(-1, new GsonUtil("{}"));
                }
            }
        });
    }
}
