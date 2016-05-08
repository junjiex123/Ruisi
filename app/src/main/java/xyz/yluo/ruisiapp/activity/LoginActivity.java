package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;


/**
 * Created by free2 on 2016/1/11 0011.
 *
 * edit in 2016 03 14
 *
 * 登陆activity
 *
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.login_name)
    protected EditText ed_username;
    @Bind(R.id.login_pas)
    protected EditText ed_pass;
    @Bind(R.id.btn_login)
    protected Button btn_login;
    @Bind(R.id.iv_login_l)
    protected ImageView imageViewl;
    @Bind(R.id.iv_login_r)
    protected ImageView imageViewr;
    @Bind(R.id.rem_user)
    protected CheckBox rem_user;
    @Bind(R.id.rem_pass)
    protected CheckBox rem_pass;
    @Bind(R.id.anwser_select)
    protected Spinner anwser_select;
    @Bind(R.id.anwser_text)
    protected EditText anwser_text;
    private ProgressDialog progress;

    private SharedPreferences perPreferences;
    private List<String> list = new ArrayList<>();
    private String loginUrl;
    private int answerSelect = 0;

    public static void open(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
            btn_login.setEnabled(true);
        }

        list.add("安全提问(未设置请忽略)");
        list.add("母亲的名字");
        list.add("爷爷的名字");
        list.add("父亲出生的城市");
        list.add("您其中一位老师的名字");
        list.add("您个人计算机的型号");
        list.add("您最喜欢的餐馆名称");
        list.add("驾驶执照最后四位数字");

        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        anwser_select.setAdapter(spinnerAdapter);
        anwser_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                answerSelect = i;
                if(i!=0){
                    anwser_text.setVisibility(View.VISIBLE);
                }else {
                    anwser_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setEnabled(false);
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
                    btn_login.setEnabled(true);
                } else {
                    btn_login.setEnabled(false);
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

    @OnClick(R.id.btn_login)
    protected void login_click() {
        progress = ProgressDialog.show(this, "正在登陆", "请等待", true);
        final String username = ed_username.getText().toString().trim();
        final String passNo = ed_pass.getText().toString().trim();
        String url = UrlUtils.getLoginUrl(false);
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if(res.contains("欢迎您回来")){
                    login_ok(res);
                }
                Document doc = Jsoup.parse(res);
                loginUrl = doc.select("form#loginform").attr("action");
                Map<String,String> params = new HashMap<>();
                params.put("fastloginfield", "username");
                params.put("cookietime", "2592000");
                params.put("username", username);
                params.put("password", passNo);
                params.put("questionid", answerSelect+"");
                if(answerSelect==0){
                    params.put("answer", "");
                }else {
                    params.put("answer", anwser_text.getText().toString());
                }

                begain_login(params);
            }

            @Override
            public void onFailure(Throwable e) {
                login_fail("网络异常！！！");
                progress.dismiss();
            }
        });
    }

    private void begain_login(Map<String,String> params){
        HttpUtil.post(getApplicationContext(), loginUrl, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if (res.contains("欢迎您回来")) {
                    login_ok(res);
                } else {
                    login_fail("账号或者密码错误");
                }
            }

            @Override
            public void onFailure(Throwable e) {
                login_fail("网络异常");
            }
        });
    }

    //登陆成功执行
    private void login_ok(String res){
        int index =  res.indexOf("欢迎您回来");
        String s = res.substring(index,index+30).split("，")[1].split(" ")[0].trim();
        if(s.length()>0){
            PublicData.USER_GRADE = s;
        }

        //写入到首选项
        SharedPreferences.Editor editor = perPreferences.edit();
        if(rem_pass.isChecked()){
            editor.putBoolean("ISREMUSER",true);
            editor.putBoolean("ISREMPASS",true);
            String userName = ed_username.getText().toString().trim();
            editor.putString("USERNAME",userName);
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
        PublicData.USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
        String url = doc.select("a[href^=home.php?mod=space&uid=]").attr("href");
        PublicData.USER_UID = GetId.getUid(url);

        //开始获取formhash
        progress.dismiss();
        PublicData.ISLOGIN = true;
        Toast.makeText(getApplicationContext(), "欢迎你"+ PublicData.USER_NAME +"登陆成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("result", "ok");
        //设置返回数据
        LoginActivity.this.setResult(RESULT_OK, intent);
        finish();
    }

    //登陆失败执行
    private void login_fail(String res){
        progress.dismiss();
        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
    }
}



