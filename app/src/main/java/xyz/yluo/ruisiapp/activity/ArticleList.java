package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ArticleListNormalAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * 一般文章列表
 * 链接到校园网时 GetNormalArticleListTaskRs
 * 外网时 GetArticleListTaskMe
 * 2个是不同的
 */
public class ArticleList extends ArticleListBase {

    //一般板块/图片板块/手机板块数据列表
    private List<ArticleListData> datas;
    private ArticleListNormalAdapter mRecyleAdapter;
    private MyDB myDB = null;
    private boolean hideZhidin = true;

    public static void open(Context context, int fid, String title) {
        Intent intent = new Intent(context, ArticleList.class);
        CurrentFid = fid;
        CurrentTitle = title;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideZhidin = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setting_hide_zhidin", true);
        datas = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyleAdapter = new ArticleListNormalAdapter(this, datas, 0);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyleAdapter);
        myToolBar.setTitle(CurrentTitle);

        myToolBar.addMenu(R.drawable.ic_search_white_24dp,"SEARCH");
        myToolBar.addMenu(R.drawable.ic_edit,"POST");

        myDB = new MyDB(ArticleList.this, MyDB.MODE_READ);
        //加载更多
        mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this, 8));
        datas.clear();
    }


    @Override
    protected void refresh() {
        CurrentPage = 1;
        getData();
    }

    @Override
    protected void getData() {
        String url = UrlUtils.getArticleListUrl(CurrentFid, CurrentPage, true);
        if (!App.IS_SCHOOL_NET) {
            url = url + UrlUtils.getArticleListUrl(CurrentFid, CurrentPage, false);
        }

        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                if (App.IS_SCHOOL_NET) {
                    new GetNormalArticleListTaskRs().execute(new String(response));
                } else {
                    //外网
                    new GetArticleListTaskMe().execute(new String(response));
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 500);
            }
        });

    }

    //加载更多
    @Override
    public void onLoadMore() {
        if (isEnableLoadMore) {
            CurrentPage++;
            isEnableLoadMore = false;
            getData();

        }
    }

    private void getDataCompete(List<ArticleListData> dataset) {
        btn_refresh.show();
        if (CurrentPage == 1) {
            datas.clear();
            mRecyleAdapter.notifyDataSetChanged();
        }
        int start = datas.size();
        datas.addAll(dataset);

        mRecyleAdapter.notifyItemRangeInserted(start, dataset.size());
        isEnableLoadMore = true;

        //隐藏正在加载的view
        hide_loading_view();
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 500);
    }

    //校园网状态下获得一个普通板块文章列表数据 根据html获得数据
    private class GetNormalArticleListTaskRs extends AsyncTask<String, Void, List<ArticleListData>> {
        @Override
        protected List<ArticleListData> doInBackground(String... params) {
            String res = params[0];
            List<ArticleListData> dataset = new ArrayList<>();
            Elements list = Jsoup.parse(res).select("div[id=threadlist]");
            Elements links = list.select("tbody");
            ArticleListData temp;
            for (Element src : links) {
                if (src.getElementsByAttributeValue("class", "by").first() != null) {
                    String type;
                    if (src.attr("id").contains("stickthread")) {
                        type = "置顶";
                    } else if (src.select("th").attr("class").contains("lock")) {
                        type = "关闭";
                    } else if (src.select(".icn").select("a").attr("title").contains("投票")) {
                        type = "投票";
                    } else if (src.select("th").select("strong").text().length() > 0) {
                        type = "金币:" + src.select("th").select("strong").text().trim();
                    } else {
                        type = "normal";
                    }
                    Elements tempEles = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]");
                    String title = tempEles.text();
                    String titleUrl = tempEles.attr("href");
                    int titleColor = GetId.getColor(tempEles.attr("style"));

                    Log.e("style",src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").attr("style"));
                    Log.e("titleColor",titleColor+"");

                    String author = src.getElementsByAttributeValue("class", "by").first().select("a").text();
                    String authorUrl = src.getElementsByAttributeValue("class", "by").first().select("a").attr("href");
                    String time = src.getElementsByAttributeValue("class", "by").first().select("em").text().trim();
                    String viewcount = src.getElementsByAttributeValue("class", "num").select("em").text();
                    String replaycount = src.getElementsByAttributeValue("class", "num").select("a").text();

                    if(hideZhidin&&type.equals("置顶")){
                        Log.i("article list","ignore zhidin");
                    }else {
                        if (title.length() > 0 && author.length() > 0) {
                            temp = new ArticleListData(type,title, titleUrl, author, authorUrl, time, viewcount, replaycount,titleColor);
                            dataset.add(temp);
                        }
                    }

                }
            }
            return myDB.handReadHistoryList(dataset);
        }

        @Override
        protected void onPostExecute(List<ArticleListData> dataset) {
            getDataCompete(dataset);
        }
    }

    //非校园网状态下获得一个板块文章列表数据
    //根据html获得数据
    //调用的手机版
    private class GetArticleListTaskMe extends AsyncTask<String, Void, List<ArticleListData>> {
        @Override
        protected List<ArticleListData> doInBackground(String... params) {
            //chiphell
            String res = params[0];
            List<ArticleListData> dataset = new ArrayList<>();
            Document doc = Jsoup.parse(res);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
            ArticleListData temp;
            Elements links = body.select("li");
            for (Element src : links) {
                String url = src.select("a").attr("href");
                int titleColor = GetId.getColor(src.select("a").attr("style"));
                String author = src.select(".by").text();
                src.select("span.by").remove();
                String title = src.select("a").text();
                String replyCount = src.select("span.num").text();
                String img = src.select("img").attr("src");
                boolean hasImage = img.contains("icon_tu.png");
                temp = new ArticleListData(hasImage, title, url, author, replyCount,titleColor);
                dataset.add(temp);
            }
            return myDB.handReadHistoryList(dataset);
        }

        @Override
        protected void onPostExecute(List<ArticleListData> dataset) {
            getDataCompete(dataset);
        }
    }

}
