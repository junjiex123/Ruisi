package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.PersistentCookieStore;
import xyz.yluo.ruisiapp.utils.MyWebView;

/**
 * Created by free2 on 16-4-2.
 *
 */
public class NewArticleActivity_2 extends AppCompatActivity {

    protected WebView myWebView;

    private boolean isfirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myWebView = new MyWebView(this);
        setContentView(myWebView);

        System.out.println("=======create=====");
        //ButterKnife.bind(this);

//        Intent  i  = getIntent();
//        String id = i.getExtras().getString("id");
//        String title = i.getExtras().getString("title");


//        WebViewClient client = new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if(isfirst){
//                    isfirst = false;
//                }else {
//                    finish();
//                }
//                return true;
//            }
//        };

        setCookie(this);
        //myWebView.setWebViewClient(client);
        //http://bbs.rs.xidian.me/

        myWebView.loadUrl("http://www.baidu.com/");

    }

    //设置cookie
    private void setCookie(Context context){

//        CookieManager cookieManager = CookieManager.getInstance();
//        PersistentCookieStore cookieStore = HttpUtil.getStore(context);
//
//        cookieManager.setCookie(MySetting.BBS_BASE_URL, cookieStore.getCookie());
//
//        cookieManager.setAcceptCookie(true);
    }

}
