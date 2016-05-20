package xyz.yluo.ruisiapp.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.utils.HandleLinkClick;

/**
 * Created by free2 on 16-3-31.
 * 能够显示图片的textview
 * 显示html
 */
public class MyHtmlTextView extends TextView{

    private Activity activity;
    private String text;
    private Map<String,Drawable> drawableMap = new HashMap<>();
    private Set<String> haveUrls = new LinkedHashSet<>();
    private Set<String> TotalUrls = new LinkedHashSet<>();
    private myImageGetter myImageGetter = null;
    private myTagHandle myTagHandle = null;
    private boolean isStart = false;
    private OnQuoteSpanClick quoteSpanClickListener;


    public void setQuoteSpanClickListener(OnQuoteSpanClick quoteSpanClickListener) {
        this.quoteSpanClickListener = quoteSpanClickListener;
    }

    public interface OnQuoteSpanClick{
        void quoteSpanClick(String res);
    }

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

        myImageGetter = new myImageGetter();
        myTagHandle = new myTagHandle();
        this.activity = activity;
        this.text = text;
        super.setText(getMyStyleHtml(text,myImageGetter,myTagHandle));
        setMovementMethod(LinkMovementMethod.getInstance());
        setLinkTextColor(0xff529ECC);
    }


    //获得textView 链接点击
    private CharSequence getMyStyleHtml(String html, Html.ImageGetter getter, Html.TagHandler handler) {
        Spanned spannedHtml = Html.fromHtml(html, getter, handler);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(spannedHtml);

        URLSpan[] urlSpans = strBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for(final URLSpan span : urlSpans) {
            replaceLinkSpans(strBuilder, span);
        }

        QuoteSpan[] quoteSpans = strBuilder.getSpans(0, spannedHtml.length(), QuoteSpan.class);
        for(final QuoteSpan span : quoteSpans) {
            replaceQuoteSpans(strBuilder,span);
        }

        return strBuilder;
    }

    private void replaceQuoteSpans(final SpannableStringBuilder strBuilder, final QuoteSpan quoteSpan){
        final int start = strBuilder.getSpanStart(quoteSpan);
        final int end = strBuilder.getSpanEnd(quoteSpan);
        int flags = strBuilder.getSpanFlags(quoteSpan);

        strBuilder.removeSpan(quoteSpan);
        strBuilder.setSpan(new CustomQuoteSpan(), start, end, flags);
        //strBuilder.setSpan(new ForegroundColorSpan(0xff888888), start, end, flags);
        strBuilder.setSpan(new RelativeSizeSpan(0.9f), start, end, flags);
        strBuilder.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(0xff888888);
            }
            @Override
            public void onClick(View view) {
                if(quoteSpanClickListener!=null){
                    quoteSpanClickListener.quoteSpanClick(strBuilder.subSequence(start,end).toString());
                }
            }
        }, start, end, flags);
    }


    private void replaceLinkSpans(final SpannableStringBuilder strBuilder, final URLSpan urlSpan) {
        int start = strBuilder.getSpanStart(urlSpan);
        int end = strBuilder.getSpanEnd(urlSpan);
        int flags = strBuilder.getSpanFlags(urlSpan);

        strBuilder.removeSpan(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                //ds.setColor(0xff529ECC);
            }

            public void onClick(View view) {
                HandleLinkClick.handleClick(activity,urlSpan.getURL());
            }
        };
        strBuilder.setSpan(clickableSpan, start, end, flags);
    }



    protected class CustomQuoteSpan implements LeadingMarginSpan, LineBackgroundSpan {
        private final int backgroundColor;
        private final int stripeColor;
        private final float stripeWidth;
        private final float gap;

        public CustomQuoteSpan() {
            this.backgroundColor =Color.argb(200,241,241,241);
            this.stripeColor = Color.argb(255,238,238,238);
            this.stripeWidth = 10;
            this.gap = 20;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            return (int) (stripeWidth + gap);
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
            Paint.Style style = p.getStyle();
            int paintColor = p.getColor();
            p.setStyle(Paint.Style.FILL);
            p.setColor(stripeColor);
            c.drawRect(x, top, x + dir * stripeWidth, bottom, p);
            p.setStyle(style);
            p.setColor(paintColor);
        }

        @Override
        public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
            int paintColor = p.getColor();
            p.setColor(backgroundColor);
            c.drawRect(left, top, right, bottom, p);
            p.setColor(paintColor);
        }

    }

    protected class myImageGetter implements Html.ImageGetter{

        @Override
        public Drawable getDrawable(String source) {
            try {
                //替换表情到本地
                if (source.contains("static/image/smiley/")) {
                    source = source.substring(source.indexOf("static"));
                    if(!source.contains("tieba")){
                        source = source.replace(".gif",".jpg").replace(".png",".jpg").replace(".GIF",".jpg").replace(".png",".jpg");
                    }
                    Drawable drawable1 = Drawable.createFromStream(activity.getAssets().open(source), null);
                    drawable1.setBounds(0, 0, 70, 70);
                    return drawable1;
                } else {
                    if(drawableMap.containsKey(source)){
                        return drawableMap.get(source);
                    }else{

                        TotalUrls.add(source);

                        if(!isStart){
                            isStart = true;
                            new LoadImage().execute(source);
                        }

                        return null;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    //下载网络图片
    protected class LoadImage extends AsyncTask<Object, Void, Drawable> {

        private String s  = "";

        @Override
        protected Drawable doInBackground(Object... params) {
            String source = (String) params[0];
            haveUrls.add(source);
            s = source;
            String mySource ;
            if(source.contains("http")){
                mySource = source;
            }else{
                if(source.charAt(0)=='/'){
                    source = source.substring(1,source.length());
                }
                mySource = PublicData.getBaseUrl()+source;
            }
            try {
                URL url = new URL(mySource);
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bm = BitmapFactory.decodeStream(bis);
                if(bm==null){
                    return null;
                }
                int mwidth = (int) (bm.getWidth()*2.3);
                int myheight = (int) (bm.getHeight()*2.3);

                Drawable drawable = new BitmapDrawable(activity.getResources(), bm);
                drawable.setBounds(0, 0,mwidth, myheight);

                return drawable;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if(drawable!=null){
                super.onPostExecute(drawable);
                drawableMap.put(s,drawable);
                setText(getMyStyleHtml(text,myImageGetter,myTagHandle));
            }

            if(haveUrls.size()<TotalUrls.size()){
                int i = haveUrls.size();
                String uurl  = (String) TotalUrls.toArray()[i];
                new LoadImage().execute(uurl);
            }
        }
    }

    protected class myTagHandle implements Html.TagHandler {

        private int startIndex = 0;
        private int stopIndex = 0;

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.equalsIgnoreCase("hr")) {
                if (opening) {
                    startIndex = output.length();
                    if (startIndex > 0 && output.charAt(output.length() - 1) != '\n') {
                        output.append("\n");
                        startIndex++;
                    }
                } else {
                    stopIndex = output.length();
                    output.append("\n");
                    stopIndex++;
                    output.append("\n");
                    stopIndex++;
                    output.setSpan(new myhrSpan(), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }else if(tag.equalsIgnoreCase("code")){
                if (opening) {
                    startIndex = output.length();
                } else {
                    stopIndex = output.length();
                    if(stopIndex>startIndex){
                        output.setSpan(new TypefaceSpan("monospace"), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //设置字体前景色
                        output.setSpan(new ForegroundColorSpan(0xff666666), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        output.setSpan(new RelativeSizeSpan(0.85f), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }


        private class myhrSpan implements LineBackgroundSpan {

            @Override
            public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int num) {
                Paint.Style pstyle = paint.getStyle();
                int ppaintColor = paint.getColor();

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0x1f000000);
                canvas.drawRect(left, bottom-3, right, bottom, paint);

                paint.setStyle(pstyle);
                paint.setColor(ppaintColor);
            }
        }
    }

}
