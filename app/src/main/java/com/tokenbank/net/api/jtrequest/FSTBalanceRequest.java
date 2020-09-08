package com.tokenbank.net.api.jtrequest;

import com.android.volley.VolleyError;
import com.tokenbank.net.apirequest.BaseGetApiRequest;

public class FSTBalanceRequest extends BaseGetApiRequest {
    @Override
    public String initUrl() {
        return null;
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
