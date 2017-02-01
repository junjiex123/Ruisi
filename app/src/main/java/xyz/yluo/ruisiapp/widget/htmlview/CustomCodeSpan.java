package xyz.yluo.ruisiapp.widget.htmlview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-2.
 * 代码Span
 */
public class CustomCodeSpan extends ReplacementSpan {

    private static final float radius = 10;

    private Drawable drawable;
    private float padding;
    private int width;

    public CustomCodeSpan(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        drawable = d;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        padding = paint.measureText("t");
        width = (int) (paint.measureText(text, start, end) + padding * 2);
        return width;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) x, top, (int) x + width, bottom);
        drawable.draw(canvas);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextScaleX(0.9f);
        paint.setColor(0xff666666);
        canvas.drawText(text, start, end, x + padding, y, paint);
    }

}
