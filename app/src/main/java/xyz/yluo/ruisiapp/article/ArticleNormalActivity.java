package xyz.yluo.ruisiapp.article;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.api.GetLevel;
import xyz.yluo.ruisiapp.api.SingleArticleData;
import xyz.yluo.ruisiapp.http.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.login.LoginActivity;

/**
 * Created by free2 on 16-3-6.
 *
 */
public class ArticleNormalActivity extends AppCompatActivity
        implements Reply_Dialog_Fragment.ReplyDialogListener,RecyclerViewClickListener{

    @Bind(R.id.topic_recycler_view)
    protected RecyclerView mRecyclerView;
    @Bind(R.id.topic_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    @Bind(R.id.fab)
    protected FloatingActionButton fab;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.replay_bar)
    protected LinearLayout replay_bar;

    //当前评论第几页
    private int CurrentPage = 1;
    //存储数据 需要填充的列表
    private List<SingleArticleData> mydatalist = new ArrayList<>();
    private static String articleUrl;
    private static String articleTitle;
    private static String replaycount;
    private static String articleauthor;
    private static String articletype;
    private ArticleRecycleAdapter mRecyleAdapter;


    public static void open(Context context, List<String> messagelist) {
        Intent intent = new Intent(context, ArticleNormalActivity.class);
        //url|标题|回复|类型|author
        articleUrl = messagelist.get(0);
        articleTitle =  messagelist.get(1);
        replaycount =  messagelist.get(2);
        articletype =  messagelist.get(3);
        articleauthor =  messagelist.get(4);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(articleTitle);
        }



        //mLayoutManager = new GridLayoutManager(getContext(),2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyleAdapter = new ArticleRecycleAdapter(this, this, mydatalist);
        // Set MyRecyleAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mRecyleAdapter);

        //item 增加删除动画
        mRecyclerView.setItemAnimator(new FadeInDownAnimator());
        mRecyclerView.getItemAnimator().setAddDuration(100);
        mRecyclerView.getItemAnimator().setRemoveDuration(10);
        mRecyclerView.getItemAnimator().setChangeDuration(10);


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

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getArticleData(articleUrl, 1);
            }
        });
        //下拉刷新
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fab.hide();
                //数据填充
                getArticleData(articleUrl, CurrentPage);
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
    public void onDialogCancelClick(DialogFragment dialog) {

    }

    //recyclerView item点击事件 加载更多事件
    @Override
    public void recyclerViewListClicked(View v, int position) {
        Toast.makeText(getApplicationContext(),"被电击"+position+"|"+mydatalist.size(),Toast.LENGTH_SHORT).show();
        if(position==mydatalist.size()){
            int newpage  = CurrentPage;
            //加载更多 被电击
            if(position%10==0){
                //本页最后一个
                //到下一页去数据填充
                CurrentPage++;
                newpage+=1;
            }
            getArticleData(articleUrl, newpage);
        }
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
            String url ="forum.php?mod=post&infloat=yes&action=reply&fid=72&extra=&tid="+tid+"&replysubmit=yes&inajax=1";
            /*
            message:帮顶
            posttime:1457620291
            formhash:70af5bb6
            usesig:1
            subject:
            */
            RequestParams params = new RequestParams();
            params.put("formhash", ConfigClass.CONFIG_FORMHASH);
            params.put("usesig", "1");
            params.put("message", text);
            params.put("subject", "");

            AsyncHttpCilentUtil.post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
        }

    }


    //文章一页的html 根据页数 url
    private void getArticleData(String url, final int page) {
        //"forum.php?mod=viewthread&tid=838333";

        AsyncHttpCilentUtil.get(this, url + "&page=" + page, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                //neednum需要的个数 page在哪一页

                int neednum = mydatalist.size() - (page - 1) * 10;
                //Toast.makeText(getApplicationContext(),"success"+res,Toast.LENGTH_LONG).show();
                new DealWithArticleData(res, neednum).execute((Void) null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public class DealWithArticleData extends AsyncTask<Void,Void,String>{

        //* 传入一篇文章html
        //* 返回list<SingleArticleData>

        //临时存储
        List<SingleArticleData> templist = new ArrayList<>();

        private String htmlData;
        private int need;

        private int index =0;

        public DealWithArticleData(String htmlData,int need) {
            this.htmlData = htmlData;
            this.need = need;
        }

        @Override
        protected String doInBackground(Void... params) {



            //list 所有楼数据
            Elements list = Jsoup.parse(htmlData).select("div[id=postlist]").select("div[id^=post_]");

            for (Element element : list) {
                //每层楼数据
                if(index<need){
                    index++;
                }else{
                    index++;
                    boolean isGetpinfen = false;
                    String pinfen = "";
                    boolean isGetgold = false;
                    String gold = "";
                    boolean isGetdianpin  = false;
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
                        System.out.print("\n>>>>>>>\n>>>>>>>>\nusername :"+username+"\nuserUrl :"+userUrl+"\ntime :"+time+"\nimgurl :"+imgurl+"\nlevel  :"+level+"\ncontent: "+newcontent+"\n");
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

                    if(listdata!=null){
                        templist.add(listdata);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(index==0){
                //没有加载到数据
                Toast.makeText(getApplicationContext(),"暂无更多",Toast.LENGTH_SHORT).show();
                CurrentPage--;
            }
            //增加了多少个
            int addnum = templist.size();

            int staart = mydatalist.size();

            //刷新全部
            //mRecyleAdapter.notifyDataSetChanged();
            //部分
            mydatalist.addAll(templist);
            mRecyleAdapter.notifyItemRangeInserted(staart,addnum);
            refreshLayout.setRefreshing(false);
            fab.show();
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

}
