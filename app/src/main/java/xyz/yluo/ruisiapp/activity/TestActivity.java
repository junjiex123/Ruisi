package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;

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
                start_post();
                break;
        }
    }

    private void start_edit(){
        //http://bbs.rs.xidian.me/forum.php?mod=viewthread&tid=879072&mobile=2
        //没有分类 http://bbs.rs.xidian.me/forum.php?mod=post&action=edit&fid=72&tid=879072&pid=22185602&mobile=2
        //有分类 http://bbs.rs.xidian.me/forum.php?mod=post&action=edit&fid=72&tid=879783&pid=22201972&mobile=2
        String url =  "http://bbs.rs.xidian.me/forum.php?mod=post&action=edit&fid=72&tid=879072&pid=22185602&mobile=2";
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements content = document.select("#e_textarea");
                Elements title = document.select("input#needsubject");
                Elements select = document.select("#typeid").select("option");
                String hash = document.select("#formhash").attr("value");
                if(!TextUtils.isEmpty(hash)){
                    App.FORMHASH = hash;
                    Log.e("hash",""+hash);
                }
                ed_title.setText(title.attr("value"));
                ed_content.setText(content.html());

                Log.e("select",select.html());
            }
        });
    }

    private void start_post(){
        String typeId = "";
        int fid = 72;

        String url = "http://bbs.rs.xidian.me/forum.php?mod=post&action=edit&extra=&editsubmit=yes&mobile=2&geoloc=&handlekey=postform&inajax=1";
        Map<String, String> params = new HashMap<>();

        params.put("formhash", App.FORMHASH);
        //params.put("posttime", time);
        params.put("editsubmit", "yes");
        if(!TextUtils.isEmpty(typeId)&&!typeId.equals("0")){
            params.put("typeid",typeId);
        }
        params.put("fid",fid+"");
        params.put("tid","879072");
        params.put("pid","22185602");
        params.put("page","");

        params.put("subject", ed_title.getText().toString());
        params.put("message", ed_content.getText().toString());
        HttpUtil.post(this,url,params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if(res.contains("帖子编辑成功")){

                }
                Log.e("res",new String(response));
            }
        });
    }


}
