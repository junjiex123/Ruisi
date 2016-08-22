package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by yang on 16-8-22.
 * 请求浏览器打开
 */

public class IntentUtils {

    public static void openBroswer(Context activity, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }


    public static void sendMail(Context activity, String username) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri to = Uri.parse("mailto:2351386755@qq.com");
        intent.setData(to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "西电手机睿思bug反馈 " + username);
        intent.putExtra(Intent.EXTRA_TEXT, "");

        activity.startActivity(intent);
    }
}
