package xyz.yluo.ruisiapp.checknet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by free2 on 16-4-13.
 * 判断现在的网络状态
 * 校园网or 外网
 */
public class CheckNet{

    private Context context;
    private final ExecutorService threadPool;

    public CheckNet(Context context) {
        this.context = context;
        threadPool = Executors.newCachedThreadPool();
    }

    public void startCheck(final CheckNetResponse handler){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                request(handler);
            }
        });
    }

    private void request(final CheckNetResponse checkNetResponse){
        //do thing in here
        final Message message = new Message();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        final String url = "http://rs.xidian.edu.cn/forum.php?mod=guide&view=hot&mobile=2";
        final String url2 = "http://bbs.rs.xidian.me/forum.php?mod=guide&view=hot&mobile=2";

        if (activeNetwork != null && activeNetwork.isConnected()) {
            //wifi 先检查校园网
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //检查校园网
                String s = getData(url);
                if (s.contains("西电睿思")) {
                    checkNetResponse.sendFinishMessage(1,s);
                }else {
                    String ss = getData(url2);
                    if (ss.contains("西电睿思")) {
                        checkNetResponse.sendFinishMessage(2,ss);
                    }
                }
            }else {
                //检查校外网
                String s = getData(url2);
                if (s.contains("西电睿思")) {
                    checkNetResponse.sendFinishMessage(2,s);
                }else {
                    checkNetResponse.sendFinishMessage(0,"error");
                }
            }
        }

        else {
            checkNetResponse.sendFinishMessage(0,"error");
        }
    }

    private String getData(String url){
        HttpURLConnection connection;
        URL resourceUrl;
        try {
            resourceUrl = new URL(url);
            connection = (HttpURLConnection) resourceUrl.openConnection();
            connection.setConnectTimeout(3000);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            int code = connection.getResponseCode();
            if(code>=200&&code<300){
                return new String(readFrom(connection.getInputStream()));
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private byte[] readFrom(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return new byte[0];
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        return os.toByteArray();
    }
}
