package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.FriendAdapter;
import xyz.yluo.ruisiapp.data.FriendData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-4-12.
 * 好友列表
 * 数据{@link FriendData}
 * adapter{@link FriendAdapter}
 */
public class FrageFriends extends BaseFragment implements LoadMoreListener.OnLoadMoreListener{
    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;
    private FriendAdapter adapter;
    private List<FriendData> datas;
    private int CurrentPage = 1;
    private int TotalPage = Integer.MAX_VALUE;
    private boolean isEnableLoadMore = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        initToolbar(true,"我的好友");
        recycler_view = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        datas = new ArrayList<>();
        adapter = new FriendAdapter(datas, getActivity());
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(lm);
        recycler_view.addOnScrollListener(new LoadMoreListener(lm, this, 12));
        recycler_view.setAdapter(adapter);

        final String url = "home.php?mod=space&do=friend&mobile=2";

        refreshLayout.setEnabled(false);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        new GetDataTask().execute(url);

        return mRootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_toolbar;
    }


    @Override
    public void onLoadMore() {
        //加载更多被电击
        if (isEnableLoadMore) {
            Log.e("loadmore","loadmore");
            isEnableLoadMore = false;
            CurrentPage = CurrentPage+1;
            if(CurrentPage>1&&CurrentPage<=TotalPage){
                String url = "home.php?mod=space&do=friend&mobile=2"+"&page="+CurrentPage;
                new GetDataTask().execute(url);
                Log.e("loadmore",""+CurrentPage);
            }
        }
    }


    private class GetDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpUtil.SyncGet(getActivity(), params[0], new TextResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Document document = Jsoup.parse(response);
                    Elements lists = document.select("#friend_ul").select("li");
                    if(lists.size()<=0){
                        CurrentPage = CurrentPage-1;
                        TotalPage = CurrentPage;
                        Log.e("好友","暂无更多，总页数"+TotalPage);
                    }else{
                        for (Element element : lists) {
                            String imgurl = element.select(".avt").select("img").attr("src");
                            String lastOnline = element.select(".avt").select(".gol").attr("title");
                            String userName = element.select("h4").select("a[href^=home.php?mod=space&uid=]").text();
                            String uid = GetId.getid("uid=",imgurl);
                            String info = element.select("p.maxh").text();
                            //userName,imgUrl,info,uid,lastOnlineTime
                            datas.add(new FriendData(userName, imgurl, info, uid, lastOnline));
                        }
                    }

                }

                @Override
                public void onFailure(Throwable e) {
                    super.onFailure(e);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            adapter.notifyDataSetChanged();
            super.onPostExecute(s);
            isEnableLoadMore = true;
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            },500);
        }
    }
}
