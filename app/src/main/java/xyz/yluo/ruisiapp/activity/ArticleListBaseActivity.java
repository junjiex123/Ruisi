package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jude.swipbackhelper.SwipeBackHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.listener.HidingScrollListener;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-3-31.
 * 文章列表基类
 * 一般文章列表
 * 图片文章列表都继承这个类
 */
public abstract class ArticleListBaseActivity extends AppCompatActivity
        implements LoadMoreListener.OnLoadMoreListener{

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.fab)
    protected FloatingActionMenu fabMenu;
    @Bind(R.id.main_recycler_view)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;

    protected static int CurrentFid =72;
    protected static String CurrentTitle = "首页";
    //当前页数
    protected int CurrentPage = 1;
    protected boolean isEnableLoadMore = false;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        SwipeBackHelper.onCreate(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
        //子类实现获取数据
        getData();
    }

    private void init(){
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
                prerefresh();
            }
        });

        //隐藏按钮
        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                if (fabMenu.isOpened()){
                    fabMenu.close(true);
                }
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
    }

    private void prerefresh(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        refresh();
    }
    protected abstract void refresh();
    protected abstract void getData();

    @OnClick(R.id.fab_post)
    protected void fab_post_click(){
        startActivity(new Intent(getApplicationContext(), NewArticleActivity_2.class));
        fabMenu.toggle(true);
    }
    @OnClick(R.id.fab_refresh)
    protected void fab_refresh_click(){
        fabMenu.toggle(true);
        prerefresh();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_setting) {
            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id==R.id.new_topic){
            startActivity(new Intent(getApplicationContext(),NewArticleActivity_2.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
