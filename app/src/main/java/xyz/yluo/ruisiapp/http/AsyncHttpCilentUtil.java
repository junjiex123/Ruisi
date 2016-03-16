package xyz.yluo.ruisiapp.http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.ConfigClass;

/**
 * Created by free2 on 16-3-16.
 *
 */
public class AsyncHttpCilentUtil {

    private static AsyncHttpClient client = new AsyncHttpClient();


    public static void get(Context context,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        init(context);
        client.get(ConfigClass.BBS_BASE_URL + url, params, responseHandler);
    }

    public static void post(Context context,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        init(context);
        client.post(ConfigClass.BBS_BASE_URL + url, params, responseHandler);
    }

    private static void init(Context context){

        client.setTimeout(1000*8);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        System.out.print("\ncookie--->>>>" + myCookieStore.getCookies());
    }
}
