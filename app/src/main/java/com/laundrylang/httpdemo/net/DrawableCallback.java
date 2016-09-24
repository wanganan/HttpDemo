package com.laundrylang.httpdemo.net;

import android.graphics.drawable.Drawable;

import com.laundrylang.httpdemo.constant.ConstantValue;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;

/**
 * Created by Sinaan on 2016/9/21.
 */
public class DrawableCallback implements Callback.CommonCallback<Drawable>{
    private static final EventBus eventBus = EventBus.getDefault();
    private int id;

    public DrawableCallback(int id) {
       this.id = id;
    }

    @Override
    public void onSuccess(Drawable d) {
        eventBus.post(new Event(id,d));
    }

    @Override
    public void onError(Throwable throwable, boolean b) {
        if(throwable.toString().contains("UnknownHostException"))
        {
            eventBus.post(new Event(id, ConstantValue.UnknownHostException));
        }
        else if(throwable.toString().contains("SocketTimeoutException"))
        {
            eventBus.post(new Event(id, ConstantValue.SocketTimeoutException));
        }
    }

    @Override
    public void onCancelled(CancelledException e) {
        eventBus.post(new Event(id, ConstantValue.CancelledException));
    }

    @Override
    public void onFinished() {
        eventBus.post(new Event(id, ConstantValue.ConnectFinished));
    }
}
