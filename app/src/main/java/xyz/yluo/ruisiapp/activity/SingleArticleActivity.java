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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import xyz.yluo.ruisiapp.View.ArticleJumpDialog;
import xyz.yluo.ruisiapp.View.MyReplyView;
import xyz.yluo.ruisiapp.View.NeedLoginDialogFragment;
import xyz.yluo.ruisiapp.View.ReplyDialog;
import xyz.yluo.ruisiapp.adapter.SingleArticleAdapter;
import xyz.yluo.ruisiapp.data.LoadMoreType;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.data.SingleType;
import xyz.yluo.ruisiapp.database.MyDbUtils;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.listener.ReplyBarListner;
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
        implements RecyclerViewClickListener,LoadMoreListener.OnLoadMoreListener,
        ReplyDialog.ReplyDialogListener,ArticleJumpDialog.JumpDialogListener{

    private RecyclerView mRecyclerView;
    protected SwipeRefreshLayout refreshLayout;
    private MyReplyView MyReplyView;
    private ActionBar actionBar;
    private ProgressDialog progress;

    //上一次回复时间
    private long replyTime = 0;
    //当前第几页
    private int page_now = 1;
    private int page_sum = 1;
    //是否倒序
    private boolean isRevere = false;
    //是否允许加载更多
    private boolean isEnableLoadMore = false;
    //回复楼主的链接
    private String replyUrl = "";
    private SingleArticleAdapter mRecyleAdapter;
    //存储数据 需要填充的列表
    private List<SingleArticleData> mydatalist = new ArrayList<>();
    private boolean isSetToolBar = false;
    //是否调到指定页数and楼层???
    private boolean isRedirect,isSaveToDataBase  = false;
    private String Title,Author,Tid= "";

    public static void open(Context context, String url,String title,String author) {
        Intent intent = new Intent(context, SingleArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url",url);

        Log.i("open article",title+url+author);

        if(title!=null){
            intent.putExtra("title",title);
        }
        if(author!=null){
            intent.putExtra("author",author);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_chat);

        mRecyclerView = (RecyclerView) findViewById(R.id.topic_recycler_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.topic_refresh_layout);
        MyReplyView = (xyz.yluo.ruisiapp.View.MyReplyView) findViewById(R.id.replay_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("正在加载...");
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
        mRecyclerView.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) mLayoutManager, this,8));


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

        try {
            String url =  getIntent().getExtras().getString("url");
            if(getIntent().getExtras().containsKey("title")){
                Title = getIntent().getExtras().getString("title");
            }
            if(getIntent().getExtras().containsKey("author")){
                Author = getIntent().getExtras().getString("author");
            }
            Tid = GetId.getTid(url);

            if(url!=null&&url.contains("redirect")){
                if(!PublicData.IS_SCHOOL_NET){
                    url = url+"&mobile=2";
                }
                isRedirect = true;
                HttpUtil.head(this, url, new ResponseHandler() {
                    @Override
                    public void onSuccess(byte[] response) {
                        int page = GetId.getPage(new String(response));
                        firstGetData(page);
                    }
                });
            }else{
                firstGetData(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void firstGetData(int page){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        //数据填充
        getArticleData(page);
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
            int page = page_now;
            if(page_now < page_sum){
                page= page_now +1;
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

    //文章一页的html 根据页数 Tid
    private void getArticleData(final int page) {
        String url = UrlUtils.getSingleArticleUrl(Tid,page,false);
        if(isRevere){
            url += "&ordertype=1";
        }
        HttpUtil.get(this ,url,new ResponseHandler() {
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

    private class DealWithArticleData extends AsyncTask<String,Void,List<SingleArticleData>>{

        private String toolBarTitle = "";
        @Override
        protected List<SingleArticleData> doInBackground(String... params) {
            List<SingleArticleData> tepdata = new ArrayList<>();
            String htmlData = params[0];
            //list 所有楼数据
            Document doc = Jsoup.parse(htmlData);
            if(!isSetToolBar){
                String titleText = doc.select("title").text();
                String[] array = titleText.split("-");
                int len = array.length;
                if(len>=5){
                    toolBarTitle = array[len-4].trim();
                    if("".equals(Title)){
                        Title = "";
                        for(int lnea=0;lnea<len-4;lnea++){
                            Title = Title+array[lnea]+"-";
                            if(lnea==len-5){
                                Title = Title.substring(0,Title.length()-2);
                            }
                        }
                    }
                }
            }
            //获取回复/hash
            if (doc.select("input[name=formhash]").first() != null) {
                replyUrl = doc.select("form#fastpostform").attr("action");
                String hash = doc.select("input[name=formhash]").attr("value");
                if (!hash.isEmpty()){
                    PublicData.FORMHASH =hash;
                }
            }

            int index =0;
            if((page_now ==1&&mydatalist.size()==0)|| page_now < page_sum){
                index = 0;
            }else if(page_now >= page_sum){
                if(mydatalist.size()==0){
                    index = 0;
                }else{
                    index = mydatalist.size()%10;
                    if(index==0){
                        index = 10;
                    }
                }
            }
            //获取总页数 和当前页数
            if(doc.select(".pg").text().length()>0){
                if(doc.select(".pg").text().length()>0){
                    page_now = GetNumber.getNumber(doc.select(".pg").select("strong").text());
                    int n = GetNumber.getNumber(doc.select(".pg").select("span").attr("title"));
                    if(n>0&&n> page_sum){
                        page_sum = n;
                    }

                    Log.i("page info",page_now+" "+page_sum);
                }
            }
            Elements elements = doc.select(".postlist");
            Elements postlist = elements.select("div[id^=pid]");
            for(int i =index;i<postlist.size();i++){
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
                if(PublicData.ISSHOW_PLAIN){
                    //移除所有style
                    contentels.select("[style]").removeAttr("style");
                    contentels.select("font").removeAttr("color").removeAttr("size").removeAttr("face");
                }

                //处理引用
                Elements blockquotes =  contentels.select(".grey.quote").select("blockquote");
                if(blockquotes.text().contains("引用:")&&blockquotes.text().contains("发表于")){
                    System.out.println(blockquotes.text());
                    String[] arrayb = blockquotes.text().split(" ",6);
                    if(arrayb.length==6){
                        String usernameb = arrayb[1];
                        String contentb = arrayb[5];
                        blockquotes.html("回复: "+usernameb+"<br>"+contentb);
                    }
                }

                for(Element codee:contentels.select(".blockcode")){
                    codee.html("<code>"+codee.html().trim()+"</code>");
                }

                //删除修改日期
                contentels.select("i.pstatus").remove();
                String finalcontent = contentels.html().trim();
                //替换开头的br
                while (finalcontent.startsWith("<br>")){
                    finalcontent = finalcontent.substring(4,finalcontent.length()).trim();
                }

                //替换结尾的br
                while (finalcontent.endsWith("<br>")){
                    finalcontent = finalcontent.substring(0,finalcontent.length()-4).trim();
                }

                //这是内容
                if(commentindex.contains("楼主")||commentindex.contains("收藏")){
                    String newtime = posttime.replace("收藏","");
                    data = new SingleArticleData(SingleType.CONTENT,Title,userimg,username,newtime,commentindex,replyUrl,finalcontent);
                } else {
                    data = new SingleArticleData(SingleType.COMMENT,Title,userimg,username,posttime,commentindex,replyUrl,finalcontent);
                }
                tepdata.add(data);
            }

            return tepdata;
        }

        @Override
        protected void onPostExecute(List<SingleArticleData> tepdata) {
            if(!isSetToolBar){
                actionBar.setTitle(toolBarTitle);
                isSetToolBar = true;
            }

            if(!isSaveToDataBase){
                //插入数据库
                Log.i("insert ","tid:"+Tid+"title:"+Title+"author:"+Author);
                MyDbUtils myDbUtils = new MyDbUtils(getApplicationContext(),false);
                myDbUtils.handleSingle(Tid,Title,Author);//String Tid,String title,String author
                isSaveToDataBase = true;
            }
            int add = tepdata.size();
            if(add>0){
                if(add%10!=0){
                    mRecyleAdapter.setLoadMoreType(LoadMoreType.NOTHING);
                }else{
                    mRecyleAdapter.setLoadMoreType(LoadMoreType.LOADING);
                }
                int start = mydatalist.size();
                mydatalist.addAll(tepdata);
                mRecyleAdapter.notifyItemChanged(start);
                mRecyleAdapter.notifyItemRangeInserted(start+1, add);

                if(isRedirect){
                    isRedirect = false;
                    for(int i =0;i<mydatalist.size();i++){
                        if(mydatalist.get(i).getCotent().contains(PublicData.USER_NAME)){
                            mRecyclerView.scrollToPosition(i);
                            break;
                        }
                    }
                }

            }else{
                //add = 0 没有添加
                mRecyleAdapter.setLoadMoreType(LoadMoreType.NOTHING);
                mRecyleAdapter.notifyItemChanged(mRecyleAdapter.getItemCount()-1);
            }
            isEnableLoadMore = true;
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            },200);

        }
    }


    @Override
    public void recyclerViewListClicked(View v, int position) {
        switch (v.getId()){
            case R.id.btn_star:
                if(isNeedLoginDialog()){
                    starTask(v);
                }
                break;
            case R.id.btn_reply_2:
                if(isNeedLoginDialog()){
                    SingleArticleData single = mydatalist.get(position);
                    String replyUrl = single.getReplyUrlTitle();
                    String replyIndex = single.getIndex();
                    String replyName = single.getUsername();
                    String  ref = Jsoup.parse(single.getCotent()).text();
                    ReplyCen(replyUrl,replyIndex,replyName,ref);
                }
                break;
        }
    }

    //收藏 任务

    private void starTask(final View v){
        final String url = UrlUtils.getStarUrl(Tid);
        Map<String,String> params = new HashMap<>();
        params.put("favoritesubmit","true");
        params.put("formhash", PublicData.FORMHASH);
        HttpUtil.post(this, url, params, new ResponseHandler() {

            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                boolean isok = false;
                if(res.contains("成功")){
                    Toast.makeText(getApplicationContext(),"收藏成功",Toast.LENGTH_SHORT).show();
                    isok = true;
                }else if(res.contains("您已收藏")){
                    Toast.makeText(getApplicationContext(),"您已收藏请勿重复收藏",Toast.LENGTH_SHORT).show();
                    isok = true;
                }

                if(isok){
                    if(v!=null){
                        ImageView mv = (ImageView)v;
                        mv.setImageResource(R.drawable.ic_favorite_border_accent_24dp);
                    }
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
        ReplyDialog fragment = ReplyDialog.newInstance(this);
        fragment.setTitle("回复:"+index+" "+name);
        fragment.setUrl(url);
        fragment.setLasttime(replyTime);
        fragment.setReply_ref(ref);
        fragment.show(getFragmentManager(),"reply");
    }

    private void hide_ime(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        if(System.currentTimeMillis()-replyTime>15000){
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
                String url = UrlUtils.getSingleArticleUrl(Tid, page_now,false);
                RequestOpenBrowser.openBroswer(this,url);
                break;
            case R.id.menu_refresh:
                refresh();
                break;
            case R.id.menu_star:
                if(isNeedLoginDialog()){
                    Toast.makeText(getApplicationContext(),"正在收藏......",Toast.LENGTH_SHORT).show();
                    starTask(null);
                }
                break;
            case R.id.menu_jump:
                ArticleJumpDialog dialogFragment = new ArticleJumpDialog();
                dialogFragment.setCurrentPage(page_now);
                dialogFragment.setMaxPage(page_sum);
                dialogFragment.show(getFragmentManager(),"jump");
                break;
            case R.id.menu_reverse:
                isRevere = !isRevere;
                refresh();
                break;
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
