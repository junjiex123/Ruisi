package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import xyz.yluo.ruisiapp.utils.HandleLinkClick;


public class MyWebViewClient extends WebViewClient {

    private volatile static MyWebViewClient singleton;

    public static MyWebViewClient with(Context context) {
        if (singleton == null) {
            synchronized (MyWebViewClient.class) {
                if (singleton == null) {
                    singleton = new MyWebViewClient(context);
                }
            }
        }
        return singleton;
    }

    private final Context context;

    private MyWebViewClient(Context context) {
        this.context = context.getApplicationContext();
    }

    //重写文章中点击连接的事件
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        HandleLinkClick.handleClick(context,url);
        return true;
    }

}
