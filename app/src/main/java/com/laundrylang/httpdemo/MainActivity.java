package com.laundrylang.httpdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.laundrylang.httpdemo.constant.ConstantUrl;
import com.laundrylang.httpdemo.constant.ConstantValue;
import com.laundrylang.httpdemo.net.Event;
import com.laundrylang.httpdemo.net.ResponseHttp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends Activity {
    private EventBus eventBus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        ResponseHttp.execute(ConstantUrl.Discount_Off);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Event event) {
        Object key = event.getkey();
        Object value = event.getvalue();
        if(key instanceof String && value instanceof String){
            String url = (String) key;
            String result = (String) value;
            switch (url) {
                case ConstantUrl.Discount_Off:
                    Log.e(ConstantValue.LogTag, "数据返回==" + result);
                    if(ResponseHttp.isJsonReturn(result)){
                        //成功返回数据
                    }
                    break;
            }
        }
    }

}
