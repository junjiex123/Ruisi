package xyz.yluo.ruisiapp.utils;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by free2 on 16-3-20.
 * 表情处理
 * 格式处理 加粗 斜体等。。。
 */
public class PostHandler implements TextWatcher {

    private final EditText mEditor;
    private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<>();


    public PostHandler(EditText editor) {
        mEditor = editor;
        mEditor.addTextChangedListener(this);
    }

    public void insertSmiley(String emoticon, Drawable drawable) {
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        // Get the selected text.
        int start = mEditor.getSelectionStart();
        int end = mEditor.getSelectionEnd();
        Editable message = mEditor.getEditableText();

        // Insert the emoticon.
        message.replace(start, end, emoticon);
        message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void insertbold(String text) {
        Editable message = mEditor.getEditableText();
        message.replace(mEditor.getSelectionStart(), mEditor.getSelectionEnd(),
                Html.fromHtml("<b>" + text + "</b>"));
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        if (count > 0) {
            int end = start + count;
            Editable message = mEditor.getEditableText();
            ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);
            for (ImageSpan span : list) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                if ((spanStart < end) && (spanEnd > start)) {
                    // Add to remove list
                    mEmoticonsToRemove.add(span);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable text) {
        Editable message = mEditor.getEditableText();
        for (ImageSpan span : mEmoticonsToRemove) {
            int start = message.getSpanStart(span);
            int end = message.getSpanEnd(span);
            message.removeSpan(span);
            if (start != end) {
                message.delete(start, end);
            }
        }
        mEmoticonsToRemove.clear();
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
    }

}