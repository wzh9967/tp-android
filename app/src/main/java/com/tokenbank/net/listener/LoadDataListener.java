package com.tokenbank.net.listener;

import org.json.JSONException;

/**
 */
public interface LoadDataListener{

    void loadSuccess(String result) throws JSONException;

    void loadFailed(Throwable throwable, int reqId) throws JSONException;

    void loadFinish();

}
