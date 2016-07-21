package xyz.yluo.ruisiapp.checknet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xyz.yluo.ruisiapp.Config;

/**
 * Created by free2 on 16-4-13.
 * 判断现在的网络状态
 * 校园网or 外网
 */
public class CheckNet {

    private final ExecutorService threadPool;
    private Context context;

    public CheckNet(Context context) {
        this.context = context;
        threadPool = Executors.newCachedThreadPool();
    }

    public void startCheck(final CheckNetResponse handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                request(handler);
            }
        });
    }

    private void request(final CheckNetResponse checkNetResponse) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                checkNetResponse.sendFinishMessage(2, "ok");
                setData(false);
            } else {
                try {
                    Connection con1 = Jsoup.connect("http://202.117.119.1/portal.php").timeout(1500);

                    if (con1.get().title().contains("西电睿思")) {
                        checkNetResponse.sendFinishMessage(1, "ok");
                        setData(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Connection con2 = Jsoup.connect("http://bbs.rs.xidian.me/forum.php?mod=guide&view=hot&mobile=2").timeout(1500);
                    try {
                        if (con2.get().title().contains("西电睿思")) {
                            checkNetResponse.sendFinishMessage(2, "ok");
                            setData(false);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        checkNetResponse.sendFinishMessage(0, "无法连接服务器");
                    }
                }
            }
        } else {
            checkNetResponse.sendFinishMessage(0, "请打开网络连接");
        }


        /*


        */
    }

    private void setData(boolean isInner) {
        Config.IS_SCHOOL_NET = isInner;
    }
}
