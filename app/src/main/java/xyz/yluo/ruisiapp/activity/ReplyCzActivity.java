package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.model.SingleArticleData;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.KeyboardUtil;
import xyz.yluo.ruisiapp.utils.UrlUtils;
import xyz.yluo.ruisiapp.view.emotioninput.PanelViewRoot;
import xyz.yluo.ruisiapp.view.emotioninput.SmileyInputRoot;
import xyz.yluo.ruisiapp.view.myhtmlview.HtmlView;

public class ReplyCzActivity extends BaseActivity {

    private PanelViewRoot mPanelRoot;
    private EditText input;
    Map<String, String> params = new HashMap<>();
    private String postUrl = "";
    private ProgressDialog dialog;
    SingleArticleData data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_cz);

        boolean isLz = false;
        String title = getString(R.string.app_name);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            data = b.getParcelable("data");
            isLz = b.getBoolean("islz", false);
            title = "回复" + data.getIndex() + ": " + data.getUsername();
        }
        initToolBar(true, title);
        input = (EditText) findViewById(R.id.ed_comment);
        findViewById(R.id.tv_edit).setVisibility(View.GONE);
        findViewById(R.id.tv_remove).setVisibility(View.GONE);
        findViewById(R.id.bt_lable_lz).setVisibility(isLz ? View.VISIBLE : View.GONE);

        SmileyInputRoot rootViewGroup = (SmileyInputRoot) findViewById(R.id.root);
        mPanelRoot = rootViewGroup.getmPanelLayout();

        View btnSend = findViewById(R.id.btn_send);
        View smuleyBtn = findViewById(R.id.btn_emotion);
        KeyboardUtil.attach(this, mPanelRoot, isShowing -> Log.e("key board", String.valueOf(isShowing)));
        mPanelRoot.init(input, smuleyBtn, btnSend);

        findViewById(R.id.content).setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mPanelRoot.hidePanelAndKeyboard();
            }
            return false;
        });

        findViewById(R.id.btn_reply_cz).setOnClickListener(view -> {
            if (!mPanelRoot.isKeyboardShowing()) {
                KeyboardUtil.showKeyboard(input);
            }
        });

        if (data == null) {
            showToast("加载失败......");
            return;
        } else {
            ImageView imageView = (ImageView) findViewById(R.id.article_user_image);
            String img_url = UrlUtils.getAvaterurlm(data.getImg());
            Picasso.with(this).load(img_url).placeholder(R.drawable.image_placeholder).into(imageView);
            ((TextView) findViewById(R.id.replay_author)).setText(data.getUsername());
            ((TextView) findViewById(R.id.replay_index)).setText(data.getIndex());
            ((TextView) findViewById(R.id.replay_time)).setText(data.getPostTime());

            ((HtmlView) findViewById(R.id.html_text)).setHtmlText(data.getCotent(), true);
            input.setHint("回复: " + data.getUsername());

            imageView.setOnClickListener(v -> UserDetailActivity.openWithAnimation(
                    this, data.getUsername(),
                    imageView, data.getUid()));

        }

        HttpUtil.get(this, data.getReplyUrlTitle(), new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements els = document.select("#postform");
                params.put("formhash", els.select("input[name=formhash]").attr("value"));
                params.put("posttime", els.select("input[name=posttime]").attr("value"));
                params.put("noticeauthor", els.select("input[name=noticeauthor]").attr("value"));
                params.put("noticetrimstr", els.select("input[name=noticetrimstr]").attr("value"));
                params.put("reppid", els.select("input[name=reppid]").attr("value"));
                params.put("reppost", els.select("input[name=reppost]").attr("value"));
                params.put("noticeauthormsg", els.select("input[name=noticeauthormsg]").attr("value"));
                params.put("replysubmit", "yes");
                postUrl = els.attr("action");
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
            }
        });

        btnSend.setOnClickListener(view -> {
            if (isLogin() && !TextUtils.isEmpty(input.getText())) {
                dialog = new ProgressDialog(ReplyCzActivity.this);
                dialog.setTitle("回复中");
                dialog.setMessage("请稍后......");
                dialog.show();
                replyCz(postUrl);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        KeyboardUtil.showKeyboard(input);
    }

    //回复层主
    private void replyCz(String url) {
        String inputStr = PostActivity.getPreparedReply(this, input.getText().toString());
        params.put("message", inputStr);
        HttpUtil.post(ReplyCzActivity.this, url, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                handleReply(true, res + "层主");
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                handleReply(false, e.getMessage());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

    private void handleReply(boolean isok, String res) {
        if (isok) {
            if (res.contains("成功") || res.contains("层主")) {
                Toast.makeText(this, "回复发表成功", Toast.LENGTH_SHORT).show();
                input.setText(null);
                mPanelRoot.hidePanelAndKeyboard();
                setResult(RESULT_OK);
                finish();
            } else if (res.contains("您两次发表间隔")) {
                Toast.makeText(this, "您两次发表间隔太短了......", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "由于未知原因发表失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPanelRoot.getVisibility() == View.VISIBLE) {
            mPanelRoot.hidePanelAndKeyboard();
        } else {
            super.onBackPressed();
        }
    }
}
