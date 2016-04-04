package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.MyWebView;


/**
 * Created by yluo on 2015/10/5 0005.
 * 个人信息页面
 */
public class AboutActivity extends AppCompatActivity {

    @Bind(R.id.mywebview)
    protected MyWebView mywebview;
    @Bind(R.id.text_2)
    protected TextView textView2;
    @Bind(R.id.fab)
    protected FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String ss = "西电睿思手机客户端\n目前可见bug很多，最近比较忙我也不会去修复他。。。" +
                "\nbug反馈点击按钮给我发邮件吧\n或者 <a href=\"home.php?mod=space&uid=252553&do=profile&mobile=2\">" +
                "@谁用了FREEDOM</a>或者<a href=\"home.php?mod=space&uid=261098&do=profile&mobile=2\">@wangfuyang</a>";
        mywebview.loadDataWithBaseURL(MySetting.BBS_BASE_URL,ss,"text/html","UTF-8",null);
    }


    @OnClick(R.id.fab)
    protected void fab_clidk(View view){
        Snackbar.make(view, "别点了，还没写", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
        return super.onOptionsItemSelected(item);
    }
}
