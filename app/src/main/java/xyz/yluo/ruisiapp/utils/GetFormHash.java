package xyz.yluo.ruisiapp.utils;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;

/**
 * Created by free2 on 16-3-16.
 *
 * 获得论坛FormHash
 */
public class GetFormHash {

    public static void start_get_hash(Context context){

        HttpUtil.get(context, "portal.php", new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document doc = Jsoup.parse(new String(response));
                // 具有 formhash 属性的链接
                if (doc.select("input[name=formhash]").attr("value") != null) {
                    MySetting.CONFIG_FORMHASH = doc.select("input[name=formhash]").attr("value");
                }
            }
            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    public static void start_get_hash(Context context,boolean isMobile){
        if(isMobile){
            String url = "forum.php?mod=post&action=newthread&fid=72&mobile=2";
            HttpUtil.get(context, url, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    Document doc = Jsoup.parse(new String(response));
                    MySetting.CONFIG_FORMHASH = doc.select("input[name=formhash]").attr("value"); // 具有 formhash 属性的链接
                }

                @Override
                public void onFailure(Throwable e) {

                }
            });
        }
    }
}
