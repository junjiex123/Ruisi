package xyz.yluo.ruisiapp;

import android.app.Application;

/**
 * Created by free2 on 16-3-11.
 * 共享的全局数据 //todo 更好的实现？？？
 */
public class PublicData extends Application {

    //启动时设定
    //论坛基地址
    public static final String BASE_URL_ME = "http://bbs.rs.xidian.me/";
    public static final String BASE_URL_RS = "http://rs.xidian.edu.cn/";
    //是否为校园网
    public static boolean IS_SCHOOL_NET = false;
    //论坛FORMHASH
    public static String FORMHASH = "";
    //是否登陆
    public static boolean ISLOGIN = false;
    //用户名
    public static String USER_NAME = "";
    //user uid
    public static String USER_UID = "";
    public static String USER_GRADE = "";

    public static String getBaseUrl() {
        if (IS_SCHOOL_NET) {
            return BASE_URL_RS;
        } else {
            return BASE_URL_ME;
        }
    }


}
