package xyz.yluo.ruisiapp.View.MyHtmlView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.style.LineBackgroundSpan;
import android.text.style.QuoteSpan;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-7-16.
 * <blockquate> span
 */

//CharacterStyle

public class CustomQuoteSpan extends QuoteSpan implements LineBackgroundSpan{
    private final int backgroundColor;
    private final int stripeColor;
    private final float stripeWidth;
    private final float gap;

    CustomQuoteSpan(Context context) {
        this.backgroundColor = ContextCompat.
                getColor(context, R.color.bg_secondary);
        this.stripeColor = ContextCompat.
                getColor(context, R.color.colorDivider);
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
