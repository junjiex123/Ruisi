package xyz.yluo.ruisiapp.View.MyHtmlView;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import xyz.yluo.ruisiapp.utils.HandleLinkClick;

/**
 * Created by free2 on 16-3-31.
 * 能够显示图片的textview
 * 显示html
 */
public class MyHtmlTextView extends TextView {

    private Context context;
    private MyImageGetter myImageGetter;
    private MyTagHandle myTagHandle;
    private CharSequence charSequence;



    public MyHtmlTextView(Context context) {
        super(context);
        init(context);
    }

    public MyHtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyHtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i("MyHtmlTextView","onDetachedFromWindow");
    }


    private void  init(Context context){
        this.context = context;
        setMovementMethod(LinkMovementMethod.getInstance());
        setLinkTextColor(0xff529ECC);
        myTagHandle = new MyTagHandle();
    }

    public void setHtmlText(String txt, MyImageGetter.ImageDownLoadListener listener){
        if(listener!=null&&myImageGetter==null){
            myImageGetter = new MyImageGetter(context,listener);
        }

        charSequence =  getSequence(context,txt, myImageGetter, myTagHandle);
        setText(charSequence);
    }


    //获得textView 链接点击
    private  CharSequence getSequence(Context context, String html, MyImageGetter getter, MyTagHandle handler) {
        Spanned spannedHtml = null;
        spannedHtml = Html.fromHtml(html, getter, handler);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(spannedHtml);

        URLSpan[] urlSpans = strBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for (final URLSpan span : urlSpans) {
            replaceLinkSpans(context,strBuilder, span);
        }

        QuoteSpan[] quoteSpans = strBuilder.getSpans(0, spannedHtml.length(), QuoteSpan.class);
        for (final QuoteSpan span : quoteSpans) {
            replaceQuoteSpans(strBuilder, span);
        }

        /**
         * 去掉尾部回车
         */
        while (strBuilder.length()>0&&strBuilder.charAt(strBuilder.length()-1)=='\n'){
            strBuilder = strBuilder.delete(strBuilder.length()-1,strBuilder.length());
        }

        /**
         * 去除头部
         */
        while (strBuilder.length()>0&&strBuilder.charAt(0)=='\n'){
            strBuilder = strBuilder.delete(0,1);
        }

        /**
         * 去除中间
         */
        int lenth = strBuilder.length();
        for(int i=0;i<lenth;i++){
            if(strBuilder.charAt(i)=='\n') {
                if(strBuilder.charAt(i+1)=='\n'){
                    strBuilder = strBuilder.delete(i,i+1);
                    i--;
                    lenth--;
                }
            }

        }
        return strBuilder;
    }

    private  void replaceQuoteSpans(final SpannableStringBuilder strBuilder, final QuoteSpan quoteSpan) {
        final int start = strBuilder.getSpanStart(quoteSpan);
        final int end = strBuilder.getSpanEnd(quoteSpan);
        int flags = strBuilder.getSpanFlags(quoteSpan);

        strBuilder.removeSpan(quoteSpan);
        strBuilder.setSpan(new CustomQuoteSpan(), start, end, flags);
        strBuilder.setSpan(new RelativeSizeSpan(0.9f), start, end, flags);
    }

    //连接点击事件
    private  void replaceLinkSpans(final Context context, final SpannableStringBuilder strBuilder, final URLSpan urlSpan) {
        int start = strBuilder.getSpanStart(urlSpan);
        int end = strBuilder.getSpanEnd(urlSpan);
        int flags = strBuilder.getSpanFlags(urlSpan);

        strBuilder.removeSpan(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //取消连接下划线
                ds.setUnderlineText(false);
                //ds.setColor(0xff529ECC);
            }

            public void onClick(View view) {
                HandleLinkClick.handleClick(context, urlSpan.getURL());
            }
        };
        strBuilder.setSpan(clickableSpan, start, end, flags);
    }


}
