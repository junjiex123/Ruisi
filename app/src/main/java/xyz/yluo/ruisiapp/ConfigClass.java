package xyz.yluo.ruisiapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by free2 on 16-3-11.
 *
 */
public class ConfigClass extends Application {

    public String CONFIG_COOKIE;
    public String CONFIG_FORMHASH;
    public static boolean CONFIG_ISLOGIN = false;
    public final Map<String,String> FORUM = new HashMap<>();

    public ConfigClass() {
        FORUM.put("549","文章天地");
        FORUM.put("550","心灵花园");
        FORUM.put("106","校园交易");
        FORUM.put("108","我是女生");
        FORUM.put("72","西电睿思灌水专区");
    }
}
