package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.MyWebView;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-4-2.
 *
 */
public class NewArticleActivity_2 extends AppCompatActivity {

    //@Bind(R.id.webView)
    protected MyWebView myWebView;
    private List<String> list = new ArrayList<>();

    Map<Integer,String> map = new LinkedHashMap<>();

    private static int CURRENT_FID = 72;

    public static void open(Context context) {
        Intent intent = new Intent(context, NewArticleActivity_2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_article2);
        myWebView = (MyWebView) findViewById(R.id.mwebView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        map.put(72,"灌水专区");
        map.put(549,"文章天地");
        map.put(108,"我是女生");
        map.put(551,"西电问答");
        map.put(550,"心灵花园");
        map.put(110,"普通交易");
        map.put(217,"缘聚睿思");
        map.put(142,"失物招领");
        map.put(552,"我要毕业啦");
        map.put(560,"技术博客");
        map.put(548,"学习交流");
        map.put(216,"我爱运动");
        map.put(91,"考研交流");
        map.put(555,"就业经验交流");
        map.put(145,"软件交流");
        map.put(144,"嵌入式交流");
        map.put(152,"竞赛交流");
        map.put(147,"原创精品");
        map.put(215,"西电后街");
        map.put(125,"音乐纵贯线");
        map.put(140,"绝对漫域");

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            list.add(entry.getValue());
        }

        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(this, R.layout.spinner_item, list);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        setSupportActionBar(toolbar);
        if(toolbar!=null)
            toolbar.addView(spinner);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("请选择分区");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    CURRENT_FID = getName(i).getKey();

                }catch (Exception e){
                    e.printStackTrace();
                }

                myWebView.loadUrl(MySetting.BBS_BASE_URL+UrlUtils.getPostUrl(CURRENT_FID));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        if(MySetting.CONFIG_ISLOGIN){
            myWebView.loadUrl(MySetting.BBS_BASE_URL+ UrlUtils.getPostUrl(CURRENT_FID));
        }else {
            Toast.makeText(this,"你还没有登陆",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }



    }

    private Map.Entry<Integer,String> getName(int i){
        if(i>map.size()){
            return null;
        }

        int j =0;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {

            if(j==i){
                return entry;
            }
            j++;
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
