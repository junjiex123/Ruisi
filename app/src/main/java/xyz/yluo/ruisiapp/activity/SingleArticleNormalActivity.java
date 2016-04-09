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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jude.swipbackhelper.SwipeBackHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SingleArticleAdapter;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.fragment.NeedLoginDialogFragment;
import xyz.yluo.ruisiapp.fragment.Reply_Dialog_Fragment;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.utils.PostHander;
import xyz.yluo.ruisiapp.utils.RequestOpenBrowser;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-6.
 *
 */
public class SingleArticleNormalActivity extends AppCompatActivity
        implements RecyclerViewClickListener,LoadMoreListener.OnLoadMoreListener,
        Reply_Dialog_Fragment.ReplyDialogListener{

    @Bind(R.id.topic_recycler_view)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.topic_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.input_aera)
    protected EditText input_aera;
    @Bind(R.id.smiley_container)
    protected LinearLayout smiley_container;
    private ActionBar actionBar;
    private ProgressDialog progress;
    //上一次回复时间
    private long replyTime = 0;

    //当前第几页
    private int CURRENT_PAGE = 1;
    //是否倒叙浏览
    private boolean isReverse = false;
    //全部页数
    private int TOTAL_PAGE = 1;

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
    private SingleArticleAdapter mRecyleAdapter;

    //约定好要就收的数据
    public static void open(Context context, String tid,String title,String replycount,String type) {
        Intent intent = new Intent(context, SingleArticleNormalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        SwipeBackHelper.onCreate(this);
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
        getArticleData(1);

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
        String tmp = btn.getTag().toString();
        PostHander hander = new PostHander(getApplicationContext(),input_aera);
        hander.insertSmiley("{:16" + tmp + ":}", btn.getDrawable());
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
                onLoadMore();
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

    private void refresh(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        //数据填充
        CURRENT_PAGE = 1;
        mydatalist.clear();
        mRecyleAdapter.notifyDataSetChanged();
        getArticleData(1);
    }

    //文章一页的html 根据页数 tid
    private void getArticleData(final int page) {

        String url = UrlUtils.getSingleArticleUrl(ARTICLE_TID,page,false);
        //是否倒序查看
        if(isReverse){
            url+="&ordertype=1";
        }
        HttpUtil.get(this ,url,new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                new DealWithArticleData(res,page).execute((Void) null);
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), ">>>网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class DealWithArticleData extends AsyncTask<Void,Void,String>{
        //* 传入一篇文章html
        //* 返回list<SingleArticleData>
        private List<SingleArticleData> tepdata = new ArrayList<>();
        private String htmlData;
        private int page;
        public DealWithArticleData(String htmlData,int page) {
            this.page = page;
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
                    String replyUrl = temp.select(".replybtn").select("input").attr("href");

                    Elements contentels = temp.select(".message");

                    //是否移除所有样式
                    if(MySetting.CONFIG_SHOW_PLAIN_TEXT){
                        //移除所有style
                        //移除font所有样式
                        contentels.select("[style]").removeAttr("style");
                        contentels.select("font").removeAttr("color").removeAttr("size").removeAttr("face");
                    }

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
                    String finalcontent = contentels.html().replaceAll("(\\s*<br>\\s*){2,}","");;


                    //String content = temp.select(".message").html()

                    if(mydatalist.size()==0&&tepdata.size()==0){
                        //文章内容
                        String newtime = posttime.replace("收藏","");
                        data = new SingleArticleData(ARTICLE_TITLE,ARTICLE_TYPE,ARTICLE_REPLY_COUNT,username,userimg,newtime,finalcontent);
                        tepdata.add(data);
                    }else{
                        //评论
                        data = new SingleArticleData(username,userimg,posttime,index,replyUrl,finalcontent);
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

            if(page<TOTAL_PAGE){
                mydatalist.addAll(tepdata);
                add = tepdata.size();
                CURRENT_PAGE++;
            }else if(page==TOTAL_PAGE){
                int have =mydatalist.size() - (CURRENT_PAGE-1)*10;
                int get = tepdata.size();

                for(int i = have;i<get&&i>=0;i++){
                    mydatalist.add(tepdata.get(i));
                    add++;
                }
            }

            mRecyleAdapter.notifyItemRangeInserted(start, add);
            isEnableLoadMore = true;
            refreshLayout.setRefreshing(false);
        }

    }

    //收藏 任务
    private void starTask(){
        final String url = UrlUtils.getStarUrl(ARTICLE_TID);
        Map<String,String> params = new HashMap<>();
        params.put("favoritesubmit","true");
        params.put("formhash", MySetting.CONFIG_FORMHASH);

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

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post_reply(String text){
        progress = ProgressDialog.show(this, "正在发送", "请等待", true);
        Map<String,String> params = new HashMap<>();
        params.put("formhash", MySetting.CONFIG_FORMHASH);
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
                input_aera.setText("");
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
        }else if(id==R.id.menu_refresh){
            refresh();
        }else if(id==R.id.menu_reverse){
            isReverse = !isReverse;
            refresh();
        }else if(id==R.id.menu_star){
            if(isNeedLoginDialog()){
                Toast.makeText(getApplicationContext(),"正在收藏......",Toast.LENGTH_SHORT).show();
                starTask();
            }
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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }
}
