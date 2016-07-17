package xyz.yluo.ruisiapp.View.MyHtmlView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by free2 on 16-7-16.
 * 自定义imageSpan
 */
public class CustomImageSpan extends ImageSpan{

    public CustomImageSpan(Drawable drawable,String src) {
        super(drawable,src);

    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int l, int r,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();

        //竖直居中
        int transY = 0;
        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;

        //水平居中
        int transx = ((r-l) -drawable.getBounds().right)/2  +l;

        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}
