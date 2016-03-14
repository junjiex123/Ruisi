package xyz.yluo.ruisiapp.main;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.about.AboutActivity;
import xyz.yluo.ruisiapp.api.ArticleListData;
import xyz.yluo.ruisiapp.article.ArticleNormalActivity;
import xyz.yluo.ruisiapp.article.NewArticleActivity;
import xyz.yluo.ruisiapp.http.MyHttpConnection;
import xyz.yluo.ruisiapp.login.Activity_Login;
import xyz.yluo.ruisiapp.login.Login_Dialog_Fragment;
import xyz.yluo.ruisiapp.setting.SettingActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RecyclerViewLoadMoreListener.OnLoadMoreListener,
        Login_Dialog_Fragment.LoginDialogListener{

    //当前板块
    //TODO
    private final int INDEX = 0;

    private RecyclerView mRecyclerView;
    private RecycleViewAdapter mRecyleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fab1,fab2;
    private DrawerLayout drawer;

    //存储数据 需要填充的列表
    //TODO 动态获取
    //普通文章列表
    private List<ArticleListData> mydataset;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fabMenu = (FloatingActionMenu) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_refresh_layout);

        setSupportActionBar(toolbar);

        //recylerView 用来替代listView
        mRecyclerView = (RecyclerView)findViewById(R.id.main_recycler_view);
        //可以设置不同样式
        mLayoutManager = new LinearLayoutManager(this);
        //第二个参数是列数
        //mLayoutManager = new GridLayoutManager(getContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //TODO
        //获取数据以后实现，现在是静态的
        mydataset = new ArrayList<>();

        GetListTask getListTask = new GetListTask("72",0);
        getListTask.execute((Void) null);

        mRecyleAdapter = new RecycleViewAdapter(this,mydataset);
        // Set MyRecyleAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mRecyleAdapter);

        //开始刷新
        refreshLayout.setRefreshing(true);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //加载更多实现
        mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener((LinearLayoutManager) mLayoutManager,this,20));

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mydataset.clear();

                fabMenu.hideMenu(true);

                GetListTask getListTask = new GetListTask("72",0);
                getListTask.execute((Void) null);
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


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

                }else{
                    DialogFragment newFragment = new Login_Dialog_Fragment();
                    newFragment.show(getFragmentManager(),"logindialog");
                }
            }
        });

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
            startActivity(new Intent( this,Activity_Login.class));
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
            //切换到摄影天地板块
            //TODO
            RecyclerView.LayoutManager mnewLayoutManager;
            //new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            mnewLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mnewLayoutManager);
            //TDOO 改变数据
            //刷新
            mydataset.clear();
            GetImageUrlList getImageUrlList = new GetImageUrlList("http://rs.xidian.edu.cn/forum.php?mod=forumdisplay&fid=561");
            getImageUrlList.execute((Void) null);

            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyDataSetChanged();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //加载更多
    @Override
    public void onLoadMore() {

    }


    //注册弹窗点击事件
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }




    //获得一个普通板块文章列表数据
    public class GetListTask extends AsyncTask<Void, Void, String> {
        private String Baseurl = "http://rs.xidian.edu.cn/forum.php?mod=forumdisplay&fid=";
        private String fullurl = "";
        private List<ArticleListData> dataset;

        public GetListTask(String fid,int page) {

            dataset = new ArrayList<>();

            if(page==0){
                fullurl = Baseurl+fid;
            }else{
                fullurl = Baseurl+fid+"&page="+page;
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String response ="";
            try {
                response = MyHttpConnection.Http_get(fullurl);
            } catch (Exception e) {
                return "error";
            }

            StringBuffer buffer=new StringBuffer();
            if(response!=""){
                Elements list = Jsoup.parse(response).select("div[id=threadlist]");
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
                            buffer.append(type).append(title).append(titleUrl).append(author).append(authorUrl).append(time).append(viewcount).append("\n");
                        }

                    }
                }
                System.out.print(buffer);
            }
            return response;
        }

        @Override
        protected void onPostExecute(final String res) {
            mydataset.addAll(dataset);

            fabMenu.showMenu(true);

            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {

        }
    }

    //获得图片板块数据 图片链接、标题等
    public class GetImageUrlList extends AsyncTask<Void, Void, String> {
        private String url;

        public GetImageUrlList(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {

            String response = "";
            try {
                response = MyHttpConnection.Http_get(url);
            } catch (Exception e) {
                return "error";
            }
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

                    System.out.print("!!!!!!!!!!!!!!!!!!!!"+like);
                    //String title, String titleUrl, String image, String author, String authorUrl, String viewCount
                    ArticleListData tem = new ArticleListData(title,url,img,author,authorurl,like);
                    tem.setImageCard(true);

                    mydataset.add(tem);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String res) {

            fabMenu.showMenu(true);

            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyDataSetChanged();
        }
    }
}
