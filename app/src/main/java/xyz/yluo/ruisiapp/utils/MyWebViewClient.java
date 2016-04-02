package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        if (url.startsWith("forum.php?mod=viewthread&tid=")) { // 帖子

            return true;
        } else if (url.contains("home.php?mod=space&uid=")) { // 用户
            String imageUrl = UrlUtils.getimageurl(url,true);
            UserDetailActivity.open(context,"name",imageUrl);
            return true;
            //http://bbs.rs.xidian.me/forum.php?mod=post&action=newthread&fid=72&mobile=2
            //发帖链接
        } else if(url.contains("forum.php?mod=post&action=newthread")){
            return false;
        }else {
            // 其他连接
            return true;
        }
    }

}
