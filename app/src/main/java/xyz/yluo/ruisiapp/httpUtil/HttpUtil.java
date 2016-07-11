package xyz.yluo.ruisiapp.httpUtil;

import android.content.Context;

import java.util.Map;

import xyz.yluo.ruisiapp.PublicData;

/**
 * Created by free2 on 16-4-4.
 *
 */
public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncHttpClient = new SyncHttpClient();
    private static PersistentCookieStore store;

    private static String getUrl(String url){
        if(url.startsWith("http")){
            return url;
        }else{
            return PublicData.getBaseUrl()+url;
        }
    }

    public static void get(Context context,String url,ResponseHandler handler){
        init(context);
        client.get(getUrl(url),handler);
    }

    public static void post(Context context,String url, Map<String,String> map,ResponseHandler handler){
        init(context);
        client.post(getUrl(url),map,handler);
    }

    public static void head(Context context,String url,ResponseHandler handler){
        init(context);
        client.head(getUrl(url),handler);
    }

    private static void init(Context context){
        client.setConnectionTimeout(5000);
        if(store==null){
            store = new PersistentCookieStore(context);
            client.setStore(store);
        }

    }

    //同步
    public static void SyncGet(Context context,String url,ResponseHandler handler){
        init(context);
        syncHttpClient.get(getUrl(url),handler);
    }

    public static void exit(){
        store.clearCookie();
    }

    public static PersistentCookieStore getStore(Context context){
        if(store==null){
            store = new PersistentCookieStore(context);
            return store;
        }else {
            return store;
        }
    }

    public static AsyncHttpClient getClient() {
        return client;
    }
}
