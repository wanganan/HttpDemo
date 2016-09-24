package com.laundrylang.httpdemo.net;

/**
 * Created by Sinaan on 2016/9/21.
 */
public class Event {
    private Object key;
    private Object value;

    public Event(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public Object getkey() {
        return key;
    }

    public Object getvalue() {
        return value;
    }

}
