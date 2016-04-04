package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ArticleListNormalAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class HomeFragement_2 extends Fragment implements LoadMoreListener.OnLoadMoreListener{

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    private List<ArticleListData> mydatasetnormal =new ArrayList<>();
    private ArticleListNormalAdapter adapter;

    private boolean isEnableLoadMore = false;
    private int CurrentPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_1_2_list, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        adapter = new ArticleListNormalAdapter(getActivity(),mydatasetnormal,3);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 10));

        //刷新
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(true);
                    }
                });
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mydatasetnormal.clear();
                adapter.notifyDataSetChanged();
                CurrentPage= 1 ;
                isEnableLoadMore = false;
                getData();
            }
        });

        getData();

        return view;
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

        //TODO hot
        String url = "forum.php?mod=guide&view=new&page="+CurrentPage+"&mobile=2";

        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetNewArticleListTaskMe(new String(response)).execute();
            }

            @Override
            public void onFailure(Throwable e) {
                //st.makeText(getActivity(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }
    //非校园网状态下获得一个板块文章列表数据
    //根据html获得数据
    //调用的手机版
    public class GetNewArticleListTaskMe extends AsyncTask<Void, Void, String> {

        private List<ArticleListData> dataset = new ArrayList<>();
        private String res;

        public GetNewArticleListTaskMe(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            Document doc = Jsoup.parse(res);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
            ArticleListData temp;
            Elements links = body.select("li");
            System.out.print(links);
            for (Element src : links) {
                String url = src.select("a").attr("href");
                String author = src.select(".by").text();
                src.select("span.by").remove();
                String replyCount = src.select("span.num").text();
                src.select("span.num").remove();
                String title = src.select("a").text();
                String img = src.select("img").attr("src");
                String hasImage = "";
                if(img.contains("icon_tu.png")){
                    hasImage = "0";
                }
                //String title, String titleUrl, String image, String author, String replayCount
                temp = new ArticleListData(hasImage,title, url, author, replyCount);
                dataset.add(temp);
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.getItemAnimator().setAddDuration(0);

            if(CurrentPage==1){
                //item 增加删除 改变动画
                recycler_view.setItemAnimator(new OvershootInLeftAnimator());
                recycler_view.getItemAnimator().setAddDuration(250);
                recycler_view.getItemAnimator().setRemoveDuration(10);
                recycler_view.getItemAnimator().setChangeDuration(10);
                mydatasetnormal.clear();
            }


            mydatasetnormal.addAll(dataset);
            refreshLayout.setRefreshing(false);

            adapter.notifyItemRangeInserted(mydatasetnormal.size() - dataset.size(), dataset.size());
            isEnableLoadMore = true;
        }
    }

}
