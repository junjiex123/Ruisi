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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.BaseAdapter;
import xyz.yluo.ruisiapp.adapter.HotNewListAdapter;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.model.ArticleListData;
import xyz.yluo.ruisiapp.model.GalleryData;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.myhttp.SyncHttpClient;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-19.
 * 简单的fragment 首页第二页 展示最新的帖子等
 */
public class FrageHotNew extends BaseFragment implements LoadMoreListener.OnLoadMoreListener {

    private static final int TYPE_HOT = 0;
    private static final int TYPE_NEW = 1;

    private int currentType = 1;
    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;
    private List<GalleryData> galleryDatas = new ArrayList<>();
    private List<ArticleListData> mydataset = new ArrayList<>();
    private HotNewListAdapter adapter;
    private boolean isEnableLoadMore = false;
    private int CurrentPage = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        RadioButton b1 = (RadioButton) mRootView.findViewById(R.id.btn_reply);
        RadioButton b2 = (RadioButton) mRootView.findViewById(R.id.btn_pm);
        b1.setText("新帖");
        b2.setText("热贴");
        recycler_view = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        //设置可以滑出底栏
        recycler_view.setClipToPadding(false);
        recycler_view.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.BottomBarHeight));
        adapter = new HotNewListAdapter(getActivity(), mydataset, galleryDatas);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 10));
        refreshLayout.setOnRefreshListener(this::refresh);

        RadioGroup swictchMes = (RadioGroup) mRootView.findViewById(R.id.btn_change);
        swictchMes.setOnCheckedChangeListener((radioGroup, id) -> {
            int pos = -1;
            if (id == R.id.btn_reply) {
                pos = TYPE_NEW;
            } else {
                pos = TYPE_HOT;
            }
            if (pos != currentType) {
                currentType = pos;
                refreshLayout.setRefreshing(true);
                refresh();
            }
        });

        Handler mHandler = new Handler();
        mHandler.postDelayed(this::getData, 300);

        return mRootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_msg_hot;
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
        adapter.changeLoadMoreState(BaseAdapter.STATE_LOADING);
        if (App.IS_SCHOOL_NET) {
            new getGalleryTask().execute();
        }
        String type = (currentType == TYPE_HOT) ? "hot" : "new";
        String url = "forum.php?mod=guide&view=" + type + "&page=" + CurrentPage + "&mobile=2";
        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetNewArticleListTaskMe().execute(new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                refreshLayout.postDelayed(() -> refreshLayout.setRefreshing(false), 300);

                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_FAIL);
            }
        });
    }

    private class getGalleryTask extends AsyncTask<Void, Void, List<GalleryData>> {
        @Override
        protected List<GalleryData> doInBackground(Void... voids) {
            List<GalleryData> temp = new ArrayList<>();
            String url = "http://rs.xidian.edu.cn/forum.php";
            try {
                Document doc = Jsoup.connect(url).userAgent(SyncHttpClient.DEFAULT_USER_AGENT).get();
                Elements listgallerys = doc.select("#wp").select("ul.slideshow");
                for (Element e : listgallerys.select("li")) {
                    String title = e.text();
                    String titleurl = e.select("a").attr("href");
                    String imgurl = e.select("img").attr("src");
                    Log.e("imh", imgurl);
                    temp.add(new GalleryData(imgurl, title, titleurl));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return temp;
        }

        @Override
        protected void onPostExecute(List<GalleryData> data) {
            super.onPostExecute(data);
            if (data.size() == 0) {
                return;
            }
            if (galleryDatas.size() == 0) {
                galleryDatas.addAll(data);
            } else if (galleryDatas.size() != data.size()) {//进行了一下优化 只有不相同时才刷行
                galleryDatas.clear();
                galleryDatas.addAll(data);
            } else {
                return;
            }
            adapter.notifyItemChanged(0);
        }
    }

    private class GetNewArticleListTaskMe extends AsyncTask<String, Void, List<ArticleListData>> {
        @Override
        protected List<ArticleListData> doInBackground(String... params) {
            List<ArticleListData> dataset = new ArrayList<>();
            Document doc = Jsoup.parse(params[0]);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
            Elements links = body.select("li");
            for (Element src : links) {
                String url = src.select("a").attr("href");
                int titleColor = GetId.getColor(
                        getActivity(), src.select("a").attr("style"));
                Log.e("style", src.select("a").attr("style"));
                //Log.e("titleColor",titleColor+"");
                String author = src.select(".by").text();
                src.select("span.by").remove();
                String replyCount = src.select("span.num").text();
                src.select("span.num").remove();
                String title = src.select("a").text();
                String img = src.select("img").attr("src");
                boolean hasImage = img.contains("icon_tu.png");
                dataset.add(new ArticleListData(hasImage, title, url, author, replyCount, titleColor));
            }

            MyDB myDB = new MyDB(getActivity());
            return myDB.handReadHistoryList(dataset);
        }

        @Override
        protected void onPostExecute(List<ArticleListData> datas) {
            refreshLayout.postDelayed(() -> refreshLayout.setRefreshing(false), 300);
            if (CurrentPage == 1) {
                mydataset.clear();
                mydataset.addAll(datas);
                adapter.notifyDataSetChanged();
            } else {
                if (datas.size() == 0) {
                    adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_NOTHING);
                    return;
                } else {
                    int size = mydataset.size();
                    mydataset.addAll(datas);
                    adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_OK);
                    if (galleryDatas.size() > 0) {
                        size++;
                    }
                    adapter.notifyItemRangeInserted(size, datas.size());
                }
            }
            isEnableLoadMore = true;
        }
    }

}
