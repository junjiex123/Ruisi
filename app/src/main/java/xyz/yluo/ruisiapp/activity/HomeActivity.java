package xyz.yluo.ruisiapp.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyBottomTab;
import xyz.yluo.ruisiapp.data.FrageType;
import xyz.yluo.ruisiapp.fragment.FrageForumList;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageMessage;
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
        implements MyBottomTab.OnTabChangeListener{

    private long mExitTime;
    private Fragment currentFragment;
    private Timer timer = null;
    private MyTimerTask task = null;
    private MyBottomTab bottomTab;
    private long lastCheckMsgTime = 0;
    private int interval = 45000;//60s
    private MyHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        currentFragment = new FrageForumList();
        bottomTab = (MyBottomTab) findViewById(R.id.bottom_bar);
        bottomTab.setOnTabChangeListener(this);
        getFragmentManager().beginTransaction().replace(
                R.id.fragment_container,currentFragment).commit();

        Calendar c = Calendar.getInstance();
        int HOUR_OF_DAY = c.get(Calendar.HOUR_OF_DAY);
        if(HOUR_OF_DAY<10&&HOUR_OF_DAY>1){
            //晚上一点到早上10点间隔,不同时间段检查消息间隔不同
            //减轻服务器压力
            interval = interval*2;
        }

        messageHandler = new MyHandler(bottomTab,this);
    }

    @Override
    public void tabselectChange(View v, int position) {
        changeFragement(position);
    }

    //检查消息程序
    @Override
    protected void onStart() {
        super.onStart();
        if(App.ISLOGIN(this)){
            //60s进行一次
            long need = interval-(System.currentTimeMillis()-lastCheckMsgTime);
            if(need<100){
                need = 100;
            }
            if(timer==null){
                Log.e("message","开始timer delay"+need);
                timer = new Timer(true);
            }
            task = new MyTimerTask();
            timer.schedule(task, need, interval); //延时150ms后执行，60s间隔
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
        if(to!=null&&currentFragment == to){
            return;
        }
        if(to==null){
            switch (id){
                case FrageType.NEWHOT:
                    to = new FrageHotNew();
                    break;
                case FrageType.MESSAGE:
                    bottomTab.setMessage(false);
                    to = FrageMessage.newInstance(ishaveReply,ishavePm);
                    break;
                case FrageType.MY:
                    to = new FragmentMy();
                    break;
                default:
                    to = new FrageForumList();
                    break;
            }
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

    boolean ishaveReply = false;
    boolean ishavePm = false;

    private class MyTimerTask extends TimerTask{
        public void run() {
            String url_reply = "home.php?mod=space&do=notice&view=mypost&type=post"+(App.IS_SCHOOL_NET?"":"&mobile=2");
            String url_pm = "home.php?mod=space&do=pm&mobile=2";
            HttpUtil.SyncGet(HomeActivity.this, url_reply, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    dealMessage(true,new String(response));
                }
            });
            lastCheckMsgTime = System.currentTimeMillis();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HttpUtil.SyncGet(HomeActivity.this, url_pm, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    dealMessage(false,new String(response));
                }
            });
        }
    }


    private void dealMessage(boolean isReply,String res){
        Document document = Jsoup.parse(res);
        //回复
        if(isReply){
            Elements elemens = document.select(".nts").select("dl.cl");
            if(elemens.size()>0){
                int last_message_id  = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this)
                        .getInt(App.NOTICE_MESSAGE_KEY, 0);
                int  noticeId = Integer.parseInt(elemens.get(0).attr("notice"));
                ishaveReply = last_message_id < noticeId;
            }
        }else{
            Elements lists = document.select(".pmbox").select("ul").select("li");
            if(lists.size()>0){
                ishavePm = lists.get(0).select(".num").text().length() > 0;
            }
        }

        if(ishaveReply||ishavePm){
            messageHandler.sendEmptyMessage(0);
        }else{
            messageHandler.sendEmptyMessage(-1);
        }
    }


    //// TODO: 16-8-23  
    private  static class MyHandler extends Handler {
        private final WeakReference<MyBottomTab> mytab;
        private final WeakReference<HomeActivity> act;

        private MyHandler(MyBottomTab tab,HomeActivity aa) {
            mytab = new WeakReference<>(tab);
            act = new WeakReference<>(aa);
        }
        @Override
        public void handleMessage(Message msg) {
            MyBottomTab t = mytab.get();
            HomeActivity a = act.get();
            switch (msg.what){
                //-1 - 无消息 0-有
                case -1:
                    Log.e("message","无未读消息");
                    t.setMessage(false);
                    break;
                case 0:
                    a.mkNotify();
                    Log.e("message","有未读消息");
                    t.setMessage(true);
                    break;
            }
        }
    }


    private void mkNotify(){
        boolean isnotify = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this)
                .getBoolean("setting_show_notify", false);
        if(!isnotify){
            return;
        }
        final NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle("未读消息提醒")
                .setContentText("你有未读的消息哦,去我的消息页面查看吧！")
                .setAutoCancel(true);
        final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(10, builder.build());
        Log.e("message","发送未读消息弹窗");
    }

}
