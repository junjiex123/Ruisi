package xyz.yluo.ruisiapp.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import xyz.yluo.ruisiapp.adapter.SingleArticleAdapter;
import xyz.yluo.ruisiapp.data.LoadMoreType;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.data.SingleType;
import xyz.yluo.ruisiapp.database.MyDbUtils;
import xyz.yluo.ruisiapp.fragment.FrageReplyDialog;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.GetNumber;
import xyz.yluo.ruisiapp.utils.RequestOpenBrowser;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-6.
 * 单篇文章activity
 * 一楼是楼主
 * 其余是评论
 */
public class SingleArticleActivity extends BaseActivity
        implements RecyclerViewClickListener, LoadMoreListener.OnLoadMoreListener,
        FrageReplyDialog.replyCompeteCallBack,View.OnClickListener {

    protected SwipeRefreshLayout refreshLayout;
    private RecyclerView mRecyclerView;
    //上一次回复时间
    private long replyTime = 0;
    //当前第几页
    private int page_now = 1;
    private int page_sum = 1;
    private boolean isGetTitle = false;
    //是否倒序
    private boolean isRevere = false;
    //是否允许加载更多
    private boolean isEnableLoadMore = false;
    //回复楼主的链接
    private String replyUrl = "";
    private SingleArticleAdapter mRecyleAdapter;
    //存储数据 需要填充的列表
    private List<SingleArticleData> mydatalist = new ArrayList<>();
    //是否调到指定页数and楼层???
    private boolean isRedirect, isSaveToDataBase = false;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> pageSpinnerDatas = new ArrayList<>();
    private String Title, Author, Tid = "";
    private Spinner spinner;

    public static void open(Context context, String url, @Nullable String title, @Nullable String author) {
        Intent intent = new Intent(context, SingleArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        if (title != null) {
            intent.putExtra("title", title);
        }
        if (author != null) {
            intent.putExtra("author", author);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_article);

        LinearLayout bottom_bar = (LinearLayout) findViewById(R.id.bottom_bar);
        spinner = (Spinner) findViewById(R.id.btn_jump_spinner);
        spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,pageSpinnerDatas);
        spinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        mRecyclerView = (RecyclerView) findViewById(R.id.topic_recycler_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.topic_refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        for(int i=0;i<bottom_bar.getChildCount();i++){
            View v = bottom_bar.getChildAt(i);
            if(v.getId()!=R.id.btn_jump_spinner){
                v.setOnClickListener(this);
            }
        }

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyleAdapter = new SingleArticleAdapter(this, this, mydatalist);
        mRecyleAdapter.setScrollToSomePosition(new SingleArticleAdapter.ScrollToSomePosition() {
            @Override
            public void scroolto(int position) {
                mRecyclerView.scrollToPosition(position);
            }
        });
        mRecyclerView.setAdapter(mRecyleAdapter);
        mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 8));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(!(pos+1==page_now)){
                    jump_page(pos+1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String url = getIntent().getExtras().getString("url");
        if (getIntent().getExtras().containsKey("title")) {
            Title = getIntent().getExtras().getString("title");
            isGetTitle = true;
        }

        if (getIntent().getExtras().containsKey("author")) {
            Author = getIntent().getExtras().getString("author");
        }
        Tid = GetId.getTid(url);
        if (url != null && url.contains("redirect")) {
            if (!PublicData.IS_SCHOOL_NET) {
                url = url + "&mobile=2";
            }
            isRedirect = true;
            HttpUtil.head(this, url, new ResponseHandler() {
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


    private void firstGetData(int page) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        //数据填充
        getArticleData(page);
    }

    @Override
    public void onLoadMore() {
        //加载更多被电击
        if (isEnableLoadMore) {
            isEnableLoadMore = false;
            int page = page_now;
            if (page_now < page_sum) {
                page = page_now + 1;
            }
            getArticleData(page);
        }
    }

    //跳页
    private void jump_page(int page){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        //数据填充
        mydatalist.clear();
        mRecyleAdapter.notifyDataSetChanged();
        getArticleData(page);
    }

    private void refresh() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        //数据填充
        mydatalist.clear();
        mRecyleAdapter.notifyDataSetChanged();
        getArticleData(1);
    }

    //文章一页的html 根据页数 Tid
    private void getArticleData(final int page) {
        String url = UrlUtils.getSingleArticleUrl(Tid, page, false);
        if (isRevere) {
            url += "&ordertype=1";
        }
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                new DealWithArticleData().execute(res);
            }

            @Override
            public void onFailure(Throwable e) {
                isEnableLoadMore = true;
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "网络错误(Error -1)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        switch (v.getId()) {
            case R.id.btn_reply_2:
                if (isneed_login()) {
                    SingleArticleData single = mydatalist.get(position);
                    String replyUrl = single.getReplyUrlTitle();
                    String replyIndex = single.getIndex();
                    String replyName = single.getUsername();
                    String ref = Jsoup.parse(single.getCotent()).text();

                    String replyUserInfo = "回复:" + replyIndex + " " + replyName;

                    //String url,int type,long lastreplyTime,boolean isEnableTail,String userName,String info
                    FrageReplyDialog dialog = FrageReplyDialog.newInstance(replyUrl,FrageReplyDialog.REPLY_CZ,replyTime,
                            true,replyUserInfo,ref);
                    dialog.setCallBack(SingleArticleActivity.this);
                    dialog.show(getFragmentManager(),"reply");
                }
                break;
        }
    }

    /**
     * 收藏帖子
     * @param v
     */
    private void starTask(final View v) {
        final String url = UrlUtils.getStarUrl(Tid);
        Map<String, String> params = new HashMap<>();
        params.put("favoritesubmit", "true");
        params.put("formhash", PublicData.FORMHASH);
        HttpUtil.post(this, url, params, new ResponseHandler() {

            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                boolean isok = false;
                if (res.contains("成功")) {
                    Toast.makeText(getApplicationContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                    isok = true;
                } else if (res.contains("您已收藏")) {
                    Toast.makeText(getApplicationContext(), "您已收藏请勿重复收藏", Toast.LENGTH_SHORT).show();
                    isok = true;
                }

                if (isok) {
                    if (v != null) {
                        ImageView mv = (ImageView) v;
                        mv.setImageResource(R.drawable.ic_star_accent_24dp);
                    }
                }
            }
        });
    }

    /**
     *  回复dialog完成回掉函数
     */
    @Override
    public void onReplyFinish(int status, String info) {
        Log.i("reply dialog callbak","status:"+status+" info:"+info);
        if(status==RESULT_OK){
            replyTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_reply:
                if(isneed_login()){
                    //String url,int type,long lastreplyTime,boolean isEnableTail,String userName,String info
                    String hint = "回复："+Author;
                    FrageReplyDialog dialog = FrageReplyDialog.newInstance(replyUrl,FrageReplyDialog.REPLY_LZ,replyTime,true,hint,Title);
                    dialog.setCallBack(SingleArticleActivity.this);
                    dialog.show(getFragmentManager(),"reply");
                }

                break;
            case R.id.btn_star:
                if (isneed_login()) {
                    Toast.makeText(getApplicationContext(), "正在收藏......", Toast.LENGTH_SHORT).show();
                    starTask(view);
                }
                break;

            case R.id.btn_browser:
                String url = UrlUtils.getSingleArticleUrl(Tid, page_now, false);
                RequestOpenBrowser.openBroswer(this, url);
                break;
            case R.id.btn_refresh:
                refresh();
                break;
            case R.id.btn_reverse:
                isRevere = !isRevere;
                refresh();
                break;
            case R.id.btn_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,Title+UrlUtils.getSingleArticleUrl(Tid,page_now,PublicData.IS_SCHOOL_NET));
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
        @Override
        protected List<SingleArticleData> doInBackground(String... params) {
            List<SingleArticleData> tepdata = new ArrayList<>();
            String htmlData = params[0];
            //list 所有楼数据
            Document doc = Jsoup.parse(htmlData);
            if (!isGetTitle) {
                String titleText = doc.select("title").text();
                String[] array = titleText.split("-");
                int len = array.length;
                if (len >= 5) {
                    Title = "";
                    for (int lnea = 0; lnea < len - 4; lnea++) {
                        Title = Title + array[lnea] + "-";
                        if (lnea == len - 5) {
                            Title = Title.substring(0, Title.length() - 2);
                        }
                    }
                }
                isGetTitle = true;
            }
            //获取回复/hash
            if (doc.select("input[name=formhash]").first() != null) {
                replyUrl = doc.select("form#fastpostform").attr("action");
                String hash = doc.select("input[name=formhash]").attr("value");
                if (!hash.isEmpty()) {
                    PublicData.FORMHASH = hash;
                }
            }

            int index = 0;
            if ((page_now == 1 && mydatalist.size() == 0) || page_now < page_sum) {
                index = 0;
            } else if (page_now >= page_sum) {
                if (mydatalist.size() == 0) {
                    index = 0;
                } else {
                    index = mydatalist.size() % 10;
                    if (index == 0) {
                        index = 10;
                    }
                }
            }
            //获取总页数 和当前页数
            if (doc.select(".pg").text().length() > 0) {
                if (doc.select(".pg").text().length() > 0) {
                    page_now = GetNumber.getNumber(doc.select(".pg").select("strong").text());
                    int n = GetNumber.getNumber(doc.select(".pg").select("span").attr("title"));
                    if (n > 0 && n > page_sum) {
                        page_sum = n;
                    }
                    Log.i("page info", page_now + " " + page_sum);
                }
            }
            Elements elements = doc.select(".postlist");
            Elements postlist = elements.select("div[id^=pid]");
            for (int i = index; i < postlist.size(); i++) {
                Element temp = postlist.get(i);
                SingleArticleData data;
                String userimg = temp.select("span[class=avatar]").select("img").attr("src");
                Elements userInfo = temp.select("ul.authi");
                String commentindex = userInfo.select("li.grey").select("em").text();
                String username = userInfo.select("a[href^=home.php?mod=space&uid=]").text();
                String posttime = userInfo.select("li.grey.rela").text();
                String replyUrl = temp.select(".replybtn").select("input").attr("href");
                Elements contentels = temp.select(".message");

                //是否移除所有样式
                if (PublicData.ISSHOW_PLAIN) {
                    //移除所有style
                    contentels.select("[style]").removeAttr("style");
                    contentels.select("font").removeAttr("color").removeAttr("size").removeAttr("face");
                }

                //处理引用
                Elements blockquotes = contentels.select(".grey.quote").select("blockquote");
                if (blockquotes.text().contains("引用:") && blockquotes.text().contains("发表于")) {
                    System.out.println(blockquotes.text());
                    String[] arrayb = blockquotes.text().split(" ", 6);
                    if (arrayb.length == 6) {
                        String usernameb = arrayb[1];
                        String contentb = arrayb[5];
                        blockquotes.html("回复: " + usernameb + "<br>" + contentb);
                    }
                }

                for (Element codee : contentels.select(".blockcode")) {
                    codee.html("<code>" + codee.html().trim() + "</code>");
                }

                //删除修改日期
                contentels.select("i.pstatus").remove();
                String finalcontent = contentels.html().trim();
                //替换开头的br
                while (finalcontent.startsWith("<br>")) {
                    finalcontent = finalcontent.substring(4, finalcontent.length()).trim();
                }

                //替换结尾的br
                while (finalcontent.endsWith("<br>")) {
                    finalcontent = finalcontent.substring(0, finalcontent.length() - 4).trim();
                }

                //这是内容
                if (commentindex.contains("楼主") || commentindex.contains("收藏")) {
                    String newtime = posttime.replace("收藏", "");
                    data = new SingleArticleData(SingleType.CONTENT, Title, userimg, username, newtime, commentindex, replyUrl, finalcontent);
                } else {
                    data = new SingleArticleData(SingleType.COMMENT, Title, userimg, username, posttime, commentindex, replyUrl, finalcontent);
                }
                tepdata.add(data);
            }

            return tepdata;
        }

        @Override
        protected void onPostExecute(List<SingleArticleData> tepdata) {
            if (!isSaveToDataBase) {
                //插入数据库
                Log.i("insert ", "tid:" + Tid + "title:" + Title + "author:" + Author);
                MyDbUtils myDbUtils = new MyDbUtils(getApplicationContext(), false);
                myDbUtils.handleSingle(Tid, Title, Author);//String Tid,String title,String author
                isSaveToDataBase = true;
            }
            int add = tepdata.size();
            if (add > 0) {
                if (add % 10 != 0) {
                    mRecyleAdapter.setLoadMoreType(LoadMoreType.NOTHING);
                } else {
                    mRecyleAdapter.setLoadMoreType(LoadMoreType.LOADING);
                }
                int start = mydatalist.size();
                mydatalist.addAll(tepdata);
                mRecyleAdapter.notifyItemChanged(start);
                mRecyleAdapter.notifyItemRangeInserted(start + 1, add);

                if (isRedirect) {
                    isRedirect = false;
                    for (int i = 0; i < mydatalist.size(); i++) {
                        if (mydatalist.get(i).getCotent().contains(PublicData.USER_NAME)) {
                            mRecyclerView.scrollToPosition(i);
                            break;
                        }
                    }
                }

            } else {
                //add = 0 没有添加
                mRecyleAdapter.setLoadMoreType(LoadMoreType.NOTHING);
                mRecyleAdapter.notifyItemChanged(mRecyleAdapter.getItemCount() - 1);
            }
            isEnableLoadMore = true;

            findViewById(R.id.view_loading).setVisibility(View.GONE);
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);


            pageSpinnerDatas .clear();
            for(int i=1;i<=page_sum;i++){
                pageSpinnerDatas.add(i+"/"+page_sum+"页");
            }
            spinner.setSelection(page_now-1);
            spinnerAdapter.notifyDataSetChanged();
        }
    }

}
