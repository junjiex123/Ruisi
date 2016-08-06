package xyz.yluo.ruisiapp.View.MyHtmlView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;

import org.xml.sax.XMLReader;

/**
 * Created by free2 on 16-7-16.
 * taghandle
 */
public class TagHandle implements Html.TagHandler{

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
        } else if (tag.equalsIgnoreCase("code")) {
            if (opening) {
                startIndex = output.length();
            } else {
                stopIndex = output.length();
                if (stopIndex > startIndex) {
                    output.setSpan(new TypefaceSpan("monospace"), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //设置字体前景色
                    output.setSpan(new ForegroundColorSpan(0xff666666), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    output.setSpan(new RelativeSizeSpan(0.80f), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            canvas.drawRect(left, bottom - 3, right, bottom, paint);

            paint.setStyle(pstyle);
            paint.setColor(ppaintColor);
        }
    }
}
