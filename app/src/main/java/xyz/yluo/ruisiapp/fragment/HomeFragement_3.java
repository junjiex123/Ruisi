package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.UserArticleReplyStarAdapter;
import xyz.yluo.ruisiapp.adapter.UserInfoStarAdapter;
import xyz.yluo.ruisiapp.data.MyTopicReplyListData;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.MySetting;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class HomeFragement_3 extends Fragment {

    @Bind(R.id.mytab)
    protected TabLayout mytab;
    @Bind(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @Bind(R.id.refresh_view)
    protected SwipeRefreshLayout refresh_view;

    private List<MyTopicReplyListData> datasArticleReply = new ArrayList<>();
    private List<Pair<String,String>> datasUserInfo = new ArrayList<>();
    private UserArticleReplyStarAdapter adapterArtilceReply;
    private UserInfoStarAdapter myadapterUserInfo;

    private int currentIndex =0;
    private String uid = MySetting.CONFIG_USER_UID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_3_me, container, false);

        ButterKnife.bind(this, view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        mytab.addTab(mytab.newTab().setText("我的主题"));
        mytab.addTab(mytab.newTab().setText("私人消息"));
        mytab.addTab(mytab.newTab().setText("我的收藏"));

        mytab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeRecyclerViewData(mytab.getSelectedTabPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                changeRecyclerViewData(currentIndex);
            }
        });


        adapterArtilceReply = new UserArticleReplyStarAdapter(getActivity(),datasArticleReply);
        myadapterUserInfo = new UserInfoStarAdapter(getActivity(),datasUserInfo,0);
        changeRecyclerViewData(0);

        return view;
    }

    private void changeRecyclerViewData(int position){

        datasUserInfo.clear();
        datasArticleReply.clear();
        adapterArtilceReply.notifyDataSetChanged();
        myadapterUserInfo.notifyDataSetChanged();

        //TODO 所有链接重写
        switch (position){
            case 0:
                //我回主题
                String url1 = "home.php?mod=space&uid="+uid+"&do=thread&view=me&mobile=2";
                getStringFromInternet(0,url1);
                currentIndex =0;
                break;
            case 1:
                //我的消息
                String url2 = "home.php?mod=space&do=pm&mobile=2";
                getStringFromInternet(1,url2);
                currentIndex = 1;
                break;
            case 2:
                //我的收藏
                String url3 = "home.php?mod=space&uid="+uid+"&do=favorite&view=me&type=thread&mobile=2";
                getStringFromInternet(2,url3);
                currentIndex = 2;
                break;
        }
    }

    private void getStringFromInternet(final int type,String url){

        //刷新
        refresh_view.post(new Runnable() {
            @Override
            public void run() {
                refresh_view.setRefreshing(true);
            }
        });

        AsyncHttpCilentUtil.get(getActivity(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (type == 0) {
                    //我的主题
                    new GetUserArticleask(new String(responseBody)).execute();
                } else if (type == 1) {
                    //我的消息
                    new GetUserMessageTask(new String(responseBody)).execute();
                }
                else if(type==2){
                    //我的收藏
                    new GetUserStarTask(new String(responseBody)).execute();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //st.makeText(getActivity().getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refresh_view.setRefreshing(false);
            }
        });
    }

    //获得消息
    public class GetUserMessageTask extends AsyncTask<Void, Void, String> {

        private String res;

        public GetUserMessageTask(String res) {
            this.res = res;
        }
        @Override
        protected String doInBackground(Void... params) {
            //pmbox
            Elements lists = Jsoup.parse(res).select(".pmbox").select("ul").select("li");
            if(lists!=null){
                for(Element tmp:lists){
                    String title = tmp.select(".cl").select(".name").text();
                    String time = tmp.select(".cl.grey").select(".time").text();
                    tmp.select(".cl.grey").select(".time").remove();

                    String content = tmp.select(".cl.grey").text();
                    String authorImage = tmp.select("img").attr("src");
                    String titleUrl =tmp.select("a").attr("href");

                    //int type, String title, String titleUrl, String authorImage, String time,String content
                    datasArticleReply.add(new MyTopicReplyListData(1,title,titleUrl,authorImage,time,content));
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            refresh_view.setRefreshing(false);
            adapterArtilceReply = new UserArticleReplyStarAdapter(getActivity(),datasArticleReply);
            recyclerView.setAdapter(adapterArtilceReply);
            adapterArtilceReply.notifyItemRangeInserted(0, datasArticleReply.size());
        }
    }

    //获得主题
    public class GetUserArticleask extends AsyncTask<Void, Void, String> {
        private String res;
        public GetUserArticleask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            if(lists!=null){
                for(Element tmp:lists){
                    String title = tmp.select("a").text();
                    String titleUrl =tmp.select("a").attr("href");
                    String num = tmp.select(".num").text();
                    //int type, String title, String titleUrl, String replycount
                    datasArticleReply.add(new MyTopicReplyListData(0,title,titleUrl,num));
                }
            }else{
                datasArticleReply.add(new MyTopicReplyListData(0,"你还没有回复，或者你还未登录","",""));
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            refresh_view.setRefreshing(false);
            adapterArtilceReply = new UserArticleReplyStarAdapter(getActivity(),datasArticleReply);
            recyclerView.setAdapter(adapterArtilceReply);
            adapterArtilceReply.notifyItemRangeInserted(0, datasArticleReply.size());

        }
    }

    //获得用户收藏
    public class GetUserStarTask extends AsyncTask<Void, Void, String> {

        private String res;
        public GetUserStarTask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            if(lists!=null){
                Pair<String,String> temp;
                for(Element tmp:lists){
                    String key = tmp.select("a").text();
                    String value = tmp.select("a").attr("href");
                    temp = new Pair<>(key,value);
                    datasUserInfo.add(temp);
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            refresh_view.setRefreshing(false);
            myadapterUserInfo = new UserInfoStarAdapter(getActivity(),datasUserInfo,1);
            recyclerView.setAdapter(myadapterUserInfo);
            myadapterUserInfo.notifyItemRangeInserted(0, datasUserInfo.size());

        }
    }

}
