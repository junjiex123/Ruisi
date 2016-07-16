package xyz.yluo.ruisiapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyHtmlView.getSpanned;
import xyz.yluo.ruisiapp.data.LoadMoreType;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.data.SingleType;
import xyz.yluo.ruisiapp.database.MyDbUtils;
import xyz.yluo.ruisiapp.fragment.BaseFragment;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetNumber;
import xyz.yluo.ruisiapp.utils.UrlUtils;


public class TestActivity extends BaseActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView) findViewById(R.id.main_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getArticleData("854641");
    }

    //文章一页的html 根据页数 Tid
    private void getArticleData(String Tid) {
        String url = UrlUtils.getSingleArticleUrl(Tid, 1, false);
        HttpUtil.get(this, url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                new DealWithArticleData().execute(res);
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "网络错误(Error -1)", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private class DealWithArticleData extends AsyncTask<String, Void, Element> {
        @Override
        protected Element doInBackground(String... params) {
            List<SingleArticleData> tepdata = new ArrayList<>();
            String htmlData = params[0];
            //list 所有楼数据
            Document doc = Jsoup.parse(htmlData);
            Elements elements = doc.select(".postlist");
            Elements postlist = elements.select("div[id^=pid]");

            /**
             * 楼主
             */
            Element temp = postlist.get(0);
            SingleArticleData data;
            String userimg = temp.select("span[class=avatar]").select("img").attr("src");
            Elements userInfo = temp.select("ul.authi");
            String commentindex = userInfo.select("li.grey").select("em").text();
            String username = userInfo.select("a[href^=home.php?mod=space&uid=]").text();
            String posttime = userInfo.select("li.grey.rela").text();
            String replyUrl = temp.select(".replybtn").select("input").attr("href");
            Elements contentels = temp.select(".message");

            return contentels.first();


        }

        @Override
        protected void onPostExecute(Element e) {
            getSpanned a = new getSpanned();
            textView.setText(a.getCharSequence(e));

            //textView.setText(e.text());
        }
    }

}
