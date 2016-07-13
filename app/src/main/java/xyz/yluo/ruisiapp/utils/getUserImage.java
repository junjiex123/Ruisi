package xyz.yluo.ruisiapp.utils;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by free2 on 16-7-13.
 * 获取用户头像
 */
public class GetUserImage {
    /*
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
     * 这里的path是图片的地址
     */
    public static Uri getImageURI(File path,final String uid){
        final File file = new File(path + "/" + uid);
        Log.i("launch file",file.toString()+" "+file.exists());
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    // 从网络上获取图片
                    URL url = null;
                    try {
                        url = new URL(UrlUtils.getAvaterurlb(uid));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        if (conn.getResponseCode() == 200) {
                            InputStream is = conn.getInputStream();
                            FileOutputStream fos = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            is.close();
                            fos.close();
                            // 返回一个URI对象
                            conn.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("launch","fle delete " +file.delete());
                    }
                }
            }.start();
            return null;
        }
    }
}
