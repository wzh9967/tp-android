package com.tokenbank.base;
import android.content.Context;
import android.text.TextUtils;
import com.tokenbank.config.Constant;
import com.tokenbank.net.api.jtrequest.JTTransactionDetailsRequest;
import com.tokenbank.net.api.jtrequest.JTTransactionListRequest;
import com.tokenbank.net.api.jtrequest.JTTransactionRequest;
import com.tokenbank.net.load.RequestPresenter;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.Util;

//FSTWalletBlockchain更新完毕后移除
public class MocWalletBlockchain implements BaseWalletUtil {

    private final static String TAG = "MocWalletBlockchain";
    private long gasLimit = 21000;

    @Override
    public void init() {
    }

    @Override
    public void createWallet(final String walletName, final String walletPassword, final WCallback callback) {

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
    public void gasPrice(WCallback callback) {

    }

    @Override
    public void signedTransaction(GsonUtil data, WCallback callback) {

    }

    @Override
    public void sendSignedTransaction(String rawTransaction, final WCallback callback) {

    }

    @Override
    public boolean isWalletLegal(String pk, String address) {
        //TODO, 这里再做严格限制
        if (!TextUtils.isEmpty(pk) && !TextUtils.isEmpty(address)) {
            return true;
        }
        return false;
    }

    @Override
    public void generateReceiveAddress(String walletAddress, double amount, String token, WCallback callback) {
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
    public void setGasLimit(long gasLimit){
        this.gasLimit = gasLimit;
    }

    public long getGasLimit(long gasLimit){
        return this.gasLimit;
    }

    @Override
    public void calculateGasInToken(double gas, double gasPrice, boolean defaultToken, WCallback callback) {
        double price = gasPrice*gas;
        GsonUtil gasJson = new GsonUtil("{}");
        gasJson.putString("gas", "0.01 SWT");
        gasJson.putDouble("gasPrice", 0.01f);
        callback.onGetWResult(0, gasJson);
    }

    @Override
    public void gasSetting(Context context, double gasPrice, boolean defaultToken, WCallback callback) {

    }

    @Override
    public double getRecommendGas(double gas, boolean defaultToken) {
        //从设置的Gas费中获取
        return 0.01;
    }

    @Override
    public String getDefaultTokenSymbol() {
        return "SWT";
    }

    @Override
    public int getDefaultDecimal() {
        return 0;
    }

    @Override
    public void getTokenInfo(String token, WCallback callback) {

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
    public void queryTransactionDetails(String hash, final WCallback callback) {
        if (TextUtils.isEmpty(hash)) {
            callback.onGetWResult(-1, new GsonUtil("{}"));
            return;
        }
        new RequestPresenter().loadJtData(new JTTransactionDetailsRequest(hash), new RequestPresenter.RequestCallback() {
            @Override
            public void onRequesResult(int ret, GsonUtil json) {
                if (ret == 0) {
                    GsonUtil extraJson = new GsonUtil("{}");
                    GsonUtil translatedData = new GsonUtil("{}");
                    translatedData.putLong("timeStamp", json.getLong("date", 0l));
                    translatedData.putString("input", "");
                    translatedData.putString("hash", json.getString("hash", ""));
                    translatedData.putDouble("fee", json.getDouble("fee", 0.0f));
                    translatedData.putDouble("real_value", json.getObject("amount", "{}").getDouble("value", 0.0f));
                    translatedData.putString("tokenSymbol", json.getObject("amount", "{}").getString("currency", ""));
                    int status = 5; //未知状态S
                    if (TextUtils.equals(json.getString("result", ""), "tesSUCCESS")) {
                        status = 1;
                    }
                    translatedData.putInt("txreceipt_status", status);
                    String type = json.getString("type", "");
                    if (TextUtils.equals(type, "sent")) {
                        translatedData.putString("from", WalletInfoManager.getInstance().getWAddress());
                        translatedData.putString("to", json.getString("counterparty", ""));
                    } else if (TextUtils.equals(type, "received")) {
                        translatedData.putString("to", WalletInfoManager.getInstance().getWAddress());
                        translatedData.putString("from", json.getString("counterparty", ""));
                    }
                    extraJson.put("data", translatedData);
                    callback.onGetWResult(0, extraJson);
                } else {
                    callback.onGetWResult(ret, json);
                }
            }
        });
    }


    @Override
    public void queryTransactionList(final GsonUtil params, final WCallback callback) {
        int pagesize = params.getInt("pagesize", 10);
        String token = params.getString("token", "");
        String marker = params.getString("marker", "");
        new RequestPresenter().loadJtData(new JTTransactionListRequest(pagesize, token, marker), new RequestPresenter.RequestCallback() {
            @Override
            public void onRequesResult(int ret, GsonUtil json) {
                if (ret == 0) {
                    GsonUtil translatedData = new GsonUtil("{}");
                    GsonUtil dataList = new GsonUtil("[]");
                    GsonUtil payments = json.getArray("payments", "[]");
                    int len = payments.getLength();
                    for (int i = 0; i < len; i++) {
                        GsonUtil payment = payments.getObject(i, "{}");
                        GsonUtil item = new GsonUtil("{}");
                        item.putDouble("fee", payment.getDouble("fee", 0.0f));
                        item.putString("hash", payment.getString("hash", ""));
                        item.putString("tokenSymbol", payment.getObject("amount", "{}").getString("currency", ""));
                        item.putString("real_value", payment.getObject("amount", "{}") .getString("value", ""));
                        item.putLong("timeStamp", payment.getLong("date", 0l));
                        String type = payment.getString("type", "");
                        if (TextUtils.equals(type, "sent")) {
                            item.putString("from", WalletInfoManager.getInstance().getWAddress());
                            item.putString("to", payment.getString("counterparty", ""));
                            dataList.add(item);
                        } else if (TextUtils.equals(type, "received")) {
                            item.putString("to", WalletInfoManager.getInstance().getWAddress());
                            item.putString("from", payment.getString("counterparty", ""));
                            dataList.add(item);
                        }
                        //todo 对于type 类型未知，暂不加入记录列表，以免引起困惑
                    }
                    translatedData.put("data", dataList);
                    translatedData.putString("marker", json.getObject("marker", "{}").toString());
                    callback.onGetWResult(0, translatedData);
                } else {
                    callback.onGetWResult(-1, new GsonUtil("{}"));
                }
            }
        });
    }

    @Override
    public double getValue(int decimal, double originValue) {

        if (decimal <= 0) {
            decimal = getDefaultDecimal();
        }
        return Util.formatDouble(3, Util.translateValue(decimal, originValue));
    }

    @Override
    public void queryBalance(String address, final WCallback callback) {

    }
    @Override
    public String getTransactionSearchUrl(String hash) {
        return Constant.swt_transaction_search_url + hash;
    }

    @Override
    public GsonUtil loadTransferTokens(Context context) {
        String data = FileUtil.getConfigFile(context, "jingtumTokens.json");
        return new GsonUtil(data);
    }

    private boolean checkInit(WCallback callback) {
        return JSUtil.getInstance().checkInit(callback);
    }
}
