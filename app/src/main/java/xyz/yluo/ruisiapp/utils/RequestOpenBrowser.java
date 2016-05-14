package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by free2 on 16-3-31.
 * 请求浏览器打开
 */
public class RequestOpenBrowser {

    public static void openBroswer(Context activity, String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }

}
