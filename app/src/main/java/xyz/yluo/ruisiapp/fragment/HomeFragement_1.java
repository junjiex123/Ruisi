package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.FragmentForumListAdapter;
import xyz.yluo.ruisiapp.data.FroumListData;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class HomeFragement_1 extends Fragment{

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_1, container, false);
        ButterKnife.bind(this,view);

        //刷新
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO
            }
        });

        //item 增加删除 改变动画
        recycler_view.setItemAnimator(new FadeInDownAnimator());
        recycler_view.getItemAnimator().setAddDuration(50);
        recycler_view.getItemAnimator().setRemoveDuration(10);
        recycler_view.getItemAnimator().setChangeDuration(10);


        String url = "forum.php";

        AsyncHttpCilentUtil.get(getActivity(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                new GetForumList(new String(responseBody)).execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });

        return view;
    }
    //获取首页板块数据 板块列表
    public class GetForumList extends AsyncTask<Void, Void, String> {
        private String response;
        private List<FroumListData> simpledatas = new ArrayList<>();

        public GetForumList(String res) {
            this.response = res;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (response != "") {
                Elements list = Jsoup.parse(response).select("#category_89,#category_101,#category_71,category_97,category_11").select("td.fl_g");
                for (Element tmp : list) {
                    FroumListData datatmp;
                    String img = tmp.select("img[src^=./data/attachment]").attr("src").replace("./data", "data");
                    String url = tmp.select("a[href^=forum.php?mod=forumdisplay&fid]").attr("href");
                    String title = tmp.select("a[href^=forum.php?mod=forumdisplay&fid]").text();

                    String todaynew = tmp.select("em[title=今日]").text();
                    String actualnew = "";
                    if (todaynew != "") {
                        Pattern pattern = Pattern.compile("[0-9]+");
                        Matcher matcher = pattern.matcher(todaynew);
                        String tid = "";
                        while (matcher.find()) {
                            actualnew = todaynew.substring(matcher.start(), matcher.end());
                            //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
                        }
                    }
                    //String title, String todayNew, String imgUrl, String titleUrl
                    datatmp = new FroumListData(title,actualnew,img,url);
                    simpledatas.add(datatmp);
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            refreshLayout.setRefreshing(false);

            FragmentForumListAdapter fragmentForumListAdapter = new FragmentForumListAdapter(getActivity(),simpledatas, 0);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setAdapter(fragmentForumListAdapter);

            fragmentForumListAdapter.notifyItemRangeInserted(0, simpledatas.size());
        }

    }
}
