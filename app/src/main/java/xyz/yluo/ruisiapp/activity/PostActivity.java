package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.BaseAdapter;
import xyz.yluo.ruisiapp.adapter.PostAdapter;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.model.SingleArticleData;
import xyz.yluo.ruisiapp.model.SingleType;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.IntentUtils;
import xyz.yluo.ruisiapp.utils.KeyboardUtil;
import xyz.yluo.ruisiapp.utils.UrlUtils;
import xyz.yluo.ruisiapp.widget.emotioninput.PanelViewRoot;
import xyz.yluo.ruisiapp.widget.emotioninput.SmileyInputRoot;

/**
 * Created by free2 on 16-3-6.
 * 单篇文章activity
 * 一楼是楼主
 * 其余是评论
 */
public class PostActivity extends BaseActivity
        implements ListItemClickListener, LoadMoreListener.OnLoadMoreListener, View.OnClickListener {

    private RecyclerView topicList;
    //上一次回复时间
    private long replyTime = 0;
    //当前第几页
    private int page_now = 1;
    private int page_sum = 1;
    private int edit_pos = -1;
    private boolean isGetTitle = false;
    //是否允许加载更多
    private boolean enableLoadMore = false;
    //回复楼主的链接
    private String replyUrl = "";
    private PostAdapter adapter;
    //存储数据 需要填充的列表
    private List<SingleArticleData> datas = new ArrayList<>();
    //是否调到指定页数and楼层???
    private boolean isSaveToDataBase = false;
    private String Title, AuthorName, Tid, RedirectPid = "";
    private boolean showPlainText = false;
    private boolean isRefreshing = false;
    private EditText input;
    private PanelViewRoot mPanelRoot;

    public static void open(Context context, String url, @Nullable String author) {
        Intent intent = new Intent(context, PostActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        intent.putExtra("author", TextUtils.isEmpty(author) ? "null" : author);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initToolBar(true, "正文");
        input = (EditText) findViewById(R.id.ed_comment);
        SmileyInputRoot rootViewGroup = (SmileyInputRoot) findViewById(R.id.root);
        mPanelRoot = rootViewGroup.getmPanelLayout();
        showPlainText = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setting_show_plain", false);
        topicList = (RecyclerView) findViewById(R.id.topic_list);
        View btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        View smuleyBtn = findViewById(R.id.btn_emotion);
        View btnMore = findViewById(R.id.btn_more);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        topicList.setLayoutManager(mLayoutManager);
        adapter = new PostAdapter(this, this, datas);
        /**
         * 缓存数量
         */
        topicList.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 8));
        topicList.setAdapter(adapter);

        KeyboardUtil.attach(this, mPanelRoot, isShowing -> Log.e("key board", String.valueOf(isShowing)));
        mPanelRoot.init(input, smuleyBtn, btnSend);
        mPanelRoot.setMoreView(LayoutInflater.from(this).inflate(R.layout.my_smiley_menu, null), btnMore);
        topicList.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mPanelRoot.hidePanelAndKeyboard();
            }
            return false;
        });

        Bundle b = getIntent().getExtras();
        String url = b.getString("url");
        AuthorName = b.getString("author");
        Tid = GetId.getid("tid=", url);
        if (url != null && url.contains("redirect")) {
            RedirectPid = GetId.getid("pid=", url);
            if (!App.IS_SCHOOL_NET) {
                url = url + "&mobile=2";
            }
            HttpUtil.head(this, url, null, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    int page = GetId.getPage(new String(response));
                    firstGetData(page);
                }
            });
        } else {
            firstGetData(1);
        }
    }

    private float x, y = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //当手指按下的时候
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候
                float dx = event.getX() - x;
                float dy = event.getY() - y;
                if (x < 400 && dx > 100 && dx > dy) {
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    int w_screen = dm.widthPixels;
                    if ((dx > 2 * w_screen / 5) && (x < 2 * w_screen / 5)) {
                        finish();
                    }
                }
                x = event.getX();
                y = event.getY();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void firstGetData(int page) {
        getArticleData(page);
    }

    @Override
    public void onLoadMore() {
        //加载更多被电击
        if (enableLoadMore) {
            enableLoadMore = false;
            int page = page_now;
            if (page_now < page_sum) {
                page = page_now + 1;
            }
            getArticleData(page);
        }
    }

    private void refresh() {
        adapter.changeLoadMoreState(BaseAdapter.STATE_LOADING);
        //数据填充
        datas.clear();
        adapter.notifyDataSetChanged();
        getArticleData(1);
    }

    //文章一页的html 根据页数 Tid
    private void getArticleData(final int page) {
        String url = UrlUtils.getSingleArticleUrl(Tid, page, false);
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                new DealWithArticleData().execute(res);
            }

            @Override
            public void onFailure(Throwable e) {
                enableLoadMore = true;
                e.printStackTrace();
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_FAIL);
                Toast.makeText(getApplicationContext(), "加载失败(Error -1)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onListItemClick(View v, final int position) {
        switch (v.getId()) {
            case R.id.btn_reply_cz:
                if (isLogin()) {
                    SingleArticleData single = datas.get(position);
                    Intent i = new Intent(PostActivity.this, ReplyCzActivity.class);
                    i.putExtra("islz", single.getUid().equals(datas.get(0).getUid()));
                    i.putExtra("data", single);
                    startActivityForResult(i, 20);
                }
                break;
            case R.id.need_loading_item:
                refresh();
                break;
            case R.id.tv_edit:
                edit_pos = position;
                Intent i = new Intent(this, EditActivity.class);
                i.putExtra("PID", datas.get(position).getPid());
                i.putExtra("TID", Tid);
                startActivityForResult(i, 10);
                break;
            case R.id.tv_remove:
                edit_pos = position;
                new AlertDialog.Builder(this).
                        setTitle("删除帖子!").
                        setMessage("你要删除本贴/回复吗？").
                        setPositiveButton("删除", (dialog, which) -> removeItem(position))
                        .setNegativeButton("取消", null)
                        .setCancelable(true)
                        .create()
                        .show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 10) {
                //编辑Activity返回
                Bundle b = data.getExtras();
                String title = b.getString("TITLE", "");
                String content = b.getString("CONTENT", "");
                if (edit_pos == 0 && !TextUtils.isEmpty(title)) {
                    datas.get(0).setTitle(title);
                }
                datas.get(edit_pos).setCotent(content);
                adapter.notifyItemChanged(edit_pos);
            } else if (requestCode == 20) {
                //回复层主返回
                replyTime = System.currentTimeMillis();
                if (page_now == page_sum) {
                    onLoadMore();
                }
            }

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                replyLz(replyUrl);
                break;
            case R.id.btn_star:
                if (isLogin()) {
                    showToast("正在收藏帖子...");
                    starTask(view);
                }
                break;
            case R.id.btn_browser:
                String url = UrlUtils.getSingleArticleUrl(Tid, page_now, false);
                IntentUtils.openBroswer(this, url);
                break;
            case R.id.btn_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, Title + UrlUtils.getSingleArticleUrl(Tid, page_now, App.IS_SCHOOL_NET));
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到文章到:"));
                break;
        }
    }

    /**
     * 处理数据类 后台进程
     */
    private class DealWithArticleData extends AsyncTask<String, Void, List<SingleArticleData>> {

        private String errorText = "";

        @Override
        protected List<SingleArticleData> doInBackground(String... params) {
            errorText = "";
            List<SingleArticleData> tepdata = new ArrayList<>();
            String htmlData = params[0];
            if (!isGetTitle) {
                int ih = htmlData.indexOf("keywords");
                if (ih > 0) {
                    int h_start = htmlData.indexOf('\"', ih + 15);
                    int h_end = htmlData.indexOf('\"', h_start + 1);
                    Title = htmlData.substring(h_start + 1, h_end);
                    isGetTitle = true;
                }
            }

            Document doc = Jsoup.parse(htmlData);
            //判断错误
            Elements elements = doc.select(".postlist");
            if (elements.size() <= 0) {
                //有可能没有列表处理错误
                errorText = doc.select(".jump_c").text();
                if (TextUtils.isEmpty(errorText)) {
                    errorText = "network error  !!!";
                }
                return tepdata;
            }

            //获得回复楼主的url
            if (TextUtils.isEmpty(replyUrl)) {
                //获取回复/hash
                if (!doc.select("input[name=formhash]").isEmpty()) {
                    replyUrl = doc.select("form#fastpostform").attr("action");
                    //String hash = doc.select("input[name=formhash]").attr("value");
                }
            }

            //获取总页数 和当前页数
            if (doc.select(".pg").text().length() > 0) {
                if (doc.select(".pg").text().length() > 0) {
                    page_now = GetId.getNumber(doc.select(".pg").select("strong").text());
                    int n = GetId.getNumber(doc.select(".pg").select("span").attr("title"));
                    if (n > 0 && n > page_sum) {
                        page_sum = n;
                    }
                }
            }

            Elements postlist = elements.select("div[id^=pid]");
            int size = postlist.size();
            for (int i = 0; i < size; i++) {
                Element temp = postlist.get(i);
                SingleArticleData data;
                String pid = temp.attr("id").substring(3);
                String uid = GetId.getid("uid=", temp.select("span[class=avatar]").select("img").attr("src"));
                Elements userInfo = temp.select("ul.authi");
                String commentindex = userInfo.select("li.grey").select("em").text();
                String username = userInfo.select("a[href^=home.php?mod=space&uid=]").text();
                String posttime = userInfo.select("li.grey.rela").text();
                String replyUrl = temp.select(".replybtn").select("input").attr("href");
                Elements contentels = temp.select(".message");
                //是否移除所有样式
                if (showPlainText) {
                    //移除所有style
                    contentels.select("[style]").removeAttr("style");
                    contentels.select("font").removeAttr("color").removeAttr("size").removeAttr("face");
                }
                /**
                 * 处理代码
                 */
                for (Element codee : contentels.select(".blockcode")) {
                    codee.html("<code>" + codee.html().trim() + "</code>");
                }
                //处理引用
                for (Element codee : contentels.select("blockquote")) {
                    int start = codee.html().indexOf("发表于");
                    if (start > 0) {
                        int end = codee.html().indexOf("</font>", start);
                        if (end > start) {
                            int c = end - start;
                            codee.html(codee.html().replaceAll("发表于.{" + (c - 3) + "}", ""));
                            break;
                        }
                    }
                }

                //删除修改日期
                String edittime = contentels.select("i.pstatus").remove().text();
                String finalcontent = contentels.html().trim();

                if (page_now == 1 && i == 0) {
                    data = new SingleArticleData(SingleType.CONTENT, Title, uid,
                            username, posttime.replace("收藏", ""),
                            commentindex, replyUrl, finalcontent, pid);
                    AuthorName = username;
                    if (!isSaveToDataBase) {
                        //插入数据库
                        MyDB myDB = new MyDB(PostActivity.this);
                        myDB.handSingleReadHistory(Tid, Title, AuthorName);
                        isSaveToDataBase = true;
                    }
                } else {
                    data = new SingleArticleData(SingleType.COMMENT, Title, uid,
                            username, posttime, commentindex, replyUrl, finalcontent, pid);
                }
                if (!TextUtils.isEmpty(edittime)) {
                    data.setEditTime(edittime);
                }
                tepdata.add(data);
            }
            return tepdata;
        }

        @Override
        protected void onPostExecute(List<SingleArticleData> tepdata) {
            enableLoadMore = true;
            if (!TextUtils.isEmpty(errorText)) {
                Toast.makeText(PostActivity.this, errorText, Toast.LENGTH_SHORT).show();
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_FAIL);
                setRefresh(false);
                return;
            }
            if (tepdata.size() == 0) {
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_NOTHING);
                return;
            }

            int startsize = datas.size();
            if (datas.size() == 0) {
                datas.addAll(tepdata);
            } else {
                String strindex = datas.get(datas.size() - 1).getIndex();
                if (TextUtils.isEmpty(strindex)) {
                    strindex = "-1";
                } else if (strindex.equals("沙发")) {
                    strindex = "1";
                } else if (strindex.equals("板凳")) {
                    strindex = "2";
                } else if (strindex.equals("地板")) {
                    strindex = "3";
                }
                int index = GetId.getNumber(strindex);
                for (int i = 0; i < tepdata.size(); i++) {
                    String strindexp = tepdata.get(i).getIndex();
                    if (strindexp.equals("沙发")) {
                        strindexp = "1";
                    } else if (strindex.equals("板凳")) {
                        strindexp = "2";
                    } else if (strindex.equals("地板")) {
                        strindexp = "3";
                    }
                    int indexp = GetId.getNumber(strindexp);
                    if (indexp > index) {
                        datas.add(tepdata.get(i));
                    }
                }
            }
            if (page_now < page_sum) {
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOADING);
            } else {
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_NOTHING);
            }
            if (datas.size() > 0 && (datas.get(0).getType() != SingleType.CONTENT) &&
                    (datas.get(0).getType() != SingleType.HEADER)) {
                datas.add(0, new SingleArticleData(SingleType.HEADER, Title,
                        null, null, null, null, null, null, null));
            }
            int add = datas.size() - startsize;
            if (startsize == 0) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRangeInserted(startsize, add);
            }

            //打开的时候移动到指定楼层
            if (!TextUtils.isEmpty(RedirectPid)) {
                for (int i = 0; i < datas.size(); i++) {
                    if (!TextUtils.isEmpty(datas.get(i).getPid())
                            && datas.get(i).getPid().equals(RedirectPid)) {
                        topicList.scrollToPosition(i);
                        break;
                    }
                }
                RedirectPid = "";
            }
        }
    }

    /**
     * 收藏帖子
     */
    private void starTask(final View v) {
        final String url = UrlUtils.getStarUrl(Tid);
        Map<String, String> params = new HashMap<>();
        params.put("favoritesubmit", "true");
        HttpUtil.post(this, url, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if (res.contains("成功") || res.contains("您已收藏")) {
                    showToast("收藏成功");
                    if (v != null) {
                        final ImageView mv = (ImageView) v;
                        mv.postDelayed(() -> mv.setImageResource(R.drawable.ic_star_accent_24dp), 300);
                    }
                }
            }
        });
    }

    //删除帖子或者回复
    private void removeItem(final int pos) {
        Map<String, String> params = new HashMap<>();
        params.put("editsubmit", "yes");
        //params.put("fid",);
        params.put("tid", Tid);
        params.put("pid", datas.get(pos).getPid());
        params.put("delete", "1");
        HttpUtil.post(this, UrlUtils.getDeleteReplyUrl(), params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                Log.e("resoult", res);
                if (res.contains("主题删除成功")) {
                    if (datas.get(pos).getType() == SingleType.CONTENT) {
                        showToast("主题删除成功");
                        finish();
                    } else {
                        showToast("回复删除成功");
                        datas.remove(pos);
                        adapter.notifyItemRemoved(pos);
                    }
                } else {
                    int start = res.indexOf("<p>");
                    String ss = res.substring(start + 3, start + 20);
                    showToast(ss);
                }

            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
                showToast("网络错误,删除失败！");
            }
        });
    }

    //回复楼主
    private void replyLz(String url) {
        if (!(isLogin() && checkTime() && checkInput())) {
            return;
        }
        String s = getPreparedReply(this, input.getText().toString());
        Map<String, String> params = new HashMap<>();
        params.put("message", s);
        HttpUtil.post(this, url + "&handlekey=fastpost&loc=1&inajax=1", params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                handleReply(true, res);
            }

            @Override
            public void onFailure(Throwable e) {
                handleReply(false, "");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                setRefresh(false);
            }
        });
    }


    public static String getPreparedReply(Context context, String text) {
        int len = 0;
        try {
            len = text.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(context);
        if (shp.getBoolean("setting_show_tail", false)) {
            String texttail = shp.getString("setting_user_tail", "无尾巴").trim();
            if (!texttail.equals("无尾巴")) {
                texttail = "     " + texttail;
                text += texttail;
            }
        }

        //字数补齐补丁
        if (len < 13) {
            int need = 14 - len;
            for (int i = 0; i < need; i++) {
                text += " ";
            }
        }

        return text;
    }

    private void handleReply(boolean isok, String res) {
        if (isok) {
            if (res.contains("成功") || res.contains("层主")) {
                Toast.makeText(this, "回复发表成功", Toast.LENGTH_SHORT).show();
                input.setText(null);
                replyTime = System.currentTimeMillis();
                if (page_now == page_sum) {
                    onLoadMore();
                }
                mPanelRoot.hidePanelAndKeyboard();
            } else if (res.contains("您两次发表间隔")) {
                Toast.makeText(this, "您两次发表间隔太短了......", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "由于未知原因发表失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
        }
    }

    //跳页
    private void jump_page(int page) {
        datas.clear();
        adapter.notifyDataSetChanged();
        getArticleData(page);
    }

    private boolean checkInput() {
        String s = input.getText().toString();
        if (TextUtils.isEmpty(s)) {
            showToast("你还没写内容呢!");
            return false;
        } else {
            return true;
        }
    }

    private boolean checkTime() {
        if (System.currentTimeMillis() - replyTime > 15000) {
            return true;
        } else {
            showToast("还没到15s呢，再等等吧!");
            return false;
        }
    }

    private void setRefresh(boolean refresh) {
        if (refresh && !isRefreshing) {
            isRefreshing = true;
            //// TODO: 2016/12/9
            //showLoading("处理中", "请稍后...");
        } else if (!refresh && isRefreshing) {
            isRefreshing = false;
            //dismissLoading();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPanelRoot.getVisibility() == View.VISIBLE) {
            mPanelRoot.hidePanelAndKeyboard();
        } else {
            super.onBackPressed();
        }
    }
}
