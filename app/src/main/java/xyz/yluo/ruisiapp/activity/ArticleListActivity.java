package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ArticleListAdapter;
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

    private static final int TYPE_NOMAL = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPA_MOBILE = 2;
    private static int CurrentFid =72;
    private static String CurrentTitle = "首页";
    private int CurrentType = TYPE_NOMAL;

    //
    private boolean isEnableLoadMore = false;

    //当前页数
    private int CurrentPage = 1;
    //一般板块/图片板块/手机板块数据列表
    private List<ArticleListData> mydatasetnormal = new ArrayList<>();
    private ArticleListAdapter mRecyleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static void open(Context context, int fid,String title){
        Intent intent = new Intent(context, ArticleListActivity.class);
        CurrentFid = fid;
        CurrentTitle = title;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if(!ConfigClass.CONFIG_IS_INNER){
            CurrentType = TYPA_MOBILE;
            //这三个板块自由电脑板
        }else if(CurrentFid==561||CurrentFid==157||CurrentFid==13){
            CurrentType =TYPE_IMAGE;
        }else{
            CurrentType =TYPE_NOMAL;
        }
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(CurrentTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //初始化
        init();
        startGetData();
    }



    //加载更多
    @Override
    public void onLoadMore() {
        if(isEnableLoadMore){
            Toast.makeText(getApplicationContext(),"加载更多被触发",Toast.LENGTH_SHORT).show();
            CurrentPage++;
            startGetData();
            isEnableLoadMore = false;
        }
    }

    //一系列初始化
    private void init() {
        //刷新
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(true);
                    }
                });
                CurrentPage = 1;
                mydatasetnormal.clear();
                mRecyleAdapter.notifyDataSetChanged();
                startGetData();
            }
        });

        //隐藏按钮
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

        //按钮监听
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
                //TODO
            }
        });


        //一般板块 和首页新帖
        if (CurrentType == TYPE_NOMAL||CurrentType == TYPA_MOBILE) {
            //72灌水区
            mLayoutManager = new LinearLayoutManager(this);
            //加载更多
            mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this,8));
        } else if (CurrentType == TYPE_IMAGE ) {
            //图片板 或者板块列表
            mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyleAdapter = new ArticleListAdapter(this, mydatasetnormal,0);
        mRecyclerView.setAdapter(mRecyleAdapter);
    }

    //获取数据
    private void startGetData() {

        String url = "forum.php?mod=forumdisplay&fid="+CurrentFid+"&page="+CurrentPage;
        if(!ConfigClass.CONFIG_IS_INNER){
            url = url + "&mobile=2";
        }

        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if(CurrentType==TYPE_NOMAL){
                    new GetNormalArticleListTaskRs(new String(responseBody)).execute();
                }else if(CurrentType==TYPE_IMAGE){
                    new GetImageArticleListTaskRS(new String(responseBody)).execute();
                }else{
                    //外网
                    new GetArticleListTaskMe(new String(responseBody)).execute();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    //校园网状态下获得一个普通板块文章列表数据 根据html获得数据
    public class GetNormalArticleListTaskRs extends AsyncTask<Void, Void, String> {

        private List<ArticleListData> dataset = new ArrayList<>();
        private String res;

        public GetNormalArticleListTaskRs(String res) {
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
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.getItemAnimator().setAddDuration(0);
            if(CurrentPage==1){
                //item 增加删除 改变动画
                mRecyclerView.setItemAnimator(new OvershootInLeftAnimator());
                mRecyclerView.getItemAnimator().setAddDuration(250);
                mRecyclerView.getItemAnimator().setRemoveDuration(10);
                mRecyclerView.getItemAnimator().setChangeDuration(10);
                mydatasetnormal.clear();
            }
            mydatasetnormal.addAll(dataset);
            mRecyclerView.setLayoutManager(mLayoutManager);
            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyItemRangeInserted(mydatasetnormal.size() - dataset.size(), dataset.size());
            isEnableLoadMore = true;
        }
    }

    //校园网状态下获得图片板块数据 图片链接、标题等  根据html获得数据
    public class GetImageArticleListTaskRS extends AsyncTask<Void, Void, String> {

        private String response;
        private List<ArticleListData> imgdatas = new ArrayList<>();

        public GetImageArticleListTaskRS(String res) {
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

            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.getItemAnimator().setAddDuration(0);
            if(CurrentPage==1){
                //item 增加删除 改变动画
                mRecyclerView.setItemAnimator(new FadeInDownAnimator());
                mRecyclerView.getItemAnimator().setAddDuration(250);
                mRecyclerView.getItemAnimator().setRemoveDuration(10);
                mRecyclerView.getItemAnimator().setChangeDuration(10);
                mydatasetnormal.clear();
            }
            mydatasetnormal.addAll(imgdatas);
            mRecyclerView.setLayoutManager(mLayoutManager);
            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyItemRangeInserted(mydatasetnormal.size()-imgdatas.size(), imgdatas.size());
            isEnableLoadMore = true;
        }
    }

    //非校园网状态下获得一个板块文章列表数据
    //根据html获得数据
    //调用的手机版
    public class GetArticleListTaskMe extends AsyncTask<Void, Void, String> {

        private List<ArticleListData> dataset = new ArrayList<>();
        private String res;

        public GetArticleListTaskMe(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            if(res!=""){
                //chiphell
                Document doc = Jsoup.parse(res);
                Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接

                ArticleListData temp;
                Elements links = body.select("li");
                System.out.print(links);
                for (Element src : links) {
                    String url = src.select("a").attr("href");
                    String author = src.select(".by").text();
                    src.select("span.by").remove();
                    String title = src.select("a").text();
                    String replyCount = src.select("span.num").text();

                    String img = src.select("img").attr("src");

                    System.out.print("\nimg>>>>>>>>>>>>>>>>>>>>>>\n"+img);
                    boolean hasImage = false;
                    if(img.contains("icon_tu.png")){
                        hasImage = true;
                    }
                    else{
                        hasImage = false;
                    }
                    //String title, String titleUrl, String image, String author, String replayCount
                    temp = new ArticleListData(hasImage,title, url, author, replyCount);
                    dataset.add(temp);
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.getItemAnimator().setAddDuration(0);

            if(CurrentPage==1){
                //item 增加删除 改变动画
                mRecyclerView.setItemAnimator(new OvershootInLeftAnimator());
                mRecyclerView.getItemAnimator().setAddDuration(250);
                mRecyclerView.getItemAnimator().setRemoveDuration(10);
                mRecyclerView.getItemAnimator().setChangeDuration(10);
                mydatasetnormal.clear();
            }

            mydatasetnormal.addAll(dataset);
            mRecyclerView.setLayoutManager(mLayoutManager);
            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyItemRangeInserted(mydatasetnormal.size() - dataset.size(), dataset.size());
            isEnableLoadMore = true;
        }
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
}
