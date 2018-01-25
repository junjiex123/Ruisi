package me.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.model.Category;
import me.yluo.ruisiapp.model.Forum;
import me.yluo.ruisiapp.myhttp.HttpUtil;
import me.yluo.ruisiapp.myhttp.ResponseHandler;
import me.yluo.ruisiapp.utils.RuisUtils;
import me.yluo.ruisiapp.utils.UrlUtils;
import me.yluo.ruisiapp.widget.InputValidDialog;
import me.yluo.ruisiapp.widget.MyColorPicker;
import me.yluo.ruisiapp.widget.MySmileyPicker;
import me.yluo.ruisiapp.widget.MySpinner;
import me.yluo.ruisiapp.widget.emotioninput.EmotionInputHandler;

/**
 * Created by free2 on 16-3-6.
 * 发帖activity
 */
public class NewPostActivity extends BaseActivity implements View.OnClickListener, InputValidDialog.OnInputValidListener {

    private EditText edTitle, edContent;
    private MySpinner forumSpinner, typeidSpinner;
    private MyColorPicker myColorPicker;
    private MySmileyPicker smileyPicker;
    private TextView tvSelectForum, tvSelectType;

    private View typeIdContainer;
    private EmotionInputHandler handler;

    // 验证码相关
    private boolean haveValid = false;
    private String seccodehash = null;
    private String validValue = null; //验证码输入值

    //板块列表
    private List<Forum> datas = new ArrayList<>();
    //子版块列表
    private List<Forum> typeiddatas = new ArrayList<>();

    private int fid;
    private String title;
    private int typeId;


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
        forumSpinner = new MySpinner(this);
        typeidSpinner = new MySpinner(this);
        typeIdContainer = findViewById(R.id.type_id_container);
        typeIdContainer.setVisibility(View.GONE);
        tvSelectForum = findViewById(R.id.tv_select_forum);
        tvSelectType = findViewById(R.id.tv_select_type);
        tvSelectForum.setOnClickListener(this);

        tvSelectType.setOnClickListener(this);
        edTitle = findViewById(R.id.ed_title);
        edContent = findViewById(R.id.ed_content);

        List<Category> categories = RuisUtils.getForums(this, true);
        if (categories == null) {
            showLongToast("读取板块列表出错,请确保assets目录有forums.json文件");
            finish();
            return;
        }

        for (Category c : categories) {
            if (c.canPost) {
                datas.addAll(c.forums);
            }
        }

        if (TextUtils.isEmpty(title) || fid <= 0) {
            title = datas.get(0).name;
            fid = datas.get(0).fid;
        }

        tvSelectForum.setText(title);
        forumSpinner.setData(datas);
        forumSpinner.setListener((pos, v) -> {
            fid = datas.get(pos).fid;
            tvSelectForum.setText(datas.get(pos).name);
            switchFid(fid);
        });

        typeidSpinner.setListener((pos, v) -> {
            typeId = typeiddatas.get(pos).fid;
            tvSelectType.setText(typeiddatas.get(pos).name);
        });
        final LinearLayout edit_bar = findViewById(R.id.edit_bar);
        for (int i = 0; i < edit_bar.getChildCount(); i++) {
            View c = edit_bar.getChildAt(i);
            if (c instanceof ImageView) {
                c.setOnClickListener(this);
            }
        }

        Spinner setSize = findViewById(R.id.action_text_size);
        setSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //[size=7][/size]
                if (edContent == null || (edContent.getText().length() <= 0 && i == 0)) {
                    return;
                }
                handleInsert("[size=" + (i + 1) + "][/size]");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        myColorPicker.setListener((pos, v, color) -> handleInsert("[color=" + color + "][/color]"));

        handler = new EmotionInputHandler(edContent, (enable, s) -> {

        });

        smileyPicker.setListener((str, a) -> {
            handler.insertSmiley(str, a);
        });

        findViewById(R.id.action_backspace).setOnLongClickListener(v -> {
            int start = edContent.getSelectionStart();
            int end = edContent.getSelectionEnd();
            if (start == 0) {
                return false;
            }
            if ((start == end) && start > 0) {
                start = start - 5;
            }
            if (start < 0) {
                start = 0;
            }
            edContent.getText().delete(start, end);
            return true;
        });

