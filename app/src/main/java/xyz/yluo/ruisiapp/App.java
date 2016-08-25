package xyz.yluo.ruisiapp;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;

import xyz.yluo.ruisiapp.checknet.NetworkReceiver;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.database.SQLiteHelper;

/**
 * Created by free2 on 16-3-11.
 * 共享的全局数据
 */
public class App extends Application {

    private  Context context;
    private NetworkReceiver receiver= new NetworkReceiver();


    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("setting_dark_mode", false);
        if(isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //注册网络变化广播
        Log.e("application create消息广播","注册广播");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);

        //清空消息数据库
        MyDB myDB = new MyDB(context,MyDB.MODE_WRITE);
        //最多缓存2000条历史纪录
        myDB.deleteOldHistory(2000);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        //注册网络变化广播
        if(receiver!=null){
            Log.e("application onTerminate","取消注册广播");
            unregisterReceiver(receiver);
        }

        //关闭数据库
        new SQLiteHelper(context).close();

    }

    public  Context getContext() {
        return context;
    }

    //启动时设定
    //论坛基地址
    private static final String BASE_URL_ME = "http://bbs.rs.xidian.me/";
    private static final String BASE_URL_RS = "http://rs.xidian.edu.cn/";
    //是否为校园网
    public static boolean IS_SCHOOL_NET = false;


    public static String getBaseUrl() {
        if (IS_SCHOOL_NET) {
            return BASE_URL_RS;
        } else {
            return BASE_URL_ME;
        }
    }

    public static boolean ISLOGIN(Context context){
        return !TextUtils.isEmpty(App.getUid(context));
    }

    public static String getUid(Context context){
       SharedPreferences shp =  context.getSharedPreferences(MY_SHP_NAME,MODE_PRIVATE);
        return shp.getString(USER_UID_KEY,"");
    }

    public static void setHash(Context context,String hash){
        if(TextUtils.isEmpty(hash)){
            return ;
        }
        SharedPreferences shp =  context.getSharedPreferences(MY_SHP_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor =shp.edit();
        editor.putString(HASH_KEY,hash);
        editor.apply();
    }

    public static String getName(Context context){
        SharedPreferences shp =  context.getSharedPreferences(MY_SHP_NAME,MODE_PRIVATE);
        return shp.getString(USER_NAME_KEY,"");
    }

    public static String getGrade(Context context){
        SharedPreferences shp =  context.getSharedPreferences(MY_SHP_NAME,MODE_PRIVATE);
        return shp.getString(USER_GRADE_KEY,"");
    }




    /**
     * config
     * todo 把一些常量移到这儿来
     */

    //记录上次未读消息的id
    public static final String MY_SHP_NAME = "ruisi_shp";

    public static final String NOTICE_MESSAGE_KEY = "message_notice";
    public static final String USER_UID_KEY = "user_uid";
    public static final String USER_NAME_KEY = "user_name";
    public static final String HASH_KEY = "forum_hash";
    public static final String USER_GRADE_KEY = "user_grade";
    public static final String IS_REMBER_PASS_USER = "login_rember_pass";
    public static final String LOGIN_NAME = "login_name";
    public static final String LOGIN_PASS = "login_pass";


}
