package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.DimmenUtils;
import xyz.yluo.ruisiapp.utils.UrlUtils;

public class TestActivity extends BaseActivity implements View.OnClickListener {

    private EditText ed_title,ed_content;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_content = (EditText) findViewById(R.id.ed_content);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_1:
                start_edit();
                break;
            case R.id.btn_2:
                Log.e("toast","11111");
                showToast("测试");
                break;
        }
    }

    private void start_edit(){
        //http://bbs.rs.xidian.me/forum.php?mod=viewthread&tid=879072&mobile=2
        //没有分类 http://bbs.rs.xidian.me/forum.php?mod=post&action=edit&fid=72&tid=879072&pid=22185602&mobile=2
        //有分类 http://bbs.rs.xidian.me/forum.php?mod=post&action=edit&fid=72&tid=879783&pid=22201972&mobile=2
        String url =  "http://bbs.rs.xidian.me/forum.php?mod=post&action=edit&fid=72&tid=879783&pid=22201972&mobile=2";
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements content = document.select("#e_textarea");
                Elements title = document.select("input#needsubject");
                Elements select = document.select("#typeid").select("option");

                ed_title.setText(title.attr("value"));
                ed_content.setText(content.html());

                Log.e("select",select.html());
            }
        });
    }


}
