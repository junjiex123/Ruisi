package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.ConfigClass;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class LaunchActivity extends AppCompatActivity{

    private SharedPreferences perPreferences;
    @Bind(R.id.progressBar)
    protected ProgressBar progressBar;
    private final int TYPE_INNER = 0;
    private final int TYPE_OUTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);

        //TODO  perPreferences
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        perPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //TODO
        //editor = perPreferences.edit();
        //isFirstIn = perPreferences.getBoolean("isFirstIn", true);
        //isAutoLogin = perPreferences.getBoolean("ISAUTO", false);

        checkNetWork();
    }


    //检测网络状态 有无/校园网/外网
    private void checkNetWork(){
        Context context = getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            final String url = "member.php?mod=logging&action=login&mobile=2";
            //wifi 先检查校园网
            if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI) {
                ConfigClass.BBS_BASE_URL = "http://rs.xidian.edu.cn/";
                AsyncHttpCilentUtil.get(getApplicationContext(),
                        url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                canGetRs(TYPE_INNER, new String(responseBody));
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                canGetRs(TYPE_OUTER, new String(responseBody));
                            }
                        });
            }else {
                ConfigClass.BBS_BASE_URL = "http://bbs.rs.xidian.me/";
                AsyncHttpCilentUtil.get(getApplicationContext(),
                        url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                canGetRs(TYPE_OUTER,new String(responseBody));
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                rsError();
                            }
                        });
            }
        }
        else {
            noNetWork();
        }
    }

    private void canGetRs(int type,String res){
        if(type==TYPE_INNER){
            ConfigClass.CONFIG_IS_INNER = true;
            Toast.makeText(getApplicationContext(),"校园网",Toast.LENGTH_SHORT).show();
            ConfigClass.BBS_BASE_URL="http://rs.xidian.edu.cn/";
            checklogin(res);
        }else{
            Toast.makeText(getApplicationContext(),"校外网",Toast.LENGTH_SHORT).show();
            ConfigClass.BBS_BASE_URL = "http://bbs.rs.xidian.me/";
            ConfigClass.CONFIG_IS_INNER = false;
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
        ConfigClass.CONFIG_ISLOGIN = false;
        if (res.contains("loginbox")) {
            ConfigClass.CONFIG_ISLOGIN = false;
        } else {
            Document doc = Jsoup.parse(res);
            ConfigClass.CONFIG_USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
            String url = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").attr("href");
            ConfigClass.CONFIG_USER_UID = GetId.getUid(url);
            ConfigClass.CONFIG_ISLOGIN = true;
        }

        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        finish();
    }
}
