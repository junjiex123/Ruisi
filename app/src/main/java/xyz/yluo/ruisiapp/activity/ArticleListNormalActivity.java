package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.MyPublicData;
import xyz.yluo.ruisiapp.adapter.ArticleListNormalAdapter;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/*
 *帖子列表activity
 *
 */
public class ArticleListNormalActivity extends ArticleListBaseActivity{

    //一般板块/图片板块/手机板块数据列表
    private List<ArticleListData> mydatasetnormal;
    private ArticleListNormalAdapter mRecyleAdapter;

    public static void open(Context context, int fid,String title){
        Intent intent = new Intent(context, ArticleListNormalActivity.class);
        CurrentFid = fid;
        CurrentTitle = title;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setTitle(CurrentTitle);
        mydatasetnormal = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyleAdapter = new ArticleListNormalAdapter(this, mydatasetnormal,0);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyleAdapter);
        //加载更多
        mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this,8));

        mydatasetnormal.clear();
}


    @Override
    protected void refresh() {
        CurrentPage = 1;
        mydatasetnormal.clear();
        mRecyleAdapter.notifyDataSetChanged();
        getData();
    }

    @Override
    protected void getData() {
        String url = UrlUtils.getArticleListUrl(CurrentFid,CurrentPage,true);
        if(!MyPublicData.IS_SCHOOL_NET){
            url = url + UrlUtils.getArticleListUrl(CurrentFid,CurrentPage,false);
        }

        HttpUtil.get(getApplicationContext(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                if(MyPublicData.IS_SCHOOL_NET){
                    new GetNormalArticleListTaskRs(new String(response)).execute();
                }else{
                    //外网
                    new GetArticleListTaskMe(new String(response)).execute();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    //加载更多
    @Override
    public void onLoadMore() {
        if(isEnableLoadMore){
            CurrentPage++;
            getData();
            isEnableLoadMore = false;
        }
    }

    //校园网状态下获得一个普通板块文章列表数据 根据html获得数据
    public class GetNormalArticleListTaskRs extends AsyncTask<Void, Void, String> {

        private List<ArticleListData> dataset = new ArrayList<>();
        private String res;

        public GetNormalArticleListTaskRs(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            Elements list = Jsoup.parse(res).select("div[id=threadlist]");
            Elements links = list.select("tbody");
            ArticleListData temp;
            for (Element src : links) {
                if (src.getElementsByAttributeValue("class", "by").first() != null) {

                    String type;
                    //金币
                    if (src.select("th").select("strong").text().length()>0) {
                        type = "gold:" + src.select("th").select("strong").text().trim();
                    } else if (src.attr("id").contains("stickthread")) {
                        type = "zhidin";
                    } else {
                        type = "normal";
                    }
                    String title = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").text();
                    String titleUrl = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").attr("href");
                    String author = src.getElementsByAttributeValue("class", "by").first().select("a").text();
                    String authorUrl = src.getElementsByAttributeValue("class", "by").first().select("a").attr("href");
                    String time = src.getElementsByAttributeValue("class", "by").first().select("em").text().trim();
                    String viewcount = src.getElementsByAttributeValue("class", "num").select("em").text();
                    String replaycount = src.getElementsByAttributeValue("class", "num").select("a").text();

                    if(!MyPublicData.ISSHOW_ZHIDIN &&type.equals("zhidin")){
                        //do no thing
                    }else{
                        if (title.length()>0&& author.length()>0) {
                            //新建对象
                            temp = new ArticleListData(title, titleUrl, type, author, authorUrl, time, viewcount, replaycount);
                            dataset.add(temp);
                        }
                    }

                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            mydatasetnormal.addAll(dataset);
            refreshLayout.setRefreshing(false);
            if(CurrentPage!=1){
                mRecyclerView.getItemAnimator().setAddDuration(0);
            }
            mRecyleAdapter.notifyItemRangeInserted(mydatasetnormal.size() - dataset.size(), dataset.size());
            isEnableLoadMore = true;

        }
    }

    //非校园网状态下获得一个板块文章列表数据
    //根据html获得数据
    //调用的手机版
    public class GetArticleListTaskMe extends AsyncTask<Void, Void, String> {

        private List<ArticleListData> dataset = new ArrayList<>();
        private String res;

        public GetArticleListTaskMe(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            //chiphell
            Document doc = Jsoup.parse(res);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接

            ArticleListData temp;
            Elements links = body.select("li");
            for (Element src : links) {
                String url = src.select("a").attr("href");
                String author = src.select(".by").text();
                src.select("span.by").remove();
                String title = src.select("a").text();
                String replyCount = src.select("span.num").text();

                String img = src.select("img").attr("src");
                String hasImage = "";
                if(img.contains("icon_tu.png")){
                    hasImage = "0";
                }

                //String type,String title, String titleUrl, String author, String replayCount
                temp = new ArticleListData(hasImage,title, url, author, replyCount);
                dataset.add(temp);
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            mydatasetnormal.addAll(dataset);
            refreshLayout.setRefreshing(false);
            mRecyleAdapter.notifyItemRangeInserted(mydatasetnormal.size() - dataset.size(), dataset.size());
            isEnableLoadMore = true;

        }
    }

}
