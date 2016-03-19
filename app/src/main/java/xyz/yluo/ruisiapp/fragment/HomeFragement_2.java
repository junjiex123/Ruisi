package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.FragmentNewTopAdapter;
import xyz.yluo.ruisiapp.data.NewAndTopListData;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class HomeFragement_2 extends Fragment{

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_2, container, false);
        ButterKnife.bind(this, view);

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
                Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof HomeFragement_1) {
                    FragmentTransaction fragTransaction =   (getActivity()).getFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);
                    fragTransaction.commit();
                }
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
                new GetRecentAndTop(new String(responseBody)).execute();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });


        return view;
    }


    //获取首页板块数据 最新帖子
    public class GetRecentAndTop extends AsyncTask<Void, Void, String> {

        private String response;

        private List<NewAndTopListData> simpledatas = new ArrayList<>();

        public GetRecentAndTop(String res) {
            this.response = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (response != "") {
                Elements list = Jsoup.parse(response).select("div[id=portal_block_314],div[id=portal_block_315]");
                Elements links = list.select("li");
                for (Element tmp : links) {
                    NewAndTopListData tempdata;
                    String titleurl = tmp.select("a[href^=forum.php]").attr("href").trim();
                    String title = tmp.select("a[href^=forum.php]").text();
                    //title="楼主：ansonzhang0123 回复数：0 总浏览数：0"
                    String message = tmp.select("a[href^=forum.php]").attr("title");
                    String User = message.split("\n")[0];
                    String ReplyCount = message.split("\n")[1];
                    String ViewCount = message.split("\n")[2];
                    //http://rs.xidian.edu.cn/home.php?mod=space&uid=124025
                    //String user = tmp.select("a[href^=]").text();
                    //String userurl = tmp.select("em").select("a").attr("href");

                    //去重
                    if (simpledatas.size() > 0) {
                        int i = 0;
                        for (i = 0; i < simpledatas.size(); i++) {
                            String have_url = simpledatas.get(i).getTitleUrl();
                            if (have_url.equals(title)) {
                                break;
                            }
                        }
                        if (i == simpledatas.size()) {
                           //String title, String titleUrl, String user, String replyCount, String viewCount,int type
                            tempdata = new NewAndTopListData(title, titleurl, User, ReplyCount, ViewCount,0);
                            simpledatas.add(tempdata);
                        }
                    }
                    if (simpledatas.size() == 0) {
                        tempdata = new NewAndTopListData(title, titleurl, User, ReplyCount, ViewCount,0);
                        simpledatas.add(tempdata);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String res) {
            refreshLayout.setRefreshing(false);
            FragmentNewTopAdapter adapter= new FragmentNewTopAdapter(getActivity(), simpledatas, 0);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setAdapter(adapter);
            adapter.notifyItemRangeInserted(0, simpledatas.size());
        }
    }
}
