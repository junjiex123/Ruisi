package xyz.yluo.ruisiapp.article;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.richeditor.RichEditor;
import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-6.
 *
 */
public class NewArticleActivity extends AppCompatActivity {


    @Bind(R.id.editor)
    protected RichEditor mEditor;

    @Bind(R.id.preview)
    protected TextView mPreview;

    @Bind(R.id.edit_input)
    protected EditText edit_input;

    EmoticonHandler mEmoticonHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);

        ButterKnife.bind(this);

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);
        //mEditor.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setAlignLeft();
        //    mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...");



        Drawable smiley_1 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.tb001);
        smiley_1.setBounds(0, 0, smiley_1.getIntrinsicWidth(), smiley_1.getIntrinsicHeight());

        Drawable smiley_2 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.tb002);
        smiley_1.setBounds(0, 0, 30, 30);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Some text [happy_smiley_anchor]");
        builder.setSpan(new ImageSpan(smiley_1), builder.length() - "[happy_smiley_anchor]".length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(". Some more text [sad_smiley_anchor]");
        builder.setSpan(new ImageSpan(smiley_2), builder.length() - "[sad_smiley_anchor]".length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        edit_input.setText(builder);

        mEmoticonHandler = new EmoticonHandler(edit_input);



        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

    }

    @OnClick(R.id.action_bold)
    protected  void OnBtn1Click() {
        mEmoticonHandler.insertbold("这是一个测试加粗");
    }

    @OnClick(R.id.action_italic)
    protected  void OnBtn2Click() {
        mEditor.setItalic();

        edit_input.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p><b>加粗</b>"));
    }

    @OnClick(R.id.action_color_text)
    protected  void OnBtn3Click() {
        mEmoticonHandler.insertSmiley("hahah", R.drawable.tb004);
    }




//    @OnClick(R.id.editor_bar_2)
//    protected  void onbtnClick(){
//        Drawable drawable = getResources().getDrawable(R.drawable.image_placeholder);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        //需要处理的文本，[smile]是需要被替代的文本
//
//        SpannableString spannable = new SpannableString(edtContent.getText().toString());
//        //要让图片替代指定的文字就要用ImageSpan
//
//        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
//        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
//        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
//        spannable.setSpan(span, edtContent.getText().length()-1,edtContent.getText().length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        edtContent.setText(spannable);
//
//        edtContent.append("\n");
//
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //返回按钮
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  class EmoticonHandler implements TextWatcher {

        private final EditText mEditor;
        private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<ImageSpan>();

        public EmoticonHandler(EditText editor) {
            // Attach the handler to listen for text changes.
            mEditor = editor;
            mEditor.addTextChangedListener(this);
        }

        public void insertSmiley(String emoticon, int resource) {
            // Create the ImageSpan

            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), resource);
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
            message.replace(mEditor.getSelectionStart(),mEditor.getSelectionEnd(),Html.fromHtml("<b>"+text+"</b>"));
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
}
