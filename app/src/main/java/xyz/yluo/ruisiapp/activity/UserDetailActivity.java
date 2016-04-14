package xyz.yluo.ruisiapp.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.AddFriendDialog;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.View.ExitLoginDialogFragment;
import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.SimpleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.GetLevel;
import xyz.yluo.ruisiapp.utils.UrlUtils;
/**
 * 用户信息activity
 *
 */
public class UserDetailActivity extends BaseActivity implements AddFriendDialog.AddFriendListener{

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.user_detail_img_avatar)
    protected CircleImageView imageView;
    @Bind(R.id.main_window)
    protected CoordinatorLayout layout;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.fab)
    protected FloatingActionButton fab;
    @Bind(R.id.progressBar)
    protected ProgressBar progressBar;
    private ActionBar actionBar;

    private List<SimpleListData> datas = new ArrayList<>();
    private SimpleListAdapter adapter;
    private static final String NAME_IMG_AVATAR = "imgAvatar";
    private static String userUid = "";
    private String username = "";
    private String imageUrl = "";

    public static void openWithTransitionAnimation(Activity activity, String username, ImageView imgAvatar, String avatarUrl) {
        Intent intent = new Intent(activity, UserDetailActivity.class);
        intent.putExtra("loginName", username);
        intent.putExtra("avatarUrl", avatarUrl);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imgAvatar, NAME_IMG_AVATAR);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void open(Context context, String username,String avatarUrl) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("loginName", username);
        intent.putExtra("avatarUrl", avatarUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);

        ViewCompat.setTransitionName(imageView, NAME_IMG_AVATAR);
        username = getIntent().getStringExtra("loginName");
        imageUrl = getIntent().getStringExtra("avatarUrl");
        Picasso.with(this).load(imageUrl).placeholder(R.drawable.image_placeholder).into(imageView);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(username);
        }
        adapter = new SimpleListAdapter(ListType.INFO,this,datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);
        userUid = GetId.getUid(imageUrl);
        //如果是自己
        if (userUid.equals(PublicData.USER_UID)){
            fab.setImageResource(R.drawable.ic_exit_24dp);
        }
        String url0= UrlUtils.getUserHomeUrl(userUid,false);
        getdata(url0);
    }

    private void getdata(String url){
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetUserInfoTask(new String(response)).execute();
            }
            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @OnClick(R.id.fab)
    protected void fab_click(){
        //如果是自己
        if (userUid.equals(PublicData.USER_UID)){
            ExitLoginDialogFragment dialogFragment = new ExitLoginDialogFragment();
            dialogFragment.show(getFragmentManager(), "exit");
        }else if(PublicData.ISLOGIN){
            //url      home.php?mod=space&do=pm&subop=view&touid=261098&mobile=2
            //replyurl home.php?mod=spacecp&ac=pm&op=send&pmid=452408&daterange=0&pmsubmit=yes&mobile=2
            String url = "home.php?mod=space&do=pm&subop=view&touid="+userUid+"&mobile=2";
            ChatActivity.open(this,username,url);
        }else{
            Snackbar.make(layout, "你还没有登陆，无法发送消息", Snackbar.LENGTH_LONG)
                    .setAction("点我登陆", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                    }).show();
        }

    }

    //获得用户个人信息
    public class GetUserInfoTask extends AsyncTask<Void, Void, String> {
        private String res;
        public GetUserInfoTask(String res) {
            this.res = res;
        }
        @Override
        protected String doInBackground(Void... voids) {
            username = Jsoup.parse(res).select(".user_avatar").select(".name").text();
            Elements lists = Jsoup.parse(res).select(".user_box").select("ul").select("li");
            if(lists!=null){
                for(Element tmp:lists){
                    String value = tmp.select("span").text();
                    tmp.select("span").remove();
                    String key = tmp.text();
                    if(key.contains("积分")){
                        String grade = GetLevel.getUserLevel(Integer.parseInt(value));
                        datas.add(new SimpleListData("等级",grade,""));
                    }
                    datas.add(new SimpleListData(key,value,""));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(actionBar!=null){
                actionBar.setTitle(username);
            }
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }

    }

    //加好友确认按钮点击
    @Override
    public void OkClick(DialogFragment dialog, String mes) {
        //todo 处理加好友逻辑
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_userdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.menu_add){
            AddFriendDialog dialogFragment = new AddFriendDialog();
            dialogFragment.setUserName(username);
            dialogFragment.setUserImage(imageUrl);
            dialogFragment.show(getFragmentManager(),"add");
        }
        return super.onOptionsItemSelected(item);
    }
}
