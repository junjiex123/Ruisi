package xyz.yluo.ruisiapp.fragment;

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

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.SearchActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.adapter.ForumsAdapter;
import xyz.yluo.ruisiapp.model.Category;
import xyz.yluo.ruisiapp.model.Forum;
import xyz.yluo.ruisiapp.utils.RuisUtils;
import xyz.yluo.ruisiapp.utils.UrlUtils;
import xyz.yluo.ruisiapp.widget.CircleImageView;

/**
 * Created by free2 on 16-3-19.
 * 板块列表fragemnt
 */
public class FrageForumList extends BaseLazyFragment implements View.OnClickListener {
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        userImg = (CircleImageView) mRootView.findViewById(R.id.img);
        formsList = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        formsList.setClipToPadding(false);
        formsList.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.BottomBarHeight));
        mRootView.findViewById(R.id.search).setOnClickListener(this);
        adapter = new ForumsAdapter(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == ForumsAdapter.TYPE_HEADER) {
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
        initAvater();
    }

    @Override
    public void onUserVisible() {
        if (lastLoginState != App.ISLOGIN(getActivity())) {
            lastLoginState = !lastLoginState;
            initForums(lastLoginState);
            initAvater();
        }
    }

    @Override
    public void ScrollToTop() {
        if (forumDatas != null && forumDatas.size() > 0)
            formsList.scrollToPosition(0);
    }

    private void initAvater() {
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

    void initForums(boolean loginstate) {
        new GetForumList().execute(loginstate);
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
    private class GetForumList extends AsyncTask<Boolean, Void, List<Category>> {
        @Override
        protected List<Category> doInBackground(Boolean... params) {
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
                return forumDatas;
            }

            return RuisUtils.getForums(getActivity(), params[0]);
        }

        @Override
        protected void onPostExecute(List<Category> ss) {
            if (ss == null || ss.size() == 0) {
                Toast.makeText(getActivity(), "获取板块列表失败", Toast.LENGTH_LONG).show();
            }
            forumDatas = ss;
            adapter.setDatas(forumDatas);
        }
    }
}
