package xyz.yluo.ruisiapp.activity;

import android.app.DialogFragment;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.ArticleJumpDialog;
import xyz.yluo.ruisiapp.View.MyReplyView;
import xyz.yluo.ruisiapp.View.NeedLoginDialogFragment;
import xyz.yluo.ruisiapp.View.ReplyDialog;
import xyz.yluo.ruisiapp.adapter.SingleArticleAdapter;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.data.SingleType;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.listener.ReplyBarListner;
import xyz.yluo.ruisiapp.utils.GetIndex;
import xyz.yluo.ruisiapp.utils.RequestOpenBrowser;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-6.
 * 单篇文章activity
 * 一楼是楼主
 * 其余是评论
 */
public class SingleArticleActivity extends BaseActivity
        implements RecyclerViewClickListener,LoadMoreListener.OnLoadMoreListener,
        ReplyDialog.ReplyDialogListener,ArticleJumpDialog.JumpDialogListener{

    @Bind(R.id.topic_recycler_view)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.topic_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    @Bind(R.id.replay_bar)
    protected MyReplyView MyReplyView;
    private ActionBar actionBar;
    private ProgressDialog progress;

    //上一次回复时间
    private long replyTime = 0;
    //当前第几页
    private int CURRENT_PAGE = 1;
    //全部页数
    private int TOTAL_PAGE = 1;
    //是否允许加载更多
    private boolean isEnableLoadMore = false;
    //回复楼主的链接
    private String replyUrl = "";
    private SingleArticleAdapter mRecyleAdapter;
    //存储数据 需要填充的列表
    private List<SingleArticleData> mydatalist = new ArrayList<>();

    private  String ARTICLE_TID;
    private  String ARTICLE_TITLE = "";
    private  String ARTICLE_SUB_TITLE = "";

    //约定好要就收的数据
    public static void open(Context context, String tid,String title) {
        Intent intent = new Intent(context, SingleArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tid",tid);
        intent.putExtra("titile",title);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_chat);
        ButterKnife.bind(this);

        try {
            ARTICLE_TID =  getIntent().getExtras().getString("tid");
            ARTICLE_TITLE = getIntent().getExtras().getString("titile");
        }catch (Exception e){
            e.printStackTrace();
        }

        actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(ARTICLE_TITLE);
        }

        init();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyleAdapter = new SingleArticleAdapter(this, this, mydatalist);
        mRecyclerView.setAdapter(mRecyleAdapter);
        mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this,8));
        getArticleData(1);

        MyReplyView.setListener(new ReplyBarListner() {
            @Override
            public void btnSendClick(String input) {
                hide_ime();
                //按钮监听
                if(isNeedLoginDialog()){
                    if(checkTime()){
                        post_reply(input);
                    }
                }
            }
        });

    }

    private void init(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }



    private boolean isNeedLoginDialog(){
        if (PublicData.ISLOGIN) {
            return true;
        } else {
            NeedLoginDialogFragment dialogFragment = new NeedLoginDialogFragment();
            dialogFragment.show(getFragmentManager(), "needlogin");
        }
        return false;
    }

    @Override
    public void onLoadMore() {
        //加载更多被电击
        if(isEnableLoadMore){
            isEnableLoadMore = false;
            int page = CURRENT_PAGE;
            if(CURRENT_PAGE<TOTAL_PAGE){
                page= CURRENT_PAGE+1;
            }
            getArticleData(page);
        }
    }

    //跳页确认点击回调
    @Override
    public void JumpComfirmClick(DialogFragment dialog, int page) {
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

    private void refresh(){
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

    //文章一页的html 根据页数 tid
    private void getArticleData(final int page) {
        String url = UrlUtils.getSingleArticleUrl(ARTICLE_TID,page,false);
        HttpUtil.get(this ,url,new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                new DealWithArticleData(res).execute((Void) null);
            }

            @Override
            public void onFailure(Throwable e) {
                isEnableLoadMore = true;
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), ">>>网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class DealWithArticleData extends AsyncTask<Void,Void,String>{
        //* 传入一篇文章html
        //* 返回list<SingleArticleData>
        private List<SingleArticleData> tepdata = new ArrayList<>();
        private String htmlData;
        public DealWithArticleData(String htmlData) {
            this.htmlData = htmlData;
        }
        @Override
        protected String doInBackground(Void... params) {
            //list 所有楼数据
            Document doc = Jsoup.parse(htmlData);
            String titleText = doc.select("title").text();
            if(titleText.contains("-")&&ARTICLE_SUB_TITLE.equals("")){
                ARTICLE_SUB_TITLE = doc.select("title").text().split("-")[0].trim();
                ARTICLE_TITLE = doc.select("title").text().split("-")[1].trim();
            }

            //获取回复/hash
            if (doc.select("input[name=formhash]").first() != null) {
                replyUrl = doc.select("form#fastpostform").attr("action");
                String hash = doc.select("input[name=formhash]").attr("value"); // 具有 formhash 属性的链接
                if (!hash.isEmpty()){
                    PublicData.FORMHASH =hash;
                }
            }
            //获取总页数
            Pattern pattern = Pattern.compile("[0-9]+");
            String s = doc.select(".pg").select("span").attr("title");
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                String temps = s.substring(matcher.start(),matcher.end());
                try {
                    int  n = Integer.parseInt(temps);
                    if(n>0&&n>TOTAL_PAGE){
                        TOTAL_PAGE = n;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            Elements elements = doc.select(".postlist");
            Elements postlist = elements.select("div[id^=pid]");

            int a;
            if(mydatalist.size()==0){
                a = 0;
            }else {
                String indexs= mydatalist.get(mydatalist.size()-1).getIndex();
                a = GetIndex.getIndex(indexs);
            }

            for(Element temp:postlist){
                SingleArticleData data;
                String userimg = temp.select("span[class=avatar]").select("img").attr("src");
                Elements userInfo = temp.select("ul.authi");
                String index = userInfo.select("li.grey").select("em").text();
                String username = userInfo.select("a[href^=home.php?mod=space&uid=]").text();
                String posttime = userInfo.select("li.grey.rela").text();
                String replyUrl = temp.select(".replybtn").select("input").attr("href");
                Elements contentels = temp.select(".message");
                String finalcontent = contentels.html();

                int b = GetIndex.getIndex(index);
                if(b<=a){
                    continue;
                }

                //是否移除所有样式
                if(PublicData.ISSHOW_PLAIN){
                    //移除所有style
                    //移除font所有样式
                    contentels.select("[style]").removeAttr("style");
                    contentels.select("font").removeAttr("color").removeAttr("size").removeAttr("face");
                }
                //这是内容
                if(index.contains("楼主")||index.contains("收藏")){
                    //修改表情大小 30x30
                    for (Element tempp : contentels.select("img[src^=static/image/smiley/]")) {
                        tempp.attr("style", "width:30px;height: 30px;");
                    }
                    //替换代码块里面的br
                    for(Element tempp:contentels.select(".blockcode")){
                        tempp.select("br").remove();
                    }
                    for(Element ttt:contentels.select("a[href*=from=album]")){
                        ttt.select("img").attr("style","display: block;margin:10px auto;width:80%;");
                    }
                    ////替换无意义的 br
                    finalcontent = contentels.html().replaceAll("(\\s*<br>\\s*){2,}","");
                    String newtime = posttime.replace("收藏","");
                    data = new SingleArticleData(SingleType.CONTENT, userimg,username,newtime,index,ARTICLE_TITLE,finalcontent);
                } else {
                    data = new SingleArticleData(SingleType.COMMENT,userimg,username,posttime,index,replyUrl,finalcontent);
                }

                tepdata.add(data);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(actionBar!=null&&CURRENT_PAGE==1){
                actionBar.setTitle(ARTICLE_TITLE);
                actionBar.setSubtitle(ARTICLE_SUB_TITLE);
            }
            int start = mydatalist.size();
            mydatalist.addAll(tepdata);
            String indexs ="0";
            if(mydatalist.size()>1){
                indexs= mydatalist.get(mydatalist.size()-1).getIndex();
            }

            int a = GetIndex.getIndex(indexs);
            CURRENT_PAGE = (a+9)/10;
            mRecyleAdapter.notifyItemRangeInserted(start, tepdata.size());
            isEnableLoadMore = true;
            refreshLayout.setRefreshing(false);
        }

    }

    ////recyclerView item点击事件 加载更多事件
    @Override
    public void recyclerViewListClicked(View v, int position) {
        if(v.getId()==R.id.btn_star){
            if(isNeedLoginDialog()){
                Toast.makeText(getApplicationContext(),"正在收藏......",Toast.LENGTH_SHORT).show();
                starTask();
            }
        }else if(v.getId()==R.id.btn_reply){
            if(isNeedLoginDialog()){
                show_ime();
            }
            //回复层主
        }else if(v.getId()==R.id.btn_reply_2){
            if(isNeedLoginDialog()){
                SingleArticleData single = mydatalist.get(position);
                String replyUrl = single.getReplyUrlTitle();
                String replyIndex = single.getIndex();
                String replyName = single.getUsername();
                String  ref = Jsoup.parse(single.getCotent()).text();
                ReplyCen(replyUrl,replyIndex,replyName,ref);
            }
        }
        if(position==mydatalist.size()){
            //加载更多被电击
            if(isEnableLoadMore){
                isEnableLoadMore = false;
                onLoadMore();
            }
        }
    }

    //收藏 任务
    private void starTask(){
        final String url = UrlUtils.getStarUrl(ARTICLE_TID);
        Map<String,String> params = new HashMap<>();
        params.put("favoritesubmit","true");
        params.put("formhash", PublicData.FORMHASH);
        HttpUtil.post(this, url, params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);

                if(res.contains("成功")){
                    Toast.makeText(getApplicationContext(),"收藏成功",Toast.LENGTH_SHORT).show();
                }else if(res.contains("您已收藏")){
                    Toast.makeText(getApplicationContext(),"您已收藏请勿重复收藏",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //回复楼主
    private void post_reply(String text){
        progress = ProgressDialog.show(this, "正在发送", "请等待", true);
        Map<String,String> params = new HashMap<>();
        params.put("formhash", PublicData.FORMHASH);
        params.put("message", text);
        HttpUtil.post(this, replyUrl+"&handlekey=fastpost&loc=1&inajax=1", params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                handleReply(true,res);
            }

            @Override
            public void onFailure(Throwable e) {
                handleReply(false,"");
            }
        });
    }

    //回复层主
    private void ReplyCen(String url,String index,String name,String ref){
        ReplyDialog fragment = new ReplyDialog();
        fragment.setTitle("回复:"+index+" "+name);
        fragment.setUrl(url);
        fragment.setLasttime(replyTime);
        fragment.setReply_ref(ref);
        fragment.show(getFragmentManager(),"reply");
    }

    private void hide_ime(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void show_ime(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,1);
        }
    }

    //楼中楼回复回调函数
    @Override
    public void onDialogSendClick(final DialogFragment dialog, String url, final String text) {
        progress = ProgressDialog.show(this, "正在发送", "请等待", true);
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements els = document.select("#postform");
                String formhash = els.select("input[name=formhash]").attr("value");
                String posttime = els.select("input[name=posttime]").attr("value");
                String noticeauthor = els.select("input[name=noticeauthor]").attr("value");
                String noticetrimstr = els.select("input[name=noticetrimstr]").attr("value");
                String reppid = els.select("input[name=reppid]").attr("value");
                String reppost = els.select("input[name=reppost]").attr("value");
                String noticeauthormsg = els.select("input[name=noticeauthormsg]").attr("value");
                String postUrl = els.attr("action");
                Map<String,String> params = new HashMap<>();
                params.put("formhash",formhash);
                params.put("posttime",posttime);
                params.put("noticeauthor",noticeauthor);
                params.put("noticetrimstr",noticetrimstr);
                params.put("reppid",reppid);
                params.put("reppost",reppost);
                params.put("noticeauthormsg",noticeauthormsg);
                params.put("replysubmit","yes");
                params.put("message",text);
                HttpUtil.post(getApplicationContext(), postUrl, params, new ResponseHandler() {
                    @Override
                    public void onSuccess(byte[] response) {
                        String res = new String(response);
                        handleReply(true,res+"层主");
                    }
                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                        handleReply(false,"");
                    }
                });
            }
            @Override
            public void onFailure(Throwable e) {

            }
        });
    }

    private void handleReply(boolean isok,String res){
        progress.dismiss();
        if(isok){
            if (res.contains("成功")||res.contains("层主")) {
                Toast.makeText(getApplicationContext(), "回复发表成功", Toast.LENGTH_SHORT).show();
                MyReplyView.clearText();
                hide_ime();

                replyTime = System.currentTimeMillis();
            } else if(res.contains("您两次发表间隔")){
                Toast.makeText(getApplicationContext(), "您两次发表间隔太短了......", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "由于未知原因发表失败", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkTime(){
        if(System.currentTimeMillis()-replyTime>14500){
            return  true;
        }else{
            Toast.makeText(this,"还没到15秒呢再等等吧",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article_normal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_broswer:
                String url = PublicData.BASE_URL +UrlUtils.getSingleArticleUrl(ARTICLE_TID,CURRENT_PAGE,false);
                RequestOpenBrowser.openBroswer(this,url);
                break;
            case R.id.menu_refresh:
                refresh();
                break;
            case R.id.menu_star:
                if(isNeedLoginDialog()){
                    Toast.makeText(getApplicationContext(),"正在收藏......",Toast.LENGTH_SHORT).show();
                    starTask();
                }
                break;
            case R.id.menu_jump:
                ArticleJumpDialog dialogFragment = new ArticleJumpDialog();
                dialogFragment.setCurrentPage(CURRENT_PAGE);
                dialogFragment.setMaxPage(TOTAL_PAGE);
                dialogFragment.show(getFragmentManager(),"jump");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!MyReplyView.hideSmiley()){
            super.onBackPressed();
        }
    }

}
