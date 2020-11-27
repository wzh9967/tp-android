package com.tokenbank.utils;
import android.content.Context;
import android.text.TextUtils;

import com.tokenbank.wallet.JSUtil;
import com.tokenbank.base.WCallback;
import com.tokenbank.config.AppConfig;
import com.tokenbank.config.Constant;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 分离出来的和实例无关的方法
 * 用于钱包相关数据处理
 */
public class FstWalletUtil{
    private final static String TAG = "FstWalletUtil";

    /**
     * 生成二维码字符串
     * @param walletAddress
     * @param contract
     * @param amount
     * @param token
     * @return
     */
    public static String generateReceiveAddress(String walletAddress, String contract, double amount, String token) {
        final GsonUtil address = new GsonUtil("{}");
        final double tmpAmount = amount < 0 ? 0.0f : amount;
        if (TextUtils.isEmpty(walletAddress) || TextUtils.isEmpty(token)) {
            return null;
        }
        if(contract.equals("")){
            contract = "000000000000000000000000000000000000000000";
        }
        return String.format("fst:%s?contract=%s#amount=%f&token=%s", walletAddress,contract, tmpAmount, token);
    }

    /**
     * 检查地址格式
     * @param receiveAddress
     * @return
     */
    public static boolean checkWalletAddress(String receiveAddress) {
        if (TextUtils.isEmpty(receiveAddress) || receiveAddress.length() != 42) {
            return false;
        }
        return true;
    }

    /**
     * 检查私钥格式
     * @param privateKey
     * @return
     */
    public static boolean checkWalletPk(String privateKey) {
        //todo
        return true;
    }


    /**
     * 转换交易记录的格式
     * @param json
     * @return
     */
    public static GsonUtil ConvertJson(GsonUtil json){
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

    /**
     * 通过合约地址来查询配置的数据
     * @param contract
     * @param key
     * @return
     */
    public static String getDataByContract(String contract, String key) {
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


    /**
     * 获取交易查询的浏览器地址
     * @param hash
     * @return
     */
    public static String getTransactionSearchUrl(String hash) {
        return Constant.MOC_Hash_SERVER + hash;
    }


    public GsonUtil loadTransferTokens(Context context) {
       // String data = FileUtil.getConfigFile(context, "jingtumTokens.json");
      ///  return new GsonUtil(data);
        return null;
    }

    public static boolean checkInit(WCallback callback) {
        return JSUtil.getInstance().checkInit(callback);
    }

    /**
     * 时间格式转换
     * @param strDate
     * @return
     */
    public static String DateFrom(String strDate){
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

    public void Test(WCallback callback) {
        GsonUtil data = new GsonUtil("{}");
        String hash = "0x5cef50ad6ebcf0194a9f36d94a5358deb5f4d82165e74a62d41ed282712b0b1c";
        data.putString("hash", hash);
        JSUtil.getInstance().callJS("getTransactionDetail", data, callback);
    }
}
