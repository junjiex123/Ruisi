package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ForumListAdapter;
import xyz.yluo.ruisiapp.data.FroumListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-19.
 * 板块列表fragemnt
 */
public class FrageForumList extends Fragment {

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    private ForumListAdapter forumListAdapter;
    private List<FroumListData> datas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list_view, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        forumListAdapter = new ForumListAdapter(getActivity(),datas);
        //跨列
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(forumListAdapter.getItemViewType(position)==1){
                    return 2;
                }
                return 1;
            }
        });
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(forumListAdapter);

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
            String ress =  GetId.getHash(hash);
            if(!ress.isEmpty()){
                PublicData.FORMHASH = ress;
            }

            for(Element ele:elements){
                String header = ele.select("h2").text();
                simpledatas.add(new FroumListData(true,header));

                for(Element tmp:ele.select("li")){
                    String todayNew = tmp.select("span.num").text();
                    tmp.select("span.num").remove();
                    String title = tmp.text();
                    String titleUrl = tmp.select("a").attr("href");
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
            forumListAdapter.notifyDataSetChanged();
        }

    }
}
