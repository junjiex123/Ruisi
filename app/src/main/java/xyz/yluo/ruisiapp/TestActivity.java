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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;

/**
 * Created by free2 on 16-3-10.
 *
 */
public class TestActivity extends AppCompatActivity {

    private String congig_formhash;
    private String config_url;
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
        String url = config_url;
        RequestParams params = new RequestParams();
        //params.put("formhash",congig_formhash);
        params.put("fastloginfield","username");
        params.put("cookietime", "2592000");
        params.put("username", "谁用了FREEDOM");
        params.put("password", "justice");
        params.put("questionid", "0");
        params.put("answer", "");

        AsyncHttpCilentUtil.post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                if (res.contains("欢迎您回来")) {
                    //开始获取formhash
                    responseText.setText(new String(responseBody));

                } else {
                    Toast.makeText(getApplicationContext(), "用户名或者密码错误登陆失败！！", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络异常！！", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.get_formhash)
    protected void get_formHash_click() {
        //
        String url = "member.php?mod=logging&action=login&mobile=2";

        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);

                Document doc = Jsoup.parse(res);
                if (doc.select("input[name=formhash]").first() != null) {
                    congig_formhash = doc.select("input[name=formhash]").attr("value"); // 具有 formhash 属性的链接
                }
                if (doc.select("form#loginform").attr("action") != "") {
                    config_url = doc.select("form#loginform").attr("action");
                    responseText.append("\nurl:" + config_url + "\n");
                }
                responseText.append("\nformhash:" + congig_formhash + "\n");
                responseText.append(res + "\n\n\n");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络异常！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.login_post)
    protected void login_post_click() {
        RequestParams params = new RequestParams();
        String url =  "forum.php?mod=post&action=reply&fid=72&tid=841200&extra=&replysubmit=yes&mobile=2&handlekey=fastpost&loc=1&inajax=1";
        params.put("formhash", congig_formhash);
        params.put("message", "来我帮你免费///。。。。。");

        AsyncHttpCilentUtil.post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                responseText.append(res + "\n\n\n");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络异常！！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_get_list)
    protected void get_list_click() {
        //http://rs.xidian.edu.cn/forum.php?mod=forumdisplay&fid=72&mobile=2
        GetListTask getListTask = new GetListTask("72", 0);
        getListTask.execute((Void) null);
    }

    @OnClick(R.id.get_single)
    protected void get_single_click(){

        String url =  "forum.php?mod=viewthread&tid=841441&extra=&page=14&mobile=2";

        AsyncHttpCilentUtil.get(getApplicationContext(), url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                responseText.setText(res + "\n\n\n");
                Document doc = Jsoup.parse(res);
                if (doc.select("input[name=formhash]").first() != null) {
                    congig_formhash = doc.select("input[name=formhash]").attr("value");
                }
                responseText.setText(doc.select(".pg").select("a.nxt").attr("href"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "网络异常！！", Toast.LENGTH_SHORT).show();
            }
        });
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
                //response =
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
}
