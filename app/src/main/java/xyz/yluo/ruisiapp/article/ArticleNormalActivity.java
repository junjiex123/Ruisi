package xyz.yluo.ruisiapp.article;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.api.GetLevel;
import xyz.yluo.ruisiapp.api.SingleArticleData;
import xyz.yluo.ruisiapp.http.MyHttpConnection;
import xyz.yluo.ruisiapp.login.LoginActivity;
import xyz.yluo.ruisiapp.main.RecyclerViewLoadMoreListener;

/**
 * Created by free2 on 16-3-6.
 *
 */
public class ArticleNormalActivity extends AppCompatActivity
        implements RecyclerViewLoadMoreListener.OnLoadMoreListener,
        Reply_Dialog_Fragment.ReplyDialogListener,RecyclerViewClickListener{

    //当前页面
    private int CurrentPage = 1;

    //存储数据 需要填充的列表
    //TODO 动态获取
    private List<SingleArticleData> mydatalist = new ArrayList<>();
    private static String articleUrl;
    private static String articleTitle;
    private static String replaycount;
    private static String articleauthor;
    private static String articletype;
    private RecyclerView mRecyclerView;
    private ArticleRecycleAdapter mRecyleAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fab;

    public static void open(Context context, List<String> messagelist) {
        Intent intent = new Intent(context, ArticleNormalActivity.class);
        //url|标题|回复|类型|author
        articleUrl = messagelist.get(0);
        articleTitle =  messagelist.get(1);
        replaycount =  messagelist.get(2);
        articletype =  messagelist.get(3);
        articleauthor =  messagelist.get(4);
        //System.out.print("articleUrl articleTitle replaycount articletype articleauthor>>\n"+articleUrl+articleTitle+replaycount+articletype+articleauthor);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.topic_recycler_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.topic_refresh_layout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        //可以设置不同样式
        mLayoutManager = new LinearLayoutManager(this);
        //第二个参数是列数
        //mLayoutManager = new GridLayoutManager(getContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //TODO
        //以后实现，现在是静态的
        mRecyleAdapter = new ArticleRecycleAdapter(this, mydatalist);
        // Set MyRecyleAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mRecyleAdapter);

        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //加载更多实现
        mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener((LinearLayoutManager) mLayoutManager,this));

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                new GetSingleArticleData(articleUrl,1).execute((Void) null);
                System.out.print("\narticleUrl----->>>>>>>>>>>>>>"+articleUrl+"\n");
            }
        });
        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //数据填充
                GetSingleArticleData singleArticleData = new GetSingleArticleData(articleUrl,CurrentPage);
                singleArticleData.execute((Void) null);

                fab.hide();
            }
        });
        //按钮监听

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConfigClass.CONFIG_ISLOGIN){
                    //TODO  发帖逻辑
                    Reply_Dialog_Fragment newFragment = new Reply_Dialog_Fragment();
                    newFragment.show(getFragmentManager(),"replydialog");
                }else {
                    final Snackbar snackbar = Snackbar.make(view, "你好像还没有登陆!!!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("点我登陆", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivityForResult(i,1);
                            //snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }

            }
        });
    }

    //登陆页面返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
            Toast.makeText(getApplicationContext(),"result"+result,Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
             return true;
        }else //返回按钮
            if (id == android.R.id.home) {
                finish();
                return true;
            }
        return super.onOptionsItemSelected(item);
    }

    //加载更多事件
    @Override
    public void onLoadMore() {
        Toast.makeText(getApplicationContext(),"加载更多被触发",Toast.LENGTH_SHORT).show();
        GetSingleArticleData singleArticleData = new GetSingleArticleData(articleUrl,CurrentPage+1);
        singleArticleData.execute((Void) null);
    }

    //recyclerView item点击事件
    @Override
    public void recyclerViewListClicked(View v, int position) {

    }

    //发帖框回掉函数
    @Override
    public void onDialogSendClick(DialogFragment dialog, String text) {
        int len =0;
       // Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
        try {
            len = text.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(len<13){
            Toast.makeText(getApplicationContext(),"字数不够",Toast.LENGTH_SHORT).show();
            Reply_Dialog_Fragment newFragment = new Reply_Dialog_Fragment();
            newFragment.show(getFragmentManager(),"replydialog");
        }else {
            //System.out.print("\n当前文章地址"+articleUrl+"\n");
            //尝试回复
            //articleUrl
            //String str = "forum.php?mod=viewthread&tid=837479&extra=page%3D1";
            Pattern pattern = Pattern.compile("[0-9]{3,}");
            Matcher matcher = pattern.matcher(articleUrl);
            String tid ="";
            while (matcher.find()) {
                tid = articleUrl.substring(matcher.start(),matcher.end());
                //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
            }
            //System.out.print("\narticleUrl==============>>>>"+articleUrl);
            String url =ConfigClass.BBS_BASE_URL+"forum.php?mod=post&infloat=yes&action=reply&fid=72&extra=&tid="+tid+"&replysubmit=yes&inajax=1";
            Map<String, String> params = new HashMap<>();
            /*
            message:帮顶
            posttime:1457620291
            formhash:70af5bb6
            usesig:1
            subject:
            */
            params.put("formhash", ConfigClass.CONFIG_FORMHASH);
            params.put("usesig", "1");
            params.put("message", text);
            params.put("subject", "");

            UserPostTask mAuthTask = new UserPostTask(url, params);
            mAuthTask.execute((Void) null);

        }

    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {

    }


    //获得数据
    public class GetSingleArticleData extends AsyncTask<Void, Void, String> {

        private List<SingleArticleData> singledataslist;
        private String url;
        private int page;

        public GetSingleArticleData(String urlin,int page) {
            singledataslist = new ArrayList<>();
            this.url = urlin;
            this.page = page;
            //this.url = "forum.php?mod=viewthread&tid=838333";
        }

        @Override
        protected String doInBackground(Void... params) {
            System.out.print("???????????????????in exe\n");
            String response ="";
            try {
                response = MyHttpConnection.Http_get(ConfigClass.BBS_BASE_URL+url+"&page="+page);
                //System.out.print(response);
            } catch (Exception e) {
                return "error";
            }

            if(response!=""){
                Elements list = Jsoup.parse(response).select("div[id=postlist]").select("div[id^=post_]");
                //System.out.print("################"+list.html()+"\n");

                int i =0;
                SingleArticleData temp;
                for (Element src : list) {
                    //System.out.print("******************singledataslist size*********************>>"+singledataslist.size()+"\n");
                    temp = getSingleList(src);
                    if(temp!=null){
                        singledataslist.add(temp);
                    }
                    //
                }
            }
            return response;
        }
        @Override
        protected void onPostExecute(final String res) {

            if(CurrentPage==1){
                mydatalist.clear();
            }
            refreshLayout.setRefreshing(false);
            mydatalist.addAll(singledataslist);
            mRecyleAdapter.notifyDataSetChanged();
            fab.show();

            CurrentPage = page;
        }
        @Override
        protected void onCancelled() {

        }

        public SingleArticleData getSingleList(Element element){
            Boolean isGetpinfen = false;
            String pinfen = "";
            Boolean isGetgold = false;
            String gold = "";
            Boolean isGetdianpin  = false;
            String dianpin = "";
            SingleArticleData listdata =null;

            //修改表情大小
            for (Element temp : element.select("img[src^=static/image/smiley/]")) {
                //System.out.print("replace before------>>>>>>>>>>>"+temp+"\n");
                //String imgUrl = temp.attr("src");
                //String newimgurl =  imgUrl.replace("static/image/smiley/tieba/","file:///android_asset/smiley/tieba/");
                //System.out.print("replace------>>>>>>>>>>>"+imgUrl+newimgurl+"\n");
                temp.attr("style", "width:30px;height: 30px;");
            }

            //替换贴吧表情到本地
            //("static/image/smiley/tieba/","file:///android_asset/smiley/tieba/");
            for (Element temp : element.select("img[src^=static/image/smiley/tieba/]")) {
                //System.out.print("replace before------>>>>>>>>>>>"+temp+"\n");
                String imgUrl = temp.attr("src");
                String newimgurl =  imgUrl.replace("static/image/smiley/tieba/","file:///android_asset/smiley/tieba/");
                //System.out.print("replace------>>>>>>>>>>>"+imgUrl+newimgurl+"\n");
                temp.attr("src", newimgurl);
            }
            //http get 无法获得正确的图片地址
            // get1-->><img id="aimg_dfzU4" onclick="zoom(this, this.src, 0, 0, 0)" class="zoom" width="249" height="356"
            // file="http://rs.xidian.edu.cn/forum.php?mod=image&amp;aid=851820&amp;size=300x300&amp;key=2a3604eec0da779f&amp;nocache=yes&amp;type=fixnone"
            // border="0" alt="" />
            //正确的地址---->>>>
            // <img src="http://rs.xidian.edu.cn/forum.php?mod=image&amp;aid=851820&amp;size=300x300&amp;key=2a3604eec0da779f&amp;nocache=yes&amp;type=fixnone" >;

            //get2--->><img id="aimg_851787" aid="851787" src="static/image/common/none.gif"
            // zoomfile="./data/attachment/forum/201603/15/110909j3zlw4uoez7we5eq.jpg"
            // file="./data/attachment/forum/201603/15/110909j3zlw4uoez7we5eq.jpg"
            // class="zoom" onclick="zoom(this, this.src, 0, 0, 0)" width="698" id="aimg_851787" inpost="1"
            // onmouseover="showMenu({'ctrlid':this.id,'pos':'12'})" />
            //正确的地址2
            // <img src="./data/attachment/forum/201603/15/110909j3zlw4uoez7we5eq.jpg">

            // 修正图片链接地址
            //[attr^=value], [attr$=value], [attr*=value]这三个语法分别代表，属性以 value 开头、结尾以及包含
            for (Element temp : element.select("img[file^=http://rs.xidian.edu.cn/forum.php?mod=image],img[file^=./data/attachment/]")) {
                //System.out.print("replace before------>>>>>>>>>>>"+temp+"\n");
                String imgUrl = temp.attr("file");
                temp.attr("src", imgUrl);
                temp.attr("file","");
                temp.attr("width","");
                //img{display: inline; height: auto; max-width: 100%;}
                //temp.attr("display: inline; height: auto; max-width: 100%;");
            }

            String username = element.select("div[class=pi]").select("div[class=authi]").select("a[href^=home.php?mod=space][class=xi2]").text().trim();

            //金币贴获得了金币
            gold = element.select("td[class=plc]").select("div[class=cm]").select("h3.psth.xs1").select("span").text();
            if(gold !=""){
                //获得了金币 flag = true；
                isGetgold = true;
                //System.out.print("\nyou get gold----->>>>>>\n"+gold+"<<<<<<-----\n");
            }

            //TODO 简单评分 以后加强
            pinfen = element.select("td[class=plc]").select("div.pcb").select("dl.rate").select("table").select("th.xw1").text().trim();
            if(pinfen !=""){
                //pinfenpeople = temppinfen.select("tr[id^=rate_]").text().trim();
                //获得了金币 flag = true；
                isGetpinfen = true;
                //System.out.print("\nyou get pinfen----->>>>>>\n"+pinfen+"<<<<<<-----\n");
            }
            //TODO 获得了内容 处理它
            Elements content= element.select("td[class=plc]").select("div[class=pcb]").select("td[class=t_f][id^=postmessage]");
            //TODO  有bug 不完整
             //content= element.select("td[class=plc]").select("div[class=pcb]").select("div[class=t_fsz]");
            //System.out.print("\n"+content.html());

            //替换影响webView宽度的标记
            String contentbefore = content.html().replaceAll("(white-space:\\s*nowrap)","white-space:normal");

            //替换连续回车为1个
            String newcontent = contentbefore.replaceAll("(\\s*<br>\\s*){2,}","<br>");
            //(<br>){1,}
            // <br />
            //<br />
            //<br />
            //  (<br />\s*){2,}
            //System.out.print("\n"+newcontent);

            if(username!=""&&content.html()!=""){
                String time = element.select("div[class=pi]").select("div[class=authi]").select("em[id^=authorposton]").text().trim();
                String userUrl = element.select("div[class=pi]").select("div[class=authi]").select("a[href^=home.php?mod=space][class=xi2]").attr("href").trim();
                String imgurl = element.select("td[class=pls]").select("div[class=avatar]").select("img[src^=http://rs.xidian.edu.cn/ucenter/data/avatar]").attr("src").trim();

                //获得用户积分
                String usergroup = element.select("a[href$=profile][class=xi2]").text().trim();
                if(usergroup.contains(" ")){
                    usergroup = usergroup.split(" ")[0];
                }

                //System.out.print("\n用户积分——————————>>>>"+usergroup+"\n");

                String level = GetLevel.getUserLevel(Integer.parseInt(usergroup));
                System.out.print("\n>>>>>>>>>>>>>>>"+username+"||>>"+userUrl+"||>>"+time+"||>>"+imgurl+"||>>"+level+"<<<<<<<<<<<<<<<<<<\n"+newcontent+"\n");
                // replaycount;String articleauthor;articletype;

                listdata = new SingleArticleData(articleTitle,articletype,username,userUrl,imgurl,time,level,replaycount,newcontent);
                if(isGetgold){
                    listdata.isGetGold = true;
                    listdata.setGoldnum(gold);
                }if(isGetdianpin){
                    listdata.isGetDianpin =true;
                    listdata.setDianpin(dianpin);
                }if(isGetpinfen){
                    listdata.isGetpingfen = true;
                    listdata.setPingfen(pinfen);
                }

            }
            return listdata;
        }
    }

    //发帖
    public class UserPostTask extends AsyncTask<Void, Void, String> {

        private final String url;
        private final Map<String, String> paramss;

        UserPostTask(String url, Map<String, String> paramss) {
            this.url = url;
            this.paramss = paramss;
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            response = MyHttpConnection.Http_post(url, paramss);

            //System.out.print("post response>>>>>>>>>>\n" + response);

            return response;
        }

        @Override
        protected void onPostExecute(final String res) {
            Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_SHORT).show();
        }
    }


}
