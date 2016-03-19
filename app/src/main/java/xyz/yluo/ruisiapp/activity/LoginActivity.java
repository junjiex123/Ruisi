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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.utils.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.GetFormHash;
import xyz.yluo.ruisiapp.utils.getMd5Pass;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;


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
                if(!TextUtils.isEmpty(ed_pass.getText())){
                    imageViewl.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_22_hide));
                    imageViewr.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_33_hide));
                }else{
                    imageViewl.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_22));
                    imageViewr.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_33));
                }
            }
        });

    }


    @OnClick(R.id.login_test_button)
    protected void login_test_button_click(){
        //Intent iiii = new Intent(getApplicationContext(), MainFramActivity.class);

        if (checkInput()) {
            //启动登陆Thread
            progressBar.setVisibility(View.VISIBLE);

            String username = ed_ip.getText().toString().trim();
            String passNo = ed_pass.getText().toString().trim();

            String passYes = getMd5Pass.getMD5(passNo);

            String url = "member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1";
            RequestParams params = new RequestParams();
            params.put("username", username);
            params.put("cookietime", "2592000");
            params.put("password", passYes);
            params.put("quickforward", "yes");
            params.put("handlekey", "ls");

            AsyncHttpCilentUtil.post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String res = new String(responseBody);
                    progressBar.setVisibility(View.INVISIBLE);

                    if (res.contains("欢迎您回来")) {
                        //开始获取formhash
                        GetFormHash.start_get_hash(getApplicationContext());
                        ConfigClass.CONFIG_ISLOGIN = true;
                        ConfigClass.CONFIG_USER_NAME = ed_ip.getText().toString().trim();
                        //数据是使用Intent返回
                        Intent intent = new Intent();
                        //把返回数据存入Intent
                        intent.putExtra("result", "ok");
                        //设置返回数据
                        LoginActivity.this.setResult(RESULT_OK, intent);
                        //关闭Activity
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "用户名或者密码错误登陆失败！！", Toast.LENGTH_SHORT).show();
                        ed_pass.setText("");
                        ed_pass.setError("用户名或者密码错误");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "网络异常！！", Toast.LENGTH_SHORT).show();

                }
            });

        } else {

            //TODO

        }
    }

    private boolean checkInput() {
        return true;
    }

}


