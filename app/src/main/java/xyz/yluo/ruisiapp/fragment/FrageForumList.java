package xyz.yluo.ruisiapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyGridDivider;
import xyz.yluo.ruisiapp.activity.ActivitySearch;
import xyz.yluo.ruisiapp.activity.ArticleList;
import xyz.yluo.ruisiapp.activity.ArticleListImage;
import xyz.yluo.ruisiapp.adapter.ForumListAdapter;
import xyz.yluo.ruisiapp.data.ForumListData;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-19.
 * 板块列表fragemnt
 */
public class FrageForumList extends BaseFragment implements ListItemClickListener,View.OnClickListener{
    protected SwipeRefreshLayout refreshLayout;
    private List<ForumListData> datas = null;
    private ForumListAdapter adapter = null;
    private boolean isSetForumToDataBase = false;
    private SharedPreferences sharedPreferences;
    //10分钟的缓存时间
    private static final int UPDATE_TIME = 1000*600;
    private static final String KEY = "FORUM_UPDATE_KEY";

    public static FrageForumList newInstance(boolean isLogin) {
        Bundle args = new Bundle();
        args.putBoolean("isLogin",isLogin);
        FrageForumList fragment = new FrageForumList();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        MyDB myDB = new MyDB(getActivity().getApplicationContext(), MyDB.MODE_READ);
        datas = myDB.getForums();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        initToolbar(false,"板块列表");
        addToolbarMenu(R.drawable.ic_search_white_24dp).setOnClickListener(this);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        //设置可以滑出底栏
        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0,0,0, (int) getResources().getDimension(R.dimen.BottomBarHeight));
        //刷新
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);

        //先从数据库读出数据
        adapter = new ForumListAdapter(datas, getActivity(),this);
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

        recyclerView.addItemDecoration(new MyGridDivider(1));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        //判断是否真正的需要请求服务器
        //获得新的数据
        long time =  sharedPreferences.getLong(KEY,0);
        if(System.currentTimeMillis()-time>UPDATE_TIME||datas==null||datas.size()==0){
            Log.e("板块列表","过了缓存时间需要刷新");
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            getData();
        }

        return mRootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_toolbar;
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

    @Override
    public void onListItemClick(View v, int position) {
        int fid = datas.get(position).getFid();
        //几个特殊的板块
        String title = datas.get(position).getTitle();
        if (App.IS_SCHOOL_NET && (fid==561|| fid==157 || fid==13)) {
            ArticleListImage.open(getActivity(),fid,title);
        } else {
            ArticleList.open(getActivity(), fid,title);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.menu:
                if(isLogin()){
                    switchActivity(ActivitySearch.class);
                }
                break;
        }
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
                App.FORMHASH = ress;
            }

            for (Element ele : elements) {
                String header = ele.select("h2").text();
                simpledatas.add(new ForumListData(true, header,"0",-1));
                for (Element tmp : ele.select("li")) {
                    String todayNew = tmp.select("span.num").text();
                    tmp.select("span.num").remove();
                    String title = tmp.text().replace("西电睿思", "");
                    String titleUrl = tmp.select("a").attr("href");
                    int fid = GetId.getFroumFid(titleUrl);
                    simpledatas.add(new ForumListData(false, title, todayNew, fid));
                }
            }

            if(!isSetForumToDataBase){
                MyDB myDB = new MyDB(getActivity().getApplicationContext(), MyDB.MODE_WRITE);
                myDB.setForums(simpledatas);
                isSetForumToDataBase = true;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(KEY,System.currentTimeMillis());
                editor.apply();
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
