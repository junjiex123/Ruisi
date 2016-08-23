package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.MessageAdapter;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.MessageData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;

//回复我的
//// TODO: 16-8-22  add 提到我的home.php?mod=space&do=notice&view=mypost&type=at&mobile=2

public class FrageMessage extends BaseFragment {
    private static final String Tag = "==FrageMessage==";
    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;
    private MessageAdapter adapter;
    private List<MessageData> datas;
    private int index = 0;
    int last_message_id  = 0;
    int current_noticeid = 1;
    public FrageMessage() {
        datas = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        recycler_view = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(getActivity(), datas, new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                if (position != index) {
                    index = position;
                    refresh();
                }

            }
        });
        recycler_view.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 500);
        setCloseIcon();
        return mRootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected String getTitle() {
        return "我的消息";
    }


    private void refresh() {
        //记录上次已读消息游标
        last_message_id = PreferenceManager.getDefaultSharedPreferences(getActivity()).
                getInt(App.NOTICE_MESSAGE_KEY, 0);
        current_noticeid = last_message_id;
        Log.d("id id",last_message_id+"");
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        getData();
    }

    private void getData() {
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
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }

    private void finishGetData(List<MessageData> temdatas) {
        datas.clear();
        datas.addAll(temdatas);
        adapter.notifyDataSetChanged();

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    //获得回复我的
    private class GetUserReplyTask extends AsyncTask<String, Void, List<MessageData>> {
        @Override
        protected List<MessageData> doInBackground(String... params) {
            //pmbox
            List<MessageData> tempdatas = new ArrayList<>();
            Elements lists = Jsoup.parse(params[0]).select(".nts").select("dl.cl");
            for (Element tmp:lists) {
                int  noticeId = Integer.parseInt(tmp.attr("notice"));
                String authorImage = tmp.select(".avt").select("img").attr("src");
                String time = tmp.select(".xg1.xw0").text();
                String authorTitle  ="";
                String titleUrl = "";
                String content = tmp.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").text().replace("查看", "");
                if (content.isEmpty()) {
                    //这是系统消息
                    authorTitle = "系统消息";
                    titleUrl = tmp.select(".ntc_body").select("a").attr("href");
                    authorImage = App.getBaseUrl()+authorImage;
                    content = tmp.select(".ntc_body").text();
                }else{
                    //这是回复消息
                    authorTitle = tmp.select(".ntc_body").select("a[href^=home.php]").text() + " 回复了我";
                    titleUrl = tmp.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").attr("href");
                }

                boolean isRead = (noticeId<=last_message_id);
                if(noticeId>current_noticeid){
                    current_noticeid = noticeId;
                }
                tempdatas.add(new MessageData(ListType.REPLAYME, authorTitle, titleUrl, authorImage, time, isRead, content));
            }

            if(last_message_id<current_noticeid){
                SharedPreferences prf =  PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor =  prf.edit();
                editor.putInt(App.NOTICE_MESSAGE_KEY,current_noticeid);
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
        }
    }
}
