package com.tokenbank.net.api.mcrequest;

import com.android.volley.VolleyError;
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
        return Constant.MOC_EXCHANGE_SERVER+mAddress+"/tx?page="+mPageSize;
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
