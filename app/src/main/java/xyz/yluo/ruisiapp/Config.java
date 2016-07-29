package xyz.yluo.ruisiapp;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import xyz.yluo.ruisiapp.checknet.NetworkReceiver;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.database.SQLiteHelper;

/**
 * Created by free2 on 16-3-11.
 * 共享的全局数据
 */
public class Config extends Application {

    private  Context context;
    private NetworkReceiver receiver= new NetworkReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();

        //注册网络变化广播
        Log.e("application create消息广播","注册广播");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);

        //清空消息数据库
        MyDB myDB = new MyDB(context,MyDB.MODE_WRITE);
        myDB.deleteOldMessage();
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
