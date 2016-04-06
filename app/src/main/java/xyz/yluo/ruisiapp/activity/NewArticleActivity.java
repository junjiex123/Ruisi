package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.PostHander;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-6.
 * 发帖activity
 */
public class NewArticleActivity extends AppCompatActivity {

    @Bind(R.id.edit_bar)
    protected LinearLayout edit_bar;
    @Bind(R.id.emotion_container)
    protected LinearLayout emotion_container;
    @Bind(R.id.edit_input_title)
    protected EditText edit_input_title;
    @Bind(R.id.edit_input_content)
    protected EditText edit_input_content;
    @Bind(R.id.action_bold)
    protected CheckBox action_bold;
    @Bind(R.id.action_italic)
    protected CheckBox action_italic;
    @Bind(R.id.main_window)
    protected CoordinatorLayout main_window;
    private ProgressDialog progress;

    private int CURRENT_FID = 72;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        action_bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edit_input_content.append("[b]");
                } else {
                    edit_input_content.append("[/b]");
                }
            }
        });

        action_italic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edit_input_content.append("[i]");

                } else {
                    edit_input_content.append("[/i]");

                }
            }
        });
    }

    @OnClick(R.id.action_emotion)
    protected void action_emotion(){
        //todo 可以加入动画
        if(emotion_container.getVisibility()==View.VISIBLE){
            emotion_container.setVisibility(View.GONE);
        }else{
            emotion_container.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id._1000, R.id._1001,R.id._1002,R.id._1003,R.id._1005,
            R.id._1006,R.id._1007,R.id._1008,R.id._1009,R.id._1010,
            R.id._1011,R.id._1012,R.id._1013,R.id._1014,R.id._1015,
            R.id._1016,R.id._1017,R.id._1018,R.id._1019,R.id._1020,
            R.id._1021,R.id._1022,R.id._1023,R.id._1024,R.id._1025,
            R.id._1027,R.id._1028,R.id._1029,R.id._1030, R.id._998,
            R.id._999,R.id._9998,R.id._9999
    })
    protected void smiley_click(ImageButton btn){
        //插入表情
        //{:16_1021:}
        //_1021
        //input_aera.append(btn.getTag().toString());
        String tmp = btn.getTag().toString();
        PostHander hander = new PostHander(getApplicationContext(),(EditText)getCurrentFocus());
        hander.insertSmiley("{:16" + tmp + ":}", btn.getDrawable());
    }

    //发帖按钮
    @OnClick(R.id.btn_send)
    protected void btn_send_click(){
        if(checkPostInput()){
            preparePost(CURRENT_FID);
        }
    }

    private boolean checkPostInput(){
        if (edit_input_title.getText().toString()==""){
            postFail("标题不能为空啊");
            return false;
        }else if(edit_input_content.getText().toString()==""){
            postFail("内容不能为空啊");
            return false;
        }else{
            return true;
        }
    }

    //准备发帖需要的东西
    private void preparePost(final int fid){
        progress = ProgressDialog.show(this, "正在发送", "请等待", true);
        String url = "forum.php?mod=post&action=newthread&fid="+fid+"&mobile=2";
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document doc  = Jsoup.parse(new String(response));
                String url  = doc.select("#postform").attr("action");
                String hash = doc.select("input#formhash").attr("value");
                String time = doc.select("input#posttime").attr("value");
                begainPost(url,hash,time);
            }

            @Override
            public void onFailure(Throwable e) {
                postFail("网络错误");
            }
        });
    }

    //开始发帖
    private void begainPost(String url,String hash,String time){

        String url3 = UrlUtils.getPostUrl(72);

         Map<String,String> params = new HashMap<>();
        params.put("formhash",hash);
        params.put("posttime",time);
        params.put("topicsubmit","yes");
        params.put("subject",edit_input_title.getText().toString());
        params.put("message",edit_input_content.getText().toString());
        HttpUtil.post(getApplicationContext(), url3, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                edit_input_content.setText(res);

                if(res.contains("非常感谢")){
                    postSuccess();
                }else{
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
    private void postSuccess(){
        progress.dismiss();
        Toast.makeText(getApplicationContext(),"主题发表成功",Toast.LENGTH_SHORT).show();
        //finish();
    }

    //发帖失败执行
    private void postFail(String str){
        progress.dismiss();
        Snackbar.make(main_window,str,Snackbar.LENGTH_SHORT).show();
    }
}
