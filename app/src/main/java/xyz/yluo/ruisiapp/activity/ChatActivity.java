package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MySmileyPicker;
import xyz.yluo.ruisiapp.adapter.ChatListAdapter;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.model.ChatListData;
import xyz.yluo.ruisiapp.utils.DimmenUtils;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.ImeUtil;
import xyz.yluo.ruisiapp.utils.PostHandler;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-30.
 * 消息聊天 activity
 * TODO 支持翻页。。。。目前只能看最后一页
 */
public class ChatActivity extends BaseActivity {

    private RecyclerView list;
    private SwipeRefreshLayout refreshLayout;
    private List<ChatListData> datas = new ArrayList<>();
    private ChatListAdapter adapter;
    private String replyUrl = "";
    private String url = "";
    private String touid = "";
    private long replyTime = 0;
    private EditText input;
    private MySmileyPicker smileyPicker;

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
        setContentView(R.layout.activity_chat);

        smileyPicker = new MySmileyPicker(this);
        list = (RecyclerView) findViewById(R.id.list);
        input = (EditText) findViewById(R.id.input_aera);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new ChatListAdapter(this, datas);
        adapter.disableLoadMore();
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datas.clear();
                adapter.notifyDataSetChanged();
                getData(true);
            }
        });
        Bundle bundle = this.getIntent().getExtras();
        initToolBar(true, bundle.getString("username"));
        url = bundle.getString("url");

        smileyPicker.setListener(new MySmileyPicker.OnItemClickListener() {
            @Override
            public void itemClick(String str, Drawable a) {
                PostHandler handler = new PostHandler(input);
                handler.insertSmiley("{:" + str + ":}", a);
            }
        });

        findViewById(R.id.action_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_click();
            }
        });

        findViewById(R.id.btn_smiley).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ((ImageView) view).setImageResource(R.drawable.ic_edit_emoticon_accent_24dp);
                smileyPicker.showAtLocation(view, Gravity.BOTTOM, 32, DimmenUtils.dip2px(ChatActivity.this, 52));
                smileyPicker.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ((ImageView) view).setImageResource(R.drawable.ic_edit_emoticon_24dp);
                    }
                });
            }
        });
        getData(true);
    }


    private void getData(boolean needRefresh) {
        if (needRefresh) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
        }
        Log.e("chat", "get data...");

        new GetDataTask().execute(url);
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
                        touid = GetId.getid("touid=", url);
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
            if (datas.size() == 0) {
                datas.addAll(tepdata);
                adapter.notifyDataSetChanged();
            } else if (tepdata.size() > 0) {
                //处理增加部分
                String content = datas.get(datas.size() - 1).getContent();
                int type = datas.get(datas.size() - 1).getType();
                int equalpos = -1;
                for (int i = 0; i < tepdata.size(); i++) {
                    String contentadd = tepdata.get(i).getContent();
                    Log.e("data", contentadd);
                    int typeadd = tepdata.get(i).getType();
                    if (content.equals(contentadd) && typeadd == type) {
                        equalpos = i;
                        break;
                    }
                }

                int add = 0;
                for (int i = equalpos + 1; i < tepdata.size(); i++) {
                    datas.add(tepdata.get(i));
                    add++;
                }
                adapter.notifyItemRangeInserted(datas.size() - add, add);
            }

            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                    list.scrollToPosition(datas.size() - 1);
                }
            }, 400);
        }
    }

    private void send_click() {
        String text = input.getText().toString();
        int len = 0;
        try {
            len = text.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (len == 0) {
            //input.setError("你还没写内容呢!");
            final Snackbar s = Snackbar.make(list, "你还没写内容呢", Snackbar.LENGTH_SHORT);
            s.setAction("好的", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    s.dismiss();
                }
            })
                    .show();
        } else {
            //时间检测
            if (!(System.currentTimeMillis() - replyTime > 15000)) {
                Snackbar.make(list, "还没到15s呢，再等等吧！", Snackbar.LENGTH_SHORT).show();
                return;
            }
            //字数补齐补丁
            if (len < 13) {
                int need = 14 - len;
                for (int i = 0; i < need; i++) {
                    text += " ";
                }
            }

            final Snackbar snackbar = Snackbar.make(list, "回复发表中...", Snackbar.LENGTH_LONG);
            ImeUtil.hide_ime(this);
            snackbar.show();
            Map<String, String> params = new HashMap<>();
            params.put("touid", touid);
            params.put("message", text);
            HttpUtil.post(this, replyUrl, params, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    String res = new String(response);
                    if (res.contains("操作成功")) {
                        list.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showToast("回复发表成功");
                                getData(false);
                            }
                        }, 500);
                        replyTime = System.currentTimeMillis();
                        input.setText("");
                    } else {
                        if (res.contains("两次发送短消息太快")) {
                            showToast("两次发送短消息太快，请稍候再发送");
                        } else {
                            System.out.println(res);
                            showToast("由于未知原因发表失败");
                        }
                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    showToast("网络错误！！！");
                }

                @Override
                public void onFinish() {
                    list.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            snackbar.dismiss();
                        }
                    }, 500);
                    super.onFinish();
                }
            });
        }
    }
}
