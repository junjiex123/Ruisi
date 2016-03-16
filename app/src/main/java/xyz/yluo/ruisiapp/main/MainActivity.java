package xyz.yluo.ruisiapp.main;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.about.AboutActivity;
import xyz.yluo.ruisiapp.api.ArticleListData;
import xyz.yluo.ruisiapp.article.ArticleNormalActivity;
import xyz.yluo.ruisiapp.article.NewArticleActivity;
import xyz.yluo.ruisiapp.http.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.http.MyHttpConnection;
import xyz.yluo.ruisiapp.login.LoginActivity;
import xyz.yluo.ruisiapp.TestActivity;
import xyz.yluo.ruisiapp.login.UserDakaActivity;
import xyz.yluo.ruisiapp.setting.SettingActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RecyclerViewLoadMoreListener.OnLoadMoreListener{

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.fab1)
    protected FloatingActionButton fab1;
    @Bind(R.id.fab2)
    protected FloatingActionButton fab2;
    @Bind(R.id.fab)
    protected FloatingActionMenu fabMenu;
    @Bind(R.id.main_recycler_view)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    @Bind(R.id.drawer_layout)
    protected DrawerLayout drawer;
    @Bind(R.id.nav_view)
    protected NavigationView navigationView;

    //TODO
    //当前板块
    private int CurrentFid = 72;
    //当前页数
    private int CurrentPage = 0;
    //列表数据填充
    private List<ArticleListData> mydataset = new ArrayList<>();
    private RecycleViewAdapter mRecyleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //初始化
        init(CurrentFid);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO 根据当前板块加载内容
                fabMenu.hideMenu(true);
                String url = "forum.php?mod=forumdisplay&fid=";

                startGetData(72, 0);

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

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"fab1",Toast.LENGTH_SHORT).show();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"fab2",Toast.LENGTH_SHORT).show();
            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        View nav_header_login = header.findViewById(R.id.nav_header_login);
        View nav_header_notlogin = header.findViewById(R.id.nav_header_notlogin);
        //判断是否登陆
        if(ConfigClass.CONFIG_ISLOGIN){
            nav_header_login.setVisibility(View.VISIBLE);
            nav_header_notlogin.setVisibility(View.GONE);
        }else{
            nav_header_notlogin.setVisibility(View.VISIBLE);
            nav_header_login.setVisibility(View.GONE);
        }

        CircleImageView userImge  = (CircleImageView) header.findViewById(R.id.profile_image);
        userImge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConfigClass.CONFIG_ISLOGIN){
                    startActivity(new Intent(getApplicationContext(), UserDakaActivity.class));

                }else{
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(i,1);
                }
            }
        });

    }
    //登陆页面返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
            Toast.makeText(getApplicationContext(),"result"+result,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent( this,TestActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

                startActivity(new Intent( this,NewArticleActivity.class));
            //drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent( this,SettingActivity.class));

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(this, ArticleNormalActivity.class));

        }else if(id ==R.id.nav_about){
              startActivity(new Intent(this, AboutActivity.class));
        }else if(id==R.id.nav_sytd) {
            //摄影天地
            init(561);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //加载更多
    @Override
    public void onLoadMore() {
        Toast.makeText(getApplicationContext(),"加载更多被触发",Toast.LENGTH_SHORT).show();

    }


    private void startGetData(final int fid1,int page){
        String url = "forum.php?mod=forumdisplay&fid=";
        url = url+fid1+"&page="+page;

        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //普通板块
                if(fid1==72){
                    new GetListTask(new String(responseBody)).execute();
                }else{
                    //TODO
                    //图片板块
                    new GetImageUrlList(new String(responseBody)).execute();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"网络错误！！",Toast.LENGTH_SHORT).show();
                fabMenu.showMenu(true);
                refreshLayout.setRefreshing(false);
                mRecyleAdapter.notifyDataSetChanged();
            }
        });

    }


    //
    //获得一个普通板块文章列表数据 根据html获得数据
    public class GetListTask extends AsyncTask<Void, Void, String> {

        private List<ArticleListData> dataset = new ArrayList<>();
        private String res;
        public GetListTask(String res) {
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
                    if(src.getElementsByAttributeValue("class", "by").first()!=null) {

                        String type = "normal";
                        //金币
                        if(src.select("th").select("strong").text()!=""){
                            type = "gold:"+src.select("th").select("strong").text();
                        }else if(src.attr("id").contains("stickthread")){
                            type = "zhidin";
                        }else{
                            type = "normal";
                        }
                        String title = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").text();
                        String titleUrl = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").attr("href");
                        //http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=836820&extra=page%3D1
                        String author = src.getElementsByAttributeValue("class", "by").first().select("a").text();
                        String authorUrl = src.getElementsByAttributeValue("class", "by").first().select("a").attr("href");
                        String time = src.getElementsByAttributeValue("class", "by").first().select("em").text().trim();
                        String viewcount = src.getElementsByAttributeValue("class","num").select("em").text();
                        String replaycount = src.getElementsByAttributeValue("class","num").select("a").text();

                        if(title!=""&&author!=""&&viewcount!=""){
                            //新建对象
                            temp = new ArticleListData(title,titleUrl,type,author,authorUrl,time,viewcount,replaycount);
                            dataset.add(temp);
                        }

                    }
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            mydataset.clear();
            mydataset.addAll(dataset);
            fabMenu.showMenu(true);
            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyDataSetChanged();
        }
    }

    //
    //获得图片板块数据 图片链接、标题等  根据html获得数据
    public class GetImageUrlList extends AsyncTask<Void, Void, String> {

        private String response;
        private List<ArticleListData> imgdatas = new ArrayList<>();

        public GetImageUrlList(String res) {
            this.response = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (response != "") {

                Elements list = Jsoup.parse(response).select("ul[id=waterfall]");
                Elements imagelist = list.select("li");

                for(Element tmp:imagelist){
                    //链接不带前缀
                    //http://rs.xidian.edu.cn/
                    String  img= tmp.select("img").attr("src");
                    String url = tmp.select("h3.xw0").select("a[href^=forum.php]").attr("href");
                    String  title=tmp.select("h3.xw0").select("a[href^=forum.php]").text();
                    String author = tmp.select("a[href^=home.php]").text();
                    String authorurl = tmp.select("a[href^=home.php]").attr("href");
                    String like = tmp.select("div.auth").select("a[href^=forum.php]").text();
                    //String title, String titleUrl, String image, String author, String authorUrl, String viewCount
                    ArticleListData tem = new ArticleListData(title,url,img,author,authorurl,like);
                    tem.setImageCard(true);
                    imgdatas.add(tem);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String res) {

            mydataset.clear();
            mydataset.addAll(imgdatas);
            fabMenu.showMenu(true);
            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyDataSetChanged();
        }
    }

    //一系列初始化
    private void init(int Fid){
        //刷新
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        mydataset.clear();

        if (Fid==72){
            //72灌水区
            //可以设置不同样式
            mLayoutManager = new LinearLayoutManager(this);
            //第二个参数是列数
            //mLayoutManager = new GridLayoutManager( getContext(),2);
            //加载更多实现
            mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener((LinearLayoutManager) mLayoutManager, this));

        }else{
            //切换到摄影天地板块
            mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        }

        startGetData(Fid,0);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyleAdapter = new RecycleViewAdapter(this,mydataset);
        mRecyclerView.setAdapter(mRecyleAdapter);
    }

}
