package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import xyz.yluo.ruisiapp.CheckMessageService;
import xyz.yluo.ruisiapp.Config;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.checknet.CheckNet;
import xyz.yluo.ruisiapp.checknet.CheckNetResponse;
import xyz.yluo.ruisiapp.database.MyDbUtils;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.ImageUtils;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-19.
 * 启动activity
 * 检查是否登陆
 * 读取相关设置写到{@link Config}
 */
public class LaunchActivity extends BaseActivity {
    private long starttime = 0;
    private TextView launch_text;
    private CircleImageView user_image;
    private SharedPreferences perUserInfo = null;
    private boolean isrecieveMessage = false;
    private boolean isForeGround = true;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(isForeGround){
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                i.putExtra("isLogin", Config.ISLOGIN);
                startActivity(i);
                finish();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        launch_text = (TextView) findViewById(R.id.launch_text);
        Button btn_inner = (Button) findViewById(R.id.btn_login_inner);
        Button btn_outer = (Button) findViewById(R.id.btn_login_outer);
        findViewById(R.id.login_fail_view).setVisibility(View.GONE);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        user_image.setVisibility(View.GONE);
        starttime = System.currentTimeMillis();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn_inner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.ISLOGIN = true;
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
        btn_outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.ISLOGIN = true;
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
        getSetting();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlphaAnimation anima = new AlphaAnimation(0.1f, 1.0f);
        anima.setDuration(1000);// 设置动画显示时间
        TranslateAnimation animation = new TranslateAnimation(0, 0, 80, 0);
        animation.setDuration(1000);
        launch_text.startAnimation(animation);
        user_image.startAnimation(anima);

        new CheckNet(this).startCheck(new CheckNetResponse() {
            @Override
            public void onFinish(int type, String response) {
                canGetRs(type);
            }
        });

        MyDbUtils myDbUtils = new MyDbUtils(this, MyDbUtils.MODE_READ);
        myDbUtils.showHistoryDatabase();

    }

    //从首选项读出设置
    private void getSetting() {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        perUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String uid = perUserInfo.getString("USER_UID", "0");
        Log.i("LaunchActivity", perUserInfo.getString("USER_NAME", "null"));
        Log.i("LaunchActivity", uid);

        if (!uid.equals("0")) {
            Uri uri = ImageUtils.getImageURI(getFilesDir(), uid);
            if (uri != null) {
                user_image.setVisibility(View.VISIBLE);
                user_image.setImageURI(uri);
            }
        }


        //boolean theme = shp.getBoolean("setting_swich_theme",false);
        isrecieveMessage = shp.getBoolean("setting_show_notify", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeGround = true;
    }

    private void canGetRs(int type) {
        if (type == 1 || type == 2) {
            String url = UrlUtils.getLoginUrl(false);
            checklogin(url);
        } else {
            noNetWork();
            findViewById(R.id.login_view).setVisibility(View.GONE);
            findViewById(R.id.login_fail_view).setVisibility(View.VISIBLE);
        }
    }

    private void checklogin(String url) {
        HttpUtil.get(this, url, new TextResponseHandler() {
            @Override
            public void onSuccess(String res) {
                Config.ISLOGIN = false;
                if (res.contains("loginbox")) {
                    Config.ISLOGIN = false;
                } else {
                    Document doc = Jsoup.parse(res);
                    int index = res.indexOf("欢迎您回来");
                    String s = res.substring(index, index + 30).split("，")[1].split(" ")[0].trim();
                    if (s.length() > 0) {
                        Config.USER_GRADE = s;
                    }
                    Config.USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
                    String url = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").attr("href");
                    Config.USER_UID = GetId.getUid(url);

                    SharedPreferences.Editor editor = perUserInfo.edit();
                    editor.putString("USER_NAME", Config.USER_NAME);
                    editor.putString("USER_UID", Config.USER_UID);
                    editor.apply();

                    Config.ISLOGIN = true;

                    startCheckMessageService();
                }
            }

            @Override
            public void onFinish() {
                finishthis();
            }
        });
    }

    //没网是执行
    private void noNetWork() {
        Toast.makeText(getApplicationContext(), "无法连接到服务器请检查网络设置！", Toast.LENGTH_SHORT).show();
    }

    private void startCheckMessageService() {
        //启动后台服务
        Log.e("launch","启动了服务");
        Intent i = new Intent(this, CheckMessageService.class);
        i.putExtra("isRunning", true);
        i.putExtra("isNotisfy", isrecieveMessage);
        startService(i);
    }


    private void finishthis() {
        long currenttime = System.currentTimeMillis();
        long delay = 1200 - (currenttime - starttime);
        if (delay < 0) {
            delay = 0;
        }
        mHandler.postDelayed(mRunnable, delay);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
        isForeGround = false;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}
