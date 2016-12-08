package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.NewPostActivity;
import xyz.yluo.ruisiapp.activity.PostActivity;
import xyz.yluo.ruisiapp.activity.PostsActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.activity.ViewImgActivity;
import xyz.yluo.ruisiapp.downloadfile.DownloadService;
import xyz.yluo.ruisiapp.widget.myhtmlview.HtmlView;

/**
 * Created by free2 on 16-4-12.
 * 处理WebView和 {@link HtmlView}链接点击
 * <p>
 */
public class HandleLinkClick {
    public static void handleClick(final Context context, String url) {

        Log.i("handle the link", url);
        //点击了图片
        if (url.contains("from=album")) {
            ViewImgActivity.open(context, url);
        } else if (url.contains("forum.php?mod=viewthread&tid=") || url.contains("forum.php?mod=redirect&goto=findpost")) { // 帖子
            PostActivity.open(context, url, null);
        } else if (url.contains("home.php?mod=space&uid=")) { // 用户
            String imageUrl = UrlUtils.getAvaterurlb(url);
            UserDetailActivity.open(context, "name", imageUrl);
        } else if (url.contains("forum.php?mod=post&action=newthread")) { //发帖链接
            context.startActivity(new Intent(context, NewPostActivity.class));
        } else if (url.contains("member.php?mod=logging&action=login")) {//登陆
            LoginActivity.open(context);
        } else if (url.contains("forum.php?mod=forumdisplay&fid=")) {
            int fid = GetId.getFroumFid(url);
            PostsActivity.open(context, fid, "分区帖子");
        } else if (url.contains("forum.php?mod=attachment")) {
            final String finalUrl = url;
            /**
             * 启动下载服务
             */
            new AlertDialog.Builder(context).
                    setTitle("下载附件").
                    setMessage("你要开始下载此附件吗？").
                    setPositiveButton("下载", (dialog, which) -> {
                        Intent intent = new Intent(context, DownloadService.class);
                        intent.putExtra("download_url", finalUrl);
                        context.startService(intent);
                    })
                    .setNegativeButton("取消", null)
                    .setCancelable(true)
                    .create()
                    .show();

        } else {
            if (!url.startsWith("http")) {
                url = App.getBaseUrl() + url;
            }
            IntentUtils.openBroswer(context, url);
        }
    }
}
