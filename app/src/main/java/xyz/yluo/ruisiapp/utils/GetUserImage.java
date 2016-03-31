package xyz.yluo.ruisiapp.utils;

/**
 * Created by free2 on 16-3-12.
 * 通过userUrl 获得头像URL
 */
public class GetUserImage {
    public static String getimageurl(String userurl){
        //http://bbs.rs.xidian.me/274679
        String uid = GetId.getUid(userurl);

        return ConfigClass.BBS_BASE_URL+"ucenter/avatar.php?uid="+uid+"&size=small";
    }
}
