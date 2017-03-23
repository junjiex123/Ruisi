package xyz.yluo.ruisiapp.widget.htmlview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;


class CustomHrSpan implements LineBackgroundSpan {

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int num) {
        Paint.Style pstyle = paint.getStyle();
        int ppaintColor = paint.getColor();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x1f000000);
        canvas.drawRect(left, bottom - 3, right, bottom, paint);

        paint.setStyle(pstyle);
        paint.setColor(ppaintColor);
    }
}
