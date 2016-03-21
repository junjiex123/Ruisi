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
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.Home3RecylerAdapter;
import xyz.yluo.ruisiapp.adapter.UserInfoAdapter;
import xyz.yluo.ruisiapp.data.MyTopicReplyListData;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;

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
    private int CurrentIndex = 0;
    private List<MyTopicReplyListData> datasArticleReply = new ArrayList<>();
    private Home3RecylerAdapter adapterArtilceReply;
    private RecyclerView.LayoutManager layoutManager;
    private UserInfoAdapter myadapterUserInfo;
    private List<Pair<String,String>> datasUserInfo = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_3, container, false);

        ButterKnife.bind(this, view);
        layoutManager =new LinearLayoutManager(getActivity());
        adapterArtilceReply = new Home3RecylerAdapter(getActivity(),datasArticleReply);
        myadapterUserInfo = new UserInfoAdapter(datasUserInfo);
        recyclerView.setLayoutManager(layoutManager);

        mytab.addTab(mytab.newTab().setText("个人信息"));
        mytab.addTab(mytab.newTab().setText("我回复的"));
        mytab.addTab(mytab.newTab().setText("我的主题"));

        mytab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeRecyclerViewData(mytab.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //自己的页面home.php?mod=space&do=profile
        ////用户个人信息home.php?mod=space&uid=50545&do=profile
        String url = "home.php?mod=space&uid=50545&do=profile";
        getStringFromInternet(0,url);
        return view;
    }

    private void changeRecyclerViewData(int position){

        datasUserInfo.clear();
        datasArticleReply.clear();
        adapterArtilceReply.notifyDataSetChanged();
        myadapterUserInfo.notifyDataSetChanged();

        switch (position){
            case 1:
                //我的回复
                //home.php?mod=space&do=thread&view=me&type=reply&from=space
                String url1 = "home.php?mod=space&do=thread&view=me&type=reply&from=space";
                CurrentIndex =1;
                getStringFromInternet(1,url1);
                break;
            case 2:
                String url2 = "home.php?mod=space&do=thread&view=me&type=thread&from=space";
                getStringFromInternet(2,url2);
                CurrentIndex =2 ;
                //我的主题
                break;
            default:
                String url0= "home.php?mod=space&uid=50545&do=profile";
                getStringFromInternet(0,url0);
                CurrentIndex = 0;
                //0 用户信息
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

        AsyncHttpCilentUtil.get(getActivity(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (type == 1) {
                    //获得回复 1
                    new GetUserReplayTask(new String(responseBody)).execute();

                } else if (type == 2) {
                    //获得主题 0
                    new GetUserArticleask(new String(responseBody)).execute();

                } else {
                    //获得用户信息
                    new GetUserInfoTask(new String(responseBody)).execute();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refresh_view.setRefreshing(false);
            }
        });
    }

    //获得用户个人信息
    public class GetUserInfoTask extends AsyncTask<Void, Void, String> {

        private String res;

        public GetUserInfoTask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            if(res!=""){
                Elements lists = Jsoup.parse(res).select("div[id=ct]").select(".mn").select(".bm").select(".bm_c");
                if(lists!=null){
                    Pair<String,String> temp;
                    Elements datas1 = lists.select(".pbm.mbm.bbda.cl").select("li");
                    Elements datas2 = lists.select("#psts").select("li");
                    for(Element tmp:datas1){
                        String txt = tmp.text();
                        if(txt.startsWith("用户组")||txt.startsWith("空间访问量")){
                            temp = new Pair<>(tmp.text()," ");
                            //System.out.print("\nli"+tmp.text());
                            datasUserInfo.add(temp);
                        }
                    }
                    for(Element tmp:datas2){
                        String txt = tmp.text();

                        temp = new Pair<>(txt,"");

                        datasUserInfo.add(temp);
                    }
                }

            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            refresh_view.setRefreshing(false);
            myadapterUserInfo = new UserInfoAdapter(datasUserInfo);
            recyclerView.setAdapter(myadapterUserInfo);
            myadapterUserInfo.notifyItemRangeInserted(0, datasUserInfo.size());

        }
    }

    //获得回复
    public class GetUserReplayTask extends AsyncTask<Void, Void, String> {

        private String res;

        public GetUserReplayTask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {

            for(int i =0;i<20;i++){
                //int type, String title, String titleUrl, String author, String time, String froumName
                datasArticleReply.add(new MyTopicReplyListData(1,"","","","",""));
            }

            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            refresh_view.setRefreshing(false);
            adapterArtilceReply = new Home3RecylerAdapter(getActivity(),datasArticleReply);
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

            for(int i =0;i<20;i++){
                //int type, String title, String titleUrl, String author, String time, String froumName
                datasArticleReply.add(new MyTopicReplyListData(0,"","","","",""));
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            refresh_view.setRefreshing(false);
            adapterArtilceReply = new Home3RecylerAdapter(getActivity(),datasArticleReply);
            recyclerView.setAdapter(adapterArtilceReply);
            adapterArtilceReply.notifyItemRangeInserted(0, datasArticleReply.size());

        }
    }
}
