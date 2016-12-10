package xyz.yluo.ruisiapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.SearchActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.adapter.ForumsAdapter;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.model.ForumListData;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;
import xyz.yluo.ruisiapp.widget.CircleImageView;
import xyz.yluo.ruisiapp.widget.MyGridDivider;

/**
 * Created by free2 on 16-3-19.
 * 板块列表fragemnt
 */
public class FrageForumList extends BaseLazyFragment implements View.OnClickListener {
    protected SwipeRefreshLayout refreshLayout;
    private List<ForumListData> datas = new ArrayList<>();
    private List<ForumListData> starDatas = new ArrayList<>();
    private ForumsAdapter adapter = null;
    private boolean isSetForumToDataBase = false;
    private SharedPreferences sharedPreferences;
    private CircleImageView userImg;
    private RecyclerView recyclerView;
    //15分钟的缓存时间
    private static final int UPDATE_TIME = 1500 * 600;
    private static final String KEY = "FORUM_UPDATE_KEY";
    private boolean lastLoginState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("FrageForumList", "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        userImg = (CircleImageView) mRootView.findViewById(R.id.img);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.BottomBarHeight));
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        mRootView.findViewById(R.id.search).setOnClickListener(this);

        adapter = new ForumsAdapter(getActivity(), starDatas, datas);
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
        userImg.setOnClickListener(this);
        recyclerView.addItemDecoration(new MyGridDivider(1, ContextCompat.getColor(getActivity(), R.color.colorDivider)));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this::getData);
        return mRootView;
    }

    @Override
    public void onFirstUserVisible() {
        Log.e("FrageForumList", "onFirstUserVisible");
        lastLoginState = App.ISLOGIN(getActivity());
        MyDB myDB = new MyDB(getActivity().getApplicationContext());

        starDatas.clear();
        starDatas.addAll(myDB.getStarForums());

        datas.clear();
        datas.addAll(myDB.getForums());
        adapter.notifyDataSetChanged();

        //判断是否真正的需要请求服务器
        //获得新的数据
        long time = sharedPreferences.getLong(KEY, 0);
        if (System.currentTimeMillis() - time > UPDATE_TIME || datas == null || datas.size() == 0) {
            Log.e("板块列表", "过了缓存时间需要刷新");
            getData();
        }

        refreshAvaterView();
    }

    @Override
    public void onUserVisible() {
        Log.e("FrageForumList", "onUserVisible");
        if (lastLoginState != App.ISLOGIN(getActivity())) {
            lastLoginState = !lastLoginState;
            getData();
            refreshAvaterView();
        }
    }

    private void refreshAvaterView() {
        lastLoginState = App.ISLOGIN(getActivity());
        if (lastLoginState) {
            Picasso.with(getActivity()).load(UrlUtils.getAvaterurls(App.getUid(getActivity())))
                    .placeholder(R.drawable.image_placeholder)
                    .into(userImg);
        } else {
            userImg.setImageResource(R.drawable.image_placeholder);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forums;
    }


    private void getData() {
        refreshLayout.setRefreshing(true);
        String url = "forum.php?forumlist=1&mobile=2";
        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetForumList().execute(new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                refreshLayout.postDelayed(() -> refreshLayout.setRefreshing(false), 300);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                if (isLogin()) {
                    switchActivity(SearchActivity.class);
                }
                break;
            case R.id.img:
                if (lastLoginState) {
                    String imgurl = UrlUtils.getAvaterurlb(App.getUid(getActivity()));
                    UserDetailActivity.open(getActivity(), App.getName(getActivity()), imgurl);
                } else {
                    switchActivity(LoginActivity.class);
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
            Elements elements = Jsoup.parse(response).select("#wp.wp.wm").select(".bm.bmw.fl");
            for (Element ele : elements) {
                String header = ele.select("h2").text();
                simpledatas.add(new ForumListData(true, header, "0", -1));
                for (Element tmp : ele.select("li")) {
                    String todayNew = tmp.select("span.num").text();
                    tmp.select("span.num").remove();
                    String title = tmp.text().replace("西电睿思", "");
                    String titleUrl = tmp.select("a").attr("href");
                    int fid = GetId.getFroumFid(titleUrl);
                    simpledatas.add(new ForumListData(false, title, todayNew, fid));
                }
            }

            MyDB myDB = new MyDB(getActivity());
            if (!isSetForumToDataBase) {
                myDB.setForums(simpledatas);
                isSetForumToDataBase = true;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(KEY, System.currentTimeMillis());
                editor.apply();
            }

            starDatas.clear();
            starDatas.addAll(myDB.getStarForums());
            return simpledatas;
        }

        @Override
        protected void onPostExecute(List<ForumListData> simpledatas) {
            if (simpledatas.size() > 0) {
                datas.clear();
                datas.addAll(simpledatas);
                adapter.notifyDataSetChanged();
            }
            refreshLayout.setRefreshing(false);
        }
    }
}
