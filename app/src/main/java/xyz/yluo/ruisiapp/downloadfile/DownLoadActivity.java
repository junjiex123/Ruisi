package xyz.yluo.ruisiapp.downloadfile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;

public class DownLoadActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private downloadMsgReceiver downloadMsgReceiver;
    private TextView download_info;
    private String fileName = "";
    private TextView btnClose = null;
    private TextView btn_cancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        fileName =  getIntent().getStringExtra("fileName");
        int progress = getIntent().getIntExtra("progress",0);

        //FileUtil.requestHandleFile(this,fileName);
        Log.i("fileInfo",fileName);
        TextView downPath = (TextView) findViewById(R.id.down_path);
        downPath.setText("文件下载目录："+FileUtil.path);

        download_info = (TextView) findViewById(R.id.download_info);
        mProgressBar = (ProgressBar) findViewById(R.id.download_progress);
        btnClose = (TextView) findViewById(R.id.btn_close);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        mProgressBar.setProgress(progress);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDown();
            }
        });

        if(progress==100){
            download_compete();
            return;
        }

        //动态注册广播接收器
        downloadMsgReceiver = new downloadMsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.communication.RECEIVER");
        registerReceiver(downloadMsgReceiver, intentFilter);
        download_info.setText("下载"+fileName+" "+progress+"%");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cancelDown(){
        //to do
        Intent intent = new Intent(this,DownloadService.class);
        stopService(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if(downloadMsgReceiver !=null){
            unregisterReceiver(downloadMsgReceiver);
        }
        super.onDestroy();
    }

    /**
     * 广播接收器
     * @author len
     *
     */
    public class downloadMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            int download_type = intent.getIntExtra("type", DownloadService.DOWNLOADING);
            int progress = intent.getIntExtra("progress", 0);
            Log.i("recieve from service",progress+" "+download_type);
            switch (download_type){
                case DownloadService.DOWN_ERROR:
                    download_info.setText("文件下载失败！");
                    mProgressBar.setProgress(progress);
                    break;
                case DownloadService.DOWNLOADING:
                    mProgressBar.setProgress(progress);
                    download_info.setText("下载"+fileName+" "+progress+"%");
                    break;
                case DownloadService.DOWN_OK:

                    Log.i("recieve ok 广播",".............");
                    download_compete();
                    break;
            }

        }
    }


    /**
     * 下载完成
     */
    private void download_compete(){
        download_info.setText(fileName+"下载完成！");
        mProgressBar.setProgress(100);
        btnClose.setText("打开");

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileUtil.requestHandleFile(getApplicationContext(),fileName);
            }
        });

        btn_cancel.setText("关闭");
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
