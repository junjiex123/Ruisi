package xyz.yluo.ruisiapp.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import xyz.yluo.ruisiapp.MySetting;

/**
 * Created by free2 on 16-3-16.
 *
 */
public abstract class AsyncHttpCilentUtil {

    //异步
    private static AsyncHttpClient client = new AsyncHttpClient();
    //同步
    private static SyncHttpClient syncHttpClient;

    private static PersistentCookieStore myCookieStore;

    public static void get(Context context,String url, AsyncHttpResponseHandler responseHandler) {
        init(context);
        client.get(MySetting.BBS_BASE_URL + url, null, responseHandler);
    }

    public static void post(Context context,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        init(context);
        client.post(MySetting.BBS_BASE_URL + url, params, responseHandler);
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

    public static SyncHttpClient getSyncHttpClient(Context context){
        if(syncHttpClient!=null){
            return syncHttpClient;
        }else {
            syncHttpClient = new SyncHttpClient();
            myCookieStore = new PersistentCookieStore(context);
            syncHttpClient.setCookieStore(myCookieStore);

            return syncHttpClient;
        }
    }
}
