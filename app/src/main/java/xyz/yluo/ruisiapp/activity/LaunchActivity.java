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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xyz.yluo.ruisiapp.CheckMessageService;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.checknet.CheckNet;
import xyz.yluo.ruisiapp.checknet.CheckNetResponse;
import xyz.yluo.ruisiapp.database.MyDbUtils;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-19.
 * 启动activity
 * 检查是否登陆
 * 读取相关设置写到{@link PublicData}
 */
public class LaunchActivity extends BaseActivity{
    private long starttime = 0;
    private TextView launch_text;
    private CircleImageView user_image;
    private SharedPreferences perUserInfo = null;

    private boolean isrecieveMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        launch_text = (TextView) findViewById(R.id.launch_text);
        Button btn_inner = (Button) findViewById(R.id.btn_login_inner);
        Button btn_outer = (Button) findViewById(R.id.btn_login_outer);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        user_image.setVisibility(View.GONE);
        starttime = System.currentTimeMillis();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btn_inner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicData.ISLOGIN = true;
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }
        });
        btn_outer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicData.ISLOGIN = true;
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
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
        TranslateAnimation animation = new TranslateAnimation(0,0,80,0);
        animation.setDuration(1000);
        launch_text.startAnimation(animation);
        user_image.startAnimation(anima);

        new CheckNet(this).startCheck(new CheckNetResponse() {
            @Override
            public void onFinish(int type, String response) {
                canGetRs(type);
            }
        });

        MyDbUtils myDbUtils = new MyDbUtils(this,true);
        myDbUtils.showDatabase();

    }

    //从首选项读出设置
    private void getSetting(){
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        perUserInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String uid = perUserInfo.getString("USER_UID","0");
        Log.i("LaunchActivity",perUserInfo.getString("USER_NAME","null"));
        Log.i("LaunchActivity",uid);

        if(!uid.equals("0")){
            Uri uri =   getImageURI(uid);
            if(uri!=null){
                user_image.setVisibility(View.VISIBLE);
                user_image.setImageURI(uri);
            }
        }

        String urlSetting  = shp.getString("setting_forums_url", "0");
        boolean isShowZhidin  = shp.getBoolean("setting_show_zhidin",false);
        //boolean theme = shp.getBoolean("setting_swich_theme",false);
        boolean setting_show_plain = shp.getBoolean("setting_show_plain",false);
        isrecieveMessage = shp.getBoolean("setting_show_notify",false);

        PublicData.ISSHOW_ZHIDIN = isShowZhidin;
        PublicData.ISSHOW_PLAIN = setting_show_plain;
    }

    private void canGetRs(int type){
        if(type==1||type==2){
            String url = UrlUtils.getLoginUrl(false);
            checklogin(url);
        }else{
            noNetWork();
            findViewById(R.id.login_view).setVisibility(View.GONE);
            findViewById(R.id.login_fail_view).setVisibility(View.VISIBLE);
        }
    }

    private void checklogin(String url){
        HttpUtil.get(this, url, new TextResponseHandler() {
            @Override
            public void onSuccess(String res) {
                PublicData.ISLOGIN = false;
                if (res.contains("loginbox")) {
                    PublicData.ISLOGIN = false;
                } else {
                    Document doc = Jsoup.parse(res);
                    int index =  res.indexOf("欢迎您回来");
                    String s = res.substring(index,index+30).split("，")[1].split(" ")[0].trim();
                    if(s.length()>0){
                        PublicData.USER_GRADE = s;
                    }
                    PublicData.USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
                    String url = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").attr("href");
                    PublicData.USER_UID = GetId.getUid(url);

                    SharedPreferences.Editor editor =perUserInfo.edit();
                    editor.putString("USER_NAME",PublicData.USER_NAME);
                    editor.putString("USER_UID",PublicData.USER_UID);
                    editor.apply();

                    PublicData.ISLOGIN = true;

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
    private void noNetWork(){
        Toast.makeText(getApplicationContext(),"无法连接到服务器请检查网络设置！",Toast.LENGTH_SHORT).show();
    }

    private Handler mHandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
    };

    private void startCheckMessageService(){
        //启动后台服务
        Intent i = new Intent(this, CheckMessageService.class);
        i.putExtra("isRunning",true);
        i.putExtra("isNotisfy",isrecieveMessage);
        startService(i);
    }



    private void finishthis(){
        long currenttime = System.currentTimeMillis();
        long delay = 1500-(currenttime-starttime);
        if(delay<0){
            delay = 0;
        }
        mHandler.postDelayed(mRunnable, delay);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    /*
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
     * 这里的path是图片的地址
     */
    public Uri getImageURI(final String uid){
        final File file = new File(getFilesDir() + "/" + uid);

        Log.i("launch file",file.toString()+" "+file.exists());
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    // 从网络上获取图片
                    URL url = null;
                    try {
                        url = new URL(UrlUtils.getAvaterurlb(uid));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        if (conn.getResponseCode() == 200) {
                            InputStream is = conn.getInputStream();
                            FileOutputStream fos = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            is.close();
                            fos.close();
                            // 返回一个URI对象
                            conn.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("launch","fle delete " +file.delete());
                    }
                }
            }.start();
            return null;
        }
    }
}
