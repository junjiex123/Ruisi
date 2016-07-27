package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import xyz.yluo.ruisiapp.Config;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyProgressDialog;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-6.
 * 发帖activity
 */
public class NewArticleActivity extends BaseActivity{

    private EditText ed_title,ed_content;
    private CoordinatorLayout main_window;
    private MyProgressDialog dialog;
    private MyToolBar myToolBar;

    private int fid = 72;
    private int[] fids = new int[]{72, 549, 108, 551, 550, 110, 217, 142, 552,
            560, 548, 216, 91, 555, 145, 144, 152, 147, 215, 125, 140};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        myToolBar = (MyToolBar) findViewById(R.id.myToolBar);

        myToolBar.setTitle("发表新帖");
        myToolBar.setHomeEnable(this);
        myToolBar.addButton("发表",R.drawable.btn_light_red_bg,"BTN_SUBMIT");
        myToolBar.setToolBarClickListener(new MyToolBar.OnToolBarItemClick() {
            @Override
            public void OnItemClick(View v, String Tag) {
                if(Tag.equals("BTN_SUBMIT")&&checkPostInput()){
                    dialog = new MyProgressDialog(NewArticleActivity.this).setLoadingText("发贴中请稍后 ...");
                    dialog.show();
                    begainPost(Config.FORMHASH);

                }
            }
        });


        Spinner choose_froums = (Spinner) findViewById(R.id.choose_froums);
        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_content = (EditText) findViewById(R.id.ed_content);
        main_window = (CoordinatorLayout) findViewById(R.id.main_window);


        choose_froums.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>=fids.length){
                    return;
                }else{
                    fid = fids[i];
                    Log.e("fid",fid+"");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean checkPostInput() {
        if(TextUtils.isEmpty(ed_title.getText().toString().trim())){
            Snackbar.make(main_window, "标题不能为空啊", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(ed_content.getText().toString().trim())){
            Snackbar.make(main_window, "内容不能为空啊", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //开始发帖
    private void begainPost(String hash  /*, String time*/) {
        String url = UrlUtils.getPostUrl(fid);
        Map<String, String> params = new HashMap<>();
        params.put("formhash", hash);
        //params.put("posttime", time);
        params.put("topicsubmit", "yes");
        params.put("subject", ed_title.getText().toString());
        params.put("message", ed_content.getText().toString());
        HttpUtil.post(getApplicationContext(), url, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                //// TODO: 16-7-26 delete it later
                //ed_content.setText(res);
                //逆向工程 反向判断有没有发帖成功
                Log.e("post",res);
                if(res.contains("已经被系统拒绝")){
                    postFail("由于未知原因发帖失败");
                }else{
                    postSuccess();
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
        dialog.dismiss();
        Toast.makeText(getApplicationContext(), "主题发表成功", Toast.LENGTH_SHORT).show();
        //finish();
        new MyAlertDialog(this,MyAlertDialog.SUCCESS_TYPE)
                .setTitleText("发帖成功")
                .setContentText("要离开此页面吗？")
                .setCancelText("取消")
                .setConfirmText("离开")
                .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                    @Override
                    public void onClick(MyAlertDialog myAlertDialog) {
                        finish();
                    }
                });

    }
//
    //发帖失败执行
    private void postFail(String str) {
        dialog.dismiss();
        Toast.makeText(getApplicationContext(), "发帖失败", Toast.LENGTH_SHORT).show();
    }


//    //准备发帖需要的东西
//    private void preparePost(final int fid) {
//        progress = ProgressDialog.show(this, "正在发送", "请等待", true);
//        String url = "forum.php?mod=post&action=newthread&fid=" + fid + "&mobile=2";
//        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
//            @Override
//            public void onSuccess(byte[] response) {
//                Document doc = Jsoup.parse(new String(response));
//                String url = doc.select("#postform").attr("action");
//                String hash = doc.select("input#formhash").attr("value");
//                String time = doc.select("input#posttime").attr("value");
//                begainPost(hash, time);
//            }
//
//            @Override
//            public void onFailure(Throwable e) {
//                postFail("网络错误");
//            }
//        });
//    }

}
