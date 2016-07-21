package xyz.yluo.ruisiapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by free2 on 16-3-11.
 * 共享的全局数据
 */
public class Config extends Application {

    private  Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
    }

    public  Context getContext() {
        return context;
    }

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
