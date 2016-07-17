package xyz.yluo.ruisiapp.View.MyHtmlView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.PublicData;

/**
 * Created by free2 on 16-7-16.
 * 图片下载
 */
public class MyImageGetter implements Html.ImageGetter{
    private Context context;
    /**
     * 标记是否开始下载
     */
    private boolean isStart = false;

    /**
     * 外部程序控制结束
     */
    private boolean needStop = false;


    private List<String> urls;
    /**
     * 每4张更新一次
     */
    private final int STEP = 4;

    private int successCount = 0;

    private Map<String,Drawable> haveDown;
    private ImageDownLoadListener listener;

    public MyImageGetter(Context context,ImageDownLoadListener listener) {
        this.context = context;
        this.listener = listener;
        if(haveDown==null){
            haveDown = new HashMap<>();
        }
        urls = new ArrayList<>();
    }
    public void setNeedStop(boolean needStop) {
        this.needStop = needStop;
    }

    public void reStart(){
        needStop = false;
        startDown();
    }
    private void startDown(){
        if(needStop){
            return;
        }
        isStart = true;
        if(listener==null){
            return;
        }
        if(urls!=null&&urls.size()>0){
            String uurl = urls.get(0);
            Log.i("MYIMAGEDOWN","LoadImage 启动......");
            new LoadImage().execute(uurl);
        }
    }


    @Override
    public Drawable getDrawable(String source) {
        try {
            if (source.contains("static/image/smiley/")) {
                File dir = new File(context.getFilesDir()+"/smiley");
                if(!dir.exists()){
                    Log.e("MYIMAGEDOWN","成功创建目录"+dir.getPath()+dir.mkdirs());
                }
                String fileName = source.substring(source.lastIndexOf('/'));
                /**
                 * 缓存表情到本地
                 */
                File f = new File(dir+fileName);
                if(f.exists()){
                    Drawable d =  Drawable.createFromPath(f.getPath());
                    Log.e("image getter","获得已经存在本地的表情");
                    d.setBounds(0, 0, 80, 80);
                    return d;
                    /**
                     * source = source.replace(".gif", ".jpg").replace(".GIF", ".jpg");
                     Drawable d = Drawable.createFromStream(context.getAssets().open(source), null);
                     */
                }
            }

            if(haveDown.containsKey(source)){
                return haveDown.get(source);
            }else{
                /**
                 * 由此添加到队列
                 */
                if(!urls.contains(source)){
                    urls.add(source);
                }
                if(!isStart){
                    startDown();
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //下载网络图片
    private class LoadImage extends AsyncTask<Object, Void, Drawable> {
        private String s = "";
        private URLConnection conn;

        @Override
        protected Drawable doInBackground(Object... params) {
            String source = (String) params[0];
            s = source;
            if(haveDown.containsKey(source)){
                return haveDown.get(source);
            }

            String mySource;
            if (source.contains("http")) {
                mySource = source;
            } else {
                if (source.charAt(0) == '/') {
                    source = source.substring(1, source.length());
                }
                mySource = PublicData.getBaseUrl() + source;
            }

            try {
                URL url = new URL(mySource);
                conn= url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();

                /**
                 * 这是表情文件 返回的同时还要存入文件
                 */
                if(source.startsWith("static/image/smiley")){
                    File dir = new File(context.getFilesDir()+"/smiley");
                    String fileName = source.substring(source.lastIndexOf('/'));
                    File f = new File(dir+fileName);
                    Log.e("image getter","create new smiley file"+f.getPath()+">>"+f.createNewFile()) ;
                    FileOutputStream fos = new FileOutputStream(f);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.flush();
                    fos.close();
                    Drawable d = Drawable.createFromPath(f.getPath());
                    d.setBounds(0,0,80,80);
                    return Drawable.createFromPath(f.getPath());
                }

                /**
                 * 这是一般的图片
                 */
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bm = BitmapFactory.decodeStream(bis);
                if (bm == null) {
                    return null;
                }
                Drawable drawable = new BitmapDrawable(context.getResources(), bm);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth()*3, drawable.getIntrinsicHeight()*3);
                return drawable;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            /**
             * 顺利的返回了drawable
             */
            if (drawable != null) {
                successCount++;
                haveDown.put(s,drawable);
                urls.remove(s);
                if(urls.size()==0){
                    Log.i("IMAGEGETTER","SEND CALL BACK 全部下载已经完成");
                    listener.downloadCallBack(s,drawable);
                }else {
                    /**
                     * 每成功4次开始通知刷新
                     */
                    if(successCount>=STEP){
                        successCount=0;
                        listener.downloadCallBack(s,drawable);
                        Log.i("IMAGEGETTER","SEND CALL BACK");
                    }
                }
            }

            /**
             * 下载队列还存在的话 继续下载
             */
            if(urls.size()>0){
                startDown();
            }
        }
    }

    interface ImageDownLoadListener{
        void downloadCallBack(String url,Drawable d);
    }

}
