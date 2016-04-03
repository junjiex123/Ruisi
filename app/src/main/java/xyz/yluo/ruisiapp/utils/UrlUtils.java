package xyz.yluo.ruisiapp.utils;

import xyz.yluo.ruisiapp.MySetting;

/**
 * Created by free2 on 16-4-1.
 * 返回各种url;
 */
public class UrlUtils {

    public static String getBaseUrl(boolean isInner){
        if(isInner){
            return "http://rs.xidian.edu.cn/";
        }else{
            return "http://bbs.rs.xidian.me/";
        }
    }
    public static String getArticleListUrl(int fid,int page,boolean isInner){
        if(isInner){
            return "forum.php?mod=forumdisplay&fid="+fid+"&page="+page;
        }else{
            return "forum.php?mod=forumdisplay&fid="+fid+"&page="+page+"&mobile=2";
        }
    }

    public static String getSingleArticleUrl(String tid,int page,boolean isInner){
        if(isInner){
            return "forum.php?mod=viewthread&tid="+tid+"&page="+page;
        }else{
            return "forum.php?mod=viewthread&tid="+tid+"&page="+page+"&mobile=2";
        }
    }
    public static String getLoginUrl(boolean isInner){
        String url = "member.php?mod=logging&action=login";
        if(isInner){
            return url;
        }else{
            url+="&mobile=2";
        }

        return url;
    }

    public static String getimageurl(String userurl){
        //http://bbs.rs.xidian.me/274679
        String uid = GetId.getUid(userurl);

        return MySetting.BBS_BASE_URL+"ucenter/avatar.php?uid="+uid+"&size=small";
    }

    public static String getimageurl(String userurl,boolean ismiddle){
        String uid = GetId.getUid(userurl);
        return MySetting.BBS_BASE_URL+"ucenter/avatar.php?uid="+uid+"&size=middle";
    }

    public static String getSignUrl(){
        return "plugin.php?id=dsu_paulsign:sign&operation=qiandao&infloat=1&inajax=1";
    }

    public static String getUserPmUrl(String uid, boolean isInner){
        if(!isInner){
            return "home.php?mod=space&do=pm&subop=view&touid="+uid+"&mobile=2";
        }
        return "";
    }

    public static String getUserHomeUrl(String uid,boolean isInner){
        if(!isInner){
            return "home.php?mod=space&uid="+uid+"&do=profile&mobile=2";
        }
        return "";
    }

    public static String getStarUrl(String id){
        return "home.php?mod=spacecp&ac=favorite&type=thread&id="+id+"&mobile=2&handlekey=favbtn&inajax=1";
    }

    public static String getBtListUrl(String id,int page){
        return "bt.php?mod=browse&"+id+"&page="+page;
    }
}
