package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.UrlUtils;
import xyz.yluo.ruisiapp.widget.MyColorPicker;
import xyz.yluo.ruisiapp.widget.MySmileyPicker;
import xyz.yluo.ruisiapp.widget.MySpinner;
import xyz.yluo.ruisiapp.widget.emotioninput.EmotionInputHandler;

/**
 * Created by free2 on 16-3-6.
 * 发帖activity
 */
public class NewPostActivity extends BaseActivity implements View.OnClickListener {

    private EditText ed_title, ed_content;
    private MySpinner forum_spinner, typeid_spinner;
    private MyColorPicker myColorPicker;
    private MySmileyPicker smileyPicker;
    private TextView tv_select_forum, tv_select_type;
    private List<Pair<String, String>> typeiddatas;
    private View type_id_container;
    private EmotionInputHandler handler;
    private String typeId = "";

    private static final int[] fids = new int[]{
            72, 549, 108, 551, 550,
            110, 217, 142, 552, 560,
            554, 548, 216, 91, 555,
            145, 144, 152, 147, 215,
            125, 140, 563};
    private static final String[] forums = new String[]{
            "灌水专区", "文章天地", "我是女生", "西电问答", "心灵花园",
            "普通交易", "缘聚睿思", "失物招领", "我要毕业啦", "技术博客",
            "就业信息发布", "学习交流", "我爱运动", "考研交流", "就业交流", "软件交流",
            "嵌入式交流", "竞赛交流", "原创精品", "西电后街", "音乐纵贯线",
            "绝对漫域", "邀请专区"};
    private int fid = fids[0];
    private String title = forums[0];


    public static void open(Context context, int fid, String title) {
        Intent intent = new Intent(context, NewPostActivity.class);
        intent.putExtra("FID", fid);
        intent.putExtra("TITLE", title);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        initToolBar(true, "发表新帖");
        dialog = new ProgressDialog(this);
        if (getIntent().getExtras() != null) {
            fid = getIntent().getExtras().getInt("FID");
            title = getIntent().getExtras().getString("TITLE");
        }
        addToolbarMenu(R.drawable.ic_send_white_24dp).setOnClickListener(this);
        myColorPicker = new MyColorPicker(this);
        smileyPicker = new MySmileyPicker(this);
        forum_spinner = new MySpinner(this);
        typeid_spinner = new MySpinner(this);
        typeiddatas = new ArrayList<>();
        type_id_container = findViewById(R.id.type_id_container);
        type_id_container.setVisibility(View.GONE);
        tv_select_forum = (TextView) findViewById(R.id.tv_select_forum);
        tv_select_type = (TextView) findViewById(R.id.tv_select_type);
        tv_select_forum.setOnClickListener(this);
        tv_select_forum.setText(title);
        tv_select_type.setOnClickListener(this);
        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_content = (EditText) findViewById(R.id.ed_content);

        forum_spinner.setData(forums);
        forum_spinner.setListener((pos, v) -> {
            fid = fids[pos];
            tv_select_forum.setText(forums[pos]);
            switch_fid(fid);
        });
        typeid_spinner.setListener((pos, v) -> {
            typeId = typeiddatas.get(pos).first;
            tv_select_type.setText(typeiddatas.get(pos).second);
        });
        final LinearLayout edit_bar = (LinearLayout) findViewById(R.id.edit_bar);
        for (int i = 0; i < edit_bar.getChildCount(); i++) {
            View c = edit_bar.getChildAt(i);
            if (c instanceof ImageView) {
                c.setOnClickListener(this);
            }
        }

        Spinner setSize = (Spinner) findViewById(R.id.action_text_size);
        setSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //[size=7][/size]
                if (ed_content == null || (ed_content.getText().length() <= 0 && i == 0)) {
                    return;
                }
                handleInsert("[size=" + (i + 1) + "][/size]");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        myColorPicker.setListener((pos, v, color) -> handleInsert("[color=" + color + "][/color]"));

        handler = new EmotionInputHandler(ed_content, (enable, s) -> {

        });

        smileyPicker.setListener((str, a) -> {
            handler.insertSmiley(str, a);
        });

        findViewById(R.id.action_backspace).setOnLongClickListener(v -> {
            int start = ed_content.getSelectionStart();
            int end = ed_content.getSelectionEnd();
            if (start == 0) {
                return false;
            }
            if ((start == end) && start > 0) {
                start = start - 5;
            }
            if (start < 0) {
                start = 0;
            }
            ed_content.getText().delete(start, end);
            return true;
        });

        switch_fid(fid);
    }

