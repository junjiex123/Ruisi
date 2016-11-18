package xyz.yluo.ruisiapp.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyHtmlView.HtmlView;
import xyz.yluo.ruisiapp.utils.IntentUtils;


/**
 * Created by yluo on 2015/10/5 0005.
 * 关于页面
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
        }
        TextView version = (TextView) findViewById(R.id.version);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        String ss = "<b>西电睿思手机客户端</b><br />功能不断完善中，bug较多还请多多反馈......<br />" +
                "bug反馈:<br />" +
                "1.到 <a href=\"forum.php?mod=viewthread&tid=" + App.POST_TID + "&mobile=2\">本帖</a> 回复<br />" +
                "2.本站 <a href=\"home.php?mod=space&uid=252553&do=profile&mobile=2\">@谁用了FREEDOM</a><br />" +
                "3.本站 <a href=\"home.php?mod=space&uid=261098&do=profile&mobile=2\">@wangfuyang</a><br />" +
                "4.github提交 <a href=\"https://github.com/freedom10086/Ruisi\">点击这儿<br /></a><br />" +
                "5.xhinliang xhinliang@gmail.com";

        HtmlView htmlView = (HtmlView) findViewById(R.id.html_text);
        htmlView.setHtmlText(ss, false);

        PackageInfo info = null;
        PackageManager manager = getPackageManager();
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (info != null) {
            int version_code = info.versionCode;
            String version_name = info.versionName;
            String a = "版本号：" + version_code + "\n版本：" + version_name;
            version.setText(a);
        }

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "你要提交bug或者建议吗?", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String user = App.getName(AboutActivity.this);
                                if (user != null) {
                                    user = "by:" + user;
                                }
                                IntentUtils.sendMail(getApplicationContext(), user);
                            }
                        })
                        .show();
            }
        });
    }

}
