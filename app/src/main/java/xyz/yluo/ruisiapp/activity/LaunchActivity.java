package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;
import xyz.yluo.ruisiapp.widget.CircleImageView;

/**
 * Created by free2 on 16-3-19.
 * 启动activity
 * 检查是否登陆
 * 读取相关设置写到{@link App}
 */
public class LaunchActivity extends BaseActivity implements View.OnClickListener {
    private final static int WAIT_TIME = 900;//最少等待时间ms
    private TextView launch_text;
    private CircleImageView user_image;
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
        /**
         * 切换主题
         */
        boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("setting_dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);
        timeEnter = System.currentTimeMillis();
        launch_text = (TextView) findViewById(R.id.launch_text);
        findViewById(R.id.btn_login_inner).setOnClickListener(this);
        findViewById(R.id.btn_login_outer).setOnClickListener(this);
        findViewById(R.id.login_fail_view).setVisibility(View.INVISIBLE);
        user_image = (CircleImageView) findViewById(R.id.user_image);

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
        ((TextView) findViewById(R.id.copyright)).setText("©2016-" + year + " 谁用了FREEDPOM");
    }

    //设置头像
    private void loadUserImg() {
        String uid = App.getUid(this);
        if (TextUtils.isEmpty(uid)) {
            user_image.setVisibility(View.GONE);
            return;
        }

        File f = new File(getFilesDir() + uid);
        if (f.exists()) {
            Picasso.with(this)
                    .load(f)
                    .error(R.drawable.image_placeholder)
                    .into(user_image);
        } else {
            new GetImageTask().execute(uid);
        }
    }

    private void startLogin() {
        mHandler.postDelayed(finishRunable, 2500);

        new Thread(() -> {
            HttpUtil.get(LaunchActivity.this, App.LOGIN_RS, new ResponseHandler() {
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
                HttpUtil.get(LaunchActivity.this, App.LOGIN_ME, new ResponseHandler() {
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

    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = UrlUtils.getAvaterurlm(strings[0]);
            File f = new File(getFilesDir() + strings[0]);
            Bitmap b = null;
            try {
                b = Picasso.with(LaunchActivity.this).load(url).get();
                FileOutputStream out = new FileOutputStream(f);
                b.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                user_image.setImageDrawable(d);
            }
        }
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
        user_image.startAnimation(alphaAnimation);
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
                    String info = res.substring(i + 6, i + 26);
                    int pos1 = info.indexOf(" ");
                    int pos2 = info.indexOf("，");
                    String grade = info.substring(0, pos1);
                    String name = info.substring(pos1 + 1, pos2);
                    String uid = GetId.getid("uid=", res.substring(i));
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
                            enterHome();
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
                findViewById(R.id.login_view).setVisibility(View.GONE);
                View fail = findViewById(R.id.login_fail_view);
                fail.setVisibility(View.VISIBLE);
            }
        }
    }
}
