package xyz.yluo.ruisiapp.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cz.msebera.android.httpclient.Header;

/**
 * Created by free2 on 16-3-16.
 *
 * 获得论坛FormHash
 */
public class GetFormHash {

    public static void start_get_hash(Context context){

        AsyncHttpCilentUtil.get(context, "portal.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                Document doc = Jsoup.parse(new String(responseBody));
                // 具有 formhash 属性的链接
                if (doc.select("input[name=formhash]").attr("value") != null) {
                    ConfigClass.CONFIG_FORMHASH = doc.select("input[name=formhash]").attr("value");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
