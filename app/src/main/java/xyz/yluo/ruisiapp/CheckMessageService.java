package xyz.yluo.ruisiapp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xyz.yluo.ruisiapp.database.MyDbUtils;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.SyncHttpClient;


/**
 * 一分钟检擦一次有没有未读消息
 */

public class CheckMessageService extends Service {

    private final String Tag = "checkMsgService";
    //是否弹窗提醒
    private boolean isNotisfy = false;
    private Intent intent = new Intent("com.ruisi.checkmsg");
    private boolean isHaveUnreadMessage = false;
    private boolean isRunning = false;

    public CheckMessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;

        Log.i(Tag, "service create");

        final NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle("未读消息提醒")
                .setContentText("你有未读的消息哦,去我的消息页面查看吧！")
                .setAutoCancel(true);

        final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final SyncHttpClient client = new SyncHttpClient();
        final ResponseHandler handler = new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements elemens = document.select(".nts").select("dl.cl");
                for (Element e : elemens) {
                    String s = e.select(".ntc_body").attr("style");
                    if (s.contains("bold")) {
                        /**
                         * get message
                         */
                        String url = e.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").attr("href");
                        String info = e.select(".ntc_body").text();
                        //只要有未读的就插入 到数据库在判断
                        MyDbUtils myDbUtils = new MyDbUtils(getApplicationContext(), MyDbUtils.MODE_WRITE);
                        myDbUtils.insertMessage(url,info);
                    }
                }

                MyDbUtils myDbUtilsR = new MyDbUtils(getApplicationContext(), MyDbUtils.MODE_READ);
                if(myDbUtilsR.isHaveUnReadMessage()){
                    isHaveUnreadMessage = true;
                    if (isNotisfy) {
                        mNotifyMgr.notify(10, builder.build());
                        Log.e("message","发送未读消息弹窗");
                    }
                }
                intent.putExtra("isHaveMessage", isHaveUnreadMessage);
                sendBroadcast(intent);
                Log.i(Tag, "发送广播...." + isHaveUnreadMessage);

            }


        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    Log.i(Tag, "thread running.....check message......");
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String url = Config.getBaseUrl() + "home.php?mod=space&do=notice&view=mypost&type=post";
                    if (!Config.IS_SCHOOL_NET) {
                        url = url + "&mobile=2";
                    }
                    client.get(url, handler);


                    try {
                        Thread.sleep(58500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

    /**
     * 消息已读设为未读
     * todo 有一些延迟要解决
     */
    private void clearAllMessage() {
        isHaveUnreadMessage = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.e(Tag, "service destroy");
    }

    //start service时执行
    //可以start多次
    //通过intent 可以传入数据
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = intent.getExtras();
        boolean isClearMessage = false;
        if(b!=null){
            this.isRunning = b.getBoolean("isRunning", false);
            this.isNotisfy = b.getBoolean("isNotisfy", false);
            isClearMessage = b.getBoolean("isClearMessage", false);
        }

        /**
         * todo 消息已读清除
         */
        if (isClearMessage) {
            clearAllMessage();
            Log.e("clear_ALL","已将所有消息已读");
        }
        Log.i(Tag, "service start is running" + isRunning + " is notify " + isNotisfy);
        return super.onStartCommand(intent, flags, startId);
    }
}
