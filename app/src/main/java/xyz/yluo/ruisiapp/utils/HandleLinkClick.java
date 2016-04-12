package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.Intent;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.NewArticleActivity_2;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;

/**
 * Created by free2 on 16-4-12.
 * 处理{@link MyWebView}和 {@link MyHtmlTextView}链接点击
 */
public class HandleLinkClick {
    public static void handleClick(Context context, String url){
        System.out.println("link"+url);
        //点击了图片
        if(url.contains("from=album")){
            //do nothing
        }else if (url.contains("forum.php?mod=viewthread&tid=")) { // 帖子
            String tid = GetId.getTid(url);
            SingleArticleActivity.open(context,tid,"查看主题");
        } else if (url.contains("home.php?mod=space&uid=")) { // 用户
            String imageUrl = UrlUtils.getimageurl(url,true);
            UserDetailActivity.open(context,"name",imageUrl);
        } else if(url.contains("forum.php?mod=post&action=newthread")){ //发帖链接
            context.startActivity(new Intent(context,NewArticleActivity_2.class));
        }else if(url.contains("member.php?mod=logging&action=login")) {//登陆
            LoginActivity.open(context);
        }else{
            System.out.println("request b"+url);
            RequestOpenBrowser.openBroswer(context, PublicData.BASE_URL +url);
        }
    }
}
