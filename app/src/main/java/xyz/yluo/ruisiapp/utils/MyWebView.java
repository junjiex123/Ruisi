package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebView;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.PersistentCookieStore;

/**
 * Created by free2 on 16-3-8.
 * 自定义WebView
 */
public class MyWebView extends WebView{

    //指定css
    private static final String FILE_CSS = "file:///android_asset/style.css";

    private static final String HTML_HEADER = "" +
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<link type=\"text/css\" rel=\"stylesheet\" href=\""+ FILE_CSS + "\">\n"+
            "<style type=\"text/css\">" +"img{display: inline;height: auto;max-width: 100%;}"+"</style>\n"+
            "<meta charset=\"UTF-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1, maximum-scale=1\">\n"+
            "</head>\n"+
            "<body>\n";

    private static final String HTML_REAR = "" +
            "</body>\n" +
            "</html>";


    public MyWebView(Context context) {
        super(context);

        setWebViewClient(MyWebViewClient.with(context));
        setCookie(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebViewClient(MyWebViewClient.with(context));
        setCookie(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWebViewClient(MyWebViewClient.with(context));
        setCookie(context);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        String newData = HTML_HEADER+data+HTML_REAR;
        super.loadData(newData, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        String newData = HTML_HEADER+data+HTML_REAR;


        super.loadDataWithBaseURL(baseUrl, newData, mimeType, encoding, historyUrl);
    }

    //设置cookie
    private void setCookie(Context context) {

        PersistentCookieStore cookieStore = HttpUtil.getStore(context);
        CookieManager cookieManager = CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);

        String domain = ";domain=" + PublicData.BASE_URL.replace("http://", "").replace("/", "");

        for (String s : cookieStore.getCookie().split(";")) {
            s = s + domain;
            cookieManager.setCookie(PublicData.BASE_URL, s);
        }
    }
}
