package com.laundrylang.httpdemo.net;

import android.widget.ImageView;

import org.xutils.ImageManager;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by Sinaan on 2016/9/20.
 */
public class ResponseImage {

    private static final ImageManager image = x.image();

    public static void display(ImageView view,String uri){
        display(view,uri,null);
    }

    public static void display(ImageView view,String uri,ImageOptions options ){
        if(options==null){
            options = ImageOptions.DEFAULT;
        }
        image.bind(view,uri,options,new DrawableCallback(view.getId()));
    }
//        //网络图片加载
//        x.image().bind(imageView, url, imageOptions);
//
//// assets file
//        x.image().bind(imageView, "assets://test.gif", imageOptions);
//
//// local file
//        x.image().bind(imageView, new File("/sdcard/test.gif").toURI().toString(), imageOptions);
//        x.image().bind(imageView, "/sdcard/test.gif", imageOptions);
//        x.image().bind(imageView, "file:///sdcard/test.gif", imageOptions);
//        x.image().bind(imageView, "file:/sdcard/test.gif", imageOptions);

//    x.image().bind(imageView, url, imageOptions, new Callback.CommonCallback<Drawable>() {...});
//    x.image().loadDrawable(url, imageOptions, new Callback.CommonCallback<Drawable>() {...});
//    x.image().loadFile(url, imageOptions, new Callback.CommonCallback<File>() {...});
}
