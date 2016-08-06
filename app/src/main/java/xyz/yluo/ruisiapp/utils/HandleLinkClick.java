package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.View.MyHtmlView.HtmlView;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.NewArticleActivity_2;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.downloadfile.DownloadService;

/**
 * Created by free2 on 16-4-12.
 * 处理WebView和 {@link HtmlView}链接点击
 * <p>
 * //todo 图片点击事件 展示大图片
 */
public class HandleLinkClick {
    public static void handleClick(final Context context, String url) {

        Log.i("handle the link", url);
        //点击了图片
        if (url.contains("from=album")) {
            //do nothing
            System.out.println("to do img click");
        } else if (url.contains("forum.php?mod=viewthread&tid=")) { // 帖子
            SingleArticleActivity.open(context, url, null);
        } else if (url.contains("home.php?mod=space&uid=")) { // 用户
            String imageUrl = UrlUtils.getAvaterurlb(url);
            UserDetailActivity.open(context, "name", imageUrl);
        } else if (url.contains("forum.php?mod=post&action=newthread")) { //发帖链接
            context.startActivity(new Intent(context, NewArticleActivity_2.class));
        } else if (url.contains("member.php?mod=logging&action=login")) {//登陆
            LoginActivity.open(context);
        } else if (url.contains("forum.php?mod=attachment")) {
            //forum.php?mod=attachment&aid=ODk0NjM4fDdmMmIxZjE3fDE0NjgxMjc1Mjl8MjUyNTUzfDg0NjgxOQ%3D%3D&mobile=2
            final String finalUrl = url;
            /**
             * 启动下载服务
             */
            new MyAlertDialog(context, MyAlertDialog.NORMAL_TYPE)
                    .setTitleText("下载附件")
                    .setContentText("你要开始下载此附件吗？")
                    .setCancelText("取消")
                    .setConfirmText("下载")
                    .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(MyAlertDialog myAlertDialog) {
                            Intent intent = new Intent(context, DownloadService.class);
                            intent.putExtra("download_url", finalUrl);
                            context.startService(intent);
                        }
                    })
                    .show();

        } else {
            if (!url.startsWith("http")) {
                url = App.getBaseUrl() + url;
            }
            RequestOpenBrowser.openBroswer(context, url);
        }
    }
}
