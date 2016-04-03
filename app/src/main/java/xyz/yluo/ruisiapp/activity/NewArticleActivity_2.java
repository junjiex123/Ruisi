package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.PersistentCookieStore;

import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.MySetting;
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
        //ButterKnife.bind(this);

//        Intent  i  = getIntent();
//        String id = i.getExtras().getString("id");
//        String title = i.getExtras().getString("title");


        WebViewClient client = new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(isfirst){
                    isfirst = false;
                }else {
                    finish();
                }
                return true;
            }
        };

        setCookie(this);
        myWebView.setWebViewClient(client);
        //http://bbs.rs.xidian.me/
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(MySetting.BBS_BASE_URL+"forum.php?mod=post&action=newthread&fid=72&mobile=2");

    }

    //设置cookie
    private void setCookie(Context context){
        CookieManager cookieManager = CookieManager.getInstance();
        PersistentCookieStore cookieStore = AsyncHttpCilentUtil.getMyCookieStore(context);

        List<Cookie> cookies = cookieStore.getCookies();
        for (int i = 0; i < cookies.size(); i++) {
            Cookie eachCookie = cookies.get(i);
            String cookieString = eachCookie.getName() + "=" + eachCookie.getValue();
            cookieManager.setCookie(MySetting.BBS_BASE_URL, cookieString);
            Log.i(">>>>>", "cookie : " + cookieString);
        }

        cookieManager.setAcceptCookie(true);
    }
}
