package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ArticleListAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;

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
    private ArticleListAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private boolean isEnableLoadMore = false;
    private int CurrentPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_2_new, container, false);
        ButterKnife.bind(this, view);


        mLayoutManager = new LinearLayoutManager(getActivity());
        adapter = new ArticleListAdapter(getActivity(),mydatasetnormal,3);
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
                Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof HomeFragement_1) {
                    FragmentTransaction fragTransaction =   (getActivity()).getFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);
                    fragTransaction.commit();
                }
            }
        });

        getData();

        return view;
    }

    @Override
    public void onLoadMore() {
        if(isEnableLoadMore){
            Toast.makeText(getActivity(),"加载更多被触发",Toast.LENGTH_SHORT).show();
            CurrentPage++;
            getData();
            isEnableLoadMore = false;
        }
    }


    private void getData(){

        String url = "forum.php?mod=guide&view=hot&page="+CurrentPage+"&mobile=2";

        AsyncHttpCilentUtil.get(getActivity(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                new GetNewArticleListTaskMe(new String(responseBody)).execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络错误！！", Toast.LENGTH_SHORT).show();
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
            if(res!=""){
                Document doc = Jsoup.parse(res);
                Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
                ArticleListData temp;
                Elements links = body.select("li");
                System.out.print(links);
                for (Element src : links) {
                    String url = src.select("a").attr("href");
                    String author = src.select(".by").text();
                    src.select("span.by").remove();
                    String title = src.select("a").text();
                    String replyCount = src.select("span.num").text();

                    String img = src.select("img").attr("src");

                    System.out.print("\nimg>>>>>>>>>>>>>>>>>>>>>>\n"+img);
                    boolean hasImage = false;
                    if(img.contains("icon_tu.png")){
                        hasImage = true;
                    }
                    else{
                        hasImage = false;
                    }
                    //String title, String titleUrl, String image, String author, String replayCount
                    temp = new ArticleListData(hasImage,title, url, author, replyCount);
                    dataset.add(temp);
                }
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

            recycler_view.setLayoutManager(mLayoutManager);
            mydatasetnormal.addAll(dataset);
            refreshLayout.setRefreshing(false);

            adapter.notifyItemRangeInserted(mydatasetnormal.size() - dataset.size(), dataset.size());
            isEnableLoadMore = true;
        }
    }

}
