package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
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

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.adapter.SingleArticleAdapter;
import xyz.yluo.ruisiapp.data.LoadMoreType;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.data.SingleType;
import xyz.yluo.ruisiapp.database.MyDB;
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
    private int edit_pos = -1;
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
    private boolean  isSaveToDataBase = false;
    private ArrayAdapter<String> spinnerAdapter;
    private List<String> pageSpinnerDatas = new ArrayList<>();
    private String Title, AuthorName,AuthorUid, Tid ,RedirectPid= "";
    private Spinner spinner;

    private boolean showPlainText = false;

    public static void open(Context context, String url, @Nullable String author) {
        Intent intent = new Intent(context, SingleArticleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        intent.putExtra("author", TextUtils.isEmpty(author)?"null":author);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_article);

        showPlainText =  PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setting_show_plain",false);

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

        LinearLayout bottom_bar_top = (LinearLayout) findViewById(R.id.bottom_bar_top);
        for(int i=0;i<bottom_bar_top.getChildCount();i++){
            View v = bottom_bar_top.getChildAt(i);
            if(v.getId()!=R.id.btn_jump_spinner){
                v.setOnClickListener(this);
            }
        }
        LinearLayout bottom_bar_bottom = (LinearLayout) findViewById(R.id.bottom_bar_bottom);
        for(int i=0;i<bottom_bar_bottom.getChildCount();i++){
            View v = bottom_bar_bottom.getChildAt(i);
            v.setOnClickListener(this);
        }
        bottom_bar_bottom.setVisibility(View.GONE);

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
        /**
         * 缓存数量
         */
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

        mRecyclerView.setAdapter(mRecyleAdapter);
        Bundle b = getIntent().getExtras();
        String url = b.getString("url");
        AuthorName = b.getString("author");
        Tid = GetId.getTid(url);
        if (url != null && url.contains("redirect")) {
            RedirectPid = GetId.getPid(url);
            if (!App.IS_SCHOOL_NET) {
                url = url + "&mobile=2";
            }
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

    private float x,y = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                View bottomBar_bottom = findViewById(R.id.bottom_bar_c);
                //当手指按下的时候
                x = event.getX();
                y = event.getY();
                if(y<bottomBar_bottom.getTop()){
                    View v = findViewById(R.id.bottom_bar_bottom);
                    if(v.getVisibility()==View.VISIBLE){
                        v.setVisibility(View.GONE);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //当手指离开的时候
                float dx = event.getX()-x;
                float dy = event.getY()-y;
                if(dx>100&&dx>dy) {
                    DisplayMetrics dm =getResources().getDisplayMetrics();
                    int w_screen = dm.widthPixels;
                    int h_screen = dm.heightPixels;
                    //Log.i("BASEACTIVITY", "屏幕尺寸：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi);
                    if((dx>w_screen/4)&&(x<w_screen/2)){
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
    public void recyclerViewListClicked(View v, final int position) {
        switch (v.getId()) {
            case R.id.btn_reply_2:
                if (isneed_login()) {
                    SingleArticleData single = mydatalist.get(position);
                    String replyUrl = single.getReplyUrlTitle();
                    String replyIndex = single.getIndex();
                    String replyName = single.getUsername();
                    String ref = single.getCotent();
                    String replyUserInfo = "回复:" + replyIndex + " " + replyName;
                    //String url,int type,long lastreplyTime,boolean isEnableTail,String userName,String info
                    FrageReplyDialog dialog = FrageReplyDialog.newInstance(replyUrl,FrageReplyDialog.REPLY_CZ,replyTime,
                            true,replyUserInfo,ref);
                    dialog.setCallBack(SingleArticleActivity.this);
                    dialog.show(getFragmentManager(),"reply");
                }
                break;
            case R.id.need_loading_item:
                refresh();
                break;
            case R.id.tv_edit:
                edit_pos = position;
                Intent i = new Intent(this,EditActivity.class);
                i.putExtra("PID",mydatalist.get(position).getPid());
                i.putExtra("TID",Tid);
                startActivityForResult(i,0);
                break;
            case R.id.tv_remove:
                edit_pos = position;
                if(mydatalist.get(edit_pos).getType()==SingleType.CONTENT){
                    new MyAlertDialog(this,MyAlertDialog.WARNING_TYPE)
                            .setTitleText("删除帖子!")
                            .setConfirmText("删除")
                            .setCancelText("取消")
                            .setContentText("只能够删除没有回复的帖子，你要删除本贴吗？")
                            .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                                @Override
                                public void onClick(MyAlertDialog myAlertDialog) {
                                    removeItem(position);
                                }
                            }).show();
                }else{
                    new MyAlertDialog(this,MyAlertDialog.WARNING_TYPE)
                            .setTitleText("删除回复!")
                            .setContentText("你要删除此条回复吗？")
                            .setConfirmText("删除")
                            .setCancelText("取消")
                            .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                                @Override
                                public void onClick(MyAlertDialog myAlertDialog) {
                                    removeItem(position);
                                }
                            }).show();
                }

                break;
        }
    }

    //编辑Activity返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Log.e("onActivityResult","=====");
            Bundle b = data.getExtras();
            String title = b.getString("TITLE","");
            String content = b.getString("CONTENT","");
            if(edit_pos==0&&!TextUtils.isEmpty(title)){
                mydatalist.get(0).setTitle(title);
            }
            mydatalist.get(edit_pos).setCotent(content);
            mRecyleAdapter.notifyItemChanged(edit_pos);
        }
    }

    /**
     * 收藏帖子
     */
    private void starTask(final View v) {
        final String url = UrlUtils.getStarUrl(Tid);
        Map<String, String> params = new HashMap<>();
        params.put("favoritesubmit", "true");
        params.put("formhash", App.FORMHASH);
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
                    String hint = "回复："+ AuthorName;
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
                ImageView v = (ImageView)view;
                isRevere = !isRevere;
                if(isRevere){
                    v.setImageResource(R.drawable.ic_reverse_ordinary);
                }else  {
                    v.setImageResource(R.drawable.ic_normal_ordinary);
                }
                refresh();
                break;
            case R.id.btn_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,Title+UrlUtils.getSingleArticleUrl(Tid,page_now, App.IS_SCHOOL_NET));
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到文章到:"));
                break;
            case R.id.btn_more:
                View bottomBar_bottom = findViewById(R.id.bottom_bar_bottom);
                bottomBar_bottom.setVisibility((bottomBar_bottom.getVisibility()==View.VISIBLE)?View.GONE:View.VISIBLE);
                break;
            case R.id.btn_back_top:
                mRecyclerView.scrollToPosition(0);
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
                int ih = htmlData.indexOf("keywords");
                int h_start = htmlData.indexOf('\"',ih+15);
                int h_end = htmlData.indexOf('\"',h_start+1);
                Title = htmlData.substring(h_start+1,h_end);
                isGetTitle = true;
            }
            //获取回复/hash
            if (doc.select("input[name=formhash]").first() != null) {
                replyUrl = doc.select("form#fastpostform").attr("action");
                String hash = doc.select("input[name=formhash]").attr("value");
                if (!hash.isEmpty()) {
                    App.FORMHASH = hash;
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
                }
            }
            Elements elements = doc.select(".postlist");
            Elements postlist = elements.select("div[id^=pid]");
            int size = postlist.size();
            for (int i = index; i < size; i++) {
                Element temp = postlist.get(i);
                SingleArticleData data;
                String pid = temp.attr("id").substring(3);
                String uid = GetId.getUid(temp.select("span[class=avatar]").select("img").attr("src"));
                Elements userInfo = temp.select("ul.authi");
                String commentindex = userInfo.select("li.grey").select("em").text();
                String username = userInfo.select("a[href^=home.php?mod=space&uid=]").text();
                String posttime = userInfo.select("li.grey.rela").text();
                String replyUrl = temp.select(".replybtn").select("input").attr("href");
                Elements contentels = temp.select(".message");
                //是否移除所有样式
                if(showPlainText){
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
                //删除修改日期
                contentels.select("i.pstatus").remove();
                String finalcontent = contentels.html().trim();
                data = new SingleArticleData(SingleType.COMMENT, Title, uid,
                        username, posttime, commentindex, replyUrl, finalcontent,pid);
                tepdata.add(data);
            }

            return tepdata;
        }

        @Override
        protected void onPostExecute(List<SingleArticleData> tepdata) {
            //这是楼主
            if(page_now==1&&tepdata.size()>0&&tepdata.get(0).getIndex().contains("收藏")){
                SingleArticleData data = tepdata.get(0);
                data.setType(SingleType.CONTENT);
                data.setIndex(data.getIndex().replace("收藏",""));
                AuthorName = data.getUsername();
                AuthorUid = data.getUid();
            }
            if (!isSaveToDataBase) {
                //插入数据库
                MyDB myDB = new MyDB(SingleArticleActivity.this, MyDB.MODE_WRITE);
                myDB.handSingleReadHistory(Tid, Title, AuthorName);
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
                if(mydatalist.size()>0&&(mydatalist.get(0).getType()!=SingleType.CONTENT)&&
                        (mydatalist.get(0).getType()!=SingleType.HEADER)){
                    mydatalist.add(0,new SingleArticleData(SingleType.HEADER,Title,null,null,null,null,null,null,null));
                    mRecyleAdapter.notifyItemInserted(0);
                }
                mRecyleAdapter.notifyItemChanged(start);
                mRecyleAdapter.notifyItemRangeInserted(start + 1, add);

                //精确定位到某一层
                if (!TextUtils.isEmpty(RedirectPid)) {
                    for (int i = 0; i < mydatalist.size(); i++) {
                        if(!TextUtils.isEmpty(mydatalist.get(i).getPid())
                                && mydatalist.get(i).getPid().equals(RedirectPid)){
                            mRecyclerView.scrollToPosition(i);
                            break;
                        }
                    }
                    RedirectPid = "";
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

            pageSpinnerDatas.clear();
            for(int i=1;i<=page_sum;i++){
                pageSpinnerDatas.add(i+"/"+page_sum+"页");
            }
            spinner.setSelection(page_now-1);
            spinnerAdapter.notifyDataSetChanged();
        }
    }


    //删除帖子或者回复
    private void removeItem(final int pos){
        String url = "forum.php?mod=post&action=edit&extra=&editsubmit=yes&mobile=2&geoloc=&handlekey=postform&inajax=1";
        Map<String, String> params = new HashMap<>();
        params.put("formhash", App.FORMHASH);
        //params.put("posttime", time);
        params.put("editsubmit", "yes");
        //params.put("fid",);
        params.put("tid",Tid);
        params.put("pid",mydatalist.get(pos).getPid());
        params.put("delete","1");
        HttpUtil.post(this,url,params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                Log.e("resoult",res);
                if(res.contains("主题删除成功")){
                    if(mydatalist.get(pos).getType()==SingleType.CONTENT){
                        showToast("主题删除成功");
                        finish();
                    }else{
                        showToast("回复删除成功");
                        mydatalist.remove(pos);
                        mRecyleAdapter.notifyItemRemoved(pos);
                    }
                }else{
                    int start = res.indexOf("<p>");
                    String ss = res.substring(start+3,start+20);
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

}
