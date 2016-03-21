package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.MainArticleListAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.listener.HidingScrollListener;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.ConfigClass;

/*
 *帖子列表activity
 *
 */

public class ArticleListActivity extends AppCompatActivity
        implements LoadMoreListener.OnLoadMoreListener {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.fab_post)
    protected FloatingActionButton fab_post;
    @Bind(R.id.fab_refresh)
    protected FloatingActionButton fab_refresh;
    @Bind(R.id.fab)
    protected FloatingActionMenu fabMenu;
    @Bind(R.id.main_recycler_view)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;


    //-1 为首页
    private static int CurrentFid =72;
    private static String CurrentTitle = "首页";
    private int CurrentType = -1;
    //当前页数
    private int CurrentPage = 0;

    //一般板块/图片板块数据列表
    private List<ArticleListData> mydatasetnormal = new ArrayList<>();
    private MainArticleListAdapter mRecyleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static void open(Context context, int fid,String title){
        Intent intent = new Intent(context, ArticleListActivity.class);
        CurrentFid = fid;
        CurrentTitle = title;
        System.out.print("\n>>>>>>>>>fid: "+CurrentFid+"title: "+CurrentTitle+"<<<<<<<<<<<\n");
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //初始化
        init(CurrentFid, CurrentTitle);

        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fabMenu.getLayoutParams();
                int bottomMargin = lp.bottomMargin;
                int distanceToScroll = fabMenu.getHeight() + bottomMargin;
                fabMenu.animate().translationY(distanceToScroll).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
            }

            @Override
            public void onShow() {
                fabMenu.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
            }
        });


        //TODO 根据当前板块加载内容
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init(CurrentFid,CurrentTitle);
            }
        });

//        //按钮监听
        fabMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenu.isOpened()) {
                    //Snackbar.make(v, fabMenu.getMenuButtonLabelText(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                fabMenu.toggle(true);
            }
        });

        fab_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "fab", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), NewArticleActivity.class));
                fabMenu.toggle(true);

            }
        });
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "fab2", Toast.LENGTH_SHORT).show();
                fabMenu.toggle(true);
                init(CurrentFid,CurrentTitle);

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
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //加载更多
    @Override
    public void onLoadMore() {
        //Toast.makeText(getApplicationContext(),"加载更多被触发",Toast.LENGTH_SHORT).show();
    }

    //一系列初始化
    private void init(int fid,String title) {

        //刷新
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        //item 增加删除 改变动画
        mRecyclerView.setItemAnimator(new FadeInDownAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(150);
        mRecyclerView.getItemAnimator().setRemoveDuration(10);
        mRecyclerView.getItemAnimator().setChangeDuration(10);

        CurrentType =0;
        CurrentFid = fid;
        CurrentTitle = title;
        //摄影板块
        if(CurrentFid==561){
            CurrentType =1;
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(CurrentTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //一般板块 和首页新帖
        if (CurrentType == 0) {
            //72灌水区
            //可以设置不同样式
            mLayoutManager = new LinearLayoutManager(this);
            //第二个参数是列数
            //mLayoutManager = new GridLayoutManager( getContext(),2);
            //加载更多实现
            mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this));
        } else if (CurrentType == 1 ) {
            //图片板 或者板块列表
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }

        mydatasetnormal.clear();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyleAdapter = new MainArticleListAdapter(this, mydatasetnormal);

        startGetData();

        mRecyclerView.setAdapter(mRecyleAdapter);

    }

    private void startGetData() {
        String url = url = "forum.php?mod=forumdisplay&fid=";
            url = url + CurrentFid + "&page=" + CurrentPage;

        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //普通板块
                if (CurrentType == 0) {
                    new GetNormalListTask(new String(responseBody)).execute();
                } else if (CurrentType == 1) {
                    //TODO
                    //图片板块
                    new GetImageListTask(new String(responseBody)).execute();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    //获得一个普通板块文章列表数据 根据html获得数据
    public class GetNormalListTask extends AsyncTask<Void, Void, String> {

        private List<ArticleListData> dataset = new ArrayList<>();
        private String res;

        public GetNormalListTask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            if(res!=""){
                Elements list = Jsoup.parse(res).select("div[id=threadlist]");
                Elements links = list.select("tbody");
                //System.out.print(links);
                ArticleListData temp;
                for (Element src : links) {
                    if (src.getElementsByAttributeValue("class", "by").first() != null) {

                        String type = "normal";
                        //金币
                        if (src.select("th").select("strong").text() != "") {
                            type = "gold:" + src.select("th").select("strong").text().trim();
                        } else if (src.attr("id").contains("stickthread")) {
                            type = "zhidin";
                        } else {
                            type = "normal";
                        }
                        String title = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").text();
                        String titleUrl = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").attr("href");
                        //http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=836820&extra=page%3D1
                        String author = src.getElementsByAttributeValue("class", "by").first().select("a").text();
                        String authorUrl = src.getElementsByAttributeValue("class", "by").first().select("a").attr("href");
                        String time = src.getElementsByAttributeValue("class", "by").first().select("em").text().trim();
                        String viewcount = src.getElementsByAttributeValue("class", "num").select("em").text();
                        String replaycount = src.getElementsByAttributeValue("class", "num").select("a").text();

                        if(!ConfigClass.CONFIG_ISSHOW_ZHIDIN&&type.equals("zhidin")){
                            //do no thing
                        }else{
                            if (title != "" && author != "" && viewcount != "") {
                                //新建对象
                                temp = new ArticleListData(title, titleUrl, type, author, authorUrl, time, viewcount, replaycount);
                                dataset.add(temp);
                            }
                        }

                    }
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            mydatasetnormal.clear();
            mydatasetnormal.addAll(dataset);
            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyItemRangeInserted(0, dataset.size());
        }
    }

    //
    //获得图片板块数据 图片链接、标题等  根据html获得数据
    public class GetImageListTask extends AsyncTask<Void, Void, String> {

        private String response;
        private List<ArticleListData> imgdatas = new ArrayList<>();

        public GetImageListTask(String res) {
            this.response = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (response != "") {

                Elements list = Jsoup.parse(response).select("ul[id=waterfall]");
                Elements imagelist = list.select("li");

                for (Element tmp : imagelist) {
                    //链接不带前缀
                    //http://rs.xidian.edu.cn/
                    String img = tmp.select("img").attr("src");
                    String url = tmp.select("h3.xw0").select("a[href^=forum.php]").attr("href");
                    String title = tmp.select("h3.xw0").select("a[href^=forum.php]").text();
                    String author = tmp.select("a[href^=home.php]").text();
                    String authorurl = tmp.select("a[href^=home.php]").attr("href");
                    String like = tmp.select("div.auth").select("a[href^=forum.php]").text();
                    //String title, String titleUrl, String image, String author, String authorUrl, String viewCount
                    ArticleListData tem = new ArticleListData(title, url, img, author, authorurl, like);
                    tem.setImageCard(true);
                    imgdatas.add(tem);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String res) {
            mydatasetnormal.clear();
            mydatasetnormal.addAll(imgdatas);
            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyItemRangeInserted(0, imgdatas.size());
        }
    }

}
