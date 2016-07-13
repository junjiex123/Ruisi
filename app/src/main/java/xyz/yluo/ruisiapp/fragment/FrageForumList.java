package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

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

    private static final  String Tag = "==FrageForumList==";
    protected SwipeRefreshLayout refreshLayout;
    private TextView view_loading;

    private List<FroumListData> datas = null;
    private ForumListAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(Tag,"onCreateView");

        View view = inflater.inflate(R.layout.frage_forum_list, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        view_loading = (TextView) view.findViewById(R.id.view_loading);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //刷新
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        datas = new ArrayList<>();
        adapter = new ForumListAdapter(datas,getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(adapter.getItemViewType(position)==0){
                    return 4;
                }else{
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


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
                new GetForumList().execute(new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });
    }


    //获取首页板块数据 板块列表
    private class GetForumList extends AsyncTask<String, Void, List<FroumListData>> {
        @Override
        protected List<FroumListData> doInBackground(String... params) {
            String response = params[0];
            List<FroumListData> simpledatas = new ArrayList<>();
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
                    String title = tmp.text().replace("西电睿思","");
                    String titleUrl = tmp.select("a").attr("href");
                    simpledatas.add(new FroumListData(false,title,todayNew,titleUrl));
                }
            }
            return simpledatas;
        }

        @Override
        protected void onPostExecute(List<FroumListData> simpledatas) {
            datas.clear();
            datas.addAll(simpledatas);
            adapter.notifyDataSetChanged();

            view_loading.setVisibility(View.GONE);
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            },500);
        }
    }
}
