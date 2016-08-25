package xyz.yluo.ruisiapp.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.AddFriendDialog;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.adapter.BaseAdapter;
import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.SimpleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.DataManager;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.GetLevel;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * 用户信息activity
 */
public class UserDetailActivity extends BaseActivity implements AddFriendDialog.AddFriendListener {

    private static final String NAME_IMG_AVATAR = "imgAvatar";
    private static String userUid = "";
    protected RecyclerView recycler_view;
    protected CoordinatorLayout layout;
    protected Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private List<SimpleListData> datas = new ArrayList<>();
    private SimpleListAdapter adapter = null;
    private String username = "";
    private String imageUrl = "";

    public static void openWithAnimation(Activity activity, String username, ImageView imgAvatar, String uid) {
        Intent intent = new Intent(activity, UserDetailActivity.class);
        intent.putExtra("loginName", username);
        intent.putExtra("avatarUrl", UrlUtils.getAvaterurlm(uid));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imgAvatar, NAME_IMG_AVATAR);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void open(Context context, String username, String avatarUrl) {
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

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        CircleImageView imageView = (CircleImageView) findViewById(R.id.user_detail_img_avatar);
        layout = (CoordinatorLayout) findViewById(R.id.main_window);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_click();
            }
        });

        ViewCompat.setTransitionName(imageView, NAME_IMG_AVATAR);
        username = getIntent().getStringExtra("loginName");
        imageUrl = getIntent().getStringExtra("avatarUrl");
        Picasso.with(this).load(imageUrl).placeholder(R.drawable.image_placeholder).into(imageView);

        toolbarLayout.setTitle(username);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        adapter = new SimpleListAdapter(ListType.INFO, this, datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);
        userUid = GetId.getid("uid=",imageUrl);
        //如果是自己
        if (userUid.equals(App.getUid(this))) {
            fab.setImageResource(R.drawable.ic_close_24dp);
        }
        String url0 = UrlUtils.getUserHomeUrl(userUid, false);
        getdata(url0);
    }

    private void getdata(String url) {
        adapter.changeLoadMoreState(BaseAdapter.STATE_LOADING);
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                new GetUserInfoTask().execute(new String(response));
            }

            @Override
            public void onFailure(Throwable e) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_FAIL);
            }
        });
    }

    private void fab_click() {
        //如果是自己 退出登录
        if (userUid.equals(App.getUid(this))) {
            new MyAlertDialog(this,MyAlertDialog.WARNING_TYPE)
                    .setTitleText("退出登录")
                    .setContentText("你确定要注销吗？")
                    .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(MyAlertDialog myAlertDialog) {
                            DataManager.cleanApplicationData(UserDetailActivity.this);
                            finish();
                        }
                    }).show();

        } else if (App.ISLOGIN(this)) {
            String url = "home.php?mod=space&do=pm&subop=view&touid=" + userUid + "&mobile=2";
            ChatActivity.open(this, username, url);
        } else {
            Snackbar.make(layout, "你还没有登陆，无法发送消息", Snackbar.LENGTH_LONG)
                    .setAction("点我登陆", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }).show();
        }

    }

    //加好友确认按钮点击
    @Override
    public void OkClick(final DialogFragment dialog, String mes) {
        final ProgressDialog dialog1 = new ProgressDialog(this);
        dialog1.setTitle("正在发送请求");
        dialog1.setMessage("请等待......");
        Map<String, String> paras = new HashMap<>();
        paras.put("addsubmit", "true");
        paras.put("handlekey", "friend_" + userUid);
        paras.put("note", mes);
        paras.put("gid", "1");
        paras.put("addsubmit_btn", "true");
        HttpUtil.post(this, UrlUtils.getAddFrirndUrl(userUid), paras, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if (res.contains("好友请求已")) {
                    Toast.makeText(getApplicationContext(), "请求已发送成功，正在请等待对方验证", Toast.LENGTH_SHORT).show();
                } else if (res.contains("正在等待验证")) {
                    Toast.makeText(getApplicationContext(), "好友请求已经发送了，正在等待对方验证", Toast.LENGTH_SHORT).show();
                } else if (res.contains("你们已成为好友")) {
                    Toast.makeText(getApplicationContext(), "你们已经是好友了不用添加了...", Toast.LENGTH_SHORT).show();
                }

                dialog1.dismiss();
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
                Toast.makeText(getApplicationContext(), "出错了，我也不知道哪儿错了...", Toast.LENGTH_SHORT).show();
                dialog1.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!userUid.equals(App.getUid(this))){
            getMenuInflater().inflate(R.menu.menu_userdetail, menu);
            return true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            if (!App.ISLOGIN(this)) {
                Snackbar.make(layout, "你还没有登陆，无法进行操作", Snackbar.LENGTH_LONG)
                        .setAction("点我登陆", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        }).show();
            } else {
                AddFriendDialog dialogFragment = AddFriendDialog.newInstance(this);
                dialogFragment.setUserName(username);
                dialogFragment.setUserImage(imageUrl);
                dialogFragment.show(getFragmentManager(), "add");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    //获得用户个人信息
    private class GetUserInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String res = params[0];
            username = Jsoup.parse(res).select(".user_avatar").select(".name").text();
            Elements lists = Jsoup.parse(res).select(".user_box").select("ul").select("li");
            if (lists != null) {
                for (Element tmp : lists) {
                    String value = tmp.select("span").text();
                    tmp.select("span").remove();
                    String key = tmp.text();
                    if (key.contains("积分")) {
                        String grade = GetLevel.getUserLevel(Integer.parseInt(value));
                        datas.add(new SimpleListData("等级", grade, ""));
                    } else if (key.contains("上传量") || key.contains("下载量")) {
                        long a = Long.parseLong(value.trim());
                        DecimalFormat decimalFormat = new DecimalFormat(".00");
                        float GBsize = (float) (a / 1024 / 1024 / 1024.0);
                        if (GBsize > 500) {
                            float TBsize = GBsize / 1024.0f;
                            value = decimalFormat.format(TBsize) + " TB";
                        } else {
                            value = decimalFormat.format(GBsize) + " GB";
                        }

                    }
                    datas.add(new SimpleListData(key, value, ""));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            toolbarLayout.setTitle(username);
            adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_NOTHING);
            adapter.notifyDataSetChanged();
        }

    }
}
