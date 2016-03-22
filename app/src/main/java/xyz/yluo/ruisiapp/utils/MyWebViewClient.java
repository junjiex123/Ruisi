package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.activity.ArticleNormalActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;


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


        //http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=840272&extra=
        if (url.startsWith("http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=")) { // 帖子


        } else if (url.startsWith("http://rs.xidian.edu.cn/home.php?mod=space&uid=")) { // 用户
            //ArticleNormalActivity.open(context, url.substring(26));
            UserDetailActivity.open(context, "todo");
        } else { // 其他连接
            //todo
            Toast.makeText(context,"链接被电击",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}
