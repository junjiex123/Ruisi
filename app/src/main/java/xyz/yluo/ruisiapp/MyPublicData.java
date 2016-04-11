package xyz.yluo.ruisiapp;

import android.app.Application;

/**
 * Created by free2 on 16-3-11.
 *
 */
public class MyPublicData extends Application {

    //启动时设定
    //论坛基地址
    public String BBS_BASE_URL = "http://rs.xidian.edu.cn/";
    //是否为校园网
    public boolean CONFIG_IS_INNER = false;
    //论坛FORMHASH
    public String CONFIG_FORMHASH = "";
    //是否登陆
    public boolean CONFIG_ISLOGIN = false;
    //用户名
    public String CONFIG_USER_NAME = "";
    //是否显示置顶帖
    public boolean CONFIG_ISSHOW_ZHIDIN = false;
    //是否将文章页面的样式删除
    public boolean CONFIG_SHOW_PLAIN_TEXT = true;
    //user uid
    public String CONFIG_USER_UID = "";


}
