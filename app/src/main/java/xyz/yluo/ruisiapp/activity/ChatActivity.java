package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyReplyView;
import xyz.yluo.ruisiapp.adapter.ChatListAdapter;
import xyz.yluo.ruisiapp.data.ChatListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.utils.DimmenUtils;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-30.
 * 消息聊天 activity
 * TODO 支持翻页。。。。目前只能看最后一页
 */
public class ChatActivity extends BaseActivity implements MyReplyView.replyCompeteCallBack {

    private RecyclerView recycler_view;
    private SwipeRefreshLayout refreshLayout;

    private List<ChatListData> datas = new ArrayList<>();
    private ChatListAdapter adapter;
    private String replyUrl = "";
    private String username = "消息";
    private String url = "";
    private String touid = "";
    private long replyTime = 0;
    private AutoGetTask task;


    public static void open(Context context, String username, String url) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("username", username);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_toolbar_btn);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        FloatingActionButton btn_chat = (FloatingActionButton) findViewById(R.id.btn);
        btn_chat.setImageResource(R.drawable.ic_reply_white_24dp);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new ChatListAdapter(this, datas);
        adapter.disableLoadMore();
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);
        recycler_view.setClipToPadding(false);
        recycler_view.setPadding(0,0,0, DimmenUtils.dip2px(this,50));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datas.clear();
                adapter.notifyDataSetChanged();
                getData(true);
            }
        });
        Bundle bundle = this.getIntent().getExtras();
        username = bundle.getString("username");
        initToolBar(true,username);
        url = bundle.getString("url");
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyReplyView dialog = MyReplyView.newInstance(replyUrl, MyReplyView.REPLY_HY, replyTime,
                        false, "回复：" + username, touid);
                dialog.setCallBack(ChatActivity.this);
                dialog.show(getFragmentManager(), "chate");
            }
        });
        task = new AutoGetTask(this);
        getData(true);
    }


    private static class AutoGetTask implements Runnable {

        private final WeakReference<ChatActivity> act;

        private AutoGetTask(ChatActivity a) {
            act = new WeakReference<>(a);
        }

        @Override
        public void run() {
            ChatActivity aa = act.get();
            if(aa!=null){
                aa.getData(false);
            }

        }
    }

    private void getData(boolean needRefresh){
        if(needRefresh){
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
        }
        Log.e("chat","get data...");
        new GetDataTask().execute(url);
    }

    @Override
    public void onReplyFinish(int status, String txt) {
        if (status == RESULT_OK) {
            replyTime = System.currentTimeMillis();
            new GetDataTask().execute(url);
        }
    }

    private class GetDataTask extends AsyncTask<String, Void, List<ChatListData>> {
        @Override
        protected List<ChatListData> doInBackground(String... params) {
            final String url = params[0];
            final List<ChatListData> tepdata = new ArrayList<>();
            HttpUtil.SyncGet(getApplicationContext(), url, new TextResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    int type;
                    //list 所有楼数据
                    Document doc = Jsoup.parse(response);
                    String temps = doc.select("form#pmform").attr("action");
                    if (!temps.isEmpty()) {
                        replyUrl = temps;
                        touid = doc.select("input[name=touid]").attr("value");
                    } else {
                        touid = GetId.getid("touid=",url);
                        replyUrl = "home.php?mod=spacecp&ac=pm&op=send&pmid=" + touid + "&daterange=0&pmsubmit=yes&mobile=2";
                    }
                    Elements elements = doc.select(".msgbox.b_m");
                    //还没有消息
                    if (elements.text().contains("当前没有相应的短消息")) {
                        String userimg = UrlUtils.getAvaterurlm(touid);
                        tepdata.add(new ChatListData(0, userimg, "给我发消息吧...", "刚刚"));
                    } else {
                        Elements listdata = elements.select(".cl");
                        for (Element temp : listdata) {
                            //左边
                            if (temp.attr("class").contains("friend_msg")) {
                                type = 0;
                            } else {//右边
                                type = 1;
                            }
                            String userimg = temp.select(".avat").select("img").attr("src");
                            String content = temp.select(".dialog_t").html();
                            String posttime = temp.select(".date").text();
                            tepdata.add(new ChatListData(type, userimg, content, posttime));
                        }
                    }

                }
            });
            return tepdata;
        }

        @Override
        protected void onPostExecute(List<ChatListData> tepdata) {
            if(datas.size()==0){
                datas.addAll(tepdata);
                adapter.notifyDataSetChanged();
            }else if(tepdata.size()>0){
                //处理增加部分
                String content = datas.get(datas.size()-1).getContent();
                int type = datas.get(datas.size()-1).getType();

                int equalpos = -1;
                for(int i=0;i<tepdata.size();i++){
                    String contentadd = tepdata.get(i).getContent();
                    int typeadd = tepdata.get(i).getType();
                    if(content.equals(contentadd)&&typeadd==type){
                        equalpos = i;
                        break;
                    }
                }

                int add = 0;
                for(int i = equalpos+1;i<tepdata.size();i++){
                    datas.add(tepdata.get(i));
                    add++;
                }
                adapter.notifyItemRangeInserted(datas.size()-add,add);
            }

            recycler_view.scrollToPosition(datas.size());
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 400);

            //25s自动加载一次
            recycler_view.removeCallbacks(task);
            recycler_view.postDelayed(task,25000);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
