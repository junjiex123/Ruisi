package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
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
    private final static int WAIT_TIME = 800;
    private TextView launch_text;
    private CircleImageView user_image;
    private SharedPreferences shp = null;
    private boolean isForeGround = true;
    private Handler mHandler = new Handler();

    //记录2个检查网络的返回值，如果都为空说明没网...
    private String mobileRes = "";
    private String pcResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        launch_text = (TextView) findViewById(R.id.launch_text);
        findViewById(R.id.btn_login_inner).setOnClickListener(this);
        findViewById(R.id.btn_login_outer).setOnClickListener(this);
        findViewById(R.id.login_fail_view).setVisibility(View.GONE);
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
        mHandler.postDelayed(finishRunable, 1500);
        String urlin = "http://rs.xidian.edu.cn/member.php?mod=logging&action=login&mobile=2";
        String urlout = "http://bbs.rs.xidian.me/member.php?mod=logging&action=login&mobile=2";

        HttpUtil.get(this, urlout, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                mobileRes  = new String(response);
                if(!TextUtils.isEmpty(pcResponse)){
                    loginOk();
                }
            }
        });

        HttpUtil.get(this, urlin, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                pcResponse = new String(response);
                loginOk();
            }
        });
    }


    private Runnable finishRunable = new Runnable() {
        @Override
        public void run() {
            loginOk();
        }
    };

    private void loginOk(){
        mHandler.removeCallbacks(finishRunable);
        if(!isForeGround){
            return;
        }
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
            enterHome();
        }else{
            Toast.makeText(this, "无法连接到服务器请检查网络设置！", Toast.LENGTH_SHORT).show();
            findViewById(R.id.login_view).setVisibility(View.GONE);
            findViewById(R.id.login_fail_view).setVisibility(View.VISIBLE);
        }
    }

    private void enterHome(){
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        //// TODO: 16-8-24
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
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration((long) (WAIT_TIME*0.85));// 设置动画显示时间

        TranslateAnimation animation = new TranslateAnimation(0, 0, 80, 0);
        animation.setDuration((long) (WAIT_TIME*0.8));

        // 初始化需要加载的动画资源
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.always_rotate);

        launch_text.startAnimation(animation);
        user_image.startAnimation(alphaAnimation);
        findViewById(R.id.loading_view).startAnimation(rotateAnimation);
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
}
