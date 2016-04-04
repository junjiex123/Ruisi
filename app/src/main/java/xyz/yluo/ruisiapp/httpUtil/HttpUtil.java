package xyz.yluo.ruisiapp.httpUtil;

import android.content.Context;

import java.util.Map;

import xyz.yluo.ruisiapp.MySetting;

/**
 * Created by free2 on 16-4-4.
 *
 */
public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static PersistentCookieStore store;

    public static void get(Context context,String url,ResponseHandler handler){
        init(context);
        client.get(MySetting.BBS_BASE_URL+url,handler);
    }

    public static void post(Context context,String url, Map<String,String> map,ResponseHandler handler){
        init(context);
        client.post(MySetting.BBS_BASE_URL+url,map,handler);
    }

    private static void init(Context context){
        store = new PersistentCookieStore(context);
        client.setStore(store);
    }
}
