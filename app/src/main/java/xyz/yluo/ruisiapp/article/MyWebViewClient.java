package xyz.yluo.ruisiapp.article;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import xyz.yluo.ruisiapp.user.UserDetailActivity;


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
        //TODO 处理不同的链接点击事件

        if (url.startsWith("https://cnodejs.org/user/")) { // 用户主页协议
            UserDetailActivity.open(context, url.substring(25));
        } else if (url.startsWith("https://cnodejs.org/topic/")) { // 话题主页协议
            //ArticleNormalActivity.open(context, url.substring(26));
        } else { // 其他连接
            //todo
            Toast.makeText(context,"链接被电击",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}
