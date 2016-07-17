package xyz.yluo.ruisiapp.View.MyHtmlView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.HandleLinkClick;

/**
 * Created by free2 on 16-3-31.
 * 能够显示图片的textview
 * 显示html
 */
public class MyHtmlTextView extends TextView implements MyImageGetter.ImageDownLoadListener {

    private Context context;
    private MyImageGetter myImageGetter;
    private MyTagHandle myTagHandle;
    private SpannableStringBuilder strBuilderContent;


    public SpannableStringBuilder getStrBuilderContent() {
        return strBuilderContent;
    }

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
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(myImageGetter!=null){
            myImageGetter.reStart();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(myImageGetter!=null){
            myImageGetter.setNeedStop(true);
        }
    }


    private void  init(Context context){
        this.context = context;
        setMovementMethod(LinkMovementMethod.getInstance());
        setLinkTextColor(0xff529ECC);
        myTagHandle = new MyTagHandle();
    }

    public void setHtmlText(String txt, boolean isLoadImage){
        if(isLoadImage&&myImageGetter==null){
            myImageGetter = new MyImageGetter(context,this);
        }
        Log.i("my htmltext","=====new strBuilderContent=====");
        strBuilderContent =  getSequence(context,txt, myImageGetter, myTagHandle);
        setText(strBuilderContent);
    }

    public void setSpannedHtmlText(SpannableStringBuilder t){
        this.strBuilderContent = t;
        setText(t);
    }

    private void upDateTextImage(){
        ImageSpan[] spans = strBuilderContent.getSpans(0, strBuilderContent.length(), ImageSpan.class);
        for(ImageSpan span:spans){
            Log.i("myhtml text","replace ImageSpan");
            replaceImageSpans(strBuilderContent,span);
        }
        setText(strBuilderContent);
    }


    //获得textView 链接点击
    private  SpannableStringBuilder getSequence(Context context, String html, MyImageGetter getter, MyTagHandle handler) {
        Spanned spannedHtml = null;
        spannedHtml = Html.fromHtml(html, getter, handler);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(spannedHtml);

        Object[] obj = strBuilder.getSpans(0, strBuilder.length(), ParcelableSpan.class);

        for (Object oo : obj) {
            if (oo instanceof URLSpan) {
                Log.i("myhtml text","replace urlspan");
                replaceLinkSpans(context, strBuilder, (URLSpan) oo);
            } else if (oo instanceof QuoteSpan) {
                Log.i("myhtml text","replace QuoteSpan");
                replaceQuoteSpans(strBuilder, (QuoteSpan) oo);
            }
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


    private  void replaceImageSpans(final SpannableStringBuilder strBuilder, final ImageSpan imageSpan) {
        final int start = strBuilder.getSpanStart(imageSpan);
        final int end = strBuilder.getSpanEnd(imageSpan);
        int flags = strBuilder.getSpanFlags(imageSpan);
        strBuilder.removeSpan(imageSpan);

        String src = imageSpan.getSource();
        Drawable d = null;
        if (myImageGetter != null) {
            d = myImageGetter.getDrawable(src);
        }

        if (d == null) {
            d = context.getDrawable(R.drawable.image_placeholder);
            if(d!=null)
            d.setBounds(0, 0, 80, 80);
        }
        strBuilder.setSpan(new CustomImageSpan(d, src), start, end, flags);
    }

    private  void replaceQuoteSpans(final SpannableStringBuilder strBuilder, final QuoteSpan quoteSpan) {
        final int start = strBuilder.getSpanStart(quoteSpan);
        final int end = strBuilder.getSpanEnd(quoteSpan);
        final int flags = strBuilder.getSpanFlags(quoteSpan);
        strBuilder.removeSpan(quoteSpan);


        TextAppearanceSpan span = new TextAppearanceSpan(context,3){
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(0xff888888);
                float size = ds.getTextSize();
                ds.setTextSize((float)(0.9*size));
            }
        };
        strBuilder.setSpan(span,start,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        strBuilder.setSpan(new CustomQuoteSpan(), start, end, flags);
        //strBuilder.setSpan(new RelativeSizeSpan(0.9f), start, end, flags);
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


    /**
     * 图片加载完成回掉
     * @param url
     * @param d
     */
    @Override
    public void downloadCallBack(String url, Drawable d) {
        upDateTextImage();
    }
}
