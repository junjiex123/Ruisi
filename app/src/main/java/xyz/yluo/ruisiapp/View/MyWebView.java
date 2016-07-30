package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.PersistentCookieStore;
import xyz.yluo.ruisiapp.utils.HandleLinkClick;

/**
 * Created by free2 on 16-7-16.
 * 自定义webView
 */
public class MyWebView extends WebView{
    private final String TAG = "MyWebView";
    private Context context;

    private static final String HTML_0 = "" +
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n";

    private static final String HTML_CSS = "" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"" + "file:///android_asset/style.css" + "\">\n";

    private static final String HTML_1 = "" +
            "</head>\n" +
            "<body>\n" +
            "<div id=\"main_container\">\n";

    private static final String HTML_2 = "" +
            "</div>\n" +
            "</body>\n" +
            "</html>";



    public MyWebView(Context context) {
        super(context);
        init(context,null,0,0);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0,0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,null,defStyleAttr,0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.context = context;
        setCookie(context);
        setWebViewClient(client);
    }

    public void setContent(String data) {
        Log.i(TAG,"setContent");
        data = HTML_0 + HTML_CSS + HTML_1 + data + "\n" + HTML_2;
        String baseUrl = App.getBaseUrl();
        loadDataWithBaseURL(baseUrl, data, "text/html", "utf-8", null);
    }


    private final WebViewClient client = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            HandleLinkClick.handleClick(context,url);
            return true;
            //如果不需要其他对点击链接事件的处理返回true，否则返回false
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(TAG,"onPageFinished");
        }
    };

    //设置cookie
    private void setCookie(Context context) {
        PersistentCookieStore cookieStore = HttpUtil.getStore(context);
        CookieManager cookieManager = CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);

        String domain = ";domain=" + App.getBaseUrl().replace("http://", "").replace("/", "");

        for (String s : cookieStore.getCookie().split(";")) {
            s = s + domain;
            cookieManager.setCookie(App.getBaseUrl(), s);
        }
    }
}