        switchFid(fid);
        checkValid();
    }

    private boolean checkPostInput() {
        if (typeiddatas.size() > 0 && typeId <= 0) {
            Toast.makeText(this, "请选择主题分类", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(edTitle.getText().toString().trim())) {
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(edContent.getText().toString().trim())) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //显示填写验证码框子
    private void showInputValidDialog() {
        InputValidDialog dialog = InputValidDialog.newInstance(this, seccodehash, "");
        dialog.show(getFragmentManager(), "valid");
    }

    // 判断是否有验证码
    private void checkValid() {
        HttpUtil.get(App.CHECK_POST_URL, new ResponseHandler() {
            @Override
            public void onStart() {
                haveValid = false;
            }

            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                int index = res.indexOf("updateseccode");
                if (index > 0) {
                    haveValid = true;
                    int start = res.indexOf("'", index) + 1;
                    int end = res.indexOf("'", start);
                    seccodehash = res.substring(start, end);
                    Log.v("valid", "seccodehash:" + seccodehash);
                }
            }
        });
    }

    private void prePost() {
        if (checkPostInput()) {
            if (haveValid && TextUtils.isEmpty(validValue)) {
                showInputValidDialog();
                return;
            }
            dialog.setMessage("发贴中,请稍后......");
            dialog.show();
            beginPost();
        }
    }

    //开始发帖
    private void beginPost() {
        String url = UrlUtils.getPostUrl(fid);
        Map<String, String> params = new HashMap<>();
        params.put("topicsubmit", "yes");
        if (typeId > 0) {
            params.put("typeid", String.valueOf(typeId));
        }
        params.put("subject", edTitle.getText().toString());
        params.put("message", edContent.getText().toString());

        if (haveValid) { //是否有验证码
            params.put("seccodehash", seccodehash);
            params.put("seccodeverify", validValue);
        }

        HttpUtil.post(url, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                //Log.e("==========", res);
                if (res.contains("class=\"jump_c\"")) {
                    int start = res.indexOf("<p>", res.indexOf("class=\"jump_c\"")) + 3;
                    int end = res.indexOf("</p>", start);
                    String reason = res.substring(start, end);
                    if ("抱歉，验证码填写错误".equals(reason)) {
                        showInputValidDialog();
                        reason = "抱歉，验证码填写错误";
                    }
                    postFail(reason);
                } else {
                    postSuccess();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                postFail("发帖失败:" + e.getMessage());
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

    //发帖失败执行
    private void postFail(String str) {
        dialog.dismiss();
        Toast.makeText(this, "发帖失败:" + str, Toast.LENGTH_SHORT).show();
    }

    private void handleInsert(String s) {
        int start = edContent.getSelectionStart();
        Editable edit = edContent.getEditableText();//获取EditText的文字
        if (start < 0 || start >= edit.length()) {
            edit.append(s);
        } else {
            edit.insert(start, s);//光标所在位置插入文字
        }
        //[size=7][/size]
        int a = s.indexOf("[/");
        if (a > 0) {
            edContent.setSelection(start + a);
        }
    }

    private ProgressDialog dialog;

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.menu:
                prePost();
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
                int start = edContent.getSelectionStart();
                int end = edContent.getSelectionEnd();
                if (start == 0) {
                    return;
                }
                if ((start == end) && start > 0) {
                    start = start - 1;
                }
                edContent.getText().delete(start, end);
                break;
            case R.id.tv_select_forum:
                forumSpinner.setWidth(view.getWidth());
                //MySpinner.setWidth(mTView.getWidth());
                forumSpinner.showAsDropDown(view, 0, 15);
                break;
            case R.id.tv_select_type:
                typeidSpinner.setData(typeiddatas);
                typeidSpinner.setWidth(view.getWidth());
                typeidSpinner.showAsDropDown(view, 0, 15);
        }

    }

    private void switchFid(int fid) {
        typeiddatas.clear();
        typeId = 0;
        String url = "forum.php?mod=post&action=newthread&fid=" + fid + "&mobile=2";
        HttpUtil.get(url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements types = document.select("#typeid").select("option");
                for (Element e : types) {
                    typeiddatas.add(new Forum(Integer.parseInt(e.attr("value")), e.text()));
                }

                if (typeiddatas.size() > 0) {
                    typeIdContainer.setVisibility(View.VISIBLE);
                    tvSelectType.setText(typeiddatas.get(0).name);
                    typeId = typeiddatas.get(0).fid;
                } else {
                    typeIdContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onInputFinish(boolean click, String hash, String value) {
        // 输入验证码
        seccodehash = hash;
        validValue = value;
        if (click) { //提交
            prePost();
        }
    }
}
