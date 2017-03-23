package xyz.yluo.ruisiapp.widget.htmlview;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;

import org.xml.sax.XMLReader;

/**
 * Created by free2 on 16-7-16.
 * // TODO: 2017/2/1 自己实现Taghandler 不使用系统自带的
 */
public class HtmlTagHandler implements Html.TagHandler {

    private static final int code_color = Color.parseColor("#F0F0F0");
    private static final int h1_color = Color.parseColor("#333333");

    private int statrt;

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (opening) {
            statrt = output.length();
            handleStartTag(tag.toLowerCase(), output);
        } else {
            handleEndTag(statrt, tag.toLowerCase(), output);
        }
    }

    private void handleStartTag(String tag, Editable output) {
        if (tag.equals("hr")) {
            if (output.charAt(output.length() - 1) != '\n') {
                output.append("\n");
                statrt++;
            }
        }
    }

    private void handleEndTag(int start, String tag, Editable output) {
        int end = output.length();
        switch (tag) {
            case "hr":
                if (output.charAt(end - 1) != '\n') {
                    output.append("\n");
                    end++;
                }
                output.setSpan(new CustomHrSpan(), statrt, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case "code":

                break;
        }

        if (tag.equals("hr")) {
            output.setSpan(new CustomCodeSpan(code_color), statrt, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }


    /*
    private static void start(Editable text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private static void end(Editable text, Class kind, Object repl) {
        int len = text.length();
        Object obj = getLast(text, kind);
        if (obj != null) {
            setSpanFromMark(text, obj, repl);
        }
    }

    private static <T> T getLast(Spanned text, Class<T> kind) {
        T[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            return objs[objs.length - 1];
        }
    }

    private static void setSpanFromMark(Spannable text, Object mark, Object... spans) {
        int where = text.getSpanStart(mark);
        text.removeSpan(mark);
        int len = text.length();
        if (where != len) {
            for (Object span : spans) {
                text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
    */

}
