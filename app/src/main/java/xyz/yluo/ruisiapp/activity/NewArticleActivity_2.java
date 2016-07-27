package xyz.yluo.ruisiapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.Config;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.PersistentCookieStore;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-4-2.
 * 无法绕过验证码
 * 备份的发帖
 */
public class NewArticleActivity_2 extends BaseActivity {

    private static int CURRENT_FID = 72;
    private WebView myWebView;
    private List<String> list = new ArrayList<>();
    private Map<Integer, String> map = new LinkedHashMap<>();
    private MyToolBar myToolBar;

    public static void open(Context context) {
        Intent intent = new Intent(context, NewArticleActivity_2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_topic2);
        myToolBar = (MyToolBar) findViewById(R.id.myToolBar);

        //设置cookie
        myWebView = (WebView) findViewById(R.id.mwebView);
        setCookie(this);

        map.put(72, "灌水专区");
        map.put(549, "文章天地");
        map.put(108, "我是女生");
        map.put(551, "西电问答");
        map.put(550, "心灵花园");
        map.put(110, "普通交易");
        map.put(217, "缘聚睿思");
        map.put(142, "失物招领");
        map.put(552, "我要毕业啦");
        map.put(560, "技术博客");
        map.put(548, "学习交流");
        map.put(216, "我爱运动");
        map.put(91, "考研交流");
        map.put(555, "就业经验交流");
        map.put(145, "软件交流");
        map.put(144, "嵌入式交流");
        map.put(152, "竞赛交流");
        map.put(147, "原创精品");
        map.put(215, "西电后街");
        map.put(125, "音乐纵贯线");
        map.put(140, "绝对漫域");

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            list.add(entry.getValue());
        }

        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        myToolBar.setTitle("发表新帖");
        myToolBar.setHomeEnable(this);
        myToolBar.addView(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    CURRENT_FID = getName(i).getKey();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                myWebView.loadUrl(UrlUtils.getPostUrl(CURRENT_FID));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        if (Config.ISLOGIN) {
            myWebView.loadUrl(UrlUtils.getPostUrl(CURRENT_FID));
        } else {
            Toast.makeText(this, "你还没有登陆", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    private void setCookie(Context context) {

        PersistentCookieStore cookieStore = HttpUtil.getStore(context);
        CookieManager cookieManager = CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);

        String domain = ";domain=" + Config.getBaseUrl().replace("http://", "").replace("/", "");

        for (String s : cookieStore.getCookie().split(";")) {
            s = s + domain;
            cookieManager.setCookie(Config.getBaseUrl(), s);
        }
    }

    private Map.Entry<Integer, String> getName(int i) {
        if (i > map.size()) {
            return null;
        }

        int j = 0;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {

            if (j == i) {
                return entry;
            }
            j++;
        }
        return null;
    }
}
