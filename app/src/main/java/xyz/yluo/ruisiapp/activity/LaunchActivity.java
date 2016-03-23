package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.ConfigClass;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class LaunchActivity extends AppCompatActivity{

    private SharedPreferences perPreferences;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //TODO  perPreferences
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        perPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //TODO
        //editor = perPreferences.edit();
        //isFirstIn = perPreferences.getBoolean("isFirstIn", true);
        //isAutoLogin = perPreferences.getBoolean("ISAUTO", false);
        checklogin();
    }

    private void checklogin(){
        String url = "member.php?mod=logging&action=login&mobile=2";
        ConfigClass.CONFIG_ISLOGIN = false;
        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                if (res.contains("loginbox")) {
                    ConfigClass.CONFIG_ISLOGIN = false;
                } else {
                    Document doc = Jsoup.parse(res);
                    ConfigClass.CONFIG_USER_NAME = doc.select(".footer").select("a[href^=home.php?mod=space&uid=]").text();
                    ConfigClass.CONFIG_ISLOGIN = true;
                    Toast.makeText(getApplication(),"欢迎你："+ConfigClass.CONFIG_USER_NAME,Toast.LENGTH_SHORT).show();
                }

                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
