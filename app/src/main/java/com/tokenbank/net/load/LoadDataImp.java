package com.tokenbank.net.load;


import com.tokenbank.net.apirequest.ApiRequest;
import com.tokenbank.net.listener.LoadDataListener;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */
//发送请求到对应节点
public class LoadDataImp implements ILoadData {

    @Override
    public void loadData(final ApiRequest request, boolean shouldCache, final LoadDataListener listener) {
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                //对完成事件作出响应
                listener.loadFinish();
            }

            @Override
            public void onError(Throwable throwable) {
                //对错误事件作出响应
                listener.loadFailed(throwable, request.getReqId());
            }

            @Override
            public void onNext(String response) {
                //对下一步事件作出响应
                listener.loadSuccess(response);
            }
        };

        //此处的request是继承BaseGetApiRequest而来的构造好的请求   例如JTTransactionDetailsRequest
        request.getObservableObj(shouldCache)
                .subscribeOn(Schedulers.io())//改变了调用前序列所运行的线程
                .observeOn(AndroidSchedulers.mainThread())//线程切换
                .subscribe(subscriber);
    }
}
