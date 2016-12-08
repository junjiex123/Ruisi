package xyz.yluo.ruisiapp.widget.myhtmlview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xyz.yluo.ruisiapp.App;

import static java.lang.System.in;

/**
 * Created by free2 on 16-7-16.
 * 图片下载
 * //todo make ImageGetter static
 * 出入list<url,callback>
 * 下载完毕 根据url调用callback
 */
class ImageGetter implements Html.ImageGetter {

    private static final int DOWMLOAD_OK = 1;
    private final WeakReference<Context> context;
    private static ExecutorService mPool;
    private ImageHandler handler;
    private Map<String, Drawable> haveGet = new HashMap<>();
    private Set<String> doing = new HashSet<>();

    ImageGetter(Context context, ImageDownLoadListener listener) {
        this.context = new WeakReference<>(context);
        if (mPool == null) {
            int thread = Runtime.getRuntime().availableProcessors();
            mPool = Executors.newFixedThreadPool(thread);
        }
        handler = new ImageHandler(listener);
    }

    @Override
    public Drawable getDrawable(String source) {
        source = getFullUrl(source);
        if (doing.contains(source)) {
            return null;
        }

        try {
            //读取本地表情
            if (source.contains("static/image/smiley/")) {
                final String fileName = source.substring(source.lastIndexOf('/'));
                Drawable d = null;
                if (source.contains("tieba")) {
                    d = getAssertImage("tieba", fileName);
                } else if (source.contains("jgz")) {
                    d = getAssertImage("jgz", fileName);
                } else if (source.contains("acn")) {
                    d = getAssertImage("acn", fileName);
                }

                if (d != null) {
                    return d;
                }
                String fileTosave = source.substring(source.indexOf("/smiley"));
                File f = new File(context.get().getFilesDir() + fileTosave);
                if (f.exists()) {
                    d = Drawable.createFromPath(f.getPath());
                    d.setBounds(0, 0, 80, 80);
                    return d;
                }
            }

            if (!mPool.isShutdown()) {
                if (haveGet.containsKey(source)) {
                    return haveGet.get(source);
                }
                doing.add(source);
                mPool.execute(new DownLoadRunable(source));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean isCancel() {
        return mPool.isShutdown();
    }

    public void cancel() {
        if (mPool != null) {
            synchronized (mPool) {
                mPool.shutdownNow();
            }
        }
    }

    private class DownLoadRunable implements Runnable {
        private String url;

        DownLoadRunable(String url) {
            this.url = url;
        }

        public void run() {
            if (isCancel()) {
                return;
            }

            Log.e("image getter", "开始下载url=" + url);
            try {
                //这是表情文件 返回的同时还要存入文件
                if (this.url.contains("static/image/smiley")) {
                    String fileTosavedir = this.url.substring(this.url.indexOf("/smiley"), this.url.lastIndexOf("/"));
                    File dir = new File(context.get().getFilesDir() + fileTosavedir);
                    if (!dir.exists()) {
                        Log.e("image getter", "创建目录" + dir.mkdirs());
                    }
                    String fulldir = this.url.substring(this.url.indexOf("/smiley"));
                    File f = new File(context.get().getFilesDir() + fulldir);
                    Bitmap b;
                    if (f.exists()) {
                        b = Picasso.with(context.get()).load(f).get();
                    } else {
                        b = Picasso.with(context.get()).load(this.url).get();
                        FileOutputStream out = new FileOutputStream(f);
                        b.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                    }
                    if (b != null) {
                        Drawable d = new BitmapDrawable(context.get().getResources(), b);
                        d.setBounds(0, 0, 80, 80);
                        haveGet.put(url, d);
                        sendMessage(d);
                    }
                } else {
                    /**
                     * 这是一般的图片
                     */
                    Bitmap bm = Picasso.with(context.get()).load(this.url).get();
                    if (bm != null) {
                        Drawable d = new BitmapDrawable(context.get().getResources(), bm);
                        d.setBounds(0, 0, d.getIntrinsicWidth() * 2, d.getIntrinsicHeight() * 2);
                        haveGet.put(this.url, d);
                        sendMessage(d);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                doing.remove(url);
            }
        }
    }

    private void sendMessage(Drawable d) {
        Message msg = Message.obtain(handler, DOWMLOAD_OK, d);
        handler.sendMessage(msg);
    }


    private static class ImageHandler extends Handler {
        private final WeakReference<ImageDownLoadListener> callback;

        private ImageHandler(ImageDownLoadListener callback) {
            this.callback = new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            ImageDownLoadListener l = this.callback.get();
            if (msg.what == DOWMLOAD_OK) {
                l.downloadCallBack((Drawable) msg.obj);
            }
        }
    }

    private Drawable getAssertImage(String type, String fileName) {
        try {
            InputStream i = context.get().getAssets().open("static/image/smiley/" + type + fileName);
            Log.e("bendi smiley ", type + fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(i);
            Drawable d = new BitmapDrawable(context.get().getResources(), bitmap);
            d.setBounds(0, 0, 80, 80);
            in.close();
            return d;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFullUrl(String s) {
        if (s.contains("http")) {
            return s;
        } else {
            if (s.charAt(0) == '/') {
                s = s.substring(1, s.length());
            }
            return App.getBaseUrl() + s;
        }
    }

    interface ImageDownLoadListener {
        void downloadCallBack(Drawable d);
    }
}
