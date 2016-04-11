package xyz.yluo.ruisiapp.httpUtil;

import android.content.Context;

import java.util.Map;

import xyz.yluo.ruisiapp.MyPublicData;

/**
 * Created by free2 on 16-4-4.
 *
 */
public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static PersistentCookieStore store;
    private static final String UAC_MOBILE = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3 like Mac OS X; en-us) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";
    private static final String UAC_PC = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.163.400";

    public static void get(Context context,String url,ResponseHandler handler){
        init(context);
        client.get(MyPublicData.BBS_BASE_URL+url,handler);
    }

    public static void post(Context context,String url, Map<String,String> map,ResponseHandler handler){
        init(context);
        client.post(MyPublicData.BBS_BASE_URL+url,map,handler);
    }

    private static void init(Context context){
        client.setConnectionTimeout(5000);
        if(store==null){
            store = new PersistentCookieStore(context);
            client.setStore(store);
        }

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
