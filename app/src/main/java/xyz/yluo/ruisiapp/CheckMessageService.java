package xyz.yluo.ruisiapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.SyncHttpClient;

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
                .setContentTitle("回复提醒")
                .setContentText("你有新的回复点击查看")
                .setAutoCancel(true);

        final Intent resultIntent = new Intent(this, SingleArticleActivity.class);
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
                        if (isNotisfy) {
                            String url = e.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").attr("href");
                            resultIntent.putExtra("url", url);
                            PendingIntent rIntent = PendingIntent.getActivity(getApplicationContext(), 1, resultIntent, 0);
                            builder.setContentText(e.select(".ntc_body").text());
                            builder.setContentIntent(rIntent);
                            builder.setFullScreenIntent(rIntent, true);
                            mNotifyMgr.notify(2, builder.build());
                            break;
                        }
                        isHaveUnreadMessage = true;
                    }
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    Log.i(Tag, "thread running.....check message......");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String url = PublicData.getBaseUrl() + "home.php?mod=space&do=notice&view=mypost&type=post";
                    if (!PublicData.IS_SCHOOL_NET) {
                        url = url + "&mobile=2";
                    }
                    client.get(url, handler);

                    intent.putExtra("isHaveMessage", isHaveUnreadMessage);
                    sendBroadcast(intent);
                    Log.i(Tag, "发送广播...." + isHaveUnreadMessage);
                    try {
                        Thread.sleep(40000);
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
        Log.i(Tag, "service destroy");
    }

    //start service时执行
    //可以start多次
    //通过intent 可以传入数据
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.isRunning = intent.getBooleanExtra("isRunning", false);
        this.isNotisfy = intent.getBooleanExtra("isNotisfy", false);
        boolean isClearMessage = intent.getBooleanExtra("isClearMessage", false);
        /**
         * todo 消息已读清除
         */
        if (isClearMessage) {
            clearAllMessage();
        }

        Log.i(Tag, "service start is running" + isRunning + " is notify " + isNotisfy);
        return super.onStartCommand(intent, flags, startId);
    }
}
