package xyz.yluo.ruisiapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.adapter.ArticleListBtAdapter;
import xyz.yluo.ruisiapp.data.ArticleListBtData;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-4-2.
 * 种子列表页面
 */
public class ArticleListBtActivity extends ArticleListBaseActivity{

    private List<ArticleListBtData> mydatasetnormal;
    private ArticleListBtAdapter adapter;
    private String CurrentId = "all";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CurrentTitle = "最新种子";//板块
        actionBar.setTitle(CurrentTitle);
        mydatasetnormal =  new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new ArticleListBtAdapter(this,mydatasetnormal);
        mRecyclerView.setAdapter(adapter);
        //加载更多
        mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this,10));
    }

    @Override
    protected void refresh() {
        CurrentPage = 1;
        mydatasetnormal.clear();
        adapter.notifyDataSetChanged();
        getData();
    }

    @Override
    protected void getData() {
        String url = UrlUtils.getBtListUrl(CurrentId,CurrentPage);
        AsyncHttpCilentUtil.get(getApplicationContext(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                new GetBtArticleListTaskRS(new String(responseBody)).execute();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onLoadMore() {
        if(isEnableLoadMore){
            CurrentPage++;
            getData();
            isEnableLoadMore = false;
        }
    }

    protected class GetBtArticleListTaskRS extends AsyncTask<Void, Void, String>{

        private List<ArticleListBtData> dataset = new ArrayList<>();
        private String res;

        public GetBtArticleListTaskRS(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Elements document = Jsoup.parse(res).select("table.tl");
            Elements list  = document.select("tbody");

            for(Element singleList:list){
                Elements items = singleList.select("td");

                String logoUrl = singleList.select("img[src^=./static/image/bt/]").attr("src");
                String title = singleList.select("a[href^=./forum.php?mod=viewthread&tid=]").text();
                String titleUrl = singleList.select("a[href^=./forum.php?mod=viewthread&tid=]").attr("href");
                boolean isFree = false;
                if(singleList.select("img[src=./static/image/bt/free.gif]").attr("src").contains("free")){
                    isFree = true;
                }

                String size = items.get(3).text();
                String time = items.get(5).text();
                String bt_num = items.get(6).text();
                String bt_down = items.get(7).text();
                String bt_com = items.get(8).text();
                String author = items.get(9).text();

                System.out.print(title+titleUrl+author+logoUrl+time+size+bt_num+bt_down+bt_com+isFree+"\n");
                //title, titleUrl, author, logoUrl, time, btSize, btNum, btDownLoadNum, btCompeteNum, isFree
                dataset.add(new ArticleListBtData(title,titleUrl,author,logoUrl,time,size,bt_num,bt_down,bt_com,isFree));

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mydatasetnormal.addAll(dataset);
            refreshLayout.setRefreshing(false);
            if(CurrentPage!=1){
                mRecyclerView.getItemAnimator().setAddDuration(0);
            }
            adapter.notifyItemRangeInserted(mydatasetnormal.size() - dataset.size(), dataset.size());
            isEnableLoadMore = true;
        }
    }
}
