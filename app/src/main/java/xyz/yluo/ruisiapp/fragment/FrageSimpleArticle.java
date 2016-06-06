package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import xyz.yluo.ruisiapp.adapter.ArticleListNormalAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-3-19.
 * 简单的fragment 首页第二页
 */
public class FrageSimpleArticle extends Fragment implements LoadMoreListener.OnLoadMoreListener{

    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;

    private List<ArticleListData> mydataset =new ArrayList<>();
    private ArticleListNormalAdapter adapter;

    private boolean isEnableLoadMore = false;
    private int CurrentPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list_view, container, false);
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        adapter = new ArticleListNormalAdapter(getActivity(), mydataset,3);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 10));

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        getData();
        return view;
    }

    private void refresh(){
        CurrentPage= 1 ;
        isEnableLoadMore = false;
        getData();
    }
    @Override
    public void onLoadMore() {
        if(isEnableLoadMore){
            CurrentPage++;
            getData();
            isEnableLoadMore = false;
        }
    }

    private void getData(){
        String url = "forum.php?mod=guide&view=new&page="+CurrentPage+"&mobile=2";
        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetNewArticleListTaskMe().execute(new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },500);

            }
        });
    }
    //非校园网状态下获得一个板块文章列表数据
    //根据html获得数据
    //调用的手机版
    private class GetNewArticleListTaskMe extends AsyncTask<String, Void, List<ArticleListData>> {
        @Override
        protected List<ArticleListData> doInBackground(String... params) {
            List<ArticleListData> dataset = new ArrayList<>();
            Document doc = Jsoup.parse(params[0]);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
            ArticleListData temp;
            Elements links = body.select("li");
            for (Element src : links) {
                String url = src.select("a").attr("href");
                String author = src.select(".by").text();
                src.select("span.by").remove();
                String replyCount = src.select("span.num").text();
                src.select("span.num").remove();
                String title = src.select("a").text();
                String img = src.select("img").attr("src");
                boolean hasImage = img.contains("icon_tu.png");

                temp = new ArticleListData(hasImage,title, url, author, replyCount);
                dataset.add(temp);
            }
            return dataset;
        }

        @Override
        protected void onPostExecute(List<ArticleListData> dataset) {
            if(CurrentPage==1){
                //item 增加删除 改变动画
                mydataset.clear();
            }
            int size = mydataset.size();
            mydataset.addAll(dataset);
            if(size>0){
                adapter.notifyItemChanged(size);
                adapter.notifyItemRangeInserted(size+1, dataset.size());
            }else{
                adapter.notifyDataSetChanged();
            }
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
