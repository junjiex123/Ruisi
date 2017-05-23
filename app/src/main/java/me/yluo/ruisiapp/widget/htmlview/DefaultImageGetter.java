package me.yluo.ruisiapp.widget.htmlview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.widget.htmlview.callback.ImageGetter;
import me.yluo.ruisiapp.widget.htmlview.callback.ImageGetterCallBack;

//rs 表情static/image/smiley/jgz/jgz065.png
public class DefaultImageGetter implements ImageGetter {

    private static final String TAG = DefaultImageGetter.class.getSimpleName();
    private Context context;
    private ImageCacher imageCacher;
    private int maxWidth;//最大宽度 图片不要大于这个值
    private static Set<BitmapWorkerTask> taskCollection;
    private static ExecutorService mPool;
    private final int smileySize;//限制表情最大值

    //表情链接
    private static final String SMILEY_PREFIX = "static/image/smiley/";

    static {
        taskCollection = new HashSet<>();
        if (mPool == null) {
            int thread = Runtime.getRuntime().availableProcessors();
            mPool = Executors.newFixedThreadPool(thread);
        }
    }


    public DefaultImageGetter(int maxWidth, Context context) {
        this.context = context;
        this.maxWidth = maxWidth;
        imageCacher = ImageCacher.instance(context.getCacheDir() + "/imageCache/");
        smileySize = (int) (HtmlView.FONT_SIZE * 1.6f);
    }


