package xyz.yluo.ruisiapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.SimpleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-5-3.
 * 我的主题activity
 */
public class ActivityMyTopicStar extends BaseActivity implements LoadMoreListener.OnLoadMoreListener{
    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    protected SwipeRefreshLayout refreshLayout;

    private List<SimpleListData> datas;
    private SimpleListAdapter adapter;
    private int CurrentPage = 0;
    private boolean isEnableLoadMore = true;
    private boolean isHaveMore = true;
    private int currentIndex = 0;

    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list_view);
        ButterKnife.bind(this);

        try{
            String type =  getIntent().getExtras().getString("type");
            if(type!=null&&type.equals("mytopic")){
                currentIndex = 0;
            }else {
                currentIndex = 1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String uid = PublicData.USER_UID;
        if(currentIndex==0){
            //主题
            if(actionBar!=null)
                actionBar.setTitle("我的帖子");
            url = "home.php?mod=space&uid="+uid+"&do=thread&view=me&mobile=2";
        }else{
            //我的收藏
            if(actionBar!=null)
                actionBar.setTitle("我的收藏");
            url = "home.php?mod=space&uid="+uid+"&do=favorite&view=me&type=thread&mobile=2";
        }
        datas = new ArrayList<>();
        adapter = new SimpleListAdapter(ListType.ARTICLE,this,datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) layoutManager, this,10));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();
    }

    @Override
    public void onLoadMore() {
        if(isEnableLoadMore&&isHaveMore){
            int a = CurrentPage;
            String newurl = url+"&page="+(a+1);
            getStringFromInternet(newurl);
            isEnableLoadMore = false;
        }
    }


    protected void refresh() {
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

    private void getStringFromInternet(String url){

        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res= new String(response);
                if(currentIndex==0){
                    new GetUserArticleask().execute(res);
                }else{
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
    protected class GetUserArticleask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String res = strings[0];
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            for(Element tmp:lists){
                String title = tmp.select("a").text();
                if(title.isEmpty()){
                    datas.add(new SimpleListData("暂无更多","",""));
                    isHaveMore = false;
                    break;
                }
                String titleUrl =tmp.select("a").attr("href");
                String num = tmp.select(".num").text();
                datas.add(new SimpleListData(title,num,titleUrl));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isEnableLoadMore = true;
            CurrentPage++;
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }

    }
    //获得用户收藏
    protected class GetUserStarTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String res= params[0];
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            for(Element tmp:lists){
                String key = tmp.select("a").text();
                if(key.isEmpty()){
                    datas.add(new SimpleListData("暂无更多","",""));
                    isHaveMore = false;
                    break;
                }
                String link = tmp.select("a").attr("href");
                datas.add(new SimpleListData(key,"",link));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            isEnableLoadMore = true;
            CurrentPage++;
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }
}
