package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.data.FrageType;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.SimpleListData;
import xyz.yluo.ruisiapp.database.MyDbUtils;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-7-14.
 */
public class FrageTopicStarHistory extends Fragment implements LoadMoreListener.OnLoadMoreListener {

    protected SwipeRefreshLayout refreshLayout;
    private List<SimpleListData> datas;
    private SimpleListAdapter adapter;
    private int CurrentPage = 0;
    private boolean isEnableLoadMore = true;
    private boolean isHaveMore = true;

    private int currentIndex = 0;

    private String url;

    public FrageTopicStarHistory() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list_view, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);

        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            int type = bundle.getInt("type", -1);

            Log.i("type is", "==" + type + "==");
            switch (type) {
                case FrageType.TOPIC:
                    currentIndex = 0;
                    break;
                case FrageType.START:
                    currentIndex = 1;
                    break;
                default:
                    currentIndex = 2;
            }
        }

//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar!=null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        String uid = PublicData.USER_UID;
        switch (currentIndex) {
            case 0:
                //主题
                //if(actionBar!=null)
                //    actionBar.setTitle("我的帖子");
                url = "home.php?mod=space&uid=" + uid + "&do=thread&view=me&mobile=2";
                break;
            case 1:
                //我的收藏
                //if(actionBar!=null)
                //   actionBar.setTitle("我的收藏");
                url = "home.php?mod=space&uid=" + uid + "&do=favorite&view=me&type=thread&mobile=2";
                break;
            default:

                isEnableLoadMore = false;
                //if(actionBar!=null)
                //    actionBar.setTitle("历史纪录");
                break;
        }

        datas = new ArrayList<>();
        adapter = new SimpleListAdapter(ListType.ARTICLE, getActivity(), datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) layoutManager, this, 10));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();
        return view;
    }

    @Override
    public void onLoadMore() {
        if (isEnableLoadMore && isHaveMore) {
            int a = CurrentPage;
            String newurl = url + "&page=" + (a + 1);
            getStringFromInternet(newurl);
            isEnableLoadMore = false;
        }
    }

    private void refresh() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        datas.clear();
        adapter.notifyDataSetChanged();
        getStringFromInternet(url);
    }

    private void getStringFromInternet(String url) {

        if (currentIndex == 2) {
            //datas.add()
            MyDbUtils myDbUtils = new MyDbUtils(getActivity(), true);
            for (ArticleListData data : myDbUtils.getHistory(30)) {

                //Log.i("history",data.getTitleUrl());
                datas.add(new SimpleListData(data.getTitle(), data.getAuthor(), "tid=" + data.getTitleUrl()));
            }
            datas.add(new SimpleListData("暂无更多", "", ""));
            adapter.notifyDataSetChanged();
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);
            return;
        }


        HttpUtil.get(getActivity(), url, new ResponseHandler() {

            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if (currentIndex == 0) {
                    new GetUserArticles().execute(res);
                } else if (currentIndex == 1) {
                    new GetUserStarTask().execute(res);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    //获得主题
    private class GetUserArticles extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String res = strings[0];
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            for (Element tmp : lists) {
                String title = tmp.select("a").text();
                if (title.isEmpty()) {
                    datas.add(new SimpleListData("暂无更多", "", ""));
                    isHaveMore = false;
                    break;
                }
                String titleUrl = tmp.select("a").attr("href");
                String num = tmp.select(".num").text();
                datas.add(new SimpleListData(title, num, titleUrl));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isEnableLoadMore = true;
            CurrentPage++;
            adapter.notifyDataSetChanged();
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);
        }

    }

    //获得用户收藏
    private class GetUserStarTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String res = params[0];
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            for (Element tmp : lists) {
                String key = tmp.select("a").text();
                if (key.isEmpty()) {
                    datas.add(new SimpleListData("暂无更多", "", ""));
                    isHaveMore = false;
                    break;
                }
                String link = tmp.select("a").attr("href");
                datas.add(new SimpleListData(key, "", link));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            isEnableLoadMore = true;
            CurrentPage++;
            adapter.notifyDataSetChanged();
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);
        }
    }
}
