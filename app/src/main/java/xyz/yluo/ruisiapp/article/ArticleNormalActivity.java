package xyz.yluo.ruisiapp.article;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.api.GetLevel;
import xyz.yluo.ruisiapp.api.SingleArticleData;
import xyz.yluo.ruisiapp.http.MyHttpConnection;
import xyz.yluo.ruisiapp.main.RecyclerViewLoadMoreListener;

/**
 * Created by free2 on 16-3-6.
 *
 */
public class ArticleNormalActivity extends AppCompatActivity implements RecyclerViewLoadMoreListener.OnLoadMoreListener{
    //存储数据 需要填充的列表
    //TODO 动态获取
    private List<SingleArticleData> mydatalist = new ArrayList<>();
    private static String articleUrl;
    private static String articleTitle;
    private static String replaycount;
    private static String articleauthor;
    private static String articletype;

    private MyWebView myWebView;
    private RecyclerView mRecyclerView;
    private ArticleRecycleAdapter mRecyleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fab;

    public static void open(Context context, List<String> messagelist) {
        Intent intent = new Intent(context, ArticleNormalActivity.class);
        //url|标题|回复|类型|author

        articleUrl = messagelist.get(0);
        articleTitle =  messagelist.get(1);
        replaycount =  messagelist.get(2);
        articletype =  messagelist.get(3);
        articleauthor =  messagelist.get(4);

        System.out.print("articleUrl articleTitle replaycount articletype articleauthor>>\n"+articleUrl+articleTitle+replaycount+articletype+articleauthor);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_article);


        myWebView = (MyWebView) findViewById(R.id.content_webView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.topic_recycler_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.topic_refresh_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //可以设置不同样式
        mLayoutManager = new LinearLayoutManager(this);
        //第二个参数是列数
        //mLayoutManager = new GridLayoutManager(getContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //TODO
        //以后实现，现在是静态的
        //数据填充
        System.out.print("???????????????????brfore exe\n");
        new GetSingleArticleData(articleUrl).execute((Void) null);

        mRecyleAdapter = new ArticleRecycleAdapter(this, mydatalist);
        // Set MyRecyleAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mRecyleAdapter);

        refreshLayout.setRefreshing(true);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //加载更多实现
        mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener((LinearLayoutManager) mLayoutManager,this,20));

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                //数据填充
                GetSingleArticleData singleArticleData = new GetSingleArticleData(articleUrl);
                singleArticleData.execute((Void) null);
                mydatalist.clear();
                fab.hide();
            }
        });
        //按钮监听

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
             return true;
        }else //返回按钮
            if (id == android.R.id.home) {
                finish();
                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadMore() {

    }


    //获得数据
    public class GetSingleArticleData extends AsyncTask<Void, Void, String> {

        private List<SingleArticleData> singledataslist;
        private String url;

        public GetSingleArticleData(String urlin) {
            singledataslist = new ArrayList<>();
            this.url = urlin;
            //this.url = "forum.php?mod=viewthread&tid=838333";
        }

        @Override
        protected String doInBackground(Void... params) {
            System.out.print("???????????????????in exe\n");
            String response ="";
            try {
                response = MyHttpConnection.Http_get("http://rs.xidian.edu.cn/"+url);
                //System.out.print(response);
            } catch (Exception e) {
                return "error";
            }

            if(response!=""){
                Elements list = Jsoup.parse(response).select("div[id=postlist]").select("div[id^=post_]");
                //System.out.print("################"+list.html()+"\n");

                int i =0;
                SingleArticleData temp;
                for (Element src : list) {
                    //System.out.print("******************singledataslist size*********************>>"+singledataslist.size()+"\n");
                    temp = getSingleList(src);
                    if(temp!=null){
                        singledataslist.add(temp);
                    }
                    //
                }
            }
            return response;
        }
        @Override
        protected void onPostExecute(final String res) {

            refreshLayout.setRefreshing(false);
            System.out.print(")))))))))))))))))"+mydatalist.size()+"\n");

            mydatalist.addAll(singledataslist);
            System.out.print(")))))))))))))))))"+mydatalist.size()+"\n");
            mRecyleAdapter.notifyDataSetChanged();
            fab.show();
        }
        @Override
        protected void onCancelled() {

        }

        public SingleArticleData getSingleList(Element element){
            Boolean isGetpinfen = false;
            String pinfen = "";
            Boolean isGetgold = false;
            String gold = "";
            Boolean isGetdianpin  = false;
            String dianpin = "";
            SingleArticleData listdata =null;

            //替换贴吧表情到本地
            //("static/image/smiley/tieba/","file:///android_asset/smiley/tieba/");
            for (Element temp : element.select("img[src^=static/image/smiley/tieba/]")) {
                //System.out.print("replace before------>>>>>>>>>>>"+temp+"\n");
                String imgUrl = temp.attr("src");
                String newimgurl =  imgUrl.replace("static/image/smiley/tieba/","file:///android_asset/smiley/tieba/");
                //System.out.print("replace------>>>>>>>>>>>"+imgUrl+newimgurl+"\n");
                temp.attr("src", newimgurl);
            }

            String username = element.select("div[class=pi]").select("div[class=authi]").select("a[href^=home.php?mod=space][class=xi2]").text().trim();

            //金币贴获得了金币
            gold = element.select("td[class=plc]").select("div[class=cm]").select("h3.psth.xs1").select("span").text();
            if(gold !=""){
                //获得了金币 flag = true；
                isGetgold = true;
                //System.out.print("\nyou get gold----->>>>>>\n"+gold+"<<<<<<-----\n");
            }

            //TODO 简单评分 以后加强
            pinfen = element.select("td[class=plc]").select("div.pcb").select("dl.rate").select("table").select("th.xw1").text().trim();
            if(pinfen !=""){
                //pinfenpeople = temppinfen.select("tr[id^=rate_]").text().trim();
                //获得了金币 flag = true；
                isGetpinfen = true;
                System.out.print("\nyou get pinfen----->>>>>>\n"+pinfen+"<<<<<<-----\n");
            }
            //TODO 获得了内容 处理它
            Elements content= element.select("td[class=plc]").select("div[class=pcb]").select("td[class=t_f][id^=postmessage]");


            if(username!=""&&content.html()!=""){
                String time = element.select("div[class=pi]").select("div[class=authi]").select("em[id^=authorposton]").text().trim();
                String userUrl = element.select("div[class=pi]").select("div[class=authi]").select("a[href^=home.php?mod=space][class=xi2]").attr("href").trim();
                String imgurl = element.select("td[class=pls]").select("div[class=avatar]").select("img[src^=http://rs.xidian.edu.cn/ucenter/data/avatar]").attr("src").trim();
                String usergroup = element.select("a[href$=profile][class=xi2]").text().trim();

                String level = GetLevel.getUserLevel(Integer.parseInt(usergroup));
                System.out.print("\n>>>>>>>>>>>>>>>"+username+"||>>"+userUrl+"||>>"+time+"||>>"+imgurl+"||>>"+level+"<<<<<<<<<<<<<<<<<<\n");
                // replaycount;String articleauthor;articletype;

                listdata = new SingleArticleData(articleTitle,articletype,username,userUrl,imgurl,time,level,replaycount,content.html());
                if(isGetgold){
                    listdata.isGetGold = true;
                    listdata.setGoldnum(gold);
                }if(isGetdianpin){
                    listdata.isGetDianpin =true;
                    listdata.setDianpin(dianpin);
                }if(isGetpinfen){
                    listdata.isGetpingfen = true;
                    listdata.setPingfen(pinfen);
                }

            }
            return listdata;
        }
    }
}
