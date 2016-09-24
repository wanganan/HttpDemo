package com.laundrylang.httpdemo.net;

import com.laundrylang.httpdemo.constant.ConstantValue;

import org.xutils.HttpManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Sinaan on 2016/9/20.
 */
public class ResponseHttp {

    private static final HttpManager http = x.http();

    public static Callback.Cancelable execute(String url) {
        return execute(url,null);
    }

    public static Callback.Cancelable execute(String url,HashMap<String,Object> params) {
        return execute(url,params,null);
    }

    public static Callback.Cancelable execute(String url,HashMap<String,Object> params,HashMap<String,String> headers) {
        RequestParams requestParams = new RequestParams(url);
        if(headers!=null){
            Iterator it = headers.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if(value instanceof String){
                    requestParams.addHeader(key, ((String) value));
                }
            }
        }
        if(params!=null){
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if(value instanceof String){
                    requestParams.addBodyParameter(key, ((String) value));
                }
                if(value instanceof File){
                    requestParams.addBodyParameter(key, ((File) value));
                }
            }
        }
        Callback.Cancelable post = http.post(requestParams, new StringCallback(url));
        return post;
    }

    public static boolean isJsonReturn(String result){
        switch (result) {
            case ConstantValue.UnknownHostException:
            case ConstantValue.SocketTimeoutException:
            case ConstantValue.CancelledException:
            case ConstantValue.ConnectFinished:
            case ConstantValue.ConnectSuccess:
                return false;
            default:return true;
        }
    }
}
