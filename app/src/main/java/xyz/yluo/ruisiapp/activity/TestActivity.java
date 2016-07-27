package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyProgressDialog;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.UrlUtils;

public class TestActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_out;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        tv_out = (TextView) findViewById(R.id.tv_out);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_1:
                new MyProgressDialog(this)
                        .setLoadingText("加 载 中 . . . . . .")
                        .show();
                break;
            case R.id.btn_2:
                new MyAlertDialog(this, MyAlertDialog.ERROR_TYPE)
                        .setTitleText("Good job!")
                        .setContentText("You clicked the btn_blue_bg!")
                        .show();
                break;
            case R.id.btn_3:
                //begainPost(Config.FORMHASH);
                startActivity(new Intent(this,NewArticleActivity.class));
                break;

        }
    }

    //准备发帖需要的东西
    private void preparePost(final int fid) {
        //dialog = ProgressDialog.show(this, "正在发送", "请等待", true);
        String url = "forum.php?mod=post&action=newthread&fid=" + fid + "&mobile=2";
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document doc = Jsoup.parse(new String(response));
                String url = doc.select("#postform").attr("action");
                String hash = doc.select("input#formhash").attr("value");
                String time = doc.select("input#posttime").attr("value");
                //begainPost(hash, time);
            }

            @Override
            public void onFailure(Throwable e) {
                postFail("网络错误");
            }
        });
    }

    //开始发帖
    private void begainPost(String hash/*, String time*/) {
        String url3 = UrlUtils.getPostUrl(72);
        Map<String, String> params = new HashMap<>();
        params.put("formhash", hash);
        //params.put("posttime", time);
        params.put("topicsubmit", "yes");
        params.put("subject", "测试发帖");
        params.put("message", "这是帖子的内容。。。。。。");
        HttpUtil.post(this, url3, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                tv_out.setText(res);
                if (res.contains("非常感谢")) {
                    postSuccess();
                } else {
                    postFail("由于未知原因发帖失败");
                }
            }
            @Override
            public void onFailure(Throwable e) {
                postFail("由于未知原因发帖失败");
            }
        });
    }

    //发帖成功执行
    private void postSuccess() {
        //dialog.dismiss();
        Toast.makeText(getApplicationContext(), "主题发表成功", Toast.LENGTH_SHORT).show();
        //finish();
    }

    //发帖失败执行
    private void postFail(String str) {
        //dialog.dismiss();
        Toast.makeText(getApplicationContext(), "主题发表失败", Toast.LENGTH_SHORT).show();
        //Snackbar.make(main_window, str, Snackbar.LENGTH_SHORT).show();
    }
}
