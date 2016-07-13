package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyHtmlTextView;
import xyz.yluo.ruisiapp.utils.RequestOpenBrowser;

/**
 * Created by free2 on 16-3-6.
 * 单篇文章activity
 * 一楼是楼主
 * 其余是评论
 */
public class SingleNewsActivity extends BaseActivity{

    protected SwipeRefreshLayout refreshLayout;
    private WebView webView;
    private String Url= "";

    private static final String HTML_H="<!DOCTYPE HTML>\n" + "<html>\n" + "<body>\n";

    private static final String HTML_T = "</body>\n"+ "</html>" ;

    public static void open(Context context, String url,String title) {
        Intent intent = new Intent(context, SingleNewsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url",url);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_single);

        //http://jwc.xidian.edu.cn/info/1070/4428.htm
        //info/1070/4438.htm
        Url =  "http://jwc.xidian.edu.cn/"+getIntent().getExtras().getString("url");
        String title = getIntent().getExtras().getString("title");
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        webView = (WebView) findViewById(R.id.webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        getData();

    }

    private void getData(){
        new GetDataTask().execute();
    }


    private void refresh(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        getData();
    }


    private class GetDataTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            Document document = null;
            try {
                document = Jsoup.connect(Url).timeout(5000).get();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }

            Elements article =  document.select("table.winstyle49757");
            Elements patchs = article.select("span.attachfont49757").select("span").select("a");
            String main_content = article.select("#vsb_newscontent").html();
            //todo 处理附件
            for(Element e:patchs){
                String url =  e.attr("href");
                //../../system/_content/download.jsp?urltype=news.DownloadAttachUrl&owner=1070628979&wbfileid=496161
                //http://jwc.xidian.edu.cn/system/_content/download.jsp?urltype=news.DownloadAttachUrl&owner=1070628979&wbfileid=496161
                String name = e.text();
                System.out.println(name+" "+url);
            }

            return main_content;
        }

        @Override
        protected void onPostExecute(String  dataStr) {

            String data = HTML_H+dataStr+HTML_T;
            webView.loadDataWithBaseURL("http://jwc.xidian.edu.cn/",data,"type/html","utf-8",null);

            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            },400);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_broswer:
                RequestOpenBrowser.openBroswer(this,Url);
                break;
            case R.id.menu_refresh:
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
