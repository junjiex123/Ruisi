package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-4-12.
 * 好友列表
 * 数据{@link FriendData}
 * adapter{@link FriendAdapter}
 */
public class FrageFriends extends Fragment {
    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;
    private FriendAdapter adapter;
    private List<FriendData> datas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list_view, container, false);
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        datas = new ArrayList<>();
        adapter = new FriendAdapter(datas,getActivity());
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_view.setAdapter(adapter);

        final String url = "home.php?mod=space&do=friend&mobile=2";

        refreshLayout.setEnabled(false);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new GetDataTask().execute(url);
            }
        }, 800);

        return view;
    }


    private class GetDataTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            HttpUtil.SyncGet(getActivity(), params[0], new TextResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Document document = Jsoup.parse(response);
                    Elements lists = document.select("#friend_ul").select("li");
                    for(Element element:lists){
                        String imgurl = element.select(".avt").select("img").attr("src");
                        String lastOnline = element.select(".avt").select(".gol").attr("title");
                        String userName = element.select("h4").select("a[href^=home.php?mod=space&uid=]").text();
                        String uid = GetId.getUid(imgurl);
                        String info = element.select("p.maxh").text();
                        //userName,imgUrl,info,uid,lastOnlineTime
                        datas.add(new FriendData(userName,imgurl,info,uid,lastOnline));
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
        }
    }
}
