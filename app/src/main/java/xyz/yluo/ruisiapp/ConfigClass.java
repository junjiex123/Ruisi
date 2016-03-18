package xyz.yluo.ruisiapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.api.ForumsData;

/**
 * Created by free2 on 16-3-11.
 *
 */
public class ConfigClass extends Application {

    public static boolean CONFIG_ISDAKA = true;
    public static String CONFIG_COOKIE = "";
    public static String CONFIG_FORMHASH = "";
    public static boolean CONFIG_ISLOGIN = false;
    public static String CONFIG_USER_NAME = "";
    public static String BBS_BASE_URL = "http://rs.xidian.edu.cn/";
    private static List<ForumsData> BBS_FORUM = new ArrayList<>();


    public ConfigClass() {
        BBS_FORUM.add(new ForumsData("西电睿思灌水专区",72,0));
        BBS_FORUM.add(new ForumsData("技术博客",560,0));
        BBS_FORUM.add(new ForumsData("西电问答",551,0));
        BBS_FORUM.add(new ForumsData("考研交流",91,0));
        BBS_FORUM.add(new ForumsData("摄影天地",561,1));
        //TODO

    }

    public static List<ForumsData> getBbsForum(){
        return BBS_FORUM;
    }
}
