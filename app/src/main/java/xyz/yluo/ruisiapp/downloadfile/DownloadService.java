package xyz.yluo.ruisiapp.downloadfile;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.httpUtil.FileResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;

/***
 * 下载服务 2016 07 11
 * @author yang
 */
public class DownloadService extends Service {
    public static final int DOWN_OK = 1;
    public static final int DOWNLOADING = 0;
    public static final int DOWN_ERROR = -1;
    private static String down_url;
    private String filename = null;
    private int downloadProgress = 0;

    private NotificationCompat.Builder mBuilder;
    private NotificationManager  mNotifyManager;
    private Intent intent = new Intent("com.example.communication.RECEIVER");

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * 方法描述：onStartCommand方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadProgress = 0;
        down_url = intent.getStringExtra("download_url");
        filename = FileUtil.getFileName(down_url);

        HttpUtil.get(getApplicationContext(), down_url, new FileResponseHandler(filename) {
            @Override
            public void onStartDownLoad(String fileName) {
                Log.i("download","====on statt start down load "+fileName);
                if(filename.equals("null")&&!fileName.equals("null")){
                    filename = fileName;
                }
                createNotification(filename);
            }

            @Override
            public void onSuccess(File file) {
                Log.i("download","success");
                updateProgress(DOWN_OK,100);
            }
            @Override
            public void onFailure(Throwable throwable, File file) {
                Log.i("download","fail");
                updateProgress(DOWN_ERROR,0);
            }
            @Override
            public void onProgress(int progress, long totalBytes) {
                super.onProgress(progress, totalBytes);
                Log.i("download progress",progress+""+totalBytes);
                updateProgress(DOWNLOADING,progress);
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 方法描述：createNotification方法
     * @see     DownloadService
     */
    public void createNotification(String filename) {
        Toast.makeText(this,"开始下载"+filename,Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent(this, DownLoadActivity.class);
        resultIntent.putExtra("fileName",filename);
        resultIntent.putExtra("progress",downloadProgress);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("下载文件"+filename)
                .setContentIntent(notifyPendingIntent)
                .setSmallIcon(R.mipmap.logo);
        mBuilder.setProgress(100, 0, false);
        mBuilder.setContentText("下载进度："+0+"%");
        mNotifyManager.notify(0, mBuilder.build());
    }
    //type
    private void updateProgress(int type, int progress){
        // Start a lengthy operation in a background thread
        /**
         * 发送广播给ui activity
         */
        downloadProgress = progress;
        intent.putExtra("progress", progress);
        if(progress==100){
            type = DOWN_OK;
        }
        intent.putExtra("type",type);
        sendBroadcast(intent);
        Log.i("===发送广播===",type+" "+progress);
        switch (type){
            case DOWN_ERROR:
                mBuilder.setContentText("文件下载失败！")
                        .setContentIntent(null)
                        // Removes the progress bar
                        .setProgress(0,0,false);

                mNotifyManager.notify(0, mBuilder.build());
                break;
            case DOWN_OK:
                mBuilder.setContentText("文件下载完成！")
                        // Removes the progress bar
                        .setProgress(0,0,false);
                mNotifyManager.notify(0, mBuilder.build());
                /**
                 * 取消之前的notification 新建
                 */
                mNotifyManager.cancel(0);

                Intent okIntent = new Intent(this, DownLoadActivity.class);
                okIntent.putExtra("fileName",filename);
                okIntent.putExtra("progress",100);
                // Creates the PendingIntent
                PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 1, okIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setContentTitle(filename+"下载完成")
                        .setContentText("文件下载完成，点击打开！！")
                        .setContentIntent(notifyPendingIntent)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.logo);

                mNotifyManager.notify(1, mBuilder.build());
                break;
            case DOWNLOADING:
                mBuilder.setProgress(100, progress, false);
                mBuilder.setContentText("下载进度："+progress+"%");
                downloadProgress = progress;
                //发送Action为com.example.communication.RECEIVER的广播
                mNotifyManager.notify(0, mBuilder.build());
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("service","destroy");
        //HttpUtil.cancel() /todo 取消下载
        if(mNotifyManager!=null){
            FileUtil.deleteFile(filename);
            mNotifyManager.cancelAll();
        }
    }
}