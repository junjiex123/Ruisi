package xyz.yluo.ruisiapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.activity.ChatActivity;
import xyz.yluo.ruisiapp.activity.NewArticleActivity;
import xyz.yluo.ruisiapp.activity.NewArticleActivity_2;
import xyz.yluo.ruisiapp.data.ArticleListData;

/**
 * Created by free2 on 16-3-10.
 *
 */
public class TestActivity extends AppCompatActivity implements ServiceConnection {

    @Bind(R.id.text_response)
    protected TextView responseText;
    @Bind(R.id.webview)
    protected WebView webview;

    final AsyncHttpClient client = new AsyncHttpClient();
    PersistentCookieStore myCookieStore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        myCookieStore = new PersistentCookieStore(getApplicationContext());
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_button)
    protected void login_button_click() {

        client.setCookieStore(myCookieStore);
        client.get(MySetting.BBS_BASE_URL+"member.php?mod=logging&action=login&mobile=2", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                Document doc = Jsoup.parse(res);

                if (doc.select("form#loginform").attr("action") != "") {
                    String loginUrl = "";
                    loginUrl = doc.select("form#loginform").attr("action");

                    responseText.setText(loginUrl);
                    RequestParams params = new RequestParams();
                    params.put("fastloginfield", "username");
                    params.put("cookietime", "2592000");
                    params.put("username", "FREEDOM_1");
                    params.put("password", "justice");
                    params.put("questionid", "0");
                    params.put("answer", "");

                    client.setCookieStore(myCookieStore);
                    client.post(MySetting.BBS_BASE_URL+loginUrl, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String res = new String(responseBody);

                            responseText.setText(res);

                            if (res.contains("欢迎您回来")) {
                                Document document = Jsoup.parse(res);
                            } else {
                                Toast.makeText(getApplicationContext(), "用户名或者密码错误登陆失败！！", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                responseText.setText("error");
            }
        });


    }

    @OnClick(R.id.login_post)
    protected void login_post_click() {

        client.setCookieStore(myCookieStore);
        client.get(getApplicationContext(), MySetting.BBS_BASE_URL+"forum.php?mod=post&action=newthread&fid=72&mobile=2", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Document doc  = Jsoup.parse(new String(responseBody));

                String  hash = doc.select("input#formhash").attr("value");
                String  time = doc.select("input#posttime").attr("value");

                RequestParams params = new RequestParams();
                params.add("formhash",hash);
                params.add("posttime",time);
                //params.add("topicsubmit","yes");
                params.add("usesig","1");
                params.add("subject","发帖失败了");
                params.add("message","现在看看怎么样。。。。。。[b]...[/b]");

                String url2 = MySetting.BBS_BASE_URL+"forum.php?mod=post&action=newthread&fid=72&topicsubmit=yes&infloat=yes&handlekey=fastnewpost";

                String url = MySetting.BBS_BASE_URL+"forum.php?mod=post&action=newthread&fid=72&extra=&topicsubmit=yes&mobile=2&geoloc=&handlekey=postform&inajax=1";


                client.setCookieStore(myCookieStore);
                client.post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String res = new String(responseBody);
                        responseText.setText(res + "\n\n\n");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getApplicationContext(), "网络异常！！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @OnClick(R.id.start_test)
    protected void start_test_click(){
        startActivity(new Intent(getApplicationContext(), NewArticleActivity_2.class));
    }

    @OnClick(R.id.start_chat)
    protected void start_chat_click(){
        startActivity(new Intent(getApplicationContext(), ChatActivity.class));
    }

    @OnClick(R.id.start_service)
    protected void start_service_click(){
        Intent i = new Intent(this,CheckMessageService.class);
        startService(i);
    }
    @OnClick(R.id.stop_service)
    protected void stop_service_click(){
        stopService(new Intent(this,CheckMessageService.class));
    }
    @OnClick(R.id.notification)
    protected void notification_click(){
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_favorite_24dp)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, NewArticleActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(1, mBuilder.build());

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        System.out.print(">>connected");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        System.out.print(">>disconnected");
    }



    //获得数据
    public class GetListTask extends AsyncTask<Void, Void, String> {

        //http://rs.xidian.edu.cn/forum.php?mod=forumdisplay&fid=72&mobile=2
        private final String Baseurl = "http://rs.xidian.edu.cn/forum.php?mod=forumdisplay&fid=";
        private String fullurl = "";
        private List<ArticleListData> dataset;

        public GetListTask(String fid, int page) {

            dataset = new ArrayList<>();

            if (page == 0) {
                fullurl = Baseurl + fid+"&mobile=2";
            } else {
                fullurl = Baseurl + fid + "&page=" + page;
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            try {
            } catch (Exception e) {
                return "error";
            }
            StringBuffer buffer = new StringBuffer();
            if (response != "") {
                Elements list = Jsoup.parse(response).select("div[class=threadlist]").select("li");
                //Elements links = list.select("tbody");

                //System.out.print(links);

                ArticleListData temp;
                for (Element src : list) {

                    System.out.print("\n"+src.html());
                    //if (src.getElementsByAttributeValue("class", "by").first() != null) {

//                        String type = "normal";
//                        //金币
//                        if (src.select("th").select("strong").text() != "") {
//                            type = "gold:" + src.select("th").select("strong").text();
//                        }
//                        //置顶 正常
//                        if (src.attr("id").contains("normalthread")) {
//                            type = "normal";
//                        } else if (src.attr("id").contains("stickthread")) {
//                            type = "zhidin";
//                        }
//                        String title = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").text();
//                        String titleUrl = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").attr("href");
//                        //http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=836820&extra=page%3D1
//                        String author = src.getElementsByAttributeValue("class", "by").first().select("a").text();
//                        String authorUrl = src.getElementsByAttributeValue("class", "by").first().select("a").attr("href");
//                        String time = src.getElementsByAttributeValue("class", "by").first().select("em").text().trim();
//                        String viewcount = src.getElementsByAttributeValue("class", "num").select("em").text();
//                        String replaycount = src.getElementsByAttributeValue("class", "num").select("a").text();


//                        if (title != "" && author != "" && viewcount != "") {
//                            //新建对象
//                            temp = new ArticleListData(title, titleUrl, type, author, authorUrl, time, viewcount, replaycount);
//                            dataset.add(temp);
//                            buffer.append(type).append(title).append(titleUrl).append(author).append(authorUrl).append(time).append(viewcount).append("\n");
//                        }

                   // }
                }
                //System.out.print(buffer);
            }
            return response;
        }

        @Override
        protected void onPostExecute(final String res) {

        }

        @Override
        protected void onCancelled() {

        }
    }
}
