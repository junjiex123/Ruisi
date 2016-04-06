package xyz.yluo.ruisiapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import xyz.yluo.ruisiapp.MySetting;

/**
 * Created by free2 on 16-3-31.
 * 能够显示图片的textview
 * 显示html
 */
public class MyHtmlTextView extends TextView implements Html.ImageGetter {

    private Activity activity;

    public MyHtmlTextView(Context context) {
        super(context);
    }

    public MyHtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void mySetText(Activity activity, String text) {
        this.activity = activity;
        Spanned spanned = Html.fromHtml(text, this, null);
        super.setText(spanned);
    }


    @Override
    public Drawable getDrawable(String source) {
        Drawable drawable = null;

        try {
            //替换表情到本地
            if (source.contains("static/image/smiley/")) {
                source = source.substring(source.indexOf("static"));
                if(!source.contains("tieba")){
                    source = source.replace(".gif",".jpg").replace(".png",".jpg").replace(".GIF",".jpg").replace(".png",".jpg");
                }
                //asset file
                Drawable drawable1 = Drawable.createFromStream(activity.getAssets().open(source), null);
                drawable1.setBounds(0, 0, 70, 70);
                return drawable1;
            } else {

                //TODO 无法显示网络图片
                URL url = null;
                if(source.startsWith("http")){
                    url = new URL(source);
                }else{
                    url = new URL(MySetting.BBS_BASE_URL + source);
                }

                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                /* Buffered is always good for a performance plus. */
                BufferedInputStream bis = new BufferedInputStream(is);
                /* Decode url-data to a bitmap. */
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
                drawable = new BitmapDrawable(activity.getResources(), bm);
                drawable.setBounds(0, 0, bm.getWidth(), bm.getHeight());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return drawable;
    }
}
