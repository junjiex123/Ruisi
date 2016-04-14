package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.checknet.CheckNet;
import xyz.yluo.ruisiapp.checknet.CheckNetResponse;
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

    private final int TYPE_INNER = 1;
    private final int TYPE_OUTER = 2;
    private long starttime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        starttime = System.currentTimeMillis();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSetting();
        new CheckNet(this).startCheck(new CheckNetResponse() {
            @Override
            public void onFinish(int type, String response) {
                canGetRs(type,response);
            }
        });
    }

    //从首选项读出设置
    private void getSetting(){
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String urlSetting  = shp.getString("setting_forums_url", "0");
            boolean isShowZhidin  = shp.getBoolean("setting_show_zhidin",false);
            String tail = shp.getString("setting_user_tail","");
            boolean theme = shp.getBoolean("setting_swich_theme",false);
            boolean setting_show_plain = shp.getBoolean("setting_show_plain",true);

            PublicData.ISSHOW_ZHIDIN = isShowZhidin;
            PublicData.ISSHOW_PLAIN = setting_show_plain;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void canGetRs(int type,String res){
        String url = UrlUtils.getLoginUrl(false);
        if(type==TYPE_INNER){
            PublicData.IS_SCHOOL_NET = true;
            PublicData.BASE_URL = UrlUtils.getBaseUrl(true);
            checklogin(url);
        }else if(TYPE_OUTER==type){
            url = UrlUtils.getLoginUrl(false);
            PublicData.BASE_URL = UrlUtils.getBaseUrl(false);
            PublicData.IS_SCHOOL_NET = false;
            checklogin(url);
        }else{
            noNetWork();
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
                    PublicData.USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
                    String url = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").attr("href");
                    PublicData.USER_UID = GetId.getUid(url);
                    PublicData.ISLOGIN = true;
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

    private void finishthis(){
        long currenttime = System.currentTimeMillis();
        long delay = 1500-(currenttime-starttime);
        if(delay<0){
            delay = 0;
        }
        new Handler().postDelayed(new Runnable(){
            public void run() {
                //progressBar.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }

        }, delay);

    }
}
