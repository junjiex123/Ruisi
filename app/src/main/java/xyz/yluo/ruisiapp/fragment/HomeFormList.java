package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.MyPublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ActivitySearch;
import xyz.yluo.ruisiapp.adapter.ForumListAdapter;
import xyz.yluo.ruisiapp.data.FroumListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.HidingScrollListener;
import xyz.yluo.ruisiapp.utils.GetFormHash;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class HomeFormList extends Fragment{

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    @Bind(R.id.search_view)
    protected CardView search_view;
    private ForumListAdapter forumListAdapter;
    private List<FroumListData> datas = new ArrayList<>();
    @Bind(R.id.search_input)
    protected EditText search_input;
    @Bind(R.id.main_window)
    protected CoordinatorLayout main_window;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_ist, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        recycler_view.setLayoutManager(mLayoutManager);
        forumListAdapter = new ForumListAdapter(getActivity(),datas);
        recycler_view.setAdapter(forumListAdapter);

        recycler_view.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) search_view.getLayoutParams();
                int bottomMargin = lp.bottomMargin;
                int distanceToScroll = search_view.getHeight() + bottomMargin;
                search_view.animate().translationY(-distanceToScroll).setInterpolator(new AccelerateInterpolator(2));
            }

            @Override
            public void onShow() {
                search_view.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2));
            }
        });

        refreshLayout.setProgressViewOffset(true,150,200);


        //刷新
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        getData();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datas.clear();
                forumListAdapter.notifyDataSetChanged();
                getData();
            }
        });

        return view;
    }

    private void getData(){
        String url = "forum.php?forumlist=1&mobile=2";
        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetForumList(new String(response)).execute();
            }

            @Override
            public void onFailure(Throwable e) {
                //Toast.makeText(getActivity(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
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
            Document document = Jsoup.parse(response);
            Elements elements = document.select("div#wp.wp.wm").select("div.bm.bmw.fl");
            //获得hash
            String hash = document.select(".footer").select("a.dialog").attr("href");

            String ress =  GetFormHash.getHash(hash);
            if(!ress.isEmpty()){
                MyPublicData.FORMHASH = ress;
            }

            for(Element ele:elements){
                String header = ele.select("h2").text();
                simpledatas.add(new FroumListData(true,header));

                for(Element tmp:ele.select("li")){
                    String todayNew = tmp.select("span.num").text();
                    tmp.select("span.num").remove();
                    String title = tmp.text();
                    String titleUrl = tmp.select("a").attr("href");
                    //boolean isheader,String title, String todayNew,  String titleUrl
                    simpledatas.add(new FroumListData(false,title,todayNew,titleUrl));
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

    @OnClick(R.id.start_search)
    protected void start_search_click(){
        if(islogin_dialog()){
            if (search_input.getText().toString().isEmpty()){
                search_input.setError("你还没写呢");
            }else{
                Intent i = new Intent(getActivity(),ActivitySearch.class);
                i.putExtra("res",search_input.getText().toString());
                search_input.setText("");
                startActivity(new Intent(i));
            }
        }

    }


    //判断是否需要弹出登录dialog
    private boolean islogin_dialog(){

        if(MyPublicData.ISLOGIN){
            return true;
        }else{
            NeedLoginDialogFragment dialogFragment = new NeedLoginDialogFragment();
            dialogFragment.show(getFragmentManager(), "needlogin");
        }
        return false;
    }
}
