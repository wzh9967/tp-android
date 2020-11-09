package com.tokenbank.net.api.mcrequest;
import android.util.Log;

import com.android.volley.VolleyError;
import com.tokenbank.base.WalletInfoManager;
import com.tokenbank.config.Constant;
import com.tokenbank.net.apirequest.BaseGetApiRequest;


public class MCTransactionListRequest extends BaseGetApiRequest {

    private int mPageSize;
    private String mAddress;

    public MCTransactionListRequest(int pagesize ,String address) {
        this.mPageSize = pagesize;
        this.mAddress = address;
    }

    @Override
    public String initUrl() {
        return Constant.FST_EXCHANGE_SERVER+this.mAddress+"/tx?page="+this.mPageSize;
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
