package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.NewArticleActivity_2;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
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
        //点击了图片
        if(url.contains("from=album")){
            return true;
        }
        if (url.contains("forum.php?mod=viewthread&tid=")) { // 帖子
            String tid = GetId.getTid(url);
            SingleArticleActivity.open(context,tid,"查看主题","","");
        } else if (url.contains("home.php?mod=space&uid=")) { // 用户
            String imageUrl = UrlUtils.getimageurl(url,true);
            UserDetailActivity.open(context,"name",imageUrl);
        } else if(url.contains("forum.php?mod=post&action=newthread")){ //发帖链接
            context.startActivity(new Intent(context,NewArticleActivity_2.class));
        }else if(url.contains("member.php?mod=logging&action=login")) {//登陆
            LoginActivity.open(context);
        }else{
            RequestOpenBrowser.openBroswer(context, MySetting.BBS_BASE_URL+url);
        }

        return true;
    }

}
