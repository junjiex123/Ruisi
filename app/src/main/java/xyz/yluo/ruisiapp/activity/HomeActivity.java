package xyz.yluo.ruisiapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import xyz.yluo.ruisiapp.CheckMessageService;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.ChangeNetDialog;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.adapter.ViewPagerAdapter;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-17.
 * 这是首页 管理4个fragment
 * 1.板块列表{@link HomeActivity}
 * 2.新帖{@link FrageHotNew}
 * 3.消息{@link xyz.yluo.ruisiapp.fragment.FrageMessage}
 * 4.好友{@link xyz.yluo.ruisiapp.fragment.FrageFriends}
 */
public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ViewPager viewpager;
    private TabLayout tabLayout;
    private TextView usernameTitle;
    private CircleImageView userImageTitle;
    private msgReceiver myMsgReceiver;
    //新消息小红点
    private View message_badge_toolbar,message_badge_nav;

    private int clickId = 0;
    private CircleImageView userImage;
    private long mExitTime;
    private final String[] titles = {"板块","看帖","消息","好友"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.mytab);
        usernameTitle = (TextView) findViewById(R.id.userNameTitle);
        userImageTitle = (CircleImageView) findViewById(R.id.userImageTitle);
        message_badge_toolbar = findViewById(R.id.message_badge_toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }

        init();

        if(!PublicData.ISLOGIN){
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
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), titles);
        viewpager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewpager);

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                clickId = 0;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
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
                    case R.id.nav_my_topic:
                        if(isneed_login()){
                            Intent i = new Intent(getApplicationContext(),ActivityMyTopicStar.class);
                            i.putExtra("type","mytopic");
                            startActivity(i);
                        }
                        break;
                    case R.id.nav_my_star:
                        if(isneed_login()){
                            Intent i = new Intent(getApplicationContext(),ActivityMyTopicStar.class);
                            i.putExtra("type","mystar");
                            startActivity(i);
                        }
                        break;
                    case R.id.nav_history:
                        if(isneed_login()){
                            Intent i = new Intent(getApplicationContext(),ActivityMyTopicStar.class);
                            i.putExtra("type","myhistory");
                            startActivity(i);
                        }
                        break;

                    case R.id.nav_help:
                        Intent i = new Intent(getApplicationContext(),HelpActivity.class);
                        startActivity(i);
                        break;

                }
            }
        });


        findViewById(R.id.toolbar_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        final View header = navigationView.getHeaderView(0);
        userImage = (CircleImageView) header.findViewById(R.id.profile_image);
        message_badge_nav = header.findViewById(R.id.message_badge_nav);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (PublicData.ISLOGIN) {
                    String url = UrlUtils.getAvaterurlb(PublicData.USER_UID);
                    UserDetailActivity.openWithTransitionAnimation(HomeActivity.this, PublicData.USER_NAME, userImage,url);
                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            }
        });

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

        //spinner.setSelection(PublicData.IS_SCHOOL_NET?0:1);
        header.findViewById(R.id.change_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeNetDialog dialog = new ChangeNetDialog();
                dialog.setNetType(PublicData.IS_SCHOOL_NET);
                dialog.show(getFragmentManager(), "changeNet");
            }
        });

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
            String url = UrlUtils.getAvaterurlm(PublicData.USER_UID);
            Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(userImage);
            Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(userImageTitle);
        } else {
            userImage.setImageResource(R.drawable.image_placeholder);
            userImageTitle.setImageResource(R.drawable.image_placeholder);
            usernameTitle.setText("西电睿思");
            userName.setText("点击头像登陆");
            userGrade.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
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
}
