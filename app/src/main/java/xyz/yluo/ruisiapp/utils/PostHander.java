package xyz.yluo.ruisiapp.utils;

import android.content.Context;
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
public  class PostHander implements TextWatcher {

    private final EditText mEditor;
    private final Context context;
    private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<ImageSpan>();

    public PostHander(Context context, EditText editor) {
        // Attach the handler to listen for text changes.
        mEditor = editor;
        this.context = context;
        mEditor.addTextChangedListener(this);
    }

    public void insertSmiley(String emoticon, Drawable drawable) {
        // Create the ImageSpan

        //Drawable drawable = ContextCompat.getDrawable(context, resource);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);

        // Get the selected text.
        int start = mEditor.getSelectionStart();
        int end = mEditor.getSelectionEnd();
        Editable message = mEditor.getEditableText();

        // Insert the emoticon.
        message.replace(start, end, emoticon);
        message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void insertbold(String text){
        Editable message = mEditor.getEditableText();
        message.replace(mEditor.getSelectionStart(),mEditor.getSelectionEnd(), Html.fromHtml("<b>" + text + "</b>"));
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        // Check if some text will be removed.
        if (count > 0) {
            int end = start + count;
            Editable message = mEditor.getEditableText();
            ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

            for (ImageSpan span : list) {
                // Get only the emoticons that are inside of the changed
                // region.
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

        // Commit the emoticons to be removed.
        for (ImageSpan span : mEmoticonsToRemove) {
            int start = message.getSpanStart(span);
            int end = message.getSpanEnd(span);

            // Remove the span
            message.removeSpan(span);

            // Remove the remaining emoticon text.
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