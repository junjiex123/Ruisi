package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.listener.HidingScrollListener;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-3-31.
 * 一个板块文章列表基类
 * 一般文章列表{@link ArticleList}
 * 图片文章列表{@link ArticleListImage}
 */
public abstract class ArticleListBase extends BaseActivity
        implements LoadMoreListener.OnLoadMoreListener {

    static int CurrentFid = 72;
    static String CurrentTitle = "首页";
    protected SwipeRefreshLayout refreshLayout;
     FloatingActionButton btn_refresh;
    RecyclerView mRecyclerView;
    //当前页数
    int CurrentPage = 1;
    boolean isEnableLoadMore = false;
    RecyclerView.LayoutManager mLayoutManager;
    protected MyToolBar myToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        myToolBar = (MyToolBar) findViewById(R.id.myToolBar);
        btn_refresh = (FloatingActionButton) findViewById(R.id.btn_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);

        myToolBar.setBackEnable(this);
        setToolBarMenuClick(myToolBar);

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_refresh_click();
            }
        });

        init();
        //子类实现获取数据
        getData();
    }

    private void init() {
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

    private void prerefresh() {
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

    private void btn_refresh_click() {
        prerefresh();
    }


    void hide_loading_view() {
        findViewById(R.id.view_loading).setVisibility(View.GONE);
    }

}