    @Override
    public void getDrawable(String source, int start, int end, ImageGetterCallBack callBack) {
        if (callBack == null) return;
        //检查内存缓存
        Bitmap b = imageCacher.getMemCache(source);
        if (b == null && !TextUtils.isEmpty(source)) {
            if (source.startsWith(SMILEY_PREFIX)) {
                //assets 表情
                String fileToSave = source.substring(source.indexOf("smiley"));
                if (source.contains("/tieba") || source.contains("/jgz") || source.contains("/acn") || source.contains("/default")) {
                    if(source.contains("/default")){
                        fileToSave = fileToSave.replace(".gif",".png");
                    }
                    try {
                        b = decodeBitmapFromStream(context.getAssets().open(fileToSave), false, smileySize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //不在assets,检查文件
                if (b == null) {
                    File smileyFile = new File(context.getFilesDir() + "/" + fileToSave);
                    if (smileyFile.exists()) {
                        try {
                            b = decodeBitmapFromStream(new FileInputStream(smileyFile), false, smileySize);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                b = scaleSmiley(b, smileySize);
            } else {
                //网络图片再检查硬盘缓存
                b = BitmapFactory.decodeStream(imageCacher.getDiskCacheStream(source));
                b = limitBitmap(b, maxWidth);
            }

            //放到内存缓存
            if (b != null) {
                imageCacher.putMemCache(source, b);
            } else {
                //没有缓存去下载
                if (!mPool.isShutdown()) {
                    mPool.execute(new BitmapWorkerTask(source, start, end, callBack));
                }
            }
        }

        callBack.onImageReady(source, start, end, bmpToDrawable(source, b));
    }

    public void cancelAllTasks() {
        if (taskCollection != null) {
            for (BitmapWorkerTask t : taskCollection) {
                t.cancel();
            }
        }

        if (mPool != null && !mPool.isShutdown()) {
            synchronized (mPool) {
                mPool.shutdownNow();
            }
        }
    }

    //图片下载及存储
    private class BitmapWorkerTask implements Runnable {
        private String imageUrl;
        private boolean isCancel;
        private int start, end;
        private ImageGetterCallBack callBack;
        private boolean isSmiley = false;

        public BitmapWorkerTask(String imageUrl, int start, int end, ImageGetterCallBack callBack) {
            this.imageUrl = imageUrl;
            this.start = start;
            this.end = end;
            this.callBack = callBack;
        }

        public void cancel() {
            isCancel = true;
        }

        @Override
        public void run() {
            taskCollection.add(this);
            Log.d(TAG, "start download image " + imageUrl);
            HttpURLConnection urlConnection = null;
            BufferedOutputStream out = null;
            BufferedInputStream in = null;
            Bitmap bitmap = null;
            try {
                //表情文件
                isSmiley = imageUrl.startsWith(SMILEY_PREFIX);
                final URL url = new URL(imageUrl.startsWith("http") ? imageUrl : App.getBaseUrl() + imageUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), 4 * 1024);
                bitmap = BitmapFactory.decodeStream(in);
                if (bitmap != null && !isCancel) {
                    Log.d(TAG, "download image compete " + imageUrl);
                    //存到硬盘
                    Bitmap.CompressFormat f = Bitmap.CompressFormat.PNG;
                    if (imageUrl.endsWith(".jpg") || imageUrl.endsWith(".jpeg") ||
                            imageUrl.endsWith(".JPG") || imageUrl.endsWith(".JPEG")) {
                        f = Bitmap.CompressFormat.JPEG;
                    } else if (imageUrl.endsWith(".webp")) {
                        f = Bitmap.CompressFormat.WEBP;
                    }

                    if (isSmiley) { //缓存表情
                        String fileDir = imageUrl.substring(imageUrl.indexOf("/smiley"), imageUrl.lastIndexOf("/"));
                        File dir = new File(context.getFilesDir() + fileDir);
                        if (!dir.exists()) {
                            Log.e("image getter", "创建目录" + dir.mkdirs());
                        }
                        String path = imageUrl.substring(imageUrl.indexOf("/smiley"));
                        File file = new File(context.getFilesDir() + path);
                        Log.d(TAG, "save smiley to file:" + file);

                        out = new BufferedOutputStream(new FileOutputStream(file));
                        bitmap.compress(f, 100, out);
                        out.flush();
                        bitmap = scaleSmiley(bitmap, smileySize);
                    } else { //缓存一般图片
                        out = new BufferedOutputStream(
                                imageCacher.newDiskCacheStream(imageUrl), 4 * 1024);
                        bitmap.compress(f, 90, out);
                        out.flush();
                        //存到内存之前需要压缩
                        bitmap = limitBitmap(bitmap, maxWidth);
                    }

                    imageCacher.putMemCache(imageUrl, bitmap);
                } else {
                    Log.d(TAG, "download image error " + imageUrl);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            taskCollection.remove(this);

            if (!isCancel && bitmap != null) {
                Log.d(TAG, "notify update " + imageUrl);
                //如果下载失败就不用返回了 因为之前以前有holder了
                callBack.onImageReady(imageUrl, start, end, bmpToDrawable(imageUrl, bitmap));
            }
        }
    }

    //永远不要返回null
    public Drawable bmpToDrawable(String source, Bitmap b) {
        if (b == null) {
            return getPlaceHolder(source);
        } else {
            Drawable d = new BitmapDrawable(context.getResources(), b);
            d.setBounds(0, 0, b.getWidth(), b.getHeight());
            return d;
        }
    }


    private Drawable getPlaceHolder(String souce) {
        ColorDrawable colorDrawable = new ColorDrawable(0xffcccccc);
        if (souce == null || souce.isEmpty()) {
            colorDrawable.setBounds(0, 0, 120, 120);
        } else if (souce.startsWith(SMILEY_PREFIX)) {
            colorDrawable.setBounds(0, 0, smileySize, smileySize);
        } else {
            colorDrawable.setBounds(0, 0, (int) (maxWidth / 2.0f), (int) (maxWidth / 4.0f));
        }

        return colorDrawable;
    }


    public static Bitmap decodeBitmapFromStream(InputStream is, boolean needScale, int reqWidth) {
        if (is == null) return null;
        if (needScale) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeStream(is, null, options);
            return limitBitmap(src, reqWidth);
        } else {
            Bitmap src = BitmapFactory.decodeStream(is);
            return limitBitmap(src, reqWidth);
        }
    }

    public static Bitmap decodeBitmapFromRes(Resources res, int resId, boolean needScale, int reqWidth) {
        if (needScale) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeResource(res, resId, options);
            return limitBitmap(src, reqWidth);
        } else {
            Bitmap src = BitmapFactory.decodeResource(res, resId);
            return limitBitmap(src, reqWidth);
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        // 源图片的高度和宽度
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            final int halfWidth = width / 2;
            while ((halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    //限制最大图片
    private static Bitmap limitBitmap(Bitmap src, int maxWidth) {
        if (src == null) return null;
        int srcWidth = src.getWidth();
        if (srcWidth <= maxWidth) return src;

        float scale = maxWidth * 1.0f / srcWidth;
        int dstHeight = (int) (scale * src.getHeight());

        Bitmap dst = Bitmap.createScaledBitmap(src, maxWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    //缩放图片
    private Bitmap scaleSmiley(Bitmap origin, int dstWidth) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scale = ((float) dstWidth) / width;

        //一点点误差忽略不计
        if (Math.abs(scale - 1) < 0.15) {
            return origin;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }
}

/**
 * 笔记 android 分辨率和dpi关系
 * ldpi	    120dpi	0.75
 * mdpi	    160dpi	1
 * hdpi	    240dpi	1.5
 * xhdpi    320dpi	2     1280*720   1dp=2px
 * xxhdpi： 480dpi  3     1920*1080 1dp=3px
 * xxxhdpi  640dpi  4
 */
