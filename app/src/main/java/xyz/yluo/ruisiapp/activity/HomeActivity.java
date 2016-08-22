package xyz.yluo.ruisiapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Timer;
import java.util.TimerTask;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.ChangeNetDialog;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.data.FrageType;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.fragment.FragSetting;
import xyz.yluo.ruisiapp.fragment.FrageFriends;
import xyz.yluo.ruisiapp.fragment.FrageHelp;
import xyz.yluo.ruisiapp.fragment.FrageHome;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageMessage;
import xyz.yluo.ruisiapp.fragment.FrageTopicStarHistory;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.ImageUtils;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-17.
 * 这是首页 管理3个fragment
 * 1.板块列表{@link HomeActivity}
 * 2.新帖{@link FrageHotNew}
 * 3.新闻{@link xyz.yluo.ruisiapp.fragment.FrageNews}
 */
public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DrawerLayout.DrawerListener {

    private final String TAG = "HomeActivity";
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private int clickId = 0;
    private long mExitTime;
    private Fragment currentFragment;
    private MyToolBar myToolBar;
    private CircleImageView userImage;
    //新消息小红点
    private View message_bage;
    private View toolBarImagContainer;
    private boolean isNeewRefreshDrawView = true;
    private boolean isrecieveMessage;
    private MyDB myDB =  null;
    private Timer timer = null;
    private MyTimerTask task = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"onCreate");
        setContentView(R.layout.activity_home);
        myToolBar = (MyToolBar) findViewById(R.id.myToolBar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        myToolBar.setIcon(R.drawable.ic_menu_24dp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toolBarImagContainer = LayoutInflater.from(this).inflate(R.layout.user_img_with_meessage,null,false);
        userImage = (CircleImageView) toolBarImagContainer.findViewById(R.id.toolbar_user_image);
        message_bage = toolBarImagContainer.findViewById(R.id.toolbar_message_bage);
        message_bage.setVisibility(View.INVISIBLE);
        myToolBar.addView(toolBarImagContainer);
        toolBarImagContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        myToolBar.addMenu(R.drawable.ic_search_white_24dp,"SEARCH");
        myToolBar.addMenu(R.drawable.ic_edit,"POST");
        setToolBarMenuClick(myToolBar);
        navigationView.setNavigationItemSelectedListener(this);
        currentFragment = new FrageHome();
        String tag = App.ISLOGIN? App.USER_NAME:getString(R.string.app_name);
        getFragmentManager().beginTransaction().replace(R.id.fragment_home_container,currentFragment,tag).commit();
        myDB =  new MyDB(this,MyDB.MODE_WRITE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
        if(App.ISLOGIN&& !TextUtils.isEmpty(App.USER_NAME)){
            myToolBar.setTitle(App.USER_NAME);
            Uri uri = ImageUtils.getImageURI(getFilesDir(), App.USER_UID);
            if (uri != null) {//图片存在
                userImage.setImageURI(uri);
            } else {//图片不存在
                String url = UrlUtils.getAvaterurlm(App.USER_UID);
                Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(userImage);
            }
            isrecieveMessage = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this)
                    .getBoolean("setting_show_notify", false);
            if(timer==null){
                Log.e("message","开始timer");
                timer = new Timer(true);
            }
            if (task != null){
                task.cancel();  //将原任务从队列中移除
            }
            task = new MyTimerTask();
            timer.schedule(task, 300, 60000); //延时1000ms后执行，60000ms执行一
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(timer!=null){
            timer.cancel();
            timer = null;
            Log.e("message","停止timer");
        }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        clickId = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        //不然保存状态 放置白屏
    }

    private void changeFragement(int id) {
        /**
         * 所以常用的fragment用show 和 hide 比较好
         * replace 自己和自己是不会执行任何的函数的
         */
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        toolBarImagContainer.setVisibility(View.GONE);
        String Tag = FrageType.TITLE_LIST[id];
        if(id==FrageType.HOME){
            Log.e("test","id  is home");
            Tag = App.ISLOGIN&&(!TextUtils.isEmpty(App.USER_NAME))? App.USER_NAME:getString(R.string.app_name);
            toolBarImagContainer.setVisibility(View.VISIBLE);
        }
        Fragment f = fm.findFragmentByTag(Tag);
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
            FragmentTransaction transaction = fm.beginTransaction();
//            .setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out);
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(currentFragment).add(R.id.fragment_home_container, to,Tag).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(currentFragment).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            currentFragment = to;
            myToolBar.setTitle(Tag);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                if (App.ISLOGIN&&!TextUtils.isEmpty(App.USER_NAME)) {
                    String url = UrlUtils.getAvaterurlb(App.USER_UID);
                    UserDetailActivity.openWithTransitionAnimation(HomeActivity.this, App.USER_NAME, (CircleImageView)view, url);
                } else {
                    Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivityForResult(i,0);
                }
                break;
            case R.id.change_net:
                ChangeNetDialog dialog = new ChangeNetDialog();
                dialog.setNetType(App.IS_SCHOOL_NET);
                dialog.show(getFragmentManager(), "changeNet");
                break;
            case R.id.show_message:
                if(isLogin()){
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
            initDrawView();
        }
    }

    private void initDrawView(){
        final View header = navigationView.getHeaderView(0);
        findViewById(R.id.message_badge_nav).setVisibility(message_bage.getVisibility());
        CircleImageView userImage = (CircleImageView) header.findViewById(R.id.profile_image);
        TextView userGrade = (TextView) header.findViewById(R.id.user_grade);
        TextView userName = (TextView) header.findViewById(R.id.header_user_name);
        header.findViewById(R.id.change_net).setOnClickListener(this);
        ImageView btn_show_message = (ImageView) header.findViewById(R.id.show_message);
        btn_show_message.setOnClickListener(this);
        userImage.setOnClickListener(this);
        if(App.ISLOGIN&& !TextUtils.isEmpty(App.USER_NAME)){
            userGrade.setVisibility(View.VISIBLE);
            userGrade.setText(App.USER_GRADE);
            userName.setText(App.USER_NAME);
            Uri uri = ImageUtils.getImageURI(getFilesDir(), App.USER_UID);
            if (uri != null) {//图片存在
                userImage.setImageURI(uri);
            } else {//图片不存在
                String url = UrlUtils.getAvaterurlm(App.USER_UID);
                Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(userImage);
            }
        }else{
            userImage.setImageResource(R.drawable.image_placeholder);
            userName.setText("点击头像登陆");
            userGrade.setVisibility(View.GONE);
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
        if(isNeewRefreshDrawView){
            initDrawView();
            isNeewRefreshDrawView = false;
        }

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        switch (clickId) {
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_setting:
                changeFragement(FrageType.SETTING);
                break;
            case R.id.nav_sign:
                if (App.IS_SCHOOL_NET) {
                    if (isLogin()) {
                        startActivity(new Intent(this, UserDakaActivity.class));
                    }
                } else {
                    Toast.makeText(this, "你现在不是校园网无法签到", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.show_message:
                changeFragement(FrageType.MESSAGE);
                break;
            case R.id.nav_my_topic:
                if (isLogin()) {
                    changeFragement(FrageType.TOPIC);
                }
                break;
            case R.id.nav_my_star:
                if (isLogin()) {
                    changeFragement(FrageType.START);
                }
                break;
            case R.id.nav_history:
                if (isLogin()) {
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


    private MyHandler messageHandler = new MyHandler();
    final NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.logo)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentTitle("未读消息提醒")
            .setContentText("你有未读的消息哦,去我的消息页面查看吧！")
            .setAutoCancel(true);


    private class MyTimerTask extends TimerTask{
        public void run() {
            String url = App.getBaseUrl() + "home.php?mod=space&do=notice&view=mypost&type=post";
            if (!App.IS_SCHOOL_NET) {
                url = url + "&mobile=2";
            }
            HttpUtil.SyncGet(HomeActivity.this, url, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    Document document = Jsoup.parse(new String(response));
                    Elements elemens = document.select(".nts").select("dl.cl");
                    for (Element e : elemens) {
                        String s = e.select(".ntc_body").attr("style");
                        if (s.contains("bold")) {
                            String url = e.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").attr("href");
                            String info = e.select(".ntc_body").text();
                            //只要有未读的就插入 到数据库在判断
                            myDB.insertMessage(url,info);
                        }
                    }
                    if(myDB.isHaveUnReadMessage()){
                        Log.e("message","有未读读消息");
                        if (isrecieveMessage) {
                            messageHandler.sendEmptyMessage(2);
                        }else{
                            messageHandler.sendEmptyMessage(1);
                        }

                    }else{
                        messageHandler.sendEmptyMessage(0);
                    }
                }
            });
        }
    };


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //0 - 无消息 1-有 2有 且通知
                case 0:
                    Log.e("message","无未读消息");
                    if(message_bage.getVisibility()==View.VISIBLE){
                        message_bage.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 2:
                    final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(10, builder.build());
                    Log.e("message","发送未读消息弹窗");
                case 1:
                    Log.e("message","有未读消息");
                    message_bage.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

}
