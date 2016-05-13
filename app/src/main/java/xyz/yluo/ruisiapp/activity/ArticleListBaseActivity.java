package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.animation.AccelerateDecelerateInterpolator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.listener.HidingScrollListener;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-3-31.
 * 一个板块文章列表基类
 * 一般文章列表{@link ArticleListNormalActivity}
 * 图片文章列表{@link ArticleListImageActivity}
 */
public abstract class ArticleListBaseActivity extends BaseActivity
        implements LoadMoreListener.OnLoadMoreListener{

    @BindView(R.id.btn_refresh)
    protected FloatingActionButton btn_refresh;
    @BindView(R.id.main_recycler_view)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.main_refresh_layout)
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
        ButterKnife.bind(this);
        actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();
        //子类实现获取数据
        getData();
    }

    private void init(){
        btn_refresh.hide();
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
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) btn_refresh.getLayoutParams();
                int bottomMargin = lp.bottomMargin;
                int distanceToScroll = btn_refresh.getHeight() + bottomMargin;
                btn_refresh.animate().translationY(distanceToScroll).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
            }
            @Override
            public void onShow() {
                btn_refresh.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200);
            }
        });
    }

    private void prerefresh(){
        btn_refresh.hide();
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

    @OnClick(R.id.btn_refresh)
    protected void btn_refresh_click(){
        prerefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
}
