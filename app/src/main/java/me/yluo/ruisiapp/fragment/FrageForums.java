package me.yluo.ruisiapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.activity.BaseActivity;
import me.yluo.ruisiapp.activity.LoginActivity;
import me.yluo.ruisiapp.activity.SearchActivity;
import me.yluo.ruisiapp.activity.UserDetailActivity;
import me.yluo.ruisiapp.adapter.ForumsAdapter;
import me.yluo.ruisiapp.model.Category;
import me.yluo.ruisiapp.model.Forum;
import me.yluo.ruisiapp.model.WaterData;
import me.yluo.ruisiapp.myhttp.SyncHttpClient;
import me.yluo.ruisiapp.utils.GetId;
import me.yluo.ruisiapp.utils.RuisUtils;
import me.yluo.ruisiapp.utils.UrlUtils;
import me.yluo.ruisiapp.widget.CircleImageView;

/**
 * Created by free2 on 16-3-19.
 * 板块列表fragemnt
 */
public class FrageForums extends BaseLazyFragment implements View.OnClickListener {
    private ForumsAdapter adapter = null;
    private SharedPreferences sharedPreferences;
    private CircleImageView userImg;
    private RecyclerView formsList;
    //15分钟的缓存时间
    private static final int UPDATE_TIME = 1500 * 600;
    private static final String KEY = "FORUM_UPDATE_KEY";
    private boolean lastLoginState;

    private List<Category> forumDatas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        forumDatas = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        userImg = (CircleImageView) mRootView.findViewById(R.id.img);
        formsList = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        formsList.setClipToPadding(false);
        formsList.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.bottombarHeight));
        mRootView.findViewById(R.id.search).setOnClickListener(this);
        adapter = new ForumsAdapter(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = adapter.getItemViewType(position);
                if (type == ForumsAdapter.TYPE_HEADER || type == ForumsAdapter.TYPE_WATER) {
                    return 4;
                } else {
                    return 1;
                }
            }
        });
        userImg.setOnClickListener(this);
        formsList.setLayoutManager(layoutManager);
        formsList.setAdapter(adapter);
        return mRootView;
    }

    @Override
    public void onFirstUserVisible() {
        //判断是否真正的需要请求服务器
        //获得新的数据
        long time = sharedPreferences.getLong(KEY, 0);
        if (System.currentTimeMillis() - time > UPDATE_TIME) {
            Log.d("板块列表", "过了缓存时间需要刷新");
            //todo update batch
        }
        lastLoginState = App.ISLOGIN(getActivity());
        initForums(lastLoginState);
        initAvatar();
    }

    @Override
    public void onUserVisible() {
        if (lastLoginState != App.ISLOGIN(getActivity())) {
            lastLoginState = !lastLoginState;
            initForums(lastLoginState);
            initAvatar();
        }
    }

    @Override
    public void ScrollToTop() {
        if (forumDatas != null && forumDatas.size() > 0)
            formsList.scrollToPosition(0);
    }

    private void initAvatar() {
        lastLoginState = App.ISLOGIN(getActivity());
        if (lastLoginState) {
            RuisUtils.LoadMyAvatar(new WeakReference<>(getActivity()),
                    App.getUid(getActivity()),
                    new WeakReference<>(userImg), "s");
        } else {
            userImg.setImageResource(R.drawable.image_placeholder);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forums;
    }

    void initForums(boolean loginstate) {
        new GetForumList().execute(loginstate);
        if (App.IS_SCHOOL_NET)
            new GetWaterBTask().execute();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                BaseActivity b = (BaseActivity) getActivity();
                if (b.isLogin()) {
                    switchActivity(SearchActivity.class);
                }
                break;
            case R.id.img:
                if (lastLoginState) {
                    String imgurl = UrlUtils.getAvaterurlb(App.getUid(getActivity()));
                    UserDetailActivity.open(getActivity(), App.getName(getActivity()),
                            imgurl, App.getUid(getActivity()));
                } else {
                    switchActivity(LoginActivity.class);
                }
                break;
        }
    }

    //获取首页板块数据 板块列表
    private class GetForumList extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... params) {
            boolean b = params[0];
            if (!b && forumDatas != null && forumDatas.size() > 0) {
                //由登陆变为不登录 只需移除需要登录的板块
                for (Category c : forumDatas) {
                    if (c.login) {
                        forumDatas.remove(c);
                        continue;
                    }
                    for (Forum f : c.forums) {
                        if (f.login) {
                            c.forums.remove(f);
                        }
                    }
                }

                return null;
            }

            forumDatas = RuisUtils.getForums(getActivity(), params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (forumDatas == null || forumDatas.size() == 0) {
                Toast.makeText(getActivity(), "获取板块列表失败", Toast.LENGTH_LONG).show();
            }

            adapter.setDatas(forumDatas);
        }
    }

    //获得水神版
    private class GetWaterBTask extends AsyncTask<Void, Void, List<WaterData>> {
        @Override
        protected List<WaterData> doInBackground(Void... voids) {
            List<WaterData> temps = new ArrayList<>();
            String url = "http://rs.xidian.edu.cn/forum.php";
            Document doc;
            try {
                doc = Jsoup.connect(url).userAgent(SyncHttpClient.DEFAULT_USER_AGENT).get();
            } catch (IOException e) {
                e.printStackTrace();
                return temps;
            }

            Elements waters = doc.select("#portal_block_317").select("li");
            for (Element e : waters) {
                Elements es = e.select("p").select("a[href^=home.php?mod=space]");
                String uid = GetId.getId("uid=", es.attr("href"));
                String imgSrc = e.select("img").attr("src");
                String uname = es.text();
                int num = 0;
                if (e.select("p").size() > 1) {
                    if (e.select("p").get(1).text().contains("帖数")) {
                        num = GetId.getNumber(e.select("p").get(1).text());
                    }
                }
                temps.add(new WaterData(uname, uid, num, imgSrc));
                if (temps.size() >= 16) break;
            }
            return temps;
        }

        @Override
        protected void onPostExecute(List<WaterData> data) {
            super.onPostExecute(data);
            if (data.size() == 0) {
                return;
            }
            adapter.setWaterData(data);
        }
    }
}
