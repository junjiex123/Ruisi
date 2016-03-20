package xyz.yluo.ruisiapp.utils;

import android.app.Application;

/**
 * Created by free2 on 16-3-11.
 *
 */
public class ConfigClass extends Application {

    //是否已经打卡
    public static boolean CONFIG_ISDAKA = true;
    //cookie
    public static String CONFIG_COOKIE = "";
    //论坛FORMHASH
    public static String CONFIG_FORMHASH = "";
    //是否登陆
    public static boolean CONFIG_ISLOGIN = false;
    //用户名
    public static String CONFIG_USER_NAME = "";
    //论坛基地址
    public static String BBS_BASE_URL = "http://rs.xidian.edu.cn/";
    //是否显示置顶帖
    public static boolean CONFIG_ISSHOW_ZHIDIN = false;
    //是否将文章页面的样式删除
    public static boolean CONFIG_SHOW_PLAIN_TEXT = true;

}
