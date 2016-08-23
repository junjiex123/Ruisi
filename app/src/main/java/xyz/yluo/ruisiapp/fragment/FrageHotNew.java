package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.HotNewListAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.data.GalleryData;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.SyncHttpClient;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-19.
 * 简单的fragment 首页第二页 展示最新的帖子等
 */
public class FrageHotNew extends BaseFragment implements LoadMoreListener.OnLoadMoreListener {

    public static final String TAG = FrageHotNew.class.getSimpleName();
    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;
    private List<GalleryData> galleryDatas = new ArrayList<>();
    private List<ArticleListData> mydataset = new ArrayList<>();
    private HotNewListAdapter adapter;
    private boolean isEnableLoadMore = false;
    private int CurrentPage = 1;

    public static FrageHotNew newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type",type);
        FrageHotNew fragment = new FrageHotNew();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        recycler_view = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);

        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);

        adapter = new HotNewListAdapter(getActivity(), mydataset,galleryDatas);


        recycler_view.setAdapter(adapter);
        recycler_view.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 10));

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

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 300);

        return mRootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frage_hot_new_list;
    }

    @Override
    protected String getTitle() {
        return "看贴";
    }

    private void refresh() {
        CurrentPage = 1;
        isEnableLoadMore = false;
        getData();

    }

    @Override
    public void onLoadMore() {
        if (isEnableLoadMore) {
            CurrentPage++;
            getData();
            isEnableLoadMore = false;
        }
    }

    private void getData() {
        if (App.IS_SCHOOL_NET && galleryDatas.size() == 0) {
            new getGalleryTask().execute();
        }
        String url = "forum.php?mod=guide&view=new&page=" + CurrentPage + "&mobile=2";
        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetNewArticleListTaskMe().execute(new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 500);

            }
        });
    }

    private class getGalleryTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            galleryDatas.clear();
            Log.i("gallery", "=====gallery=====");
            String url = "http://rs.xidian.edu.cn/forum.php";
            try {
                Document doc = Jsoup.connect(url).userAgent(SyncHttpClient.DEFAULT_USER_AGENT).get();
                Elements listgallerys = doc.select("#wp").select("ul.slideshow");
                for (Element e : listgallerys.select("li")) {
                    String title = e.text();
                    String titleurl = e.select("a").attr("href");
                    String imgurl = e.select("img").attr("src");
                    Log.i("gallery", title + titleurl + imgurl);
                    galleryDatas.add(new GalleryData(imgurl, title, titleurl));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyItemChanged(0);
        }

    }

    //非校园网状态下获得一个板块文章列表数据
    //根据html获得数据
    //调用的手机版
    private class GetNewArticleListTaskMe extends AsyncTask<String, Void, List<ArticleListData>> {
        @Override
        protected List<ArticleListData> doInBackground(String... params) {
            List<ArticleListData> dataset = new ArrayList<>();
            Document doc = Jsoup.parse(params[0]);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
            ArticleListData temp;
            Elements links = body.select("li");
            for (Element src : links) {
                String url = src.select("a").attr("href");
                int titleColor = GetId.getColor(src.select("a").attr("style"));
                Log.e("style",src.select("a").attr("style"));
                //Log.e("titleColor",titleColor+"");
                String author = src.select(".by").text();
                src.select("span.by").remove();
                String replyCount = src.select("span.num").text();
                src.select("span.num").remove();
                String title = src.select("a").text();
                String img = src.select("img").attr("src");
                boolean hasImage = img.contains("icon_tu.png");
                temp = new ArticleListData(hasImage, title, url, author, replyCount,titleColor);
                dataset.add(temp);
            }

            MyDB myDB = new MyDB(getActivity(), MyDB.MODE_READ);
            return myDB.handReadHistoryList(dataset);
        }

        @Override
        protected void onPostExecute(List<ArticleListData> dataset) {
            if (CurrentPage == 1) {
                //item 增加删除 改变动画
                mydataset.clear();
            }
            int size = mydataset.size();
            mydataset.addAll(dataset);
            if (size > 0) {
                adapter.notifyItemChanged(size);
                adapter.notifyItemRangeInserted(size + 1, dataset.size());
            } else {
                adapter.notifyDataSetChanged();
            }
            isEnableLoadMore = true;

            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);
        }
    }

}
