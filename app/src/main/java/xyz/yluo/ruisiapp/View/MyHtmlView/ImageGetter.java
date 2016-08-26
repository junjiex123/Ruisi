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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xyz.yluo.ruisiapp.App;

/**
 * Created by free2 on 16-7-16.
 * 图片下载
 */
class ImageGetter implements Html.ImageGetter{
    private Context context;
    /**
     * 标记是否开始下载
     */
    private boolean isStart = false;

    /**
     * 外部程序控制结束
     */
    private boolean isStop = false;


    private Set<String> urls;
    /**
     * 每4张更新一次
     */
    private static final int STEP = 4;

    private int successCount = 0;

    private Map<String,Drawable> haveDown;
    private ImageDownLoadListener listener;

    ImageGetter(Context context, ImageDownLoadListener listener) {
        this.context = context;
        this.listener = listener;
        if(haveDown==null){
            haveDown = new HashMap<>();
        }
        urls = new HashSet<>();
    }

    void stopDown() {
        this.isStop = false;
    }

    void reStart(){
        isStop = false;
        startDown();
    }

    @Override
    public Drawable getDrawable(String source) {
        try {
            if(haveDown.containsKey(source)){
                return haveDown.get(source);
            }

            String fileName = source.substring(source.lastIndexOf('/'));
            if (source.contains("static/image/smiley/")) {
                Drawable d = null;
                String smiley_dir = "static/image/smiley/";
                if(source.contains("tieba")){
                    //// TODO: 16-8-26
                    InputStream in = context.getAssets().open(smiley_dir+"tieba" + fileName);
                    Log.e("bendi tieba ",smiley_dir+"tieba" + fileName);
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    d = new BitmapDrawable(context.getResources(), bitmap);
                }else{
                    File f = new File(context.getFilesDir()+"/smiley"+fileName);
                    if(f.exists()){
                        d =  Drawable.createFromPath(f.getPath());
                    }
                }

                if(d!=null){
                    d.setBounds(0, 0, 80, 80);
                    return d;
                }
            }

            urls.add(source);
            Log.e("imggetter","add queue "+source);
            if(!isStart){
                startDown();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startDown(){
        if(isStop ||listener==null){
            return;
        }
        isStart = true;
        if(!urls.isEmpty()){
            String uurl = urls.iterator().next();
            new LoadImage().execute(uurl);
        }
    }

    //下载网络图片
    private class LoadImage extends AsyncTask<String, Void, Drawable> {
        private String s = "";


        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            s = source;
            //全路径
            String mySource;
            if (source.contains("http")) {
                mySource = source;
            } else {
                if (source.charAt(0) == '/') {
                    source = source.substring(1, source.length());
                }
                mySource = App.getBaseUrl() + source;
            }
            try {
                URL url = new URL(mySource);
                URLConnection conn= url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();

                //这是表情文件 返回的同时还要存入文件
                if(source.contains("static/image/smiley")){
                    File dir = new File(context.getFilesDir()+"/smiley");
                    if(!dir.exists()){
                        Log.e("image getter","创建目录"+dir.mkdirs());
                    }
                    String fileName = source.substring(source.lastIndexOf('/'));
                    File f = new File(dir+fileName);
                    if(!f.exists()){
                        Log.e("image getter","创建"+f.getPath()+">>"+f.createNewFile()) ;
                        FileOutputStream fos = new FileOutputStream(f);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        is.close();
                        fos.flush();
                        fos.close();
                    }

                    Drawable d = Drawable.createFromPath(f.getPath());
                    d.setBounds(0,0,80,80);
                    return d;
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
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth()*2, drawable.getIntrinsicHeight()*2);
                is.close();
                bis.close();
                return drawable;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if(drawable!=null){
                successCount++;
            }

            haveDown.put(s,drawable);
            urls.remove(s);
            if(urls.isEmpty()||successCount>=STEP){
                Log.e("imggetter","全部下载已经完成");
                listener.downloadCallBack(s,drawable);
            }
            if(successCount>=STEP)
                successCount = 0;

            /**
             * 下载队列还存在的话 继续下载
             */
            if(!urls.isEmpty()){
                startDown();
            }
        }
    }

    interface ImageDownLoadListener{
        void downloadCallBack(String url,Drawable d);
    }

}
