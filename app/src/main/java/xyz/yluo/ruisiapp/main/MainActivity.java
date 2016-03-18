package xyz.yluo.ruisiapp.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.TestActivity;
import xyz.yluo.ruisiapp.api.ArticleListData;
import xyz.yluo.ruisiapp.api.Forums;
import xyz.yluo.ruisiapp.api.MainHomeListData;
import xyz.yluo.ruisiapp.http.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.login.LoginActivity;
import xyz.yluo.ruisiapp.login.UserDakaActivity;

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
    @Bind(R.id.main_radiogroup)
    protected RadioGroup main_radiogroup;
    @Bind(R.id.main_show_zhidin)
    protected LinearLayout main_show_zhidin;

    //论坛列表
    private List<Forums> forumses= ConfigClass.getBbsForum();

    //当前index
    //-1为首页 其余对应配置文件
    private int CurrentIndex = -1;
    private int CurrentFid;
    private String CurrentName;
    private int CurrentType;
    //当前页数
    private int CurrentPage = 0;

    //在home界面时 0 代表第一页 1 代表板块列表
    private int HomeCurrentPage = 0;

    //一般板块/图片板块数据列表
    private List<ArticleListData> mydataset = new ArrayList<>();
    private MainArticleListAdapter mRecyleAdapter;
    private MainHomeListAdapter mainHomeListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //初始化
        init();

        main_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio01:
                        init();
                        break;
                    case R.id.radio02:
                        GetHomeListTask_2();
                        break;
                }

            }
        });

        //TODO 根据当前板块加载内容
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fabMenu.hideMenu(true);
                init();

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
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

        if (id == R.id.nav_00) {
            startActivity(new Intent( this,TestActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_01) {
            CurrentIndex = 0;
            init();

        } else if (id == R.id.nav_02) {
            CurrentIndex = 1;
            init();

        } else if (id == R.id.nav_03) {
            CurrentIndex = 2;
            init();

        } else if (id == R.id.nav_04) {
            CurrentIndex = 3;
            init();

        } else if (id == R.id.nav_05) {
            CurrentIndex = 4;
            init();

        }else if(id ==R.id.nav_06){

        }else if(id==R.id.nav_07) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //加载更多
    @Override
    public void onLoadMore() {
        //Toast.makeText(getApplicationContext(),"加载更多被触发",Toast.LENGTH_SHORT).show();

    }

    //一系列初始化
    private void init(){

        if(CurrentIndex==-1){
            CurrentFid = -1;
            CurrentName = "首页";
            CurrentType = -1;
            main_radiogroup.setVisibility(View.VISIBLE);
            main_show_zhidin.setVisibility(View.GONE);

        }else{
            CurrentFid = forumses.get(CurrentIndex).getFid();
            CurrentName = forumses.get(CurrentIndex).getName();
            CurrentType = forumses.get(CurrentIndex).getType();
            main_radiogroup.setVisibility(View.GONE);
            main_show_zhidin.setVisibility(View.VISIBLE);
        }

        toolbar.setTitle(CurrentName);

        //刷新
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        //一般板块
        if (CurrentType==0){
            //72灌水区
            //可以设置不同样式
            mLayoutManager = new LinearLayoutManager(this);
            //第二个参数是列数
            //mLayoutManager = new GridLayoutManager( getContext(),2);
            //加载更多实现
            mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener((LinearLayoutManager) mLayoutManager, this));
        }else if(CurrentType ==1){
            //图片板块
            //切换到摄影天地板块
            mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

            //首页
        }else if(CurrentType==-1){
            if(HomeCurrentPage==0){
                mLayoutManager = new LinearLayoutManager(this);
            }else{
                mLayoutManager = new GridLayoutManager(this,2);
            }

        }

        mydataset.clear();

        startGetData();

        //item 增加删除 改变动画
        mRecyclerView.setItemAnimator(new FadeInDownAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(150);
        mRecyclerView.getItemAnimator().setRemoveDuration(10);
        mRecyclerView.getItemAnimator().setChangeDuration(10);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyleAdapter = new MainArticleListAdapter(this,mydataset);
        mRecyclerView.setAdapter(mRecyleAdapter);
    }


    private void startGetData(){
        String url = "";

        if(CurrentType==-1&&HomeCurrentPage==0){
            url = "forum.php";
        }else if(CurrentType==-1&&HomeCurrentPage==1){
            GetHomeListTask_2();
        }
        else{
            url = "forum.php?mod=forumdisplay&fid=";
            url = url+CurrentFid+"&page="+CurrentPage;
        }

        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //普通板块
                if(CurrentType==0){
                    new GetNormalListTask(new String(responseBody)).execute();
                }else if(CurrentType==1){
                    //TODO
                    //图片板块
                    new GetImageListTask(new String(responseBody)).execute();
                }else if(CurrentType==-1){
                    System.out.print("\n>>>>in here>>>>>>>>>>>>>>>>>>>>>>>\n");
                    new GetHomeListTask_1(new String(responseBody)).execute();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"网络错误！！",Toast.LENGTH_SHORT).show();
                fabMenu.showMenu(true);
                refreshLayout.setRefreshing(false);

            }
        });

    }

    //
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
            mRecyleAdapter.notifyItemRangeInserted(0, imgdatas.size());
        }
    }


    //获取首页板块数据 最新帖子
    public class GetHomeListTask_1 extends AsyncTask<Void, Void, String>{

        private String response;
        private List<MainHomeListData> simpledatas = new ArrayList<>();

        public GetHomeListTask_1(String res) {
            this.response = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (response != "") {
                Elements list = Jsoup.parse(response).select("div[id=portal_block_314],div[id=portal_block_315]");
                Elements links = list.select("li");
                for(Element tmp:links){

                    MainHomeListData tempdata;
                    String titleurl = tmp.select("a[href^=forum.php]").attr("href").trim();
                    String title = tmp.select("a[href^=forum.php]").text();
                    //title="楼主：ansonzhang0123 回复数：0 总浏览数：0"
                    String message = tmp.select("a[href^=forum.php]").attr("title");
                    String User = message.split("\n")[0];
                    String ReplyCount = message.split("\n")[1];
                    String ViewCount = message.split("\n")[2];
                    //http://rs.xidian.edu.cn/home.php?mod=space&uid=124025
                    //String user = tmp.select("a[href^=]").text();
                    //String userurl = tmp.select("em").select("a").attr("href");

                    //去重
                    if(simpledatas.size()>0){
                        int i =0;
                        for (i =0;i<simpledatas.size();i++){
                            String have_url = simpledatas.get(i).getUrl();
                            if(have_url.equals(title)){
                                break;
                            }
                        }
                        if(i==simpledatas.size()){
                            //title,titleurl,User,ViewCount,ReplyCount
                            tempdata = new MainHomeListData(title,titleurl,User,ViewCount,ReplyCount,"100");
                            simpledatas.add(tempdata);
                        }
                    }
                    if(simpledatas.size()==0){
                        tempdata = new MainHomeListData(title,titleurl,User,ViewCount,ReplyCount,"200");
                        simpledatas.add(tempdata);
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(final String res) {
            List<MainHomeListData> mainHomeListDataList = new ArrayList<>();
            fabMenu.showMenu(true);
            refreshLayout.setRefreshing(false);
            mainHomeListDataList.addAll(simpledatas);
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mainHomeListAdapter = new MainHomeListAdapter(mainHomeListDataList,0);
            mRecyclerView.setAdapter(mainHomeListAdapter);
            mRecyleAdapter.notifyItemRangeInserted(0, simpledatas.size());
        }
    }

    //获取首页板块数据 板块列表
    private void GetHomeListTask_2(){
        List<MainHomeListData> mainHomeListDataList = new ArrayList<>();
        fabMenu.showMenu(true);
        mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mainHomeListAdapter = new MainHomeListAdapter(mainHomeListDataList,1);
        mRecyclerView.setAdapter(mainHomeListAdapter);
    }

}
