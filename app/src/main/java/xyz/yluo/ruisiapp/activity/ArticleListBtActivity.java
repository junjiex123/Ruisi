package xyz.yluo.ruisiapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ArticleListBtAdapter;
import xyz.yluo.ruisiapp.data.ArticleListBtData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-4-2.
 * 种子列表页面
 */
public class ArticleListBtActivity extends ArticleListBaseActivity{

    private List<ArticleListBtData> mydatasetnormal;
    private ArticleListBtAdapter adapter;
    private String CurrentId = "all";
    private final String[] mItems = {"全部种子","热门种子","推荐种子","FREE种子","我的种子",
            "电影","剧集","音乐","动漫","游戏","综艺","体育","软件","学习","纪录片","西电","其他"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Spinner spinner = new Spinner(this);
        actionBar.setDisplayShowTitleEnabled(false); // DEPRACATED

        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(this,R.layout.spinner_item, mItems);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        toolbar.addView(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0:
                        CurrentId  = "t=all";
                        break;
                    case 1:
                        CurrentId = "t=hot";
                        break;
                    case 2:
                        CurrentId = "t=digest";
                        break;
                    case 3:
                        CurrentId = "t=highlight";
                        break;
                    case 4:
                        CurrentId = "t=user";
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                        CurrentId = "c="+position*2;
                        break;
                    case 14:
                        CurrentId = "c=30";
                        break;
                    case 15:
                        CurrentId = "c=32";
                        break;
                    case 16:
                        CurrentId = "c=28";
                        break;
                }

                CurrentPage =1;
                mydatasetnormal.clear();
                adapter.notifyDataSetChanged();
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(true);
                    }
                });
                getData();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetBtArticleListTaskRS(new String(response)).execute();
            }

            @Override
            public void onFailure(Throwable e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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

                  //title, titleUrl, author, logoUrl, time, btSize, btNum, btDownLoadNum, btCompeteNum, isFree
                dataset.add(new ArticleListBtData(title,titleUrl,author,logoUrl,time,size,bt_num,bt_down,bt_com,isFree));

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            btn_refresh.show();
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
