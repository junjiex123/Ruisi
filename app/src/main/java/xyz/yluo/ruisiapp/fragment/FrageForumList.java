package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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

    @BindView(R.id.refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    @BindView(R.id.exListView)
    protected ExpandableListView exListView;

    private List<FroumListData> datas = null;
    ForumListAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frage_forum_list, container, false);
        ButterKnife.bind(this, view);

        datas = new ArrayList<>();
        adapter = new ForumListAdapter(datas,getActivity());
        exListView.setAdapter(adapter);

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
            adapter.initData(datas);
            for(int i = 0; i < adapter.getGroupCount(); i++){
                exListView.expandGroup(i);

            }
        }
    }
}
