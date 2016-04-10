package xyz.yluo.ruisiapp.todo;

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

import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.NewArticleActivity_2;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.SyncHttpClient;

public class CheckMessageService extends Service {

    public CheckMessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_favorite_24dp)
                        .setContentTitle("回复提醒")
                        .setContentText("你有新的回复点击查看")
                        .setAutoCancel(true);

        final Intent resultIntent = new Intent(this, NewArticleActivity_2.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        //mNotifyMgr.notify(1, mBuilder.build());

        final SyncHttpClient client = new SyncHttpClient();
        final ResponseHandler handler = new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                //System.out.println(new String(responseBody));
                System.out.println(">>>>>>>service running");
                Document document = Jsoup.parse(new String(response));
                Elements elemens  = document.select(".nts").select("dl.cl");
                for(Element e:elemens){
                    String s = e.select(".ntc_body").attr("style");
                    if(s.contains("bold")){
                        mBuilder.setContentText(e.select(".ntc_body").text());
                        mNotifyMgr.notify(1, mBuilder.build());
                    }
                }
            }
            @Override
            public void onFailure(Throwable e) {
            }
        };

        System.out.println(">>>create");
        final String url1 = "http://rs.xidian.edu.cn/home.php?mod=space&do=notice&view=mypost&type=post";
        final String url2 = "http://bbs.rs.xidian.me/home.php?mod=space&do=notice&view=mypost&type=post";

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(!MySetting.CONFIG_IS_INNER){
                        client.get(url2,handler);
                    }else{
                        client.get(url1,handler);
                    }
                    try {
                        Thread.sleep(15000);
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
        System.out.println(">>>destroy");

    }

    //start service时执行
    //可以start多次
    //通过intent 可以传入数据
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }
}
