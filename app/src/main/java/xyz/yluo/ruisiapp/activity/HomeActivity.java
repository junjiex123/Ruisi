package xyz.yluo.ruisiapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import xyz.yluo.ruisiapp.View.MyBottomTab;
import xyz.yluo.ruisiapp.data.FrageType;
import xyz.yluo.ruisiapp.fragment.FragSetting;
import xyz.yluo.ruisiapp.fragment.FrageForumList;
import xyz.yluo.ruisiapp.fragment.FrageFriends;
import xyz.yluo.ruisiapp.fragment.FrageHelp;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageMessage;
import xyz.yluo.ruisiapp.fragment.FrageTopicStarHistory;
import xyz.yluo.ruisiapp.fragment.FragmentMy;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;

/**
 * Created by free2 on 16-3-17.
 * 这是首页 管理3个fragment
 * 1.板块列表{@link HomeActivity}
 * 2.新帖{@link FrageHotNew}
 */
public class HomeActivity extends BaseActivity
        implements  View.OnClickListener,MyBottomTab.OnTabChangeListener{

    private long mExitTime;
    private Fragment currentFragment;
    private Timer timer = null;
    private MyTimerTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        currentFragment = new FrageForumList();
        MyBottomTab bottomTab = (MyBottomTab) findViewById(R.id.bottom_bar);
        bottomTab.setOnTabChangeListener(this);
        getFragmentManager().beginTransaction().replace(
                R.id.fragment_container,currentFragment).commit();

    }

    @Override
    public void tabselectChange(View v, int position) {
        changeFragement(position);
    }

    //检查消息程序
    @Override
    protected void onStart() {
        super.onStart();
        if(!TextUtils.isEmpty(App.getUid(this))){
            if(timer==null){
                Log.e("message","开始timer");
                timer = new Timer(true);
            }
            if (task != null){
                task.cancel();  //将原任务从队列中移除
            }
            task = new MyTimerTask();
            timer.schedule(task, 200, 60000); //延时200ms后执行，60000ms执行一
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
    public void onBackPressed(){
        if(getFragmentManager().getBackStackEntryCount()>0){
            getFragmentManager().popBackStack();
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
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.e("HOME","onAttachFragment"+fragment.getTag());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState(outState);
        //不然保存状态 放置白屏
    }

    public void changeFragement(int id) {
        /**
         * 所以常用的fragment用show 和 hide 比较好
         * replace 自己和自己是不会执行任何的函数的
         */
        String TAG = "TAG_"+id;
        FragmentManager fm = getFragmentManager();
        Fragment to = fm.findFragmentByTag(TAG);
        FragmentTransaction transaction = fm.beginTransaction();
        if(to==null){
            switch (id){
                case FrageType.FOURMLIST:
                    to = new FrageForumList();
                    break;
                case FrageType.NEWHOT:
                    to = new FrageHotNew();
                    break;
                case FrageType.MESSAGE:
                    to = new FrageMessage();
                    break;
                case FrageType.MY:
                    to = new FragmentMy();
                    break;
                case FrageType.FRIEND:
                    to = new FrageFriends();
                    break;
                case FrageType.TOPIC:
                    to = FrageTopicStarHistory.newInstance(FrageType.TOPIC);
                    break;
                case FrageType.START:
                    to = FrageTopicStarHistory.newInstance(FrageType.START);
                    break;
                case FrageType.HISTORY:
                    to = FrageTopicStarHistory.newInstance(FrageType.HISTORY);
                    break;
                case FrageType.HELP:
                    to = new FrageHelp();
                    break;
                case FrageType.SETTING:
                    to = new FragSetting();
                    break;
            }
        }
        if (currentFragment == to) {
            return;
        }
         //.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out);
        if (!to.isAdded()) {
            transaction.add(R.id.fragment_container, to,TAG);
            if(id>=4){
                transaction.addToBackStack(null);
            }
        }
        if(to.isHidden()){
            transaction.show(to);
        }
        if(currentFragment.isVisible()){
            transaction.hide(currentFragment);
        }
        currentFragment = to;
        transaction.commit();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                if (!TextUtils.isEmpty(App.getUid(this))) {
                    UserDetailActivity.openWithAnimation(HomeActivity.this,App.getName(this), (CircleImageView)view,App.getUid(this));
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
        }

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
                    int last_message_id  = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this)
                            .getInt(App.NOTICE_MESSAGE_KEY, 0);
                    for (Element e : elemens) {
                        int  noticeId = Integer.parseInt(e.attr("notice"));
                        if(last_message_id<noticeId){
                            boolean isnotify = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this)
                                    .getBoolean("setting_show_notify", false);
                            if (isnotify) {
                                messageHandler.sendEmptyMessage(2);
                            }else{
                                messageHandler.sendEmptyMessage(1);
                            }
                            break;
                        }else{
                            messageHandler.sendEmptyMessage(0);
                            break;
                        }
                    }
                }
            });
        }
    }


    //// TODO: 16-8-23  
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //0 - 无消息 1-有 2有 且通知
                case 0:
                    Log.e("message","无未读消息");
                    //if(message_bage.getVisibility()==View.VISIBLE){
                    //    message_bage.setVisibility(View.INVISIBLE);
                    //}
                    break;
                case 2:
                    final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotifyMgr.notify(10, builder.build());
                    Log.e("message","发送未读消息弹窗");
                case 1:
                    Log.e("message","有未读消息");
                    //message_bage.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

}
