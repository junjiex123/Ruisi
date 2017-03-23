package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
 * Created by yang on 2016/1/11 0011.
 * <p>
 * edit in 2016 03 14
 * <p>
 * 登陆activity
 */
public class LoginActivity extends BaseActivity {

    private EditText edUsername, edPassword;
    private EditText edAnswer;
    private CheckBox remPassword;
    private View btnLogin;

    private SharedPreferences shp;
    private List<String> list = new ArrayList<>();
    private String loginUrl;
    private int answerSelect = 0;
    private ProgressDialog dialog;
    private TextInputLayout usernameTextInput;

    public static void open(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initToolBar(true, "登陆");
        edUsername = (EditText) findViewById(R.id.login_name);
        edPassword = (EditText) findViewById(R.id.login_pas);
        btnLogin = findViewById(R.id.btn_login);
        remPassword = (CheckBox) findViewById(R.id.rem_user);

        edAnswer = (EditText) findViewById(R.id.anwser_text);
        usernameTextInput = (TextInputLayout) findViewById(R.id.username_input);

        shp = getSharedPreferences(App.MY_SHP_NAME, Context.MODE_PRIVATE);
        if (shp.getBoolean(App.IS_REMBER_PASS_USER, false)) {
            remPassword.setChecked(true);
            edUsername.setText(shp.getString(App.LOGIN_NAME, ""));
            edPassword.setText(shp.getString(App.LOGIN_PASS, ""));
        }

        btnLogin.setOnClickListener(v -> startLogin());

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

        Spinner questionSpinner = (Spinner) findViewById(R.id.anwser_select);
        questionSpinner.setAdapter(spinnerAdapter);
        questionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                answerSelect = i;
                if (i != 0) {
                    edAnswer.setVisibility(View.VISIBLE);
                } else {
                    edAnswer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                usernameTextInput.setError(null);
                if (!TextUtils.isEmpty(edUsername.getText()) && !TextUtils.isEmpty(edPassword.getText())) {
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setEnabled(false);
                }
            }
        });

        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(edUsername.getText()) && !TextUtils.isEmpty(edPassword.getText())) {
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setEnabled(false);
                }
            }
        });
    }

    private void startLogin() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("登陆中，请稍后......");
        dialog.show();

        final String username = edUsername.getText().toString().trim();
        final String passNo = edPassword.getText().toString().trim();
        String url = UrlUtils.getLoginUrl(false);
        HttpUtil.get(LoginActivity.this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                Document doc = Jsoup.parse(res);
                loginUrl = doc.select("form#loginform").attr("action");
                Map<String, String> params = new HashMap<>();
                String hash = doc.select("input#formhash").attr("value");
                App.setHash(LoginActivity.this, hash);
                params.put("fastloginfield", "username");
                params.put("cookietime", "2592000");
                params.put("username", username);
                params.put("password", passNo);
                params.put("questionid", answerSelect + "");
                if (answerSelect == 0) {
                    params.put("answer", "");
                } else {
                    params.put("answer", edAnswer.getText().toString());
                }

                HttpUtil.post(LoginActivity.this, loginUrl, params, new ResponseHandler() {
                    @Override
                    public void onSuccess(byte[] response) {
                        String res = new String(response);
                        if (res.contains("欢迎您回来")) {
                            loginOk(res);
                        } else {
                            passwordOrUsernameErr();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        networkErr("网络异常");
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                networkErr("网络异常！！！");
                dialog.dismiss();
            }
        });
    }


    //登陆成功执行
    private void loginOk(String res) {
        //写入到首选项
        SharedPreferences.Editor editor = shp.edit();
        if (remPassword.isChecked()) {
            editor.putBoolean(App.IS_REMBER_PASS_USER, true);
            editor.putString(App.LOGIN_NAME, edUsername.getText().toString().trim());
            editor.putString(App.LOGIN_PASS, edPassword.getText().toString().trim());
        } else {
            editor.putBoolean(App.IS_REMBER_PASS_USER, false);
            editor.putString(App.LOGIN_NAME, "");
            editor.putString(App.LOGIN_PASS, "");
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

    private void passwordOrUsernameErr() {
        dialog.dismiss();
        usernameTextInput.setError("账号或者密码错误");
    }

    //登陆失败执行
    private void networkErr(String res) {
        dialog.dismiss();
        showToast(res);
    }
}



