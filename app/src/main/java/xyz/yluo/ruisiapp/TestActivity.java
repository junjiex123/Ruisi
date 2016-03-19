package xyz.yluo.ruisiapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.api.ArticleListData;
import xyz.yluo.ruisiapp.api.GetFormHash;
import xyz.yluo.ruisiapp.api.getMd5Pass;
import xyz.yluo.ruisiapp.http.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.http.MyHttpConnection;

/**
 * Created by free2 on 16-3-10.
 *
 */
public class TestActivity extends AppCompatActivity {

    private String formhash;

    @Bind(R.id.text_response)
    protected TextView responseText;
    @Bind(R.id.webview)
    protected WebView webview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_button)
    protected void login_button_click() {
        String url = "http://rs.xidian.edu.cn/member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1";
        Map<String, String> params = new HashMap<>();
        params.put("username", "谁用了FREEDOM");
        params.put("cookietime", "2592000");
        params.put("password", "9345b4e983973212313e4c809b94f75d");
        params.put("quickforward", "yes");
        params.put("handlekey", "ls");

        UserLoginTask mAuthTask = new UserLoginTask(url, params, "post");
        mAuthTask.execute((Void) null);

    }

    @OnClick(R.id.get_formhash)
    protected void get_formHash_click() {
        String url = "http://rs.xidian.edu.cn/forum.php";
        UserLoginTask mAuthTask = new UserLoginTask(url, null, "get");
        mAuthTask.execute((Void) null);
    }

    @OnClick(R.id.login_post)
    protected void login_post_click() {
        String url = "http://rs.xidian.edu.cn/forum.php?mod=post&infloat=yes&action=reply&fid=72&extra=&tid=837982&replysubmit=yes&inajax=1";
        Map<String, String> params = new HashMap<>();
        /*
        message:帮顶
        posttime:1457620291
        formhash:70af5bb6
        usesig:1
        subject:
        */
        params.put("formhash", formhash);
        params.put("usesig", "1");
        params.put("message", "来了                 ");
        params.put("subject", "");

        UserLoginTask mAuthTask = new UserLoginTask(url, params, "post");
        mAuthTask.execute((Void) null);
    }

    @OnClick(R.id.btn_get_list)
    protected void get_list_click() {
        GetListTask getListTask = new GetListTask("72", 0);
        getListTask.execute((Void) null);
    }

    @OnClick(R.id.show_cookie)
    protected void show_cookie_click(){
        responseText.setText(ConfigClass.CONFIG_COOKIE);
    }
    @OnClick(R.id.show_hash)
    protected void show_hash_click(){
        responseText.setText(ConfigClass.CONFIG_FORMHASH);
    }

    @OnClick(R.id.get_single)
    protected void get_single_click(){
        GetSingle getSingle = new GetSingle("http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=839311&extra=page%3D1");
        getSingle.execute((Void) null);
    }

    @OnClick(R.id.get_image)
    protected void get_image_click(){
        //get
        //<img id="aimg_dfzU4" onclick="zoom(this, this.src, 0, 0, 0)" class="zoom" width="249" height="356" file="http://rs.xidian.edu.cn/forum.php?mod=image&amp;aid=851820&amp;size=300x300&amp;key=2a3604eec0da779f&amp;nocache=yes&amp;type=fixnone" border="0" alt="" />
        //ok
        //String data = "<img id=\"aimg_I3N3u\" onclick=\"zoom(this, this.src, 0, 0, 0)\" class=\"zoom\" width=\"249\" height=\"356\" file=\"http://rs.xidian.edu.cn/forum.php?mod=image&amp;aid=851820&amp;size=300x300&amp;key=2a3604eec0da779f&amp;nocache=yes&amp;type=fixnone\" border=\"0\" alt=\"\" src=\"http://rs.xidian.edu.cn/forum.php?mod=image&amp;aid=851820&amp;size=300x300&amp;key=2a3604eec0da779f&amp;nocache=yes&amp;type=fixnone\" lazyloaded=\"true\">";
        //not ok
        //String data = "<img src=\"http://rs.xidian.edu.cn/forum.php?mod=image&aid=851820&size=300x300&key=2a3604eec0da779f&nocache=yes&type=fixnone\"";
        String data = "<img src=\"./data/attachment/forum/201603/15/110909j3zlw4uoez7we5eq.jpg\">";

        webview.loadDataWithBaseURL("http://rs.xidian.edu.cn/", data, "text/html", "UTF-8", null);
    }

    @OnClick(R.id.new_login)
    protected void new_login_click(){

        String url = "member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1";
        RequestParams params = new RequestParams();
        params.put("username", "谁用了FREEDOM");
        params.put("cookietime", "2592000");
        params.put("password", "9345b4e983973212313e4c809b94f75d");
        params.put("quickforward", "yes");
        params.put("handlekey", "ls");

        AsyncHttpCilentUtil.post(this, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                responseText.setText(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }

    @OnClick(R.id.new_get)
    protected void new_get_click(){

        AsyncHttpCilentUtil.get(this, "portal.php", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                responseText.setText(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @OnClick(R.id.new_get_hash)
    protected void new_get_hash_click(){
        if(ConfigClass.CONFIG_FORMHASH==""){
            GetFormHash.start_get_hash(getApplicationContext());

        }else{

            responseText.setText(ConfigClass.CONFIG_FORMHASH);
        }
    }
    @OnClick(R.id.new_get_hash_again)
    protected void new_get_hash_again_click(){

        responseText.setText(ConfigClass.CONFIG_FORMHASH);

    }

    @OnClick(R.id.pass_md5)
    protected void pass_md5_click(){
        String pass = getMd5Pass.getMD5("justice");
        responseText.setText(pass);

    }

    @OnClick(R.id.get_hot)
    protected void get_hot_click(){
        String url = "forum.php";
        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @OnClick(R.id.get_forums)
    protected void get_forums_onclick(){
        String url  = "forum.php";

        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);

                Elements list = Jsoup.parse(res).select("#category_89,#category_101,#category_71,category_97,category_11").select("td.fl_g");

                for(Element tmp:list){
                    String img = tmp.select("img[src^=./data/attachment]").attr("src").replace("./data","data");
                    String url = tmp.select("a[href^=forum.php?mod=forumdisplay&fid]").attr("href");
                    String title = tmp.select("a[href^=forum.php?mod=forumdisplay&fid]").text();

                    String todaynew = tmp.select("em[title=今日]").text();
                    String actualnew = "";
                    if(todaynew!=""){
                        Pattern pattern = Pattern.compile("[0-9]+");
                        Matcher matcher = pattern.matcher(todaynew);
                        String tid ="";
                        while (matcher.find()) {
                            actualnew = todaynew.substring(matcher.start(),matcher.end());
                            //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
                        }
                    }

                    responseText.append("\nimg>>"+img+"\nurl>>"+url+"\ntitle>>"+title+"\ntoday>>"+todaynew+"\nactual>>"+actualnew);


                    //forum.php?mod=forumdisplay&fid

                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络错误！！", Toast.LENGTH_SHORT).show();

            }
        });


    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String url;
        private final Map<String, String> paramss;
        String method;

        UserLoginTask(String url, Map<String, String> paramss, String method) {
            this.url = url;
            this.paramss = paramss;
            this.method = method;
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            try {
                if (method == "get") {
                    response = MyHttpConnection.Http_get(url);
                    System.out.print("get response>>>>>>>>>>>\n" + response);
                } else {
                    response = MyHttpConnection.Http_post(url, paramss);
                    System.out.print("post response>>>>>>>>>>\n" + response);
                }
            } catch (Exception e) {
                return "error";
            }
            return response;
        }

        @Override
        protected void onPostExecute(final String res) {
            //临时保存 解析FormHash
            Document doc;
            doc = Jsoup.parse(res);
            if (doc.select("input[name=formhash]").first() != null) {
                formhash = doc.select("input[name=formhash]").first().attr("value"); // 具有 formhash 属性的链接
            }

            responseText.setText("formhash:" + formhash + "\n");
            responseText.setText(res + "\n\n\n");
        }

        @Override
        protected void onCancelled() {
        }
    }

    //获得数据
    public class GetListTask extends AsyncTask<Void, Void, String> {

        private final String Baseurl = "http://rs.xidian.edu.cn/forum.php?mod=forumdisplay&fid=";
        private String fullurl = "";
        private List<ArticleListData> dataset;

        public GetListTask(String fid, int page) {

            dataset = new ArrayList<>();

            if (page == 0) {
                fullurl = Baseurl + fid;
            } else {
                fullurl = Baseurl + fid + "&page=" + page;
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            try {
                response = MyHttpConnection.Http_get(fullurl);
            } catch (Exception e) {
                return "error";
            }

            StringBuffer buffer = new StringBuffer();
            if (response != "") {
                Elements list = Jsoup.parse(response).select("div[id=threadlist]");
                Elements links = list.select("tbody");

                //System.out.print(links);

                ArticleListData temp;
                for (Element src : links) {
                    if (src.getElementsByAttributeValue("class", "by").first() != null) {

                        String type = "normal";
                        //金币
                        if (src.select("th").select("strong").text() != "") {
                            type = "gold:" + src.select("th").select("strong").text();
                        }
                        //置顶 正常
                        if (src.attr("id").contains("normalthread")) {
                            type = "normal";
                        } else if (src.attr("id").contains("stickthread")) {
                            type = "zhidin";
                        }
                        String title = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").text();
                        String titleUrl = src.select("th").select("a[href^=forum.php?mod=viewthread][class=s xst]").attr("href");
                        //http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=836820&extra=page%3D1
                        String author = src.getElementsByAttributeValue("class", "by").first().select("a").text();
                        String authorUrl = src.getElementsByAttributeValue("class", "by").first().select("a").attr("href");
                        String time = src.getElementsByAttributeValue("class", "by").first().select("em").text().trim();
                        String viewcount = src.getElementsByAttributeValue("class", "num").select("em").text();
                        String replaycount = src.getElementsByAttributeValue("class", "num").select("a").text();


                        if (title != "" && author != "" && viewcount != "") {
                            //新建对象
                            temp = new ArticleListData(title, titleUrl, type, author, authorUrl, time, viewcount, replaycount);
                            dataset.add(temp);
                            buffer.append(type).append(title).append(titleUrl).append(author).append(authorUrl).append(time).append(viewcount).append("\n");
                        }

                    }
                }
                System.out.print(buffer);
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

    //获得图片板块图片链接
    public class GetImageUrlList extends AsyncTask<Void, Void, String> {
        private String url;

        public GetImageUrlList(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {

            String response = "";
            try {
                response = MyHttpConnection.Http_get(url);
            } catch (Exception e) {
                return "error";
            }
            if (response != "") {
                Elements list = Jsoup.parse(response).select("ul[id=waterfall]");
                Elements imagelist = list.select("li");

                for(Element tmp:imagelist){
                    System.out.println("\nimg!!!!!!!!!!!!!!!!!!!>>"+tmp.select("img"));
                    System.out.println("\nurl!!!!!!!!!!!!!!!!!!!>>"+tmp.select("h3.xw0").select("a[href^=forum.php]").attr("href"));
                    System.out.println("\ntitle!!!!!!!!!!!!!!!!!>>"+tmp.select("h3.xw0").select("a[href^=forum.php]").text());
                    System.out.println("\nauthor!!!!!!!!!!!!!!!!>>"+tmp.select("a[href^=home.php]").text());
                    System.out.println("\naurl!!!!!!!!!!!!!!!!>>"+tmp.select("a[href^=home.php]").attr("href"));
                    System.out.println("\nlike!!!!!!!!!!!!!!!!!>>"+tmp.select("div.auth").select("a[href^=forum.php]").text());
                }
            }

            return null;
        }
    }

    //response = MyHttpConnection.Http_get("http://rs.xidian.edu.cn/"+url);
    //获得单篇文章
    public class GetSingle extends AsyncTask<Void, Void, String> {

        private final String Baseurl = "http://rs.xidian.edu.cn/forum.php?mod=forumdisplay&fid=";
        private String fullurl = "";
        private List<ArticleListData> dataset;

        public GetSingle(String url) {

            dataset = new ArrayList<>();
            fullurl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            try {
                response = MyHttpConnection.Http_get(fullurl);
            } catch (Exception e) {
                return "error";
            }

            StringBuffer buffer = new StringBuffer();
            if (response != "") {
            }
            return response;
        }

        @Override
        protected void onPostExecute(final String res) {
            responseText.setText("res:" + res + "\n");
        }

        @Override
        protected void onCancelled() {

        }
    }

}
