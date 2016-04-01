package xyz.yluo.ruisiapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.UserInfoStarAdapter;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.ConfigClass;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.GetLevel;

public class UserDetailActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.user_detail_img_avatar)
    protected CircleImageView imageView;
    @Bind(R.id.username)
    protected TextView usernameView;
    @Bind(R.id.usergrade)
    protected TextView usergrade;
    @Bind(R.id.main_window)
    protected CoordinatorLayout layout;
    private List<Pair<String,String>> datasUserInfo = new ArrayList<>();
    private UserInfoStarAdapter myadapterUserInfo;
    private static final String NAME_IMG_AVATAR = "imgAvatar";
    private String userUid = "";
    private String username = "";

    public static void openWithTransitionAnimation(Activity activity, String loginName, ImageView imgAvatar, String avatarUrl) {
        Intent intent = new Intent(activity, UserDetailActivity.class);
        intent.putExtra("loginName", loginName);
        intent.putExtra("avatarUrl", avatarUrl);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imgAvatar, NAME_IMG_AVATAR);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void open(Context context, String loginName) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("loginName", loginName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        ViewCompat.setTransitionName(imageView, NAME_IMG_AVATAR);

        username = getIntent().getStringExtra("loginName");
        usernameView.setText(username);
        String imageUrl = getIntent().getStringExtra("avatarUrl");

        Picasso.with(getApplicationContext()).load(imageUrl).placeholder(R.drawable.image_placeholder).into(imageView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        myadapterUserInfo = new UserInfoStarAdapter(this,datasUserInfo,0);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(myadapterUserInfo);

        userUid = GetId.getUid(imageUrl);
        String url0= "home.php?mod=space&uid="+userUid+"&do=profile&mobile=2";

        getdata(url0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //返回按钮
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getdata(String url){
        AsyncHttpCilentUtil.get(getApplicationContext(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                new GetUserInfoTask(new String(responseBody)).execute();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick(R.id.fab)
    protected void fab_click(){

        if(ConfigClass.CONFIG_ISLOGIN){
            //TODO
            //home.php?mod=space&do=pm&subop=view&touid=269448&mobile=2
            //Context context, String username,String url
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
        //用户积分
        String userjifen = "0";

        public GetUserInfoTask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            if(res!=""){
                Elements lists = Jsoup.parse(res).select(".user_box").select("ul").select("li");
                if(lists!=null){
                    Pair<String,String> temp;
                    for(Element tmp:lists){
                        String value = tmp.select("span").text();
                        tmp.select("span").remove();
                        String key = tmp.text();
                        if(key.contains("积分")){
                            userjifen = value;
                        }
                        temp = new Pair<>(key,value);
                        datasUserInfo.add(temp);
                    }
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            usergrade.setText(GetLevel.getUserLevel(Integer.parseInt(userjifen)));
            myadapterUserInfo.notifyItemRangeInserted(0, datasUserInfo.size());
        }
    }
}
