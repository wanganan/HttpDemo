package com.laundrylang.httpdemo.constant;

/**
 * Created by Sinaan on 2016/9/21.
 */
public interface ConstantValue {
    String LogTag = "HttpDemo";//日志tag
    String ConnectSuccess = "0";//数据成功返回
    String UnknownHostException = "-1";//断网
    String SocketTimeoutException = "-2";//网络加载超时
    String CancelledException = "1";//网络加载已取消
    String ConnectFinished = "2";//加载完成
}
