package xyz.yluo.ruisiapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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
public class ActivityFriend extends BaseActivity{

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    private FriendAdapter adapter;
    private List<FriendData> datas;
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.fragment_simple_list);
        ButterKnife.bind(this);

        datas = new ArrayList<>();
        adapter = new FriendAdapter(datas,this);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        String url = "home.php?mod=space&do=friend&mobile=2";
        new GetDataTask(url,1).execute();
        refreshLayout.setEnabled(false);
    }

    private class GetDataTask extends AsyncTask<Void,Void,String>{
        private String url;
        private int page;
        public GetDataTask(String url,int page) {
            this.page = page;
            this.url = url;
        }
        @Override
        protected String doInBackground(Void... voids) {
            HttpUtil.SyncGet(getApplicationContext(), url, new TextResponseHandler() {
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
