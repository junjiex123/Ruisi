package xyz.yluo.ruisiapp.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SingleArticleAdapter;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.fragment.NeedLoginDialogFragment;
import xyz.yluo.ruisiapp.fragment.Reply_Dialog_Fragment;
import xyz.yluo.ruisiapp.listener.HidingScrollListener;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.PostHander;
import xyz.yluo.ruisiapp.utils.RequestOpenBrowser;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-6.
 *
 */
public class SingleArticleNormalActivity extends AppCompatActivity
        implements RecyclerViewClickListener,LoadMoreListener.OnLoadMoreListener,Reply_Dialog_Fragment.ReplyDialogListener {

    @Bind(R.id.topic_recycler_view)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.topic_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.replay_bar)
    protected LinearLayout replay_bar;
    @Bind(R.id.input_aera)
    protected EditText input_aera;
    @Bind(R.id.smiley_container)
    protected LinearLayout smiley_container;
    private ActionBar actionBar;
    private ProgressDialog progress;
    private long replyTime = 0;

    //当前评论第几页
    private int CURRENT_PAGE = 1;
    //存储数据 需要填充的列表
    private List<SingleArticleData> mydatalist = new ArrayList<>();
    private static String ARTICLE_TID;
    private static String ARTICLE_TITLE = "";
    private static String ARTICLE_REPLY_COUNT;
    private static String ARTICLE_TYPE;
    //TODO 金币贴/精华。。。
    //TODO 跳转到指定楼层
    //快速 跳转

    //当前回复链接
    private boolean isEnableLoadMore = false;
    //回复楼主的链接
    private String replyUrl = "";
    //下一页链接
    private String nextPageUrl = "";
    private SingleArticleAdapter mRecyleAdapter;

    //约定好要就收的数据
    public static void open(Context context, String tid,String title,String replycount,String type) {
        Intent intent = new Intent(context, SingleArticleNormalActivity.class);
        ARTICLE_TID = tid;
        ARTICLE_TITLE = title;
        ARTICLE_REPLY_COUNT = replycount;
        ARTICLE_TYPE = type;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

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
        getArticleData();

    }

    private void init(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) replay_bar.getLayoutParams();
                int bottomMargin = lp.bottomMargin;
                int distanceToScroll = replay_bar.getHeight() + bottomMargin;
                replay_bar.animate().translationY(distanceToScroll).setInterpolator(new AccelerateInterpolator(5));
            }

            @Override
            public void onShow() {
                replay_bar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(5));
            }
        });

        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //数据填充
                CURRENT_PAGE = 1;
                nextPageUrl = "";
                mydatalist.clear();
                mRecyleAdapter.notifyDataSetChanged();
                getArticleData();
            }
        });
    }

    @OnClick(R.id.input_aera)
    protected void input_aera_click(){
        smiley_container.setVisibility(View.GONE);
    }

    @OnClick(R.id.action_smiley)
    protected void action_smiey_click(){
        if(smiley_container.getVisibility()==View.VISIBLE){
            smiley_container.setVisibility(View.GONE);
        }else{
            smiley_container.setVisibility(View.VISIBLE);
        }

    }
    @OnClick(R.id.action_send)
    protected void action_send_click(){
        smiley_container.setVisibility(View.GONE);
        hide_ime();
        //按钮监听
        if(isNeedLoginDialog()){
            if(checkTime()){
                if(checkLength(input_aera.getText().toString())){
                    post_reply(input_aera.getText().toString());
                }

            }
        }

    }

    @OnClick({R.id._1000, R.id._1001,R.id._1002,R.id._1003,R.id._1005,
            R.id._1006,R.id._1007,R.id._1008,R.id._1009,R.id._1010,
            R.id._1011,R.id._1012,R.id._1013,R.id._1014,R.id._1015,
            R.id._1016,R.id._1017,R.id._1018,R.id._1019,R.id._1020,
            R.id._1021,R.id._1022,R.id._1023,R.id._1024,R.id._1025,
            R.id._1027,R.id._1028,R.id._1029,R.id._1030, R.id._998,
            R.id._999,R.id._9998,R.id._9999
    })
    protected void smiley_click(ImageButton btn){
        //插入表情
        //{:16_1021:}
        //_1021
        //input_aera.append(btn.getTag().toString());
        String tmp = btn.getTag().toString();
        PostHander hander = new PostHander(getApplicationContext(),input_aera);
        hander.insertSmiley("{:16" + tmp + ":}", btn.getDrawable());
    }

    //登陆页面返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
        }
    }
    //recyclerView item点击事件 加载更多事件
    @Override
    public void recyclerViewListClicked(View v, int position) {
        if(v.getId()==R.id.btn_star){
            if(isNeedLoginDialog()){
                Toast.makeText(getApplicationContext(),"正在收藏......",Toast.LENGTH_SHORT).show();
                starTask();
            }
        }else if(v.getId()==R.id.btn_reply){
            if(isNeedLoginDialog()){
                replay_bar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(5));
                show_ime();
            }
        }else if(v.getId()==R.id.btn_reply_2){
            if(isNeedLoginDialog()){
                String replyUrl = mydatalist.get(position).getReplyUrl();
                String replyIndex = mydatalist.get(position).getIndex();
                String replyName = mydatalist.get(position).getUsername();
                ReplyCen(replyUrl,replyIndex,replyName);
            }
        }
        if(position==mydatalist.size()){
            //加载更多被电击
            if(isEnableLoadMore){
                isEnableLoadMore = false;
                if(!nextPageUrl.isEmpty()){
                    CURRENT_PAGE++;
                }
                getArticleData();
            }
        }
    }

    private boolean isNeedLoginDialog(){
        if (MySetting.CONFIG_ISLOGIN) {
            return true;
        } else {
            NeedLoginDialogFragment dialogFragment = new NeedLoginDialogFragment();
            dialogFragment.show(getFragmentManager(), "needlogin");
        }
        return false;
    }

    //文章一页的html 根据页数 tid
    private void getArticleData() {
        //到哪一页去加载
        if(mydatalist.size()==0){
            CURRENT_PAGE =1;
        }
        String url = UrlUtils.getSingleArticleUrl(ARTICLE_TID,CURRENT_PAGE,false);
        if(!nextPageUrl.isEmpty()){
            url = nextPageUrl;
        }

        AsyncHttpCilentUtil.get(this, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                new DealWithArticleData(res).execute((Void) null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLoadMore() {
        //加载更多被电击
        if(isEnableLoadMore){
            isEnableLoadMore = false;
            if(!nextPageUrl.isEmpty()){
                CURRENT_PAGE++;
            }
            getArticleData();
        }
    }

    public class DealWithArticleData extends AsyncTask<Void,Void,String>{
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
            //获取回复/hash
            if (doc.select("input[name=formhash]").first() != null) {
                replyUrl = doc.select("form#fastpostform").attr("action");
                String hash = doc.select("input[name=formhash]").attr("value"); // 具有 formhash 属性的链接
                if (!hash.isEmpty()){
                    MySetting.CONFIG_FORMHASH =hash;
                }
            }

            String url = doc.select(".pg").select("a.nxt").attr("href");
            if(url.contains("forum.php")){
                nextPageUrl = url;
            }else{
                nextPageUrl = "";
            }
            Elements elements = doc.select(".postlist");
            if(elements!=null){
                SingleArticleData data;
                //获取标题
                if(ARTICLE_TITLE.equals("")){
                    ARTICLE_TITLE = elements.select("h2").first().text().trim();
                }

                Elements postlist = elements.select("div[id^=pid]");

                for(Element temp:postlist){
                    String userimg = temp.select("span[class=avatar]").select("img").attr("src");
                    Elements userInfo = temp.select("ul.authi");
                    String index = userInfo.select("li.grey").select("em").text();
                    String username = userInfo.select("a[href^=home.php?mod=space&uid=]").text();
                    String posttime = userInfo.select("li.grey.rela").text();
                    String star = userInfo.select("a[href^=home.php?mod=spacecp&ac=favorite").text();
                    String replyUrl = temp.select(".replybtn").select("input").attr("href");

                    //是否移除所有样式
                    if(MySetting.CONFIG_SHOW_PLAIN_TEXT){
                        //移除所有style
                        //移除font所有样式
                        temp.select("[style]").removeAttr("style");
                        temp.select("font").removeAttr("color").removeAttr("size").removeAttr("face");
                    }

                    //修改表情大小 30x30
                    for (Element tempp : temp.select("img[src^=static/image/smiley/]")) {

                        tempp.attr("style", "width:30px;height: 30px;");
                    }

                    //替换代码块里面的br
                    for(Element tempp:temp.select(".blockcode")){
                        tempp.select("br").remove();
                    }

                    //替换无意义的 br
                    String content = temp.select(".message").html().replaceAll("(\\s*<br>\\s*){2,}","");
                    //文章内容
                    if(mydatalist.size()==0&&tepdata.size()==0){
                        String newtime = posttime.replace("收藏","");
                        //title, type, replyCount,username,userUrl,userImgUrl,postTime,cotent
                        data = new SingleArticleData(ARTICLE_TITLE,ARTICLE_TYPE,ARTICLE_REPLY_COUNT,username,userimg,newtime,content);
                        tepdata.add(data);
                    }else{
                        //又回到了1楼
                        if(star.equals("收藏")&&tepdata.size()==0){
                            break;
                        }
                        //评论
                        //String username, String userImgUrl, String postTime,String index,String replyUrl,String cotent
                        data = new SingleArticleData(username,userimg,posttime,index,replyUrl,content);
                        tepdata.add(data);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!ARTICLE_TITLE.isEmpty()&&actionBar!=null){
                actionBar.setTitle(ARTICLE_TITLE);
            }
            int start = mydatalist.size();
            int add = 0;
            if(!nextPageUrl.isEmpty()||mydatalist.size()==0){
                mydatalist.addAll(tepdata);
                add = tepdata.size();
            }else if(tepdata.size()==0){
                CURRENT_PAGE--;
                add =0 ;
            }else if(tepdata.size()>0){
                for(int i=(mydatalist.size()%10);i<tepdata.size();i++){
                    mydatalist.add(tepdata.get(i));
                    add++;
                }
            }
            if(add==0){
                Toast.makeText(getApplicationContext(),"暂无更多",Toast.LENGTH_SHORT).show();
            }
            mRecyleAdapter.notifyItemRangeInserted(start, add);
            isEnableLoadMore = true;
            refreshLayout.setRefreshing(false);
        }

    }

    //收藏 任务
    private void starTask(){
        final String url = UrlUtils.getStarUrl(ARTICLE_TID);
        RequestParams params = new RequestParams();
        //favoritesubmit:true
        //formhash:b6ba5839
        params.add("favoritesubmit","true");
        params.add("formhash", MySetting.CONFIG_FORMHASH);

        AsyncHttpCilentUtil.post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);

                if(res.contains("成功")){
                    Toast.makeText(getApplicationContext(),"收藏成功",Toast.LENGTH_SHORT).show();
                }else if(res.contains("您已收藏")){
                    Toast.makeText(getApplicationContext(),"您已收藏请勿重复收藏",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_reply(String text){
        progress = ProgressDialog.show(this, "正在发送", "请等待", true);
        /*
        message:帮顶
        formhash:70af5bb6
        */
        RequestParams params = new RequestParams();
        params.put("formhash", MySetting.CONFIG_FORMHASH);
        params.put("message", text);
        AsyncHttpCilentUtil.post(getApplicationContext(), replyUrl+"&handlekey=fastpost&loc=1&inajax=1", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                handleReply(true,res);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handleReply(false,"");
            }
        });
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

    //回复层主
    private void ReplyCen(String url,String index,String name){
        Reply_Dialog_Fragment fragment = new Reply_Dialog_Fragment();
        fragment.setTitle("回复:"+index+" "+name);
        fragment.setUrl(url);
        fragment.setLasttime(replyTime);
        fragment.show(getFragmentManager(),"reply");
    }

    //楼中楼回复回调函数
    @Override
    public void onDialogSendClick(final DialogFragment dialog, String url, final String text) {

        progress = ProgressDialog.show(this, "正在发送", "请等待", true);
        AsyncHttpCilentUtil.get(this, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Document document = Jsoup.parse(new String(responseBody));
                Elements els = document.select("#postform");
                String formhash = els.select("input[name=formhash]").attr("value");
                String posttime = els.select("input[name=posttime]").attr("value");
                String noticeauthor = els.select("input[name=noticeauthor]").attr("value");
                String noticetrimstr = els.select("input[name=noticetrimstr]").attr("value");
                String reppid = els.select("input[name=reppid]").attr("value");
                String reppost = els.select("input[name=reppost]").attr("value");
                String noticeauthormsg = els.select("input[name=noticeauthormsg]").attr("value");
                String postUrl = els.attr("action");
                RequestParams params = new RequestParams();
                params.add("formhash",formhash);
                params.add("posttime",posttime);
                params.add("noticeauthor",noticeauthor);
                params.add("noticetrimstr",noticetrimstr);
                params.add("reppid",reppid);
                params.add("reppost",reppost);
                params.add("noticeauthormsg",noticeauthormsg);
                params.add("replysubmit","yes");
                params.add("message",text);
                AsyncHttpCilentUtil.post(getApplicationContext(), postUrl, params, new AsyncHttpResponseHandler() {
                    //TODO 不管成不成功 都是 failer
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        handleReply(true,res);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        handleReply(false,"todo");
                    }
                });
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handleReply(false,"");
            }
        });
    }

    private void handleReply(boolean isok,String res){
        progress.dismiss();
        if(isok){
            if (res.contains("成功")) {
                //修正页数
                if(nextPageUrl.isEmpty()&&mydatalist.size()%10==0){
                    nextPageUrl = "mm";
                }
                Toast.makeText(getApplicationContext(), "回复发表成功", Toast.LENGTH_SHORT).show();
                input_aera.setText("");
                hide_ime();
                replyTime = System.currentTimeMillis();
            } else if(res.contains("您两次发表间隔")){
                Toast.makeText(getApplicationContext(), "您两次发表间隔太短了......", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "由于未知原因发表失败", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(res.equals("todo")){
                Toast.makeText(getApplicationContext(),"应该发送成功了.....",Toast.LENGTH_SHORT).show();
                replyTime = System.currentTimeMillis();
            }else{
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }

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

    private boolean checkLength(String str){
        int len =0;
        // Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
        try {
            len = str.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(len<13){
            Toast.makeText(getApplicationContext(),"字数不够要13个字节！！",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
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

        if (id == android.R.id.home) {
            finish();
            return true;
        }else if(id==R.id.menu_broswer){
            String url = MySetting.BBS_BASE_URL+UrlUtils.getSingleArticleUrl(ARTICLE_TID,CURRENT_PAGE,false);
            RequestOpenBrowser.openBroswer(this,url);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(smiley_container.getVisibility()==View.VISIBLE){
            smiley_container.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }

    }

}
