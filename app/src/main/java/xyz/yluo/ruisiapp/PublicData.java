package xyz.yluo.ruisiapp;

import android.app.Application;

/**
 * Created by free2 on 16-3-11.
 * 共享的全局数据 //todo 更好的实现？？？
 */
public class PublicData extends Application {

    //启动时设定
    //论坛基地址
    public static String BASE_URL = "http://rs.xidian.edu.cn/";
    //是否为校园网
    public static boolean IS_SCHOOL_NET = false;
    //论坛FORMHASH
    public static String FORMHASH = "";
    //是否登陆
    public  static boolean ISLOGIN = false;
    //用户名
    public static String USER_NAME = "";
    //是否显示置顶帖
    public  static boolean ISSHOW_ZHIDIN = false;
    //是否将文章页面的样式删除
    public static boolean ISSHOW_PLAIN = true;
    //user uid
    public static  String USER_UID = "";


}
