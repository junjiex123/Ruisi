package xyz.yluo.ruisiapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
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
    @Bind(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @Bind(R.id.refresh_layout)
    protected SwipeRefreshLayout refreshLayout;

    private List<SimpleListData> datas;
    private SimpleListAdapter adapter;
    private int CurrentPage = 0;
    private boolean isEnableLoadMore = true;
    private boolean isHaveMore = true;
    private int currentIndex = 0;

    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.simple_list_view);
        ButterKnife.bind(this);
        try{
            String type =  getIntent().getExtras().getString("url");
            if(type!=null&&type.equals("mytopic")){
                currentIndex = 0;
            }else {
                currentIndex = 1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("==================index"+currentIndex);

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

        refresh();
    }

    @Override
    public void onLoadMore() {
        if(isEnableLoadMore&&isHaveMore){
            int a = CurrentPage;
            String newurl = url+"&page="+(a+1);
            getStringFromInternet(newurl);
            System.out.println("===load more=="+newurl);
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
                    new GetUserArticleask(res).execute();
                }else{
                    new GetUserStarTask(res).execute();
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
    protected class GetUserArticleask extends AsyncTask<Void, Void, String> {
        private String res;
        public GetUserArticleask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
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
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            isEnableLoadMore = true;
            CurrentPage++;
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }
    //获得用户收藏
    protected class GetUserStarTask extends AsyncTask<Void, Void, String> {

        private String res;
        public GetUserStarTask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
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
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            isEnableLoadMore = true;
            CurrentPage++;
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }
}
