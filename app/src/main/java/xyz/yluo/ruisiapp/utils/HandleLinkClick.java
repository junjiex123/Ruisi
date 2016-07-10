package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.View.MyHtmlTextView;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.NewArticleActivity_2;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;

/**
 * Created by free2 on 16-4-12.
 * 处理WebView和 {@link MyHtmlTextView}链接点击
 *
 * //todo 图片点击事件 展示大图片 下载附件
 */
public class HandleLinkClick {
    public static void handleClick(Context context, String url){

        Log.i("handle the link",url);
        //点击了图片
        if(url.contains("from=album")){
            //do nothing
            System.out.println("to do img click");
        }else if (url.contains("forum.php?mod=viewthread&tid=")) { // 帖子
            SingleArticleActivity.open(context,url,"","");
        } else if (url.contains("home.php?mod=space&uid=")) { // 用户
            String imageUrl = UrlUtils.getAvaterurlb(url);
            UserDetailActivity.open(context,"name",imageUrl);
        } else if(url.contains("forum.php?mod=post&action=newthread")){ //发帖链接
            context.startActivity(new Intent(context,NewArticleActivity_2.class));
        }else if(url.contains("member.php?mod=logging&action=login")) {//登陆
            LoginActivity.open(context);
        }else if(url.contains("forum.php?mod=attachment")){
            //forum.php?mod=attachment&aid=ODk0NjM4fDdmMmIxZjE3fDE0NjgxMjc1Mjl8MjUyNTUzfDg0NjgxOQ%3D%3D&mobile=2

        }

        else{
            if(!url.startsWith("http")){
                url = PublicData.getBaseUrl()+url;
            }

            RequestOpenBrowser.openBroswer(context,url);
        }
    }
}
