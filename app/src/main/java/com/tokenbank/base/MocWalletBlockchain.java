package com.tokenbank.base;
import android.content.Context;
import android.text.TextUtils;
import com.tokenbank.config.Constant;
import com.tokenbank.utils.FileUtil;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.Util;

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
    public double getRecommendGas(double gas) {
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
    }


    @Override
    public void queryTransactionList(final GsonUtil params, final WCallback callback) {

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
