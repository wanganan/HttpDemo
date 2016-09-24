# HttpDemo
To encapsulate the HTTP web requests

代码写习惯了，不自觉的就有一种心理：这块应该怎样才能迁移利用，这块怎么写才能够看起来舒服一点，等等等。其实归根结底就是我们喜欢“懒”，不喜欢烦。看见那些冗余的和一遍遍重复书写的代码块，就揪心，难受。那好，今天我就教大家从菜鸟的角度，开始封装Http网络请求。

先上代码下载地址：[源码点击下载](https://github.com/wanganan/HttpDemo/tree/master)

项目中用到了Xutils和EventBus,请分别参考：
https://github.com/wyouflf/xUtils3
http://blog.csdn.net/lmj623565791/article/details/40794879

build.gradle配置

```
compile 'org.greenrobot:eventbus:3.0.0'
compile 'org.xutils:xutils:3.3.36'
```

各种网络请求异曲同工，思想一致。如果对EventBus不熟可以选用Handler代替，将handler作为参数传入即可。


我们将工程跑起来，看一下目录结构：
![目录结构](http://img.blog.csdn.net/20160924101651561)

咋一看，我去，这么多类，但是就是这么几个类，你以后调用请求只需要一步到位，而且管理分析起来也方便，回调一个类，url一个类，想混淆都难。该博客仅介绍从网络获取json结果，加载图片的两个类（ResponseImage，DrawableCallback）和json类似，一看便懂。

直接看一下MainActivity的调用方式：

```
public class MainActivity extends Activity {
    private EventBus eventBus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //订阅事件
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        //发起请求
        ResponseHttp.execute(ConstantUrl.Discount_Off);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消订阅
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
                //接收返回的json结果
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
```

怎么样，发起请求只需要一步，然后就可以在onEventMainThread里边处理结果了。只要这个类订阅了事件，以后这个类里边发起的请求，都可以集中到switch语句里处理。这样封装，以后维护代码的时候还会头疼吗。


接下来我们深入代码，看一下ResponseHttp:

```
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
```
哦，原来这是一个处理请求的工具类，封装了get,post方式，我这里一个参数的用的是Get请求。如果1你的项目有什么必传参数的话，直接就可以在这个类里边添加。我们阅读的时候会发现，其实起关键作用的还是这句代码

```
http.post(requestParams, new StringCallback(url));
```

这个StringCallback回调类就比较有意思了，打开我们会发现

```
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
```
这是一个专门接收返回状态的类，然后将状态或者结果作为事件发布出去。这里EventBus的巧妙之处就在于，它无需作为参数传进来，只需要通过eventbus.post(对象)将事件发布出去，主MainAcitivity里的onEventMainThread就可以接收到这个发布出来的事件然后处理。构造函数里的url充当了事件区分的key,我们在主类处理的时候就是依据这个key来区分到底是哪个接口返回的。key必须唯一，图片请求的key可以是view.getId()。
EventBus的使用和原理在文章一开始就提供了链接，如果你还是不喜欢或不习惯用EventBus的话，可以把Handler作为StringCallback构造函数的参数传进来，剩下的就是针对各种返回状态sendMessage了。

这里最好将事件状态也抽取出来，所有的地方都用这几种状态，代码规范容易修改

```
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
```

其实上述几个类，围绕的都是事件这个核心，这个核心类到底什么样子呢，是不是很好奇啊？哈哈，其实事件类非常的简单，请看：

```
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
```

对的，就是这么简单。他作为网络请求返回结果的携带因子，准确的将自己post给了主函数处理。属性类型为Object，是方便以后拓展，比如携带图片的时候就是这么个类型new Event(int id,Drawable drawable),是不是超有范！

其他几个必不可少的类也一并贴出来，代码都很简单，等着以后拓展。
ConstantUrl.java

```
public interface ConstantUrl {
    String Discount_Off = "http://wl.xiyilang.cc/app_order/off.do";
}

```

MyApplication.java

```
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //配置Xutil
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
```

最后别忘了这两句话：

uses-permission android:name="android.permission.INTERNET"
android:name=".MyApplication"


我能教给大家的就这些了，关键是要熟练，当你在熟练的时候，写代码就游刃有余了。相信大家一定会成为代码规范整洁的程序员！

