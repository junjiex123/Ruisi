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

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.adapter.ChatListAdapter;
import xyz.yluo.ruisiapp.data.ChatListData;
import xyz.yluo.ruisiapp.fragment.FrageReplyDialog;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-30.
 * 消息聊天 activity
 * TODO 支持翻页。。。。目前只能看最后一页
 */
public class ChatActivity extends BaseActivity implements FrageReplyDialog.replyCompeteCallBack {

    private RecyclerView recycler_view;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton btn_chat;

    private List<ChatListData> datas = new ArrayList<>();
    private ChatListAdapter adapter;

    private String replyUrl = "";
    private String username = "消息";
    private String url = "";
    private String touid = "";
    private long replyTime = 0;
    private MyToolBar myToolBar;

    public static void open(Context context, String username, String url) {
        /*isopenfromwebview 是从webview打开的是新建的回话*/
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("username", username);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myToolBar = (MyToolBar) findViewById(R.id.myToolBar);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        btn_chat = (FloatingActionButton) findViewById(R.id.btn_chat);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new ChatListAdapter(this, datas);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);
        refresh();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        Bundle bundle = this.getIntent().getExtras();
        username = bundle.getString("username");
        url = bundle.getString("url");
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrageReplyDialog dialog = FrageReplyDialog.newInstance(replyUrl, FrageReplyDialog.REPLY_HY, replyTime,
                        false, "回复：" + username, touid);
                dialog.setCallBack(ChatActivity.this);
                dialog.show(getFragmentManager(), "chate");
            }
        });

        myToolBar.setTitle(username);
        myToolBar.setBackEnable(this);
        new GetDataTask().execute(url);
    }

    private void refresh() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        datas.clear();
        adapter.notifyDataSetChanged();
        new GetDataTask().execute(url);
    }

    @Override
    public void onReplyFinish(int status, String txt) {
        Log.i("reply dialog callbak", "status:" + status + " info:" + txt);
        if (status == RESULT_OK) {
            replyTime = System.currentTimeMillis();
            String userImage = UrlUtils.getAvaterurlm(App.USER_UID);
            datas.add(new ChatListData(1, userImage, txt, "刚刚"));
            adapter.notifyItemInserted(datas.size() - 1);
            recycler_view.scrollToPosition(datas.size());
        }
    }

    private class GetDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            final String url = params[0];
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
                        touid = GetId.getTouid(url);
                        replyUrl = "home.php?mod=spacecp&ac=pm&op=send&pmid=" + touid + "&daterange=0&pmsubmit=yes&mobile=2";
                    }
                    Elements elements = doc.select(".msgbox.b_m");
                    //还没有消息
                    if (elements.text().contains("当前没有相应的短消息")) {
                        String userimg = UrlUtils.getAvaterurlm(touid);
                        datas.add(new ChatListData(0, userimg, "给我发消息吧...", "刚刚"));
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
                            datas.add(new ChatListData(type, userimg, content, posttime));
                        }
                    }

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
            recycler_view.scrollToPosition(datas.size());
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);
        }
    }

}
