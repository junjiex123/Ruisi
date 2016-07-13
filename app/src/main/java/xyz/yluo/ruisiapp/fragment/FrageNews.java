package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.NewsListAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.data.SchoolNewsData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-3-19.
 * 简单的fragment 首页第二页 展示最新的帖子等
 */
public class FrageNews extends Fragment implements LoadMoreListener.OnLoadMoreListener{

    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;
    private List<SchoolNewsData> mydataset =new ArrayList<>();
    private NewsListAdapter adapter;
    private boolean isEnableLoadMore = false;
    private int CurrentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frage_new_topic, container, false);
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);

        adapter = new NewsListAdapter(getActivity(), mydataset);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 20));

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

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 300);
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
                new GetNewsListTask().execute(new String(response));
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
    private class GetNewsListTask extends AsyncTask<String, Void, List<SchoolNewsData>> {
        @Override
        protected List<SchoolNewsData> doInBackground(String... params) {
            List<SchoolNewsData> dataset = new ArrayList<>();
            // write your code here
            Document document = null;
            try {
                document = Jsoup.connect("http://jwc.xidian.edu.cn/tzgg1.htm").timeout(3000).get();
            } catch (IOException e) {
                e.printStackTrace();
                return dataset;
            }

            Elements articlelists =  document.select("table.winstyle49756").select("tr[height=20]");
            for(Element article:articlelists){
                Elements title = article.select("a");
                String url = title.attr("href");
                String titleStr = title.text();
                boolean is_fj = !article.select("img[src=images/fj.gif]").isEmpty();
                boolean is_image = !article.select("img[src=images/tu-hz.gif]").isEmpty();
                String time =  article.select("span.timestyle49756").text();
                Log.i("news task",titleStr+" "+url+" "+is_fj+" "+is_image+" "+time);
                //String url,String title, boolean is_image, boolean is_patch, String post_time
                dataset.add(new SchoolNewsData(url,titleStr,is_image,is_fj,time));
            }
            return dataset;
        }

        @Override
        protected void onPostExecute(List<SchoolNewsData> dataset) {
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
