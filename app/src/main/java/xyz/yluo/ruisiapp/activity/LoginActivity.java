package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.ConfigClass;
import xyz.yluo.ruisiapp.utils.getMd5Pass;


/**
 * Created by free2 on 2016/1/11 0011.
 *
 * edit in 2016 03 14
 */
public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_name)
    protected EditText ed_ip;
    @Bind(R.id.login_pas)
    protected EditText ed_pass;
    @Bind(R.id.login_progressBar)
    protected ProgressBar progressBar;
    @Bind(R.id.login_test_button)
    protected Button test_btn;
    @Bind(R.id.iv_login_l)
    protected ImageView imageViewl;
    @Bind(R.id.iv_login_r)
    protected ImageView imageViewr;

    private String loginUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);


        ed_ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(ed_ip.getText()) && !TextUtils.isEmpty(ed_pass.getText())) {
                    test_btn.setEnabled(true);
                } else {
                    test_btn.setEnabled(false);
                }
            }
        });

        ed_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(ed_ip.getText()) && !TextUtils.isEmpty(ed_pass.getText())) {
                    test_btn.setEnabled(true);
                } else {
                    test_btn.setEnabled(false);
                }

                //替换密码框图片
                if (!TextUtils.isEmpty(ed_pass.getText())) {
                    imageViewl.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_22_hide));
                    imageViewr.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_33_hide));
                } else {
                    imageViewl.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_22));
                    imageViewr.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_33));
                }
            }
        });
    }

    @OnClick(R.id.login_test_button)
    protected void login_test_button_click() {
        //启动登陆Thread
        progressBar.setVisibility(View.VISIBLE);

        final String username = ed_ip.getText().toString().trim();
        final String passNo = ed_pass.getText().toString().trim();

        //加密过后的密码
        String passYes = getMd5Pass.getMD5(passNo);

        String url = "member.php?mod=logging&action=login&mobile=2";
        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);
                Document doc = Jsoup.parse(res);

                if(res.contains("欢迎您回来")){
                    ConfigClass.CONFIG_USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
                    login_ok();
                    //home.php?mod=space&uid=252553&do=profile&mycenter=1&mobile=2
                }

                if (doc.select("input[name=formhash]").first() != null) {
                    ConfigClass.CONFIG_FORMHASH = doc.select("input[name=formhash]").attr("value"); // 具有 formhash 属性的链接
                }
                if (doc.select("form#loginform").attr("action") != "") {
                    loginUrl = doc.select("form#loginform").attr("action");
                    RequestParams params = new RequestParams();
                    params.put("fastloginfield", "username");
                    params.put("cookietime", "2592000");
                    params.put("username", username);
                    params.put("password", passNo);
                    params.put("questionid", "0");
                    params.put("answer", "");

                    begain_login(params);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "网络异常！！", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void begain_login(RequestParams params){

        AsyncHttpCilentUtil.post(getApplicationContext(), loginUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);

                if (res.contains("欢迎您回来")) {
                    Document document = Jsoup.parse(res);
                    ConfigClass.CONFIG_USER_NAME = document.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
                    login_ok();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "用户名或者密码错误登陆失败！！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "网络异常！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login_ok(){
        //开始获取formhash
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "欢迎你"+ConfigClass.CONFIG_USER_NAME+"登陆成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("result", "ok");
        //设置返回数据
        LoginActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        finish();
    }
}



