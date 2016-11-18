package xyz.yluo.ruisiapp.fragment;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.BaseAdapter;
import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.model.ArticleListData;
import xyz.yluo.ruisiapp.model.FrageType;
import xyz.yluo.ruisiapp.model.ListType;
import xyz.yluo.ruisiapp.model.SimpleListData;

/**
 * Created by free2 on 16-7-14.
 * 收藏/主题/历史纪录
 * //todo 删除浏览历史
 */
public class FrageTopicStarHistory extends BaseFragment implements LoadMoreListener.OnLoadMoreListener {

    private List<SimpleListData> datas;
    private SimpleListAdapter adapter;
    private int CurrentPage = 1;
    private boolean isEnableLoadMore = true;
    private boolean isHaveMore = true;
    private int currentIndex = 0;
    private String title = "";

    private String url;

    public static FrageTopicStarHistory newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        FrageTopicStarHistory fragment = new FrageTopicStarHistory();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            int type = bundle.getInt("type", -1);
            switch (type) {
                case FrageType.TOPIC:
                    currentIndex = 0;
                    title = "我的帖子";
                    break;
                case FrageType.START:
                    currentIndex = 1;
                    title = "我的收藏";
                    break;
                default:
                    title = "浏览历史";
                    currentIndex = 2;
            }
        }
        initToolbar(true, title);
        if (currentIndex == 2)
            addToolbarMenu(R.drawable.ic_delete_24dp).setOnClickListener(view -> {
                Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                        setTitle("清空历史记录")
                        .setMessage("你确定要清空浏览历史吗？？")
                        .setPositiveButton("是的(=・ω・=)", (dialogInterface, i) -> {
                            MyDB db = new MyDB(getActivity());
                            db.clearHistory();
                            datas.clear();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "浏览历史已清空~~", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("取消", null)
                        .setCancelable(true)
                        .create();
                alertDialog.show();
            });
        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        refreshLayout.setEnabled(false);
        String uid = App.getUid(getActivity());
        switch (currentIndex) {
            case 0:
                //主题
                //    actionBar.setTitle("我的帖子");
                url = "home.php?mod=space&uid=" + uid + "&do=thread&view=me&mobile=2";
                break;
            case 1:
                //我的收藏
                //   actionBar.setTitle("我的收藏");
                url = "home.php?mod=space&uid=" + uid + "&do=favorite&view=me&type=thread&mobile=2";
                break;
        }

        datas = new ArrayList<>();
        adapter = new SimpleListAdapter(ListType.ARTICLE, getActivity(), datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) layoutManager, this, 10));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        refresh();
        return mRootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_toolbar;
    }


    @Override
    public void onLoadMore() {
        if (isEnableLoadMore && isHaveMore) {
            CurrentPage++;
            getWebDatas();
            adapter.changeLoadMoreState(BaseAdapter.STATE_LOADING);
            isEnableLoadMore = false;
        }
    }

    private void refresh() {
        datas.clear();
        adapter.notifyDataSetChanged();
        if (currentIndex == 2) {
            getDbData();
        } else {
            getWebDatas();
        }

    }


    private void getWebDatas() {
        String newurl = url + "&page=" + CurrentPage;
        HttpUtil.get(getActivity(), newurl, new ResponseHandler() {
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
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_FAIL);
            }
        });
    }

    private void getDbData() {
        isEnableLoadMore = false;
        new GetUserHistoryTask().execute(1);
    }

    //获得主题
    private class GetUserArticles extends AsyncTask<String, Void, List<SimpleListData>> {
        @Override
        protected List<SimpleListData> doInBackground(String... strings) {
            String res = strings[0];
            List<SimpleListData> temp = new ArrayList<>();
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            for (Element tmp : lists) {
                String title = tmp.select("a").text();
                if (title.isEmpty()) {
                    isHaveMore = false;
                    break;
                }
                String titleUrl = tmp.select("a").attr("href");
                String num = tmp.select(".num").text();
                temp.add(new SimpleListData(title, num, titleUrl));
            }

            if (temp.size() % 10 != 0) {
                isHaveMore = false;
            }
            return temp;
        }

        @Override
        protected void onPostExecute(List<SimpleListData> aVoid) {
            onLoadCompete(aVoid);
        }

    }

    //获得用户收藏
    private class GetUserStarTask extends AsyncTask<String, Void, List<SimpleListData>> {
        @Override
        protected List<SimpleListData> doInBackground(String... params) {
            String res = params[0];
            List<SimpleListData> temp = new ArrayList<>();
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            for (Element tmp : lists) {
                String key = tmp.select("a").text();
                if (key.isEmpty()) {
                    isHaveMore = false;
                    break;
                }
                String link = tmp.select("a").attr("href");
                temp.add(new SimpleListData(key, "", link));
            }
            if (temp.size() % 10 != 0) {
                isHaveMore = false;
            }
            return temp;
        }

        @Override
        protected void onPostExecute(List<SimpleListData> data) {
            super.onPostExecute(data);
            onLoadCompete(data);
        }
    }


    //获得历史记录
    private class GetUserHistoryTask extends AsyncTask<Integer, Void, List<SimpleListData>> {

        @Override
        protected List<SimpleListData> doInBackground(Integer... ints) {
            List<SimpleListData> temp = new ArrayList<>();
            MyDB myDB = new MyDB(getActivity());
            for (ArticleListData data : myDB.getHistory(30)) {
                temp.add(new SimpleListData(data.title, data.author, "tid=" + data.titleUrl));
            }
            return temp;
        }

        @Override
        protected void onPostExecute(List<SimpleListData> data) {
            super.onPostExecute(data);
            isHaveMore = false;
            onLoadCompete(data);
        }
    }

    //加载完成
    private void onLoadCompete(List<SimpleListData> d) {
        if (isHaveMore && d.size() > 0) {
            adapter.changeLoadMoreState(BaseAdapter.STATE_LOADING);
        } else {
            adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_NOTHING);
        }
        if (d.size() > 0) {
            int i = datas.size();
            datas.addAll(d);
            if (i == 0) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRangeInserted(i, d.size());
            }

        }
        isEnableLoadMore = true;
    }
}
