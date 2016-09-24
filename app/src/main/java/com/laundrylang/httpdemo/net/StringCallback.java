package com.laundrylang.httpdemo.net;

import com.laundrylang.httpdemo.constant.ConstantValue;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;

/**
 * Created by Sinaan on 2016/9/21.
 */
public class StringCallback implements Callback.CommonCallback<String>{
    private static final EventBus eventBus = EventBus.getDefault();
    private String url;

    public StringCallback(String url) {
       this.url = url;
    }

    @Override
    public void onSuccess(String s) {
        eventBus.post(new Event(url,s));
    }

    @Override
    public void onError(Throwable throwable, boolean b) {
        if(throwable.toString().contains("UnknownHostException"))
        {
            eventBus.post(new Event(url, ConstantValue.UnknownHostException));
        }
        else if(throwable.toString().contains("SocketTimeoutException"))
        {
            eventBus.post(new Event(url, ConstantValue.SocketTimeoutException));
        }
    }

    @Override
    public void onCancelled(CancelledException e) {
        eventBus.post(new Event(url, ConstantValue.CancelledException));
    }

    @Override
    public void onFinished() {
        eventBus.post(new Event(url, ConstantValue.ConnectFinished));
    }
}
