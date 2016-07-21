package xyz.yluo.ruisiapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import xyz.yluo.ruisiapp.CheckMessageService;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.ChangeNetDialog;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.data.FrageType;
import xyz.yluo.ruisiapp.fragment.FragSetting;
import xyz.yluo.ruisiapp.fragment.FrageFriends;
import xyz.yluo.ruisiapp.fragment.FrageHelp;
import xyz.yluo.ruisiapp.fragment.FrageHome;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageMessage;
import xyz.yluo.ruisiapp.fragment.FrageTopicStarHistory;
import xyz.yluo.ruisiapp.utils.ImageUtils;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-17.
 * 这是首页 管理3个fragment
 * 1.板块列表{@link HomeActivity}
 * 2.新帖{@link FrageHotNew}
 * 3.新闻{@link xyz.yluo.ruisiapp.fragment.FrageNews}
 * 第一次创建 oncreate->onstart->onresume->running
 * 从别的 activity 退回->restart->onstart->resume->running
 * 别的activity挡住了但是还能看到  onresume->running
 */
public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DrawerLayout.DrawerListener {

    private final String TAG = "HomeActivity";
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView usernameTitle;
    private CircleImageView userImageTitle;
    private msgReceiver myMsgReceiver;
    private View toolbarImageContainer;
    //新消息小红点
    private View message_badge_toolbar, message_badge_nav;
    private int clickId = 0;
    private CircleImageView userImage;
    private long mExitTime;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate");
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbarImageContainer = findViewById(R.id.toolbarImageContainer);

        usernameTitle = (TextView) findViewById(R.id.userNameTitle);
        userImageTitle = (CircleImageView) findViewById(R.id.userImageTitle);
        message_badge_toolbar = findViewById(R.id.message_badge_toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        drawer.addDrawerListener(this);
        findViewById(R.id.toolbar_view).setOnClickListener(this);
        final View header = navigationView.getHeaderView(0);
        userImage = (CircleImageView) header.findViewById(R.id.profile_image);
        message_badge_nav = header.findViewById(R.id.message_badge_nav);
        userImage.setOnClickListener(this);
        header.findViewById(R.id.change_net).setOnClickListener(this);

        ImageView btn_show_message = (ImageView) header.findViewById(R.id.show_message);
        btn_show_message.setOnClickListener(this);
        message_badge_nav.setVisibility(View.INVISIBLE);
        message_badge_toolbar.setVisibility(View.INVISIBLE);

        if (!getIntent().getBooleanExtra("isLogin", false)) {
            drawer.openDrawer(GravityCompat.START);
        }
        navigationView.setNavigationItemSelectedListener(this);

        currentFragment = new FrageHome();
        String tag = PublicData.ISLOGIN?PublicData.USER_NAME:getString(R.string.app_name);
        getFragmentManager().beginTransaction().replace(R.id.fragment_home_container,currentFragment,tag).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
        final View header = navigationView.getHeaderView(0);
        TextView userName = (TextView) header.findViewById(R.id.header_user_name);
        TextView userGrade = (TextView) header.findViewById(R.id.user_grade);
        //判断是否登陆
        if (PublicData.ISLOGIN&& !TextUtils.isEmpty(PublicData.USER_NAME)) {
            userGrade.setVisibility(View.VISIBLE);
            usernameTitle.setText(PublicData.USER_NAME);
            if (PublicData.USER_GRADE.length() > 0) {
                userGrade.setText(PublicData.USER_GRADE);
            }
            userName.setText(PublicData.USER_NAME);
            Uri uri = ImageUtils.getImageURI(getFilesDir(), PublicData.USER_UID);
            if (uri != null) {//图片存在
                userImage.setImageURI(uri);
                userImageTitle.setImageURI(uri);
            } else {//图片不存在
                String url = UrlUtils.getAvaterurlm(PublicData.USER_UID);
                Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(userImage);
                Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(userImageTitle);
            }
        } else {
            userImage.setImageResource(R.drawable.image_placeholder);
            userImageTitle.setImageResource(R.drawable.image_placeholder);
            usernameTitle.setText("西电睿思");
            userName.setText("点击头像登陆");
            userGrade.setVisibility(View.GONE);
        }


        //注册检查消息广播
        Log.e("消息广播","注册广播");
        myMsgReceiver = new msgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ruisi.checkmsg");
        registerReceiver(myMsgReceiver, intentFilter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("消息广播","onRestart重新启动线程");
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isrecieveMessage = shp.getBoolean("setting_show_notify", false);
        //启动后台服务
        Intent i = new Intent(this, CheckMessageService.class);
        i.putExtra("isRunning", true);
        i.putExtra("isNotisfy", isrecieveMessage);
        startService(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent i = new Intent(this, CheckMessageService.class);
        if (myMsgReceiver != null) {
            unregisterReceiver(myMsgReceiver);
        }
        stopService(i);
        Log.e("消息广播","onStop取消注册广播 停止线程");
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(!(currentFragment instanceof FrageHome)){
            changeFragement(FrageType.HOME);
        }else {
            if ((System.currentTimeMillis() - mExitTime) > 1500) {
                Toast.makeText(this, "再按一次退出手机睿思", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        clickId = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        //不然保存状态 放置白屏
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void changeFragement(int id) {
        /**
         * replace 一定会执行 子fragment 的 oncreate  oncreateView
         * 还会执行上一个fragment 的destroy
         * 所以常用的fragment用show 和 hide 比较好
         * replace 自己和自己是不会执行任何的函数的
         */
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        toolbarImageContainer.setVisibility(View.GONE);

        String Tag = FrageType.TITLE_LIST[id];
        Fragment f = fm.findFragmentByTag(Tag);

        if(id==FrageType.HOME){
            Tag = PublicData.ISLOGIN&&(!TextUtils.isEmpty(PublicData.USER_NAME))?PublicData.USER_NAME:getString(R.string.app_name);
            toolbarImageContainer.setVisibility(View.VISIBLE);
        }

        if(f==null){
            switch (id){
                case FrageType.MESSAGE:
                    f = new FrageMessage();
                    break;
                case FrageType.FRIEND:
                    f = new FrageFriends();
                    break;
                case FrageType.TOPIC:
                    f = FrageTopicStarHistory.newInstance(FrageType.TOPIC);
                    break;
                case FrageType.START:
                    f = FrageTopicStarHistory.newInstance(FrageType.START);
                    break;
                case FrageType.HISTORY:
                    f = FrageTopicStarHistory.newInstance(FrageType.HISTORY);
                    break;
                case FrageType.HELP:
                    f = new FrageHelp();
                    break;
                case FrageType.HOME:
                    f = new FrageHome();
                    break;
                case FrageType.SETTING:
                    f = new FragSetting();
                    break;
            }
        }
        switchContent(f,Tag);
    }

    private void switchContent(Fragment to, String Tag) {
        FragmentManager fm = getFragmentManager();
        if (currentFragment != to) {
            usernameTitle.setText(Tag);
            FragmentTransaction transaction = fm.beginTransaction();
//            .setCustomAnimations(
//                    android.R.anim.fade_in, R.anim.slide_out);
            if (!to.isAdded()) {    // 先判断是否被add过
                Log.e("===","to is not added");
                transaction.hide(currentFragment).add(R.id.fragment_home_container, to,Tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                Log.e("===","to is  added");
                transaction.hide(currentFragment).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }

            currentFragment = to;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_view:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.profile_image:
                if (PublicData.ISLOGIN&&!TextUtils.isEmpty(PublicData.USER_NAME)) {
                    String url = UrlUtils.getAvaterurlb(PublicData.USER_UID);
                    UserDetailActivity.openWithTransitionAnimation(HomeActivity.this, PublicData.USER_NAME, userImage, url);
                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(i,0);
                }
                break;
            case R.id.change_net:
                ChangeNetDialog dialog = new ChangeNetDialog();
                dialog.setNetType(PublicData.IS_SCHOOL_NET);
                dialog.show(getFragmentManager(), "changeNet");
                break;
            case R.id.show_message:
                if(isneed_login()){
                    clickId = R.id.show_message;
                    drawer.closeDrawer(GravityCompat.START);
                }

                break;
        }

    }

    /**
     * 登陆页面返回值
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Bundle b=data.getExtras(); //data为B中回传的Intent
            String str=b.getString("status");//str即为回传的值
            Log.i("login status",str);
        }
    }

    /**
     * 抽屉监听函数
     */
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        clickId = 0;
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        switch (clickId) {
            case R.id.nav_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
            case R.id.nav_setting:
                changeFragement(FrageType.SETTING);
                break;
            case R.id.nav_sign:
                if (PublicData.IS_SCHOOL_NET) {
                    if (isneed_login()) {
                        startActivity(new Intent(getApplicationContext(), UserDakaActivity.class));
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "你现在不是校园网无法签到", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.show_message:
                changeFragement(FrageType.MESSAGE);
                break;
            case R.id.nav_my_topic:
                if (isneed_login()) {
                    changeFragement(FrageType.TOPIC);
                }
                break;
            case R.id.nav_my_star:
                if (isneed_login()) {
                    changeFragement(FrageType.START);
                }
                break;
            case R.id.nav_history:
                if (isneed_login()) {
                    changeFragement(FrageType.HISTORY);
                }
                break;
            case R.id.nav_help:
                changeFragement(FrageType.HELP);
                break;
            case R.id.nav_home:
                changeFragement(FrageType.HOME);
                break;
            case R.id.nav_my_friend:
                changeFragement(FrageType.FRIEND);
                break;

        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    /**
     * 检查消息接收器
     */
    public class msgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            boolean isHaveMessage = intent.getBooleanExtra("isHaveMessage", false);
            Log.i("home msg reciver", "收到了新消息广播" + isHaveMessage);
            if (isHaveMessage) {
                message_badge_nav.setVisibility(View.VISIBLE);
                message_badge_toolbar.setVisibility(View.VISIBLE);
            } else {
                message_badge_nav.setVisibility(View.INVISIBLE);
                message_badge_toolbar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
