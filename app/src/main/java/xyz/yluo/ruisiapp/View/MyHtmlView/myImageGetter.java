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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-7-16.
 * 图片下载
 */
public class MyImageGetter implements Html.ImageGetter{
    private Context context;
    private boolean isRunning  = false;
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


    private void startDown(){
        isRunning = true;
        if(listener==null){
            Log.i("MYIMAGEDOWN","listner not set not down");
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
            //替换表情到本地
            if (source.contains("static/image/smiley/")) {
                source = source.substring(source.indexOf("static"));
                source = source.replace(".gif", ".jpg").replace(".GIF", ".jpg");
                Drawable d = Drawable.createFromStream(context.getAssets().open(source), null);

                int height = (int) (80);
                int width = (int) (80);
                d.setBounds(0, 0, width, height);
                return d;
            } else {
                if(haveDown.containsKey(source)){
                    return haveDown.get(source);
                }else{
                    /**
                     * 由此添加到队列
                     */
                    if(!urls.contains(source)){
                        urls.add(source);
                    }
                    if(!isRunning){
                        startDown();
                    }

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
            if (drawable != null) {
                super.onPostExecute(drawable);

                successCount++;

                haveDown.put(s,drawable);
                urls.remove(s);
                if(urls.size()==0){
                    Log.i("IMAGEGETTER","SEND CALL BACK");
                    listener.downloadCallBack(s,drawable);
                }else {
                    /**
                     * 每成功三次开始通知刷新
                     */
                    if(successCount>=STEP){
                        successCount=0;
                        listener.downloadCallBack(s,drawable);
                        Log.i("IMAGEGETTER","SEND CALL BACK");
                    }
                }
            }

            if(urls.size()>0){
                startDown();
            }
        }
    }

    public interface ImageDownLoadListener{
        void downloadCallBack(String url,Drawable d);
    }

}
