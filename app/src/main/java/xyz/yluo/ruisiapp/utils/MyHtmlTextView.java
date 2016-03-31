package xyz.yluo.ruisiapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by free2 on 16-3-31.
 * 能够显示图片的textview
 * 显示html
 */
public class MyHtmlTextView extends TextView implements Html.ImageGetter{

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

    public void mySetText(Activity activity, String text){
        this.activity = activity;
        Spanned spanned =  Html.fromHtml(text,this,null);
        super.setText(spanned);
    }


    @Override
    public Drawable getDrawable(String source) {
        Drawable drawable = null;
        try {
            //替换表情到本地
            if(source.startsWith("static/image/smiley/")&&(source.contains(".gif")||source.contains(".GIF"))){
                //asset file
                GifDrawable gifFromAssets = new GifDrawable(activity.getAssets(), source);
                gifFromAssets.setBounds(0,0,80,80);
                return  gifFromAssets;
            }else if(source.startsWith("static/image/smiley/")){
                InputStream ims = activity.getAssets().open(source);
                drawable = Drawable.createFromStream(ims, null);
                drawable.setBounds(0,0,80,80);
                return drawable;
            }
            else{
                URL url = new URL(ConfigClass.BBS_BASE_URL+source);
                drawable = Drawable.createFromStream(url.openStream(), "src");  //获取网路图片
            }
        } catch (Exception e) {
//                        drawable = ContextCompat.getDrawable(activity,R.drawable.image_placeholder);
            return null;
        }

        drawable.setBounds(0, 0,
                DensityUtil.dip2px(activity,drawable.getIntrinsicWidth()*2),
                DensityUtil.dip2px(activity,drawable.getIntrinsicHeight())*2);
        return drawable;
    }
}
