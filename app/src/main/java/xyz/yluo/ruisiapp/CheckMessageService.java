package xyz.yluo.ruisiapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.SyncHttpClient;

public class CheckMessageService extends Service {

    public CheckMessageService() {
    }

    private boolean isRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        System.out.println("service >>>create");
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
                Elements elemens  = document.select(".nts").select("dl.cl");
                for(Element e:elemens){
                    String s = e.select(".ntc_body").attr("style");
                    if(s.contains("bold")){
                        String url =e.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").attr("href");
                        resultIntent.putExtra("url",url);
                        PendingIntent rIntent = PendingIntent.getActivity(getApplicationContext(), 1, resultIntent, 0);
                        builder.setContentText(e.select(".ntc_body").text());
                        builder.setContentIntent(rIntent);
                        builder.setFullScreenIntent(rIntent,true);
                        mNotifyMgr.notify(1, builder.build());
                        break;
                    }
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning){
                    String url = PublicData.getBaseUrl()+"home.php?mod=space&do=notice&view=mypost&type=post";
                    client.get(url,handler);
                    try {
                        Thread.sleep(30000);
                        System.out.println("thread running.....");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        System.out.println("service >>>destroy");
    }
    //start service时执行
    //可以start多次
    //通过intent 可以传入数据
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("service >>>start");
        this.isRunning = intent.getBooleanExtra("isRunning",false);
        System.out.println(isRunning);
        return super.onStartCommand(intent, flags, startId);
    }
}
