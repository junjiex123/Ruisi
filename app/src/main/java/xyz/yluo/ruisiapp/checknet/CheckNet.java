package xyz.yluo.ruisiapp.checknet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-4-13.
 * 判断现在的网络状态
 * 校园网or 外网
 */
public class CheckNet{

    private Context context;
    private final ExecutorService threadPool;

    public CheckNet(Context context) {
        this.context = context;
        threadPool = Executors.newCachedThreadPool();
    }

    public void startCheck(final CheckNetResponse handler){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                request(handler);
            }
        });
    }

    private void request(final CheckNetResponse checkNetResponse){
        ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                checkNetResponse.sendFinishMessage(2,"ok");
                setData(false);
            }else{
                Document doc;
                try {
                    doc = Jsoup.connect("http://ip.xidian.cc/index.php").timeout(2000).get();
                    String content = doc.html();
                    if(content.contains("是计算流量费的")){
                        checkNetResponse.sendFinishMessage(2,"ok");
                        setData(false);
                    }else if(content.contains("是免流量费的")){
                        checkNetResponse.sendFinishMessage(1,"ok");
                        setData(true);
                    }else{
                        checkNetResponse.sendFinishMessage(0,"请打开网络连接");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    checkNetResponse.sendFinishMessage(0,"请打开网络连接");
                }
            }
        }else{
            checkNetResponse.sendFinishMessage(0,"请打开网络连接");
        }
    }

    private void setData(boolean isInner){
        if(isInner){
            PublicData.IS_SCHOOL_NET = true;
            PublicData.BASE_URL = UrlUtils.getBaseUrl(true);
        }else{
            PublicData.BASE_URL = UrlUtils.getBaseUrl(false);
            PublicData.IS_SCHOOL_NET = false;
        }
    }
}
