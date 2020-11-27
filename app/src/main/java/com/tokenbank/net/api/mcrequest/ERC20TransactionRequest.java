package com.tokenbank.net.api.mcrequest;

import com.android.volley.VolleyError;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BaseGetApiRequest;

/**
 * Created by Administrator on 2018/3/11.
 */

public class ERC20TransactionRequest extends BaseGetApiRequest {
    private int mPageSize;
    private String mAddress;
    private String contract;

    public ERC20TransactionRequest(int pagesize,String contract, String address) {
        this.mPageSize = pagesize;
        this.contract = contract;
        this.mAddress = address;
    }

    @Override
    public String initUrl() {
        return Constant.FST_EXCHANGE_SERVER+this.mAddress+"/"+this.contract+"/tx?page="+this.mPageSize;
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
