package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.NewArticleActivity_2;
import xyz.yluo.ruisiapp.activity.SingleArticleNormalActivity;
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
        if (url.contains("forum.php?mod=viewthread&tid=")) { // 帖子
            String tid = GetId.getTid(url);
            SingleArticleNormalActivity.open(context,tid,"查看主题","","");
            return true;
        } else if (url.contains("home.php?mod=space&uid=")) { // 用户
            String imageUrl = UrlUtils.getimageurl(url,true);
            UserDetailActivity.open(context,"name",imageUrl);
            return true;
        } else if(url.contains("forum.php?mod=post&action=newthread")){ //发帖链接
            context.startActivity(new Intent(context,NewArticleActivity_2.class));
            return true;
        }else if(url.contains("member.php?mod=logging&action=login")){//登陆
            LoginActivity.open(context);
            return true;
        }
        else{
            // 其他连接
            return true;
        }
    }

}
