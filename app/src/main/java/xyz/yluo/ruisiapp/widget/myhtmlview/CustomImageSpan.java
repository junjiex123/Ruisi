package xyz.yluo.ruisiapp.widget.myhtmlview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by free2 on 16-7-16.
 * 自定义imageSpan
 */
public class CustomImageSpan extends ImageSpan {

    public CustomImageSpan(Drawable drawable, String src) {
        super(drawable, src, ALIGN_BASELINE);

    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int l, int r,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        //竖直居中
        int transY = 0;

        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;

        //获得图片宽度
        int width = drawable.getIntrinsicWidth();
        if (width < 60) {//一般的表情为17
            canvas.translate(x + 8, transY);
        } else {
            canvas.translate(x + 32, transY);
        }
        drawable.draw(canvas);
        canvas.restore();
    }
}
