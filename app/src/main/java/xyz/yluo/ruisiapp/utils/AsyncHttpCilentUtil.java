package xyz.yluo.ruisiapp.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * Created by free2 on 16-3-16.
 *
 */
public abstract class AsyncHttpCilentUtil {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static PersistentCookieStore myCookieStore;

    public static void get(Context context,String url, AsyncHttpResponseHandler responseHandler) {
        init(context);
        client.get(ConfigClass.BBS_BASE_URL + url, null, responseHandler);
    }

    public static void post(Context context,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        init(context);
        client.post(ConfigClass.BBS_BASE_URL + url, params, responseHandler);
    }

    private static void init(Context context){
        client.setTimeout(1000*5);
        myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
    }

    public static void exit(){
        myCookieStore.clear();
    }

    public static PersistentCookieStore getMyCookieStore(Context context){
        myCookieStore = new PersistentCookieStore(context);
        return myCookieStore;
    }
}
