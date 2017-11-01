package me.yluo.ruisiapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.myhttp.HttpUtil;
import me.yluo.ruisiapp.myhttp.ResponseHandler;
import me.yluo.ruisiapp.utils.GetId;
import me.yluo.ruisiapp.utils.RuisUtils;
import me.yluo.ruisiapp.widget.CircleImageView;

/**
 * Created by free2 on 16-3-19.
 * 启动activity
 * 检查是否登陆
 * 读取相关设置写到{@link App}
 */
public class LaunchActivity extends BaseActivity implements View.OnClickListener {
    private final static int WAIT_TIME = 900;//最少等待时间ms
    private TextView launch_text;
    private CircleImageView logo;
    private SharedPreferences shp = null;
    private boolean isForeGround = true;
    private Handler mHandler = new Handler();
    private long timeEnter = 0;
    //记录2个检查网络的返回值，如果都为空说明没网...
    private String mobileRes = "";
    private String pcResponse = "";
    private boolean isLoginOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);
        timeEnter = System.currentTimeMillis();
        launch_text =  findViewById(R.id.app_name);
        findViewById(R.id.btn_login_inner).setOnClickListener(this);
        findViewById(R.id.btn_login_outer).setOnClickListener(this);
        findViewById(R.id.login_fail_view).setVisibility(View.INVISIBLE);
        logo = findViewById(R.id.logo);
        shp = getSharedPreferences(App.MY_SHP_NAME, MODE_PRIVATE);
        loadUserImg();
        setCopyRight();
        startLogin();
    }

    //自动续命copyright
    private void setCopyRight() {
        int year = 2016;
        int yearNow = Calendar.getInstance().get(Calendar.YEAR);

        if (year < yearNow) {
            year = yearNow;
        }
        ((TextView) findViewById(R.id.copyright))
                .setText("©2016-" + year + " 谁用了FREEDPOM");
    }

    //设置头像
    private void loadUserImg() {
        String uid = App.getUid(this);
        if (!TextUtils.isEmpty(uid)) {
            RuisUtils.LoadMyAvatar(new WeakReference<>(this),
                    uid, new WeakReference<>(logo), "m");
        }
    }

    private void startLogin() {
        mHandler.postDelayed(finishRunable, 2500);
        new Thread(() -> {
            HttpUtil.get(App.LOGIN_RS, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    pcResponse = new String(response);
                    loginOk();
                }
            });

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isLoginOk) {
                HttpUtil.get(App.LOGIN_ME, new ResponseHandler() {
                    @Override
                    public void onSuccess(byte[] response) {
                        mobileRes = new String(response);
                        if (!isLoginOk) {
                            loginOk();
                        }
                    }
                });
            }
        }).start();
    }

    private Runnable finishRunable = this::loginOk;

    private void loginOk() {
        if (!isLoginOk && isForeGround) {
            isLoginOk = true;
            new CheckTask().execute();
        }
    }

    private void enterHome() {
        if (isForeGround) {
            mHandler.removeCallbacks(finishRunable);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_inner:
                App.IS_SCHOOL_NET = true;
                enterHome();
                break;
            case R.id.btn_login_outer:
                App.IS_SCHOOL_NET = false;
                enterHome();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.4f, 1.0f);
        alphaAnimation.setDuration((long) (WAIT_TIME * 0.85));// 设置动画显示时间
        launch_text.startAnimation(alphaAnimation);
        logo.startAnimation(alphaAnimation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeGround = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(finishRunable);
        isForeGround = false;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(finishRunable);
        super.onDestroy();
    }

    private class CheckTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            String res = "";
            if (!TextUtils.isEmpty(pcResponse)) {
                App.IS_SCHOOL_NET = true;
                res = pcResponse;
            } else if (!TextUtils.isEmpty(mobileRes)) {
                App.IS_SCHOOL_NET = false;
                res = mobileRes;
            }
            if (!TextUtils.isEmpty(res)) {
                int i = res.indexOf("欢迎您回来");
                if (i > 0) {
                    String grade, name;
                    //<p>欢迎您回来，实习版主 激萌路小叔，现在将转入登录前页面</p>
                    //<p>欢迎您回来，<font color="#0099FF">实习版主</font> 激萌路小叔，现在将转入登录前页面</p>
                    String info = res.substring(i, i + 70);
                    int pos = info.indexOf("，");
                    if (info.charAt(pos + 1) == '<') {//管理员
                        int pos2 = info.indexOf(">", pos);
                        int pos3 = info.indexOf("<", pos2);
                        grade = info.substring(pos2 + 1, pos3);
                        int pos4 = info.indexOf(" ", pos3);
                        int pos5 = info.indexOf("，", pos4);
                        name = info.substring(pos4 + 1, pos5);
                    } else {
                        int pos2 = info.indexOf(" ", pos);
                        grade = info.substring(pos + 1, pos2);
                        int pos3 = info.indexOf("，", pos2);
                        name = info.substring(pos2 + 1, pos3);
                    }

                    String uid = GetId.getId("uid=", res.substring(i));
                    int indexhash = res.indexOf("formhash");
                    String hash = res.substring(indexhash + 9, indexhash + 17);
                    SharedPreferences.Editor ed = shp.edit();
                    ed.putString(App.USER_UID_KEY, uid);
                    ed.putString(App.USER_NAME_KEY, name);
                    ed.putString(App.USER_GRADE_KEY, grade);
                    ed.putString(App.HASH_KEY, hash);
                    ed.apply();
                    Log.e("res", "grade " + grade + " uid " + uid + " name " + name + " hash " + hash);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                long need = System.currentTimeMillis() - timeEnter - WAIT_TIME;
                if (need < 0) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(-need);
                            runOnUiThread(LaunchActivity.this::enterHome);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    enterHome();
                }
            } else {
                Toast.makeText(LaunchActivity.this, "没有网络,或者睿思服务器又崩溃了！",
                        Toast.LENGTH_SHORT).show();
                findViewById(R.id.app_name).setVisibility(View.GONE);
                findViewById(R.id.login_fail_view).setVisibility(View.VISIBLE);
            }
        }
    }
}
