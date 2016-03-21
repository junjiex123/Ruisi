package xyz.yluo.ruisiapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.yluo.ruisiapp.R;

public class UserDetailActivity extends AppCompatActivity {

    @Bind(R.id.user_detail_img_avatar)
    protected CircleImageView imageView;

    @Bind(R.id.user_detail_tv_login_name)
    protected TextView textView;

    private static final String NAME_IMG_AVATAR = "imgAvatar";

    public static void openWithTransitionAnimation(Activity activity, String loginName, ImageView imgAvatar, String avatarUrl) {
        Intent intent = new Intent(activity, UserDetailActivity.class);
        intent.putExtra("loginName", loginName);
        intent.putExtra("avatarUrl", avatarUrl);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imgAvatar, NAME_IMG_AVATAR);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void open(Context context, String loginName) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("loginName", loginName);
        context.startActivity(intent);
    }

    private String loginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        ViewCompat.setTransitionName(imageView, NAME_IMG_AVATAR);

        loginName = getIntent().getStringExtra("loginName");
        if (!TextUtils.isEmpty(loginName)) {
            textView.setText(loginName);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        loadcontent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //返回按钮
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadcontent(){
        //new DownloadTask().execute("http://bbs.pcbeta.com/forum-win10-1.html");
        new DownloadTask().execute("https://www.chiphell.com/forum-36-1.html");

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            //return http_client_post_get.HttpURLConnection_GET();
            StringBuffer buffer=new StringBuffer();
            Document doc;
            try {
                doc = Jsoup.connect(urls[0]).get();

                //chiphell
                Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
                //Elements body = doc.select("body");
                System.out.print(body);
                //
                //这是手机版的
                //"a[class=title]"
                //pcbeta
                //Elements links = doc.select("li[class=thread_item]");

                //chiphell
                Elements links = body.select("li");
                System.out.print(links);
                for (Element src : links) {
                    buffer.append("img:  "+src.getElementsByTag("img").attr("src")+"\n");
                    buffer.append("title:  "+src.getElementsByTag("a").text()+"\n");
                    buffer.append("url:  "+src.getElementsByTag("a").attr("href")+"\n");
                    //buffer.append("num:   "+src.getElementsByTag("span").text()+"\n");

                    //chiphell
                    buffer.append("num:   "+src.getElementsByAttributeValue("class", "num").text()+"\n");
                    buffer.append("author:   "+src.getElementsByAttributeValue("class", "by").text()+"\n");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.print(buffer.toString());
            return buffer.toString();

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("111", result);
            textView.setText(result);
        }
    }
}
