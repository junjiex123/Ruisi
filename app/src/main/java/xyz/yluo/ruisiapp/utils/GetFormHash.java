package xyz.yluo.ruisiapp.utils;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.yluo.ruisiapp.MyPublicData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;

/**
 * Created by free2 on 16-3-16.
 *
 * 获得论坛FormHash
 */
public class GetFormHash {

    public static void start_get_hash(Context context,boolean isMobile){
        if(isMobile){
            String url = "forum.php?mod=post&action=newthread&fid=72&mobile=2";
            HttpUtil.get(context, url, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    Document doc = Jsoup.parse(new String(response));
                    MyPublicData.CONFIG_FORMHASH = doc.select("input[name=formhash]").attr("value"); // 具有 formhash 属性的链接
                }

                @Override
                public void onFailure(Throwable e) {

                }
            });
        }
    }

    public static String getHash(String url){
        //member.php?mod=logging&action=logout&formhash=cb9f1c2f&mobile=2
        try {
            //fid=[0-9]+
            Pattern pattern = Pattern.compile("formhash=.*&");
            Matcher matcher = pattern.matcher(url);
            String hash ="";
            if (matcher.find()) {
                hash = url.substring(matcher.start()+9,matcher.end()-1);
            }

            return hash;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }
}
