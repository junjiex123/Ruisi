package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.MySetting;
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
        checkNetWork();
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
            Toast.makeText(getApplicationContext(),"校园网",Toast.LENGTH_SHORT).show();
            MySetting.BBS_BASE_URL= UrlUtils.getBaseUrl(true);
            checklogin(res);
        }else{
            Toast.makeText(getApplicationContext(),"校外网",Toast.LENGTH_SHORT).show();
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

    private void checkInner(){
        final String url = UrlUtils.getLoginUrl(false);
        MySetting.BBS_BASE_URL = UrlUtils.getBaseUrl(true);
        AsyncHttpCilentUtil.get(getApplicationContext(),
                url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        canGetRs(TYPE_INNER, new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        checkOuter();
                    }
                });
    }
}
