package xyz.yluo.ruisiapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.MyPublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.fragment.FragementSimpleList;
import xyz.yluo.ruisiapp.fragment.FragementUser;
import xyz.yluo.ruisiapp.fragment.HomeFormList;
import xyz.yluo.ruisiapp.fragment.NeedLoginDialogFragment;
import xyz.yluo.ruisiapp.utils.CircleImageView;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-17.
 *
 */
public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    protected DrawerLayout drawer;
    @Bind(R.id.nav_view)
    protected NavigationView navigationView;

    private ActionBar actionBar;
    private ActionBarDrawerToggle toggle;
    private int clickId = 0;
    private CircleImageView userImge;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        init();
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        btn_1_click();
        checkIsLoginView();
    }

    private void init(){
        toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                clickId = 0;
                checkIsLoginView();
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
                    case R.id.nav_bt_list:
                        if(MyPublicData.IS_SCHOOL_NET){
                            if(islogin_dialog()){
                                startActivity(new Intent(getApplicationContext(),ArticleListBtActivity.class));
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"你现在不是校园网,无法查看",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_sign:
                        if(MyPublicData.IS_SCHOOL_NET){
                            if(islogin_dialog()){
                                startActivity(new Intent(getApplicationContext(),UserDakaActivity.class));
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"你现在不是校园网无法签到",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_post:
                        if(islogin_dialog()){
                            startActivity(new Intent(getApplicationContext(),NewArticleActivity_2.class));
                        }
                        break;
                }
            }
        };

        final View header = navigationView.getHeaderView(0);
        userImge = (CircleImageView) header.findViewById(R.id.profile_image);
        userImge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (MyPublicData.ISLOGIN) {
                    String url = UrlUtils.getimageurl(MyPublicData.USER_UID,true);
                    UserDetailActivity.openWithTransitionAnimation(HomeActivity.this, MyPublicData.USER_NAME,userImge,url);
                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(i, 1);
                }
            }
        });

        checkIsLoginView();
    }


    @OnClick(R.id.btn_1)
        protected void btn_1_click(){
        if(actionBar!=null){
            actionBar.setTitle("板块");
        }
        Fragment fragment1 = new HomeFormList();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment1);
        transaction.commit();
    }
    @OnClick(R.id.btn_2)
    protected void btn_2_click(){
        if(actionBar!=null){
            actionBar.setTitle("新帖");
        }
        Fragment fragment2 = new FragementSimpleList();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment2);
        transaction.commit();

    }

    @OnClick(R.id.btn_3)
    protected void btn_3_click(){
        if(islogin_dialog()){
            if(actionBar!=null){
                actionBar.setTitle(MyPublicData.USER_NAME);
            }
            Fragment fragment3 = new FragementUser();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragment3);
            transaction.commit();
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 1500) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_topic) {

            if(islogin_dialog()){
                startActivity(new Intent(getApplicationContext(),NewArticleActivity_2.class));
            }
            return true;
        }else if(id==R.id.menu_setting){
            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        clickId = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkIsLoginView(){
        final View header = navigationView.getHeaderView(0);
        final View nav_header_login = header.findViewById(R.id.nav_header_login);
        final View nav_header_notlogin = header.findViewById(R.id.nav_header_notlogin);
        //判断是否登陆
        if (MyPublicData.ISLOGIN) {
            TextView text1 = (TextView) header.findViewById(R.id.header_user_name);
            text1.setText(MyPublicData.USER_NAME);
            nav_header_login.setVisibility(View.VISIBLE);
            nav_header_notlogin.setVisibility(View.GONE);
            String url = UrlUtils.getimageurl(MyPublicData.USER_UID,true);
            Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).resize(80,80).into(userImge);
        } else {
            userImge.setImageResource(R.drawable.image_placeholder);
            nav_header_notlogin.setVisibility(View.VISIBLE);
            nav_header_login.setVisibility(View.GONE);
        }
    }

    //判断是否需要弹出登录dialog
    private boolean islogin_dialog(){

        if(MyPublicData.ISLOGIN){
            return true;
        }else{
            NeedLoginDialogFragment dialogFragment = new NeedLoginDialogFragment();
            dialogFragment.show(getFragmentManager(), "needlogin");
        }
        return false;
    }

    //登陆页面返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkIsLoginView();
    }
}
