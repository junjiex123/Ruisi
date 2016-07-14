package xyz.yluo.ruisiapp.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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
import xyz.yluo.ruisiapp.fragment.FrageFriends;
import xyz.yluo.ruisiapp.fragment.FrageHelp;
import xyz.yluo.ruisiapp.fragment.FrageHome;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageMessage;
import xyz.yluo.ruisiapp.fragment.FrageTopicStarHistory;
import xyz.yluo.ruisiapp.utils.GetUserImage;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-17.
 * 这是首页 管理3个fragment
 * 1.板块列表{@link HomeActivity}
 * 2.新帖{@link FrageHotNew}
 * 3.新闻{@link xyz.yluo.ruisiapp.fragment.FrageNews}
 */
public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener,DrawerLayout.DrawerListener{

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView usernameTitle;
    private CircleImageView userImageTitle;
    private msgReceiver myMsgReceiver;
    //新消息小红点
    private View message_badge_toolbar,message_badge_nav;
    private int clickId = 0;
    private CircleImageView userImage;
    private long mExitTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        usernameTitle = (TextView) findViewById(R.id.userNameTitle);
        userImageTitle = (CircleImageView) findViewById(R.id.userImageTitle);
        message_badge_toolbar = findViewById(R.id.message_badge_toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }

        init();

        if(!getIntent().getBooleanExtra("isLogin",false)){
            drawer.openDrawer(GravityCompat.START);
        }

        navigationView.setNavigationItemSelectedListener(this);


        //注册检查消息广播
        myMsgReceiver = new msgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ruisi.checkmsg");
        registerReceiver(myMsgReceiver, intentFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateLoginView();
    }

    private void init(){
        drawer.addDrawerListener(this);
        findViewById(R.id.toolbar_view).setOnClickListener(this);

        final View header = navigationView.getHeaderView(0);
        userImage = (CircleImageView) header.findViewById(R.id.profile_image);
        message_badge_nav = header.findViewById(R.id.message_badge_nav);
        userImage.setOnClickListener(this);

        header.findViewById(R.id.change_net).setOnClickListener(this);

        ImageView  btn_show_message = (ImageView) header.findViewById(R.id.show_message);
        btn_show_message.setOnClickListener(this);

        message_badge_nav.setVisibility(View.INVISIBLE);
        message_badge_toolbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

    private void updateLoginView(){
        final View header = navigationView.getHeaderView(0);
        TextView userName = (TextView) header.findViewById(R.id.header_user_name);
        TextView userGrade = (TextView) header.findViewById(R.id.user_grade);

        //判断是否登陆
        if (PublicData.ISLOGIN) {
            userGrade.setVisibility(View.VISIBLE);
            usernameTitle.setText(PublicData.USER_NAME);
            if(PublicData.USER_GRADE.length()>0){
                userGrade.setText(PublicData.USER_GRADE);
            }
            userName.setText(PublicData.USER_NAME);
            Uri uri = GetUserImage.getImageURI(getFilesDir(),PublicData.USER_UID);
            if(uri!=null){//图片存在
                userImage.setImageURI(uri);
                userImageTitle.setImageURI(uri);
            }else{//图片不存在
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
    }




    /**
     * 检查消息接收器
     */
    public class msgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            boolean isHaveMessage =  intent.getBooleanExtra("isHaveMessage",false);
            Log.i("home msg reciver","收到了新消息广播"+isHaveMessage);
            if(isHaveMessage){
                message_badge_nav.setVisibility(View.VISIBLE);
                message_badge_toolbar.setVisibility(View.VISIBLE);
            }else{
                message_badge_nav.setVisibility(View.INVISIBLE);
                message_badge_toolbar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent i = new Intent(this, CheckMessageService.class);
        stopService(i);

        if(myMsgReceiver!=null){
            unregisterReceiver(myMsgReceiver);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void changeFragement(int id){
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (id)
        {
            case FrageType.MESSAGE:
                FrageMessage frageMessage = new FrageMessage();
                // 使用当前Fragment的布局替代id_content的控件
                transaction.replace(R.id.fragment_home, frageMessage);
                break;
            case FrageType.FRIEND:
                FrageFriends frageFriends = new FrageFriends();
                transaction.replace(R.id.fragment_home, frageFriends);
                break;
            case FrageType.TOPIC:
            case FrageType.START:
            case FrageType.HISTORY:
                FrageTopicStarHistory frageTopicStarHistory = new FrageTopicStarHistory();
                Bundle bundle = new Bundle();
                bundle.putInt("type",id);
                frageTopicStarHistory.setArguments(bundle);
                transaction.replace(R.id.fragment_home, frageTopicStarHistory);
                break;

            case FrageType.HELP:
                FrageHelp frageHelp = new FrageHelp();
                transaction.replace(R.id.fragment_home, frageHelp);
                break;

            case FrageType.HOME:
                FrageHome frageHome = new FrageHome();
                transaction.replace(R.id.fragment_home, frageHome);
                break;
        }
        // 事务提交
        transaction.commit();
    }




    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.toolbar_view:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.profile_image:
                if (PublicData.ISLOGIN) {
                    String url = UrlUtils.getAvaterurlb(PublicData.USER_UID);
                    UserDetailActivity.openWithTransitionAnimation(HomeActivity.this, PublicData.USER_NAME, userImage,url);
                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                break;
            case R.id.change_net:
                ChangeNetDialog dialog = new ChangeNetDialog();
                dialog.setNetType(PublicData.IS_SCHOOL_NET);
                dialog.show(getFragmentManager(), "changeNet");
                break;
            case R.id.show_message:
                clickId = R.id.show_message;
                drawer.closeDrawer(GravityCompat.START);
                break;

        }

    }

    /**
     * 抽屉监听函数
     * @param drawerView
     * @param slideOffset
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
        switch (clickId){
            case R.id.nav_about:
                startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                break;
            case R.id.nav_sign:
                if(PublicData.IS_SCHOOL_NET){
                    if(isneed_login()){
                        startActivity(new Intent(getApplicationContext(),UserDakaActivity.class));
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"你现在不是校园网无法签到",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_post:
                if(isneed_login()){
                    startActivity(new Intent(getApplicationContext(),NewArticleActivity_2.class));
                }
                break;
            case R.id.show_message:
                changeFragement(FrageType.MESSAGE);
                break;
            case R.id.nav_my_topic:
                if(isneed_login()){
                    changeFragement(FrageType.TOPIC);
                }
                break;
            case R.id.nav_my_star:
                if(isneed_login()){
                    changeFragement(FrageType.START);
                }
                break;
            case R.id.nav_history:
                if(isneed_login()){
                    changeFragement(FrageType.HISTORY);
                }
                break;
            case R.id.nav_help:
                changeFragement(FrageType.HELP);
                break;
            case R.id.nav_home:
                changeFragement(FrageType.HOME);
                break;

        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
