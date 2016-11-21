package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;


/**
 * Created by free2 on 2016/1/11 0011.
 * <p>
 * edit in 2016 03 14
 * <p>
 * 登陆activity
 */
public class LoginActivity extends BaseActivity {

    private EditText ed_username, ed_pass ;
    private EditText anwser_text;
    private Button btn_login;
    private CheckBox rem_ck;

    private SharedPreferences shp;
    private List<String> list = new ArrayList<>();
    private String loginUrl;
    private int answerSelect = 0;
    private ProgressDialog dialog;

    public static void open(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ed_username = (EditText) findViewById(R.id.login_name);
        ed_pass = (EditText) findViewById(R.id.login_pas);
        btn_login = (Button) findViewById(R.id.btn_login);
        rem_ck = (CheckBox) findViewById(R.id.rem_user);
        Spinner anwser_select = (Spinner) findViewById(R.id.anwser_select);
        anwser_text = (EditText) findViewById(R.id.anwser_text);

        initToolBar(true,getResources().getString(R.string.app_name));
        btn_login.setOnClickListener(v -> login_click());

        shp = getSharedPreferences(App.MY_SHP_NAME, Context.MODE_PRIVATE);
        boolean rember = shp.getBoolean(App.IS_REMBER_PASS_USER, false);
        if (rember) {
            rem_ck.setChecked(true);
            ed_username.setText(shp.getString(App.LOGIN_NAME, ""));
            ed_pass.setText(shp.getString(App.LOGIN_PASS, ""));
        }

        list.add("安全提问(未设置请忽略)");
        list.add("母亲的名字");
        list.add("爷爷的名字");
        list.add("父亲出生的城市");
        list.add("您其中一位老师的名字");
        list.add("您个人计算机的型号");
        list.add("您最喜欢的餐馆名称");
        list.add("驾驶执照最后四位数字");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        anwser_select.setAdapter(spinnerAdapter);
        anwser_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                answerSelect = i;
                if (i != 0) {
                    anwser_text.setVisibility(View.VISIBLE);
                } else {
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
            }
        });
    }

    private void login_click() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("登陆中，请稍后......");
        dialog.show();

        final String username = ed_username.getText().toString().trim();
        final String passNo = ed_pass.getText().toString().trim();
        String url = UrlUtils.getLoginUrl(false);
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                Document doc = Jsoup.parse(res);
                loginUrl = doc.select("form#loginform").attr("action");
                Map<String, String> params = new HashMap<>();
                String hash = doc.select("input#formhash").attr("value");
                App.setHash(LoginActivity.this,hash);
                params.put("fastloginfield", "username");
                params.put("cookietime", "2592000");
                params.put("username", username);
                params.put("password", passNo);
                params.put("questionid", answerSelect + "");
                if (answerSelect == 0) {
                    params.put("answer", "");
                } else {
                    params.put("answer", anwser_text.getText().toString());
                }
                begain_login(params);
            }

            @Override
            public void onFailure(Throwable e) {
                login_fail("网络异常！！！");
                dialog.dismiss();
            }
        });
    }

    private void begain_login(Map<String, String> params) {
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
    private void login_ok(String res) {
        //写入到首选项
        SharedPreferences.Editor editor = shp.edit();
        if (rem_ck.isChecked()) {
            editor.putBoolean(App.IS_REMBER_PASS_USER, true);
            editor.putString(App.LOGIN_NAME, ed_username.getText().toString().trim());
            editor.putString(App.LOGIN_PASS,ed_pass.getText().toString().trim());
        } else {
            editor.putBoolean(App.IS_REMBER_PASS_USER, false);
            editor.putString(App.LOGIN_NAME,"");
            editor.putString(App.LOGIN_PASS,"");
        }

        int i = res.indexOf("欢迎您回来");
        String info = res.substring(i + 6, i + 26);
        int pos1 = info.indexOf(" ");
        int pos2 = info.indexOf("，");
        String grade = info.substring(0, pos1);
        String name = info.substring(pos1 + 1, pos2);
        String uid = GetId.getid("uid=", res.substring(i));
        int indexhash = res.indexOf("formhash");
        String hash = res.substring(indexhash + 9, indexhash + 17);

        editor.putString(App.USER_UID_KEY, uid);
        editor.putString(App.USER_NAME_KEY, name);
        editor.putString(App.USER_GRADE_KEY, grade);
        editor.putString(App.HASH_KEY, hash);
        editor.apply();

        showToast("欢迎你" + name + "登陆成功");
        Log.e("res", "grade " + grade + " uid " + uid + " name " + name + " hash " + hash);

        Intent intent = new Intent();
        intent.putExtra("status", "ok");
        //设置返回数据
        dialog.dismiss();
        LoginActivity.this.setResult(RESULT_OK, intent);
        finish();
    }

    //登陆失败执行
    private void login_fail(String res) {
        dialog.dismiss();
        showToast(res);
    }
}



