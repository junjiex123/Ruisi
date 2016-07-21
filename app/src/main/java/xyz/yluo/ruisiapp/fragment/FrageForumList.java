package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.Config;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyGridDivider;
import xyz.yluo.ruisiapp.adapter.ForumListAdapter;
import xyz.yluo.ruisiapp.data.ForumListData;
import xyz.yluo.ruisiapp.database.MyDbUtils;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-19.
 * 板块列表fragemnt
 */
public class FrageForumList extends BaseFragment {

    private static final String TAG = FrageForumList.class.getSimpleName();
    protected SwipeRefreshLayout refreshLayout;

    private List<ForumListData> datas = null;
    private ForumListAdapter adapter = null;
    private boolean isSetForumToDataBase = false;
    private RecyclerView recyclerView;

    public static FrageForumList newInstance(boolean isLogin) {
        Bundle args = new Bundle();
        args.putBoolean("isLogin",isLogin);
        FrageForumList fragment = new FrageForumList();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.frage_forum_list, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //刷新
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        MyDbUtils myDbUtils = new MyDbUtils(getActivity(),MyDbUtils.MODE_READ);
        datas = myDbUtils.getForums();

        //先从数据库读出数据
        adapter = new ForumListAdapter(datas, getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == 0) {
                    return 4;
                } else {
                    return 1;
                }
            }
        });

        recyclerView.addItemDecoration(new MyGridDivider(2));
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

    @Override
    protected int getLayoutId() {
        return 0;
    }

    private void getData() {
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
                }, 500);
            }
        });
    }


    //获取首页板块数据 板块列表
    private class GetForumList extends AsyncTask<String, Void, List<ForumListData>> {
        @Override
        protected List<ForumListData> doInBackground(String... params) {
            String response = params[0];
            List<ForumListData> simpledatas = new ArrayList<>();
            Document document = Jsoup.parse(response);
            Elements elements = document.select("div#wp.wp.wm").select("div.bm.bmw.fl");
            //获得hash
            String hash = document.select(".footer").select("a.dialog").attr("href");
            String ress = GetId.getHash(hash);
            if (!ress.isEmpty()) {
                Config.FORMHASH = ress;
            }

            for (Element ele : elements) {
                String header = ele.select("h2").text();
                simpledatas.add(new ForumListData(true, header,"0",null));
                for (Element tmp : ele.select("li")) {
                    String todayNew = tmp.select("span.num").text();
                    tmp.select("span.num").remove();
                    String title = tmp.text().replace("西电睿思", "");
                    String titleUrl = tmp.select("a").attr("href");
                    String fid = GetId.getFroumFid(titleUrl);
                    simpledatas.add(new ForumListData(false, title, todayNew, fid));
                }
            }

            if(!isSetForumToDataBase){
                MyDbUtils myDbUtils = new MyDbUtils(getActivity(),MyDbUtils.MODE_WRITE);
                myDbUtils.setForums(simpledatas);
                isSetForumToDataBase = true;
            }

            return simpledatas;
        }

        @Override
        protected void onPostExecute(List<ForumListData> simpledatas) {
            datas.clear();
            datas.addAll(simpledatas);
            adapter.notifyDataSetChanged();

            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);

        }
    }
}
