package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;


/**
 * Created by free2 on 2016/1/11 0011.
 *
 * edit in 2016 03 14
 */
public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_name)
    protected EditText ed_username;
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
    @Bind(R.id.rem_user)
    protected CheckBox rem_user;
    @Bind(R.id.rem_pass)
    protected CheckBox rem_pass;
    private SharedPreferences perPreferences;

    private String loginUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);

        perPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        boolean isRemUser = perPreferences.getBoolean("ISREMUSER", false);
        boolean isRemberPass = perPreferences.getBoolean("ISREMPASS",false);
        if(isRemUser){
            rem_user.setChecked(true);
            ed_username.setText(perPreferences.getString("USERNAME",""));
        }
        if (isRemberPass){
            rem_pass.setChecked(true);
            rem_user.setChecked(true);
            ed_username.setText(perPreferences.getString("USERNAME",""));
            ed_pass.setText(perPreferences.getString("PASSWORD",""));
            test_btn.setEnabled(true);
        }

        ed_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(ed_username.getText()) && !TextUtils.isEmpty(ed_pass.getText())) {
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
                if (!TextUtils.isEmpty(ed_username.getText()) && !TextUtils.isEmpty(ed_pass.getText())) {
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
        rem_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    rem_user.setChecked(true);
                }
            }
        });

        rem_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    rem_pass.setChecked(false);
                }
            }
        });
    }

    @OnClick(R.id.login_test_button)
    protected void login_test_button_click() {
        //启动登陆Thread
        progressBar.setVisibility(View.VISIBLE);
        final String username = ed_username.getText().toString().trim();
        final String passNo = ed_pass.getText().toString().trim();
        String url = UrlUtils.getLoginUrl(false);
        AsyncHttpCilentUtil.get(getApplicationContext(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                if(res.contains("欢迎您回来")){
                    login_ok(res);
                }
                Document doc = Jsoup.parse(res);
                if (doc.select("input[name=formhash]").first() != null) {
                    String temphash = doc.select("input[name=formhash]").attr("value"); // 具有 formhash 属性的链接
                    if(!temphash.isEmpty()){
                        MySetting.CONFIG_FORMHASH = temphash;
                    }

                }
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
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                login_fail("网络异常！！！");
            }
        });
    }

    private void begain_login(RequestParams params){

        AsyncHttpCilentUtil.post(getApplicationContext(), loginUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                if (res.contains("欢迎您回来")) {
                    login_ok(res);
                } else {
                    login_fail("账号或者密码错误");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                login_fail("网络异常");
            }
        });
    }

    private void login_ok(String res){

        //写入到首选项
        SharedPreferences.Editor editor = perPreferences.edit();
        if(rem_pass.isChecked()){
            editor.putBoolean("ISREMUSER",true);
            editor.putBoolean("ISREMPASS",true);
            editor.putString("USERNAME", ed_username.getText().toString().trim());
            editor.putString("PASSWORD",ed_pass.getText().toString().trim());
        }else{
            editor.putBoolean("ISREMUSER",false);
            editor.putBoolean("ISREMPASS",false);
        }
        if(rem_user.isChecked()){
            editor.putBoolean("ISREMUSER",true);
            editor.putString("USERNAME", ed_username.getText().toString().trim());
        }else {
            editor.putBoolean("ISREMUSER",false);
        }
        editor.apply();

        Document doc = Jsoup.parse(res);
        MySetting.CONFIG_USER_NAME = ed_username.getText().toString().trim();
        String url = doc.select("a[href^=home.php?mod=space&uid=]").attr("href");
        MySetting.CONFIG_USER_UID = GetId.getUid(url);

        //开始获取formhash
        progressBar.setVisibility(View.INVISIBLE);
        MySetting.CONFIG_ISLOGIN = true;
        Toast.makeText(getApplicationContext(), "欢迎你"+ MySetting.CONFIG_USER_NAME+"登陆成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("result", "ok");
        //设置返回数据
        LoginActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        finish();
    }

    private void login_fail(String res){
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
    }
}



