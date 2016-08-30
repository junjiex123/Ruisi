package xyz.yluo.ruisiapp.activity;

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
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-19.
 * 启动activity
 * 检查是否登陆
 * 读取相关设置写到{@link App}
 */
public class LaunchActivity extends BaseActivity implements View.OnClickListener{
    //等待时间
    private final static int WAIT_TIME = 200;
    private TextView launch_text;
    private CircleImageView user_image;
    private SharedPreferences shp = null;
    private boolean isForeGround = true;
    private Handler mHandler = new Handler();

    //记录2个检查网络的返回值，如果都为空说明没网...
    private String mobileRes = "";
    private String pcResponse = "";
    private boolean isLoginOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        launch_text = (TextView) findViewById(R.id.launch_text);
        findViewById(R.id.btn_login_inner).setOnClickListener(this);
        findViewById(R.id.btn_login_outer).setOnClickListener(this);
        findViewById(R.id.login_fail_view).setVisibility(View.INVISIBLE);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        user_image.setVisibility(View.GONE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        shp = getSharedPreferences(App.MY_SHP_NAME,MODE_PRIVATE);
        String uid = App.getUid(this);
        if (!TextUtils.isEmpty(uid)) {
            String url = UrlUtils.getAvaterurlm(uid);
            Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(user_image);
            user_image.setVisibility(View.VISIBLE);
        }
        mHandler.postDelayed(finishRunable, 3000);
        final String urlin = "http://rs.xidian.edu.cn/member.php?mod=logging&action=login&mobile=2";
        final String urlout = "http://bbs.rs.xidian.me/member.php?mod=logging&action=login&mobile=2";

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtil.get(LaunchActivity.this, urlin, new ResponseHandler() {
                    @Override
                    public void onSuccess(byte[] response) {
                        pcResponse = new String(response);
                        loginOk();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        e.printStackTrace();
                        Log.e("login fial","====inner=====");
                    }
                });

                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(!isLoginOk){
                    HttpUtil.get(LaunchActivity.this, urlout, new ResponseHandler() {
                        @Override
                        public void onSuccess(byte[] response) {
                            mobileRes  = new String(response);
                            if(!isLoginOk){
                                loginOk();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    private Runnable finishRunable = new Runnable() {
        @Override
        public void run() {
            loginOk();
        }
    };

    private void loginOk(){
        if(!isLoginOk&&isForeGround){
            isLoginOk = true;
            new CheckTask().execute();
        }

    }

    private void enterHome(){
        if(isForeGround){
            mHandler.removeCallbacks(finishRunable);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
        alphaAnimation.setDuration((long) (WAIT_TIME*0.85));// 设置动画显示时间
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.always_rotate);
        findViewById(R.id.loading_view).startAnimation(rotateAnimation);
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

    private class CheckTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            String res = "";
            if(!TextUtils.isEmpty(pcResponse)){
                App.IS_SCHOOL_NET = true;
                res = pcResponse;
            }else if(!TextUtils.isEmpty(mobileRes)){
                App.IS_SCHOOL_NET = false;
                res = mobileRes;
            }
            if(!TextUtils.isEmpty(res)){
                int i = res.indexOf("欢迎您回来");
                if(i>0){
                    String info = res.substring(i+6,i+26);
                    int pos1 = info.indexOf(" ");
                    int pos2 = info.indexOf("，");
                    String grade = info.substring(0,pos1);
                    String name = info.substring(pos1+1,pos2);
                    String uid = GetId.getid("uid=",res.substring(i));
                    int indexhash = res.indexOf("formhash");
                    String hash = res.substring(indexhash+9,indexhash+17);
                    SharedPreferences.Editor ed =  shp.edit();
                    ed.putString(App.USER_UID_KEY,uid);
                    ed.putString(App.USER_NAME_KEY,name);
                    ed.putString(App.USER_GRADE_KEY,grade);
                    ed.putString(App.HASH_KEY,hash);
                    ed.apply();
                    Log.e("res","grade "+grade+" uid "+uid+" name "+name+" hash "+hash);
                }
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
                enterHome();
            }else{
                Toast.makeText(LaunchActivity.this, "没有网络,或者睿思服务器又崩溃了！",
                        Toast.LENGTH_SHORT).show();
                findViewById(R.id.login_view).setVisibility(View.GONE);
                View fail = findViewById(R.id.login_fail_view);
                fail.setVisibility(View.VISIBLE);
            }
        }
    }
}
