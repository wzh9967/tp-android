package com.tokenbank.base;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tokenbank.config.AppConfig;
import com.tokenbank.config.Constant;
import com.tokenbank.net.api.mcrequest.ERC20TransactionRequest;
import com.tokenbank.net.api.mcrequest.MCTransactionListRequest;
import com.tokenbank.net.load.RequestPresenter;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.Util;
import com.tokenbank.view.TagAdapter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;;

public class FstWalletBlockchain implements BaseWalletUtil {
    private final static String TAG = "FstWalletBlockchain";
    private String gasLimit = "22000";
    @Override
    public void init() {
    }

    @Override
    public void initStorm3(String url, WCallback wCallback) {

    }


    @Override
    public void createWallet(final String walletPassword, final WCallback callback) {
        int blockType = 1;
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putInt("blockType", blockType);
        JSUtil.getInstance().callJS("createJtWallet", json, callback);
    }

    @Override
    public void importWallet(String privateKey, int type, WCallback callback) {

    }

    @Override
    public void toIban(String ethAddress, WCallback callback) {
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
    public void generateReceiveAddress(String walletAddress,String contract,double amount, String token, WCallback callback) {
        final GsonUtil address = new GsonUtil("{}");
        final double tmpAmount = amount < 0 ? 0.0f : amount;
        if (!checkInit(callback)) {
            return;
        }
        if (TextUtils.isEmpty(walletAddress) || TextUtils.isEmpty(token)) {
            callback.onGetWResult(-1, new GsonUtil("{}"));
            return;
        }
        if(contract.equals("")){
            contract = "000000000000000000000000000000000000000000";
        }
        String receiveStr = String.format("fst:%s?contract=%s#amount=%f&token=%s", walletAddress,contract, tmpAmount, token);
        address.putString("receiveAddress", receiveStr);
        callback.onGetWResult(0, address);
    }

    @Override
    public boolean checkWalletAddress(String receiveAddress) {
        if (TextUtils.isEmpty(receiveAddress) || receiveAddress.length() != 42) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkWalletPk(String privateKey) {
        //todo
        return true;
    }

    @Override
    public void queryErc20TransactionList(int PageSize,int Decimal,final String contract,final String address ,final WCallback callback) {
        if (!checkInit(callback)) {
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
                            item.putString("timestamp", DateFrom(payment.getString("timestamp", "")));
                            String hash = payment.getString("transactionHash", "");
                            item.putString("transactionHash", hash);
                            String input = payment.getString("input","");
                            String value = new BigInteger(input.substring(74), 16).toString();
                            item.putString("value",Util.toValue(Decimal, value));
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
    public void sendErc20Transaction(GsonUtil data, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        JSUtil.getInstance().callJS("sendErc20Transaction", data, callback);
    }

    @Override
    public void sendTransaction(GsonUtil data, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        JSUtil.getInstance().callJS("sendTransaction", data, callback);
    }

    @Override
    public void queryTransactionList(int pagesize, final String address,final WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        new RequestPresenter().loadJtData(new MCTransactionListRequest(pagesize,address), new RequestPresenter.RequestCallback() {
            @Override
            public void onRequesResult(int ret, GsonUtil json) {
                if (ret == 1) {
                    GsonUtil translatedData = ConvertJson(json);
                    callback.onGetWResult(0, translatedData);
                } else {
                    callback.onGetWResult(-1, new GsonUtil("{}"));
                }
            }
        });
    }

    @Override
    public GsonUtil ConvertJson(GsonUtil json){
        GsonUtil translatedData = new GsonUtil("{}");
        GsonUtil dataList = new GsonUtil("[]");
        GsonUtil mobDataList = new GsonUtil("[]");
        GsonUtil payments = json.getArray("data", "[]");
        int len = payments.getLength();
        for (int i = 0; i < len; i++) {
            GsonUtil payment = payments.getObject(i, "{}");
            GsonUtil item = new GsonUtil("{}");
            item.putString("blockHash", payment.getString("blockHash", ""));
            item.putString("blockNumber", payment.getString("blockNumber", ""));
            item.putString("from", payment.getString("from", ""));
            item.putString("timestamp", DateFrom(payment.getString("timestamp", "")));
            item.putString("transactionHash", payment.getString("transactionHash", ""));
            item.putString("input", payment.getString("input", ""));
            item.putInt("txreceipt_status", payment.getInt("txreceipt_status", 1));
            item.putString("gasUsed", payment.getString("gasUsed", ""));
            item.putDouble("gasPrice", payment.getDouble("gasPrice", 0.0f));
            String input = payment.getString("input", "");
            if(input.length() == 138 && input.startsWith("0xa9059cbb")){
                String value = new BigInteger(input.substring(74), 16).toString();
                String contract = payment.getString("to","");
                int Decimal = Integer.parseInt(getDataByContract(contract,"decimal"));
                item.putString("value",Util.toValue(Decimal,value));
                item.putString("to", "0x"+input.substring(34,74));
                item.putString("isErc20","true");
                item.putString("contract",contract);
            }else{
                item.putString("value",payment.getString("value", ""));
                item.putString("to", payment.getString("to", ""));
                mobDataList.add(item);
            }
            dataList.add(item);
        }
        translatedData.put("data", dataList);
        translatedData.put("moabData",mobDataList);
        return translatedData;
    }

    @Override
    public String getDataByContract(String contract,String key) {
        GsonUtil currency =new GsonUtil(FileUtil.getConfigFile(AppConfig.getContext(), "currency.json"));
        GsonUtil payments = currency.getArray("data", "[]");
        int len = payments.getLength();
        for (int i = 0; i < len; i++) {
            GsonUtil payment = payments.getObject(i, "{}");
            String Contract = payment.getString("contract", "");
            if (Contract.equals(contract) || Contract.equals(contract.toLowerCase())) {
                return payment.getString(key, "");
            }
        }
        return "";
    }


    @Override
    public void getTransactionDetail(String hash, WCallback callback) {
        GsonUtil data = new GsonUtil("{}");
        data.putString("hash", hash);
        JSUtil.getInstance().callJS("getTransactionDetail", data, callback);
    }

    @Override
    public void getTransactionReceipt(String hash, WCallback callback) {
        GsonUtil data = new GsonUtil("{}");
        data.putString("hash", hash);
        JSUtil.getInstance().callJS("getTransactionReceipt", data, callback);
    }


    @Override
    public String getTransactionSearchUrl(String hash) {
        return Constant.MOC_Hash_SERVER + hash;
    }

    @Override
    public void getErc20GasPrice(String Contract, WCallback callback) {
        GsonUtil gasJson = new GsonUtil("{}");
        gasJson.putString("Contract",Contract);
        JSUtil.getInstance().callJS("getErc20GasPrice", gasJson, callback);
    }

    @Override
    public void getErc20Balance(String contract, String address, WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        if (address.equals(null) || contract.equals(null)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putString("address", address);
        json.putString("contract", contract);
        JSUtil.getInstance().callJS("getErc20Balance", json, callback);
    }


    @Override
    public void getBalance(String address, WCallback callback) {
        if (address.equals(null)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        json.putString("address", address);
        JSUtil.getInstance().callJS("getBalance", json, callback);
    }

    @Override
    public void getGasPrice(WCallback callback) {
        if (!checkInit(callback)) {
            return;
        }
        GsonUtil json = new GsonUtil("{}");
        JSUtil.getInstance().callJS("getGasPrice", json, callback);
    }


    @Override
    public void importSecret(String secret, WCallback wCallback) {

    }

    @Override
    public void importWords(String words, WCallback wCallback) {

    }

    @Override
    public GsonUtil loadTransferTokens(Context context) {
       // String data = FileUtil.getConfigFile(context, "jingtumTokens.json");
      ///  return new GsonUtil(data);
        return null;
    }

    private boolean checkInit(WCallback callback) {
        return JSUtil.getInstance().checkInit(callback);
    }

    public void Test(WCallback callback) {
        GsonUtil data = new GsonUtil("{}");
        String hash = "0x5cef50ad6ebcf0194a9f36d94a5358deb5f4d82165e74a62d41ed282712b0b1c";
        data.putString("hash", hash);
        JSUtil.getInstance().callJS("getTransactionDetail", data, callback);
    }

    private String DateFrom(String strDate){
        String date = strDate.replace("Z", " UTC");//注意是空格+UTC
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        try {
            Date d = format.parse(date);
            date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
