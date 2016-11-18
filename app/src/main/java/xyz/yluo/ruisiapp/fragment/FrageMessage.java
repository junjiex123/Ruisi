package xyz.yluo.ruisiapp.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.BaseAdapter;
import xyz.yluo.ruisiapp.adapter.MessageAdapter;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.model.ListType;
import xyz.yluo.ruisiapp.model.MessageData;

//回复我的
//// TODO: 16-8-22  add 提到我的home.php?mod=space&do=notice&view=mypost&type=at&mobile=2

public class FrageMessage extends BaseFragment {
    protected RecyclerView recycler_view;
    private View pm_badge;
    protected SwipeRefreshLayout refreshLayout;
    private MessageAdapter adapter;
    private List<MessageData> datas;
    private int index = 0;
    int last_message_id = 0;
    int current_noticeid = 1;
    private boolean lastLoginState = false;
    private boolean isHavePm;

    public static FrageMessage newInstance(boolean isHaveReply, boolean isHavePm) {
        Bundle args = new Bundle();
        args.putBoolean("isHaveReply", isHaveReply);
        args.putBoolean("isHavePm", isHavePm);
        FrageMessage fragment = new FrageMessage();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datas = new ArrayList<>();
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            boolean isHaveReply = bundle.getBoolean("isHaveReply", false);
            isHavePm = bundle.getBoolean("isHavePm", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        pm_badge = mRootView.findViewById(R.id.pm_badge);
        if (isHavePm) {
            pm_badge.setVisibility(View.VISIBLE);
        }
        recycler_view = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        //设置可以滑出底栏
        recycler_view.setClipToPadding(false);
        recycler_view.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.BottomBarHeight));
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(getActivity(), datas);
        recycler_view.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(() -> getData(false));
        RadioGroup swictchMes = (RadioGroup) mRootView.findViewById(R.id.btn_change);
        swictchMes.setOnCheckedChangeListener((radioGroup, id) -> {
            int pos = 1;
            if (id == R.id.btn_reply) {
                pos = 0;
            }
            if (pos != index) {
                index = pos;
                getData(true);
            }
        });

        getData(false);
        return mRootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_msg_hot;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && (lastLoginState != App.ISLOGIN(getActivity()))) {
            getData(true);
            lastLoginState = !lastLoginState;
        }
    }

    private void getData(boolean needRefresh) {
        lastLoginState = App.ISLOGIN(getActivity());
        //记录上次已读消息游标
        if (!App.ISLOGIN(getActivity())) {
            adapter.changeLoadMoreState(BaseAdapter.STATE_NEED_LOGIN);
            refreshLayout.setRefreshing(false);
            return;
        }

        if (needRefresh) {
            refreshLayout.setRefreshing(true);
        }

        last_message_id = getActivity().getSharedPreferences(App.MY_SHP_NAME, Activity.MODE_PRIVATE)
                .getInt(App.NOTICE_MESSAGE_KEY, 0);
        current_noticeid = last_message_id;
        //reply
        String url = "home.php?mod=space&do=notice&mobile=2";
        if (index != 0) {
            //pm
            url = "home.php?mod=space&do=pm&mobile=2";
        }
        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if (index != 0) {
                    new GetUserPmTask().execute(res);
                } else {
                    new GetUserReplyTask().execute(res);
                }

            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                refreshLayout.postDelayed(() -> refreshLayout.setRefreshing(false), 500);
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_FAIL);
            }
        });
    }

    private void finishGetData(List<MessageData> temdatas) {
        datas.clear();
        datas.addAll(temdatas);
        adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_NOTHING);
        adapter.notifyDataSetChanged();

        refreshLayout.postDelayed(() -> refreshLayout.setRefreshing(false), 500);
    }

    //获得回复我的
    private class GetUserReplyTask extends AsyncTask<String, Void, List<MessageData>> {
        @Override
        protected List<MessageData> doInBackground(String... params) {
            //pmbox
            List<MessageData> tempdatas = new ArrayList<>();
            Elements lists = Jsoup.parse(params[0]).select(".nts").select("dl.cl");
            for (Element tmp : lists) {
                int noticeId = Integer.parseInt(tmp.attr("notice"));
                String authorImage = tmp.select(".avt").select("img").attr("src");
                String time = tmp.select(".xg1.xw0").text();
                String authorTitle = "";
                String titleUrl = "";
                String content = tmp.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").text().replace("查看", "");
                if (content.isEmpty()) {
                    //这是系统消息
                    authorTitle = "系统消息";
                    titleUrl = tmp.select(".ntc_body").select("a").attr("href");
                    authorImage = App.getBaseUrl() + authorImage;
                    content = tmp.select(".ntc_body").text();
                } else {
                    //这是回复消息
                    authorTitle = tmp.select(".ntc_body").select("a[href^=home.php]").text() + " 回复了我";
                    titleUrl = tmp.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").attr("href");
                }

                boolean isRead = (noticeId <= last_message_id);
                if (noticeId > current_noticeid) {
                    current_noticeid = noticeId;
                }
                tempdatas.add(new MessageData(ListType.REPLAYME, authorTitle, titleUrl, authorImage, time, isRead, content));
            }

            if (last_message_id < current_noticeid) {
                SharedPreferences prf = getActivity().getSharedPreferences(App.MY_SHP_NAME, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prf.edit();
                editor.putInt(App.NOTICE_MESSAGE_KEY, current_noticeid);
                editor.apply();
            }
            return tempdatas;
        }

        @Override
        protected void onPostExecute(List<MessageData> tempdatas) {
            finishGetData(tempdatas);
            //todo 记录是否未读
        }
    }

    //获得pm消息
    private class GetUserPmTask extends AsyncTask<String, Void, List<MessageData>> {
        @Override
        protected List<MessageData> doInBackground(String... params) {
            //pmbox
            List<MessageData> temdatas = new ArrayList<>();
            Elements lists = Jsoup.parse(params[0]).select(".pmbox").select("ul").select("li");
            for (Element tmp : lists) {
                boolean isRead = true;
                if (tmp.select(".num").text().length() > 0) {
                    isRead = false;
                }
                String title = tmp.select(".cl").select(".name").text();
                String time = tmp.select(".cl.grey").select(".time").text();
                tmp.select(".cl.grey").select(".time").remove();
                String content = tmp.select(".cl.grey").text();
                String authorImage = tmp.select("img").attr("src");
                String titleUrl = tmp.select("a").attr("href");
                temdatas.add(new MessageData(ListType.MYMESSAGE, title, titleUrl, authorImage, time, isRead, content));
            }
            return temdatas;
        }

        @Override
        protected void onPostExecute(List<MessageData> tempdatas) {
            finishGetData(tempdatas);
            if (isHavePm) {
                isHavePm = false;
                pm_badge.setVisibility(View.GONE);
            }
        }
    }
}
