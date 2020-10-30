package com.tokenbank.net.load;

import android.util.Log;

import com.tokenbank.net.apirequest.ApiRequest;
import com.tokenbank.net.listener.LoadDataListener;
import com.tokenbank.utils.GsonUtil;
import com.tokenbank.utils.TLog;

import org.json.JSONException;

public class RequestPresenter {
    private ILoadData loadDataModel;

    public interface RequestCallback {
        void onRequesResult(int ret, GsonUtil json) throws JSONException;
    }

    public RequestPresenter() {
        this.loadDataModel = new LoadDataImp();
    }


    public void loadData(final ApiRequest request, boolean shouldCache, final RequestCallback requestCallback) {
        loadDataModel.loadData(request, shouldCache, new LoadDataListener() {

            @Override
            public void loadSuccess(String result) throws JSONException {
                //用于对事件作出响应
                if (requestCallback != null) {
                    GsonUtil jsonResult = new GsonUtil(result);
                    requestCallback.onRequesResult(jsonResult.getInt("result", -1), jsonResult);
                }
            }

            @Override
            public void loadFailed(Throwable throwable, int reqId) throws JSONException {
                if (requestCallback != null) {
                    GsonUtil errorMsg = new GsonUtil("{}");
                    requestCallback.onRequesResult(-1, errorMsg);
                }
            }

            @Override
            public void loadFinish() {
            }
        });
    }

    public void loadJcData(final ApiRequest request, boolean shouldCache, final RequestCallback requestCallback) {
        loadDataModel.loadData(request, shouldCache, new LoadDataListener() {

            @Override
            public void loadSuccess(String result) throws JSONException {
                //请求成功，结果返回
                if (requestCallback != null) {
                    //todo
                    GsonUtil jsonResult = new GsonUtil(result);
                    requestCallback.onRequesResult(Integer.parseInt(jsonResult.getString("code", "-1")), jsonResult);
                }
            }

            @Override
            public void loadFailed(Throwable throwable, int reqId) throws JSONException {
                //请求失败错误返回
                if (requestCallback != null) {
                    //todo
                    GsonUtil errorMsg = new GsonUtil("{}");
                    requestCallback.onRequesResult(-1, errorMsg);
                }
            }

            @Override
            public void loadFinish() {
            }
        });
    }

    public void loadEthplorerData(final ApiRequest request, boolean shouldCache, final RequestCallback requestCallback) {
        loadDataModel.loadData(request, shouldCache, new LoadDataListener() {

            @Override
            public void loadSuccess(String result) throws JSONException {
                if (requestCallback != null) {
                    //todo
                    GsonUtil jsonResult = new GsonUtil(result);
                    requestCallback.onRequesResult(0, jsonResult);
                }
            }

            @Override
            public void loadFailed(Throwable throwable, int reqId) throws JSONException {
                if (requestCallback != null) {
                    //todo
                    GsonUtil errorMsg = new GsonUtil("{}");
                    requestCallback.onRequesResult(-1, errorMsg);
                }
            }

            @Override
            public void loadFinish() {
            }
        });
    }

    public void loadJtData(final ApiRequest request, boolean shouldCache, final RequestCallback requestCallback) {
        loadDataModel.loadData(request, shouldCache, new LoadDataListener() {
            @Override
            public void loadSuccess(String result) throws JSONException {
                if (requestCallback != null) {
                    //todo
                    GsonUtil jsonResult = new GsonUtil(result);
                    requestCallback.onRequesResult(jsonResult.getInt("statusCode", -1), jsonResult);
                }
            }

            @Override
            public void loadFailed(Throwable throwable, int reqId) throws JSONException {
                if (requestCallback != null) {
                    //todo
                    GsonUtil errorMsg = new GsonUtil("{}");
                    requestCallback.onRequesResult(-1, errorMsg);
                }
            }

            @Override
            public void loadFinish() {
            }
        });
    }

    public void loadJcData(ApiRequest request, final RequestCallback requestCallback) {
        loadJcData(request, false, requestCallback);
    }

    public void loadJtData(ApiRequest request, final RequestCallback requestCallback) {
        loadJtData(request, false, requestCallback);
    }

    public void loadEthplorerData(ApiRequest request, final RequestCallback requestCallback) {
        loadEthplorerData(request, true, requestCallback);
    }

    public void loadData(ApiRequest request, final RequestCallback requestCallback) {
        loadData(request, false, requestCallback);
    }
}
