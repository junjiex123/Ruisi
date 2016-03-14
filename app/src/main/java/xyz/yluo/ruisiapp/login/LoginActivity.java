package xyz.yluo.ruisiapp.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.http.MyHttpConnection;


/**
 * Created by free2 on 2016/1/11 0011.
 *
 * edit in 2016 03 14
 */
public class LoginActivity extends AppCompatActivity {

    private EditText ed_ip;
    private EditText ed_pass;
    private Menu menu;
    private ProgressBar progressBar;
    private Button test_btn;
    private ImageView imageViewl;
    private ImageView imageViewr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);

        ed_ip = (EditText) findViewById(R.id.login_con_ip);
        ed_pass = (EditText) findViewById(R.id.login_con_pas);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        test_btn = (Button) findViewById(R.id.login_test_button);
        imageViewl = (ImageView) findViewById(R.id.iv_login_l);
        imageViewr = (ImageView) findViewById(R.id.iv_login_r);


        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent iiii = new Intent(getApplicationContext(), MainFramActivity.class);

                if (checkInput()) {
                    //启动登陆Thread
                    progressBar.setVisibility(View.VISIBLE);

                    String url = ConfigClass.BBS_BASE_URL+"member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1";
                    Map<String, String> params = new HashMap<>();
                    params.put("username", "谁用了FREEDOM");
                    params.put("cookietime", "2592000");
                    params.put("password", "9345b4e983973212313e4c809b94f75d");
                    params.put("quickforward", "yes");
                    params.put("handlekey", "ls");

                    UserLoginTask mAuthTask = new UserLoginTask(url, params, "post");
                    mAuthTask.execute((Void) null);

                } else {

                }
            }});

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
                    imageViewl.setImageDrawable(getResources().getDrawable(R.drawable.ic_22_hide,null));
                    imageViewr.setImageDrawable(getResources().getDrawable(R.drawable.ic_33_hide, null));
                }else{
                    imageViewl.setImageDrawable(getResources().getDrawable(R.drawable.ic_22,null));
                    imageViewr.setImageDrawable(getResources().getDrawable(R.drawable.ic_33, null));
                }
            }
        });

    }



    private boolean checkInput() {
        return true;
    }

    //登陆线程类
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String url;
        private final Map<String, String> paramss;
        String method;

        UserLoginTask(String url, Map<String, String> paramss, String method) {
            this.url = url;
            this.paramss = paramss;
            this.method = method;
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            try {
                    Thread.sleep(1000);
                    response = MyHttpConnection.Http_post(url, paramss);
                }catch (Exception e) {

                return "error";
            }

            if(response.contains("欢迎您回来")){
                return response;
            }

            return "error";
        }

        @Override
        protected void onPostExecute(final String res) {

            System.out.print("\n"+res);
            progressBar.setVisibility(View.INVISIBLE);

            if(res.equals("error")){
                Toast.makeText(getApplicationContext(),"用户名或者密码错误登陆失败！！",Toast.LENGTH_SHORT).show();
                ed_pass.setText("");
                ed_pass.setError("用户名或者密码错误");
            }else{

                ConfigClass.CONFIG_ISLOGIN = true;
                ConfigClass.CONFIG_USER_NAME = ed_ip.getText().toString();

                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", "ok");
                //设置返回数据
                LoginActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                finish();
            }




        }

        @Override
        protected void onCancelled() {
        }
    }

}
