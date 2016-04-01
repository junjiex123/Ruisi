package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ForumListAdapter;
import xyz.yluo.ruisiapp.data.FroumListData;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.ConfigClass;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class HomeFragement_1 extends Fragment {

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    private ForumListAdapter forumListAdapter;
    private List<FroumListData> datas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_1_list, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        recycler_view.setLayoutManager(mLayoutManager);
        forumListAdapter = new ForumListAdapter(getActivity(),datas);
        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(forumListAdapter);
        alphaAdapter.setDuration(150);
        recycler_view.setAdapter(alphaAdapter);


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
                    FragmentTransaction fragTransaction = (getActivity()).getFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);
                    fragTransaction.commit();
                }
            }
        });

        String url = "forum.php?forumlist=1&mobile=2";

        AsyncHttpCilentUtil.get(getActivity(), url, new AsyncHttpResponseHandler() {
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

            System.out.println("\n"+response);
            Document document = Jsoup.parse(response);

            Elements elements = document.select("div#wp.wp.wm").select("div.bm.bmw.fl");

            for(Element ele:elements){
                String header = ele.select("h2").text();
                simpledatas.add(new FroumListData(true,header));

                for(Element tmp:ele.select("li")){
                    String todayNew = tmp.select("span.num").text();
                    tmp.select("span.num").remove();
                    String title = tmp.text();
                    String titleUrl = tmp.select("a").attr("href");

                    //如果是校园网
                    if(ConfigClass.CONFIG_IS_INNER){
                        //boolean isheader,String title, String todayNew,  String titleUrl
                        simpledatas.add(new FroumListData(false,title,todayNew,titleUrl));

                        }else{

                        //摄影天地 //校园活动 //电影
                        //这三个分区只有校园网才能上
                        if(title.equals("摄影天地")|title.equals("校园活动")|title.equals("电影")){
                        }else{
                            //boolean isheader,String title, String todayNew,  String titleUrl
                            simpledatas.add(new FroumListData(false,title,todayNew,titleUrl));
                        }

                    }


                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            refreshLayout.setRefreshing(false);
            datas.clear();
            datas.addAll(simpledatas);
            forumListAdapter.notifyItemRangeInserted(0, simpledatas.size());
        }

    }
}
