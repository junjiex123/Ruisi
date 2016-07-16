package xyz.yluo.ruisiapp.View.MyHtmlView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.text.Layout;
import android.text.TextUtils;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;

/**
 * Created by free2 on 16-7-16.
 * <blockquate> span
 */

public class CustomQuoteSpan implements LeadingMarginSpan, LineBackgroundSpan {
    private final int backgroundColor;
    private final int stripeColor;
    private final float stripeWidth;
    private final float gap;

    CustomQuoteSpan() {
        this.backgroundColor = Color.argb(200, 241, 241, 241);
        this.stripeColor = Color.argb(255, 238, 238, 238);
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

    public interface OnQuoteSpanClick {
        void quoteSpanClick(String res);
    }
}
