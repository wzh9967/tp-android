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
import java.math.BigDecimal;
import java.math.BigInteger;;

public class MoacWalletBlockchain implements BaseWalletUtil {
    private final static String TAG = "MoacWalletBlockchain";
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
    public void signedTransaction(GsonUtil data, WCallback callback) {

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
        if (!checkInit(callback)) {
            return;
        }
        if (TextUtils.isEmpty(walletAddress) || TextUtils.isEmpty(token)) {
            callback.onGetWResult(-1, new GsonUtil("{}"));
            return;
        }
        final double tmpAmount = amount < 0 ? 0.0f : amount;
        final GsonUtil address = new GsonUtil("{}");
        String receiveStr = String.format("jingtum:%s?amount=%f&token=%s", walletAddress, tmpAmount, token);
        address.putString("receiveAddress", receiveStr);
        callback.onGetWResult(0, address);
    }

    @Override
    public void setGasLimit(String gasLimit){
        this.gasLimit = gasLimit;
    }

    @Override
    public Double calculateGasFee() {
        return 0.0;
    }

    @Override
    public String getGasLimit(){
        return this.gasLimit;
    }

    @Override
    public void gasSetting(Context context, double gasPrice, boolean defaultToken, WCallback callback) {

    }

    @Override
    public double getRecommendGas(double gas) {
        //从设置的Gas费中获取
        return 0.01;
    }

    @Override
    public String getDefaultTokenSymbol() {
        return "moab";
    }

    @Override
    public int getDefaultDecimal() {
        return Constant.DefaultDecimal;
    }


    @Override
    public void translateAddress(String sourceAddress, WCallback callback) {
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
                        GsonUtil item = new GsonUtil("{}");
                        item.putString("blockHash", payment.getString("blockHash", ""));
                        item.putString("blockNumber", payment.getString("blockNumber", ""));
                        item.putString("contract",payment.getString("to",""));
                        item.putString("gasUsed", payment.getString("gasUsed", ""));
                        item.putDouble("gasPrice",payment.getDouble("gasPrice",0.0f));
                        item.putDouble("status",payment.getDouble("status",0.0f));
                        item.putString("from", payment.getString("from", ""));
                        item.putString("timestamp", payment.getString("timestamp", ""));
                        item.putString("transactionHash", payment.getString("transactionHash", ""));
                        String input = payment.getString("input","");
                        String value = new BigInteger(input.substring(74), 16).toString();
                        item.putString("value",toValue(Decimal, value));
                        item.putString("to", "0x"+input.substring(34,74));
                        item.putInt("txreceipt_status",payment.getInt("txreceipt_status",1));
                        dataList.add(item);
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
                        item.putString("timestamp", payment.getString("timestamp", ""));
                        item.putString("transactionHash", payment.getString("transactionHash", ""));
                        item.putString("input", payment.getString("input", ""));
                        item.putInt("txreceipt_status", payment.getInt("txreceipt_status", 1));
                        item.putString("gasUsed", payment.getString("gasUsed", ""));
                        item.putDouble("gasPrice", payment.getDouble("gasPrice", 0.0f));
                        String input = payment.getString("input", "");
                        if(input.length() == 128 || input.startsWith("0xa9059cbb")){
                            String value = new BigInteger(input.substring(74), 16).toString();
                            String contract = payment.getString("to","");
                            int Decimal = Integer.parseInt(getDataByContract(contract,"decimal"));
                            item.putString("value",toValue(Decimal,value));
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
                    callback.onGetWResult(0, translatedData);
                } else {
                    callback.onGetWResult(-1, new GsonUtil("{}"));
                }
            }
        });
    }

    @Override
    public String getDataByContract(String contract,String key) {
        GsonUtil currency =new GsonUtil(FileUtil.getConfigFile(AppConfig.getContext(), "currency.json"));
        GsonUtil payments = currency.getArray("data", "[]");
        int len = payments.getLength();
        for (int i = 0; i < len; i++) {
            GsonUtil payment = payments.getObject(i, "{}");
            if (payment.getString("contract", "").equals(contract)) {
                return payment.getString(key, "");
            }
        }
        return "";
    }

    @Override
    public String toValue(int decimal, String originValue) {
        if (decimal <= 0) {
            decimal = getDefaultDecimal();
        }
        BigDecimal origindate = new BigDecimal(originValue);
        origindate = Util.translateValue(decimal, origindate);
        return origindate.setScale(3, BigDecimal.ROUND_DOWN).toString();
    }

    @Override
    public String fromValue(int decimal, String Value) {
        BigDecimal ValueTempe = new BigDecimal(Value);
        if (decimal <= 0) {
            decimal = getDefaultDecimal();
        }
        ValueTempe = Util.tokenToWei(decimal, ValueTempe);
        return ValueTempe.setScale(0,BigDecimal.ROUND_DOWN).toString();
    }

    @Override
    public String calculateGasInToken(int decimal,String gasLimit, Double gasPrice) {
        Double gas = Double.valueOf(gasLimit);
        //DecimalFormat df = new DecimalFormat("0.0000000");
        //String str = df.format();
        BigDecimal gasFee = new BigDecimal(gas*gasPrice);
        gasFee = Util.translateValue(decimal, gasFee);
        return  gasFee.setScale(7, BigDecimal.ROUND_DOWN).toString();
    }
    @Override
    public String getTransactionSearchUrl(String hash) {
        return Constant.swt_transaction_search_url + hash;
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
        json.putInt("gasPrice", 1000000);
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
}
