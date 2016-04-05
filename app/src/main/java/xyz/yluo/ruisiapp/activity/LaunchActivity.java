package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class LaunchActivity extends AppCompatActivity{


    @Bind(R.id.progressBar)
    protected ProgressBar progressBar;
    private final int TYPE_INNER = 0;
    private final int TYPE_OUTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSetting();
        checkNetWork();
    }


    //从首选项读出设置
    private void getSetting(){
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String urlSetting  = shp.getString("setting_forums_url", "0");
            boolean isShowZhidin  = shp.getBoolean("setting_show_zhidin",false);
            String tail = shp.getString("setting_user_tail","");
            boolean theme = shp.getBoolean("setting_swich_theme",false);
            boolean setting_show_style = shp.getBoolean("setting_show_style",true);

            MySetting.CONFIG_ISSHOW_ZHIDIN = isShowZhidin;
            MySetting.CONFIG_SHOW_PLAIN_TEXT = !setting_show_style;

            System.out.println("url"+urlSetting+"|"+"是否显示置顶"+isShowZhidin+"|"+"小尾巴"+tail+"|"+"主题"+theme+"|"+"是否显示样式"+setting_show_style);
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    //检测网络状态 有无/校园网/外网
    private void checkNetWork(){
        Context context = getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            //wifi 先检查校园网
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                checkInner();
            }else {
                checkOuter();
            }
        }
        else {
            noNetWork();
        }
    }

    private void canGetRs(int type,String res){
        if(type==TYPE_INNER){
            MySetting.CONFIG_IS_INNER = true;
            MySetting.BBS_BASE_URL= UrlUtils.getBaseUrl(true);
            checklogin(res);
        }else{
            Toast.makeText(getApplicationContext(),"已经切换到外网",Toast.LENGTH_SHORT).show();
            MySetting.BBS_BASE_URL = UrlUtils.getBaseUrl(false);
            MySetting.CONFIG_IS_INNER = false;
            checklogin(res);
        }
    }

    private void rsError(){
        Toast.makeText(getApplicationContext(),"睿思有可能崩了，或者你的网络状态不好，等等再试试",Toast.LENGTH_SHORT).show();
    }
    //没网是执行
    private void noNetWork(){
        Toast.makeText(getApplicationContext(),"没网,请打开网络连接",Toast.LENGTH_SHORT).show();
    }


    private void checklogin(String res){
        progressBar.setVisibility(View.GONE);
        MySetting.CONFIG_ISLOGIN = false;
        if (res.contains("loginbox")) {
            MySetting.CONFIG_ISLOGIN = false;
        } else {
            Document doc = Jsoup.parse(res);
            MySetting.CONFIG_USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
            String url = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").attr("href");
            MySetting.CONFIG_USER_UID = GetId.getUid(url);
            MySetting.CONFIG_ISLOGIN = true;
        }

        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        finish();
    }

    private void checkOuter(){
        final String url = UrlUtils.getLoginUrl(false);
        MySetting.BBS_BASE_URL = UrlUtils.getBaseUrl(false);
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                canGetRs(TYPE_OUTER, new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                rsError();
            }
        });
    }

    private void checkInner(){
        final String url = UrlUtils.getLoginUrl(false);
        MySetting.BBS_BASE_URL = UrlUtils.getBaseUrl(true);
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                canGetRs(TYPE_INNER, new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                checkOuter();
            }
        });
    }
}
