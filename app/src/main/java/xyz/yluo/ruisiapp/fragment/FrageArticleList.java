package xyz.yluo.ruisiapp.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ArticleListNormalAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.HidingScrollListener;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;


public class FrageArticleList extends BaseFragment implements LoadMoreListener.OnLoadMoreListener{
    private int FID;
    private String TITLE;
    protected SwipeRefreshLayout refreshLayout;
    FloatingActionButton btn_refresh;
    RecyclerView mRecyclerView;
    //当前页数
    int CurrentPage = 1;
    boolean isEnableLoadMore = false;
    RecyclerView.LayoutManager mLayoutManager;

    boolean isHideZhiding = false;
    //一般板块/图片板块/手机板块数据列表
    private List<ArticleListData> datas;
    private ArticleListNormalAdapter mRecyleAdapter;
    private MyDB myDB = null;

    public static FrageArticleList newInstance(int fid, String title) {
        FrageArticleList fragment = new FrageArticleList();
        Bundle args = new Bundle();
        args.putInt("FID", fid);
        args.putString("TITLE", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FID = getArguments().getInt("FID");
            TITLE = getArguments().getString("TITLE");
        }

        datas = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        initToolbar(true,TITLE);
        btn_refresh = (FloatingActionButton)mRootView.findViewById(R.id.btn);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        isHideZhiding = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean("setting_hide_zhidin", true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyleAdapter = new ArticleListNormalAdapter(getActivity(), datas, 0);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyleAdapter);

        myDB = new MyDB(getActivity(), MyDB.MODE_READ);
        //加载更多
        mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 8));
        datas.clear();
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        init();
        //子类实现获取数据
        getData();
        return mRootView;
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
                refresh();
            }
        });

        //隐藏按钮
        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) btn_refresh.getLayoutParams();
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

    private void refresh() {
        btn_refresh.hide();
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        CurrentPage = 1;
        getData();
    }


    private void getData() {
        String url = UrlUtils.getArticleListUrl(FID, CurrentPage, true);
        if (!App.IS_SCHOOL_NET) {
            url = url + UrlUtils.getArticleListUrl(FID, CurrentPage, false);
        }

        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                if (App.IS_SCHOOL_NET) {
                    new GetNormalArticleListTaskRs().execute(new String(response));
                } else {
                    //外网
                    new GetArticleListTaskMe().execute(new String(response));
                }
            }

            @Override
            public void onFailure(Throwable e) {
                //Toast.makeText(getActivity(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_toolbar_btn;
    }


    @Override
    public void onLoadMore() {
        if (isEnableLoadMore) {
            CurrentPage++;
            isEnableLoadMore = false;
            getData();

        }
    }

    private void getDataCompete(List<ArticleListData> dataset) {
        btn_refresh.show();
        if (CurrentPage == 1) {
            datas.clear();
            mRecyleAdapter.notifyDataSetChanged();
        }
        int start = datas.size();
        datas.addAll(dataset);

        mRecyleAdapter.notifyItemRangeInserted(start, dataset.size());
        isEnableLoadMore = true;

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    //校园网状态下获得一个普通板块文章列表数据 根据html获得数据
    private class GetNormalArticleListTaskRs extends AsyncTask<String, Void, List<ArticleListData>> {
        @Override
        protected List<ArticleListData> doInBackground(String... params) {
            String res = params[0];
            List<ArticleListData> dataset = new ArrayList<>();
            Elements list = Jsoup.parse(res).select("div[id=threadlist]");
            Elements links = list.select("tbody");
            ArticleListData temp;
            for (Element src : links) {
                if (src.getElementsByAttributeValue("class", "by").first() != null) {
                    String type;
                    if (src.attr("id").contains("stickthread")) {
                        type = "置顶";
                    } else if (src.select("th").attr("class").contains("lock")) {
                        type = "关闭";
                    } else if (src.select(".icn").select("a").attr("title").contains("投票")) {
                        type = "投票";
                    } else if (src.select("th").select("strong").text().length() > 0) {
                        type = "金币:" + src.select("th").select("strong").text().trim();
                    } else {
                        type = "normal";
                    }
                    Elements tempEles = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]");
                    String title = tempEles.text();
                    String titleUrl = tempEles.attr("href");
                    int titleColor = GetId.getColor(tempEles.attr("style"));

                    Log.e("style",src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").attr("style"));
                    Log.e("titleColor",titleColor+"");

                    String author = src.getElementsByAttributeValue("class", "by").first().select("a").text();
                    String authorUrl = src.getElementsByAttributeValue("class", "by").first().select("a").attr("href");
                    String time = src.getElementsByAttributeValue("class", "by").first().select("em").text().trim();
                    String viewcount = src.getElementsByAttributeValue("class", "num").select("em").text();
                    String replaycount = src.getElementsByAttributeValue("class", "num").select("a").text();

                    if(isHideZhiding&&type.equals("置顶")){
                        Log.i("article list","ignore zhidin");
                    }else {
                        if (title.length() > 0 && author.length() > 0) {
                            temp = new ArticleListData(type,title, titleUrl, author, authorUrl, time, viewcount, replaycount,titleColor);
                            dataset.add(temp);
                        }
                    }

                }
            }
            return myDB.handReadHistoryList(dataset);
        }

        @Override
        protected void onPostExecute(List<ArticleListData> dataset) {
            getDataCompete(dataset);
        }
    }

    //非校园网状态下获得一个板块文章列表数据
    //根据html获得数据
    //调用的手机版
    private class GetArticleListTaskMe extends AsyncTask<String, Void, List<ArticleListData>> {
        @Override
        protected List<ArticleListData> doInBackground(String... params) {
            //chiphell
            String res = params[0];
            List<ArticleListData> dataset = new ArrayList<>();
            Document doc = Jsoup.parse(res);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
            ArticleListData temp;
            Elements links = body.select("li");
            for (Element src : links) {
                String url = src.select("a").attr("href");
                int titleColor = GetId.getColor(src.select("a").attr("style"));
                String author = src.select(".by").text();
                src.select("span.by").remove();
                String title = src.select("a").text();
                String replyCount = src.select("span.num").text();
                String img = src.select("img").attr("src");
                boolean hasImage = img.contains("icon_tu.png");
                temp = new ArticleListData(hasImage, title, url, author, replyCount,titleColor);
                dataset.add(temp);
            }
            return myDB.handReadHistoryList(dataset);
        }

        @Override
        protected void onPostExecute(List<ArticleListData> dataset) {
            getDataCompete(dataset);
        }
    }
}
