package xyz.yluo.ruisiapp.View.MyHtmlView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.style.QuoteSpan;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-7-16.
 * <blockquate> span
 */

//CharacterStyle

public class CustomQuoteSpan extends QuoteSpan{
    private final int borderColor;
    private final float stripeWidth;
    private final float gap;

    CustomQuoteSpan(Context context) {
        this.borderColor = ContextCompat.
                getColor(context, R.color.colorDivider);
        this.stripeWidth = 6;
        this.gap = 16;
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
        p.setColor(borderColor);
        c.drawRect(x, top, x + dir * stripeWidth, bottom, p);
        p.setStyle(style);
        p.setColor(paintColor);
    }
}
