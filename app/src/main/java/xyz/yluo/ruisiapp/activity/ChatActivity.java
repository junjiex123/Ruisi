package xyz.yluo.ruisiapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ChatListAdapter;
import xyz.yluo.ruisiapp.data.ChatListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.ImeUtil;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-30.
 * 消息聊天 activity
 * TODO 支持翻页。。。。目前只能看最后一页
 */
public class ChatActivity extends BaseActivity {

    private RecyclerView recycler_view;
    //private MyReplyView myReplyView;
    private SwipeRefreshLayout refreshLayout;

    private List<ChatListData> datas = new ArrayList<>();
    private ChatListAdapter adapter;

    private String replyUrl = "";
    private String username = "消息";
    private String url = "";
    private String touid = "";

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
        recycler_view = (RecyclerView) findViewById(R.id.topic_recycler_view);
        //myReplyView = (MyReplyView) findViewById(R.id.replay_bar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.topic_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        try {
            Bundle bundle = this.getIntent().getExtras();
            username = bundle.getString("username");
            url = bundle.getString("url");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(username);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        new GetDataTask().execute(url);
        //myReplyView.setListener(new ReplyBarListner() {
        //    @Override
        //    public void btnSendClick(String input) {
        //        post_reply(input);
        //    }
        //});
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

    private void post_reply(final String text) {

        if (text.isEmpty()) {
            Toast.makeText(getApplicationContext(), "你还没有输入内容！！！", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progress;
            progress = ProgressDialog.show(this, "正在发送", "请等待", true);
            Map<String, String> params = new HashMap<>();
            params.put("formhash", PublicData.FORMHASH);
            params.put("touid", touid);
            params.put("message", text);
            HttpUtil.post(getApplicationContext(), replyUrl, params, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    String res = new String(response);
                    if (res.contains("操作成功")) {
                        ImeUtil.hide_ime(ChatActivity.this);
                        progress.dismiss();
                        String userImage = UrlUtils.getAvaterurlm(PublicData.USER_UID);
                        datas.add(new ChatListData(1, userImage, text, "刚刚"));
                        adapter.notifyItemInserted(datas.size() - 1);
                        recycler_view.scrollToPosition(datas.size());
                        //myReplyView.clearText();
                        Toast.makeText(getApplicationContext(), "回复成功！！", Toast.LENGTH_SHORT).show();
                    } else {
                        progress.dismiss();
                        if (res.contains("两次发送短消息太快")) {
                            Toast.makeText(getApplicationContext(), "两次发送短消息太快，请稍候再发送", Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println(res);
                            Toast.makeText(getApplicationContext(), "由于未知原因发表失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    Toast.makeText(getApplicationContext(), "网络错误！！！", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            });
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