    private boolean checkPostInput() {

        if (!TextUtils.isEmpty(typeId) && typeId.equals("0")) {
            Toast.makeText(this, "请选择主题分类", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(ed_title.getText().toString().trim())) {
            Toast.makeText(this, "标题不能为空啊", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(ed_content.getText().toString().trim())) {
            Toast.makeText(this, "内容不能为空啊", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //开始发帖
    private void begainPost() {
        String url = UrlUtils.getPostUrl(fid);
        Map<String, String> params = new HashMap<>();
        params.put("topicsubmit", "yes");
        if (!TextUtils.isEmpty(typeId) && !typeId.equals("0")) {
            params.put("typeid", typeId);
        }
        params.put("subject", ed_title.getText().toString());
        params.put("message", ed_content.getText().toString());
        HttpUtil.post(getApplicationContext(), url, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                Log.e("post", res);
                if (res.contains("已经被系统拒绝")) {
                    postFail("由于未知原因发帖失败");
                } else {
                    postSuccess();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                postFail("由于未知原因发帖失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

    //发帖成功执行
    private void postSuccess() {
        dialog.dismiss();
        Toast.makeText(this, "主题发表成功", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.putExtra("status", "ok");
        //设置返回数据
        dialog.dismiss();
        NewPostActivity.this.setResult(RESULT_OK, intent);
        finish();

    }

    //
    //发帖失败执行
    private void postFail(String str) {
        dialog.dismiss();
        Toast.makeText(this, "发帖失败", Toast.LENGTH_SHORT).show();
    }

    private void handleInsert(String s) {
        int start = ed_content.getSelectionStart();
        Editable edit = ed_content.getEditableText();//获取EditText的文字
        if (start < 0 || start >= edit.length()) {
            edit.append(s);
        } else {
            edit.insert(start, s);//光标所在位置插入文字
        }
        //[size=7][/size]
        int a = s.indexOf("[/");
        if (a > 0) {
            ed_content.setSelection(start + a);
        }
    }


    private ProgressDialog dialog;

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.menu:
                if (checkPostInput()) {
                    dialog.setMessage("发贴中,请稍后......");
                    dialog.show();
                    begainPost();
                }
                break;
            case R.id.action_bold:
                handleInsert("[b][/b]");
                break;
            case R.id.action_italic:
                handleInsert("[i][/i]");
                break;
            case R.id.action_quote:
                handleInsert("[quote][/quote]");
                break;
            case R.id.action_color_text:
                myColorPicker.showAsDropDown(view, 0, 10);
                break;
            case R.id.action_emotion:
                ((ImageView) view).setImageResource(R.drawable.ic_edit_emoticon_accent_24dp);
                smileyPicker.showAsDropDown(view, 0, 10);
                smileyPicker.setOnDismissListener(() -> ((ImageView) view).setImageResource(R.drawable.ic_edit_emoticon_24dp));
                break;
            case R.id.action_backspace:
                int start = ed_content.getSelectionStart();
                int end = ed_content.getSelectionEnd();
                if (start == 0) {
                    return;
                }
                if ((start == end) && start > 0) {
                    start = start - 1;
                }
                ed_content.getText().delete(start, end);
                break;
            case R.id.tv_select_forum:
                forum_spinner.setWidth(view.getWidth());
                //MySpinner.setWidth(mTView.getWidth());
                forum_spinner.showAsDropDown(view, 0, 15);
                break;
            case R.id.tv_select_type:
                String[] names = new String[typeiddatas.size()];
                for (int i = 0; i < typeiddatas.size(); i++) {
                    names[i] = typeiddatas.get(i).second;
                }
                typeid_spinner.setData(names);
                typeid_spinner.setWidth(view.getWidth());
                typeid_spinner.showAsDropDown(view, 0, 15);
        }

    }

    private void switch_fid(int fid) {
        typeiddatas.clear();
        typeId = "";
        String url = "forum.php?mod=post&action=newthread&fid=" + fid + "&mobile=2";
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements types = document.select("#typeid").select("option");
                for (Element e : types) {
                    typeiddatas.add(new Pair<>(e.attr("value"), e.text()));
                }
                if (typeiddatas.size() > 0) {
                    type_id_container.setVisibility(View.VISIBLE);
                    tv_select_type.setText(typeiddatas.get(0).second);
                    typeId = typeiddatas.get(0).first;
                } else {
                    type_id_container.setVisibility(View.GONE);
                }
            }
        });
    }

}
