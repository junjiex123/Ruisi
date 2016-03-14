package xyz.yluo.ruisiapp.article;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);

        ButterKnife.bind(this);

        Toolbar toolbar= (Toolbar) findViewById(R.id.control_toolbar);
        //把toolbar当作actionbar处理
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        //mEditor.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setAlignLeft();
        //    mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...");

        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

    }

    @OnClick(R.id.action_bold)
    protected  void OnBtn1Click() {
        mEditor.setBold();
    }

    @OnClick(R.id.action_italic)
    protected  void OnBtn2Click() {
        mEditor.setItalic();
    }

    @OnClick(R.id.action_color_text)
    protected  void OnBtn3Click() {
        mEditor.setBlockquote();
    }




    /**
     * 加粗
     */
//    @OnClick(R.id.editor_bar_1)
//    protected void onBtnFormatBoldClick() {
//        String text = edtContent.getText().toString();
//        edtContent.setText(Html.fromHtml("<b>"+text+"</b>"+""));
//
//    }
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
}
