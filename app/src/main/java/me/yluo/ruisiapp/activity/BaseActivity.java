package me.yluo.ruisiapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.utils.DimmenUtils;

/**
 * Created by free2 on 16-4-11.
 * 所有activity的基类
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = App.getCustomTheme(this);
        if (theme == 0 || theme == 1) {
            //夜间 白天
        } else {
            setTheme(theme);
        }
    }

    @Override
    protected void onResume() {
        switchTheme();
        super.onResume();
    }

    //切换主题
    public void switchTheme() {
        /*
        boolean enableDarkMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("setting_dark_mode", false);
        boolean auto = false;
        int cur = AppCompatDelegate.getDefaultNightMode();
        int to = cur;
        if (enableDarkMode) {//允许夜间模式
            if (auto = App.isAutoDarkMode(this)) {//自动夜间模式
                int[] time = App.getDarkModeTime(this);
                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if ((hour >= time[0] || hour < time[1])) {
                    to = AppCompatDelegate.MODE_NIGHT_YES;
                } else {
                    to = AppCompatDelegate.MODE_NIGHT_NO;
                }
            } else {
                to = AppCompatDelegate.MODE_NIGHT_YES;
            }
        } else {//不允许夜间模式
            to = AppCompatDelegate.MODE_NIGHT_NO;
        }

        if (cur != to) {
            AppCompatDelegate.setDefaultNightMode(to);
            if (auto) {
                showToast("已自动切换到" + (to == AppCompatDelegate.MODE_NIGHT_YES ?
                        "夜间模式" : "日间模式"));
            }
        }
        */
    }

    private static Toast mToast;

    //判断是否需要弹出登录dialog
    public boolean isLogin() {
        if (!TextUtils.isEmpty(App.getUid(this))) {
            return true;
        } else {
            Dialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("需要登陆")
                    .setMessage("你还没有登陆，要去登陆吗？？")
                    .setPositiveButton("登陆", (dialog, which) -> startActivity(new Intent(BaseActivity.this, LoginActivity.class)))
                    .setNegativeButton("取消", null)
                    .setCancelable(true)
                    .create();

            alertDialog.show();
        }
        return false;
    }


    protected void initToolBar(boolean isshowBack, String text) {
        View toolbar = findViewById(R.id.myToolBar);
        if (toolbar != null) {
            ((TextView) toolbar.findViewById(R.id.title)).setText(text);
            if (isshowBack) {
                findViewById(R.id.logo).setOnClickListener(view -> finish());
            } else {
                findViewById(R.id.logo).setVisibility(View.GONE);
            }
        }
    }

    protected void setTitle(String s) {
        View toolbar = findViewById(R.id.myToolBar);
        if (toolbar != null) {
            ((TextView) toolbar.findViewById(R.id.title)).setText(s);
        }
    }

    protected ImageView addToolbarMenu(int resid) {
        View toolbar = findViewById(R.id.myToolBar);
        if (toolbar != null) {
            ImageView i = (ImageView) toolbar.findViewById(R.id.menu);
            i.setImageResource(resid);
            i.setVisibility(View.VISIBLE);
            return i;
        }
        return null;
    }

    protected void addToolbarView(View v) {
        FrameLayout toolbar = (FrameLayout) findViewById(R.id.myToolBar);
        if (toolbar != null) {
            FrameLayout.LayoutParams pls = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            v.setLayoutParams(pls);
            int padding = DimmenUtils.dip2px(this, 12);
            v.setPadding(padding, padding, padding, padding);
            pls.setMarginEnd(padding);
            pls.gravity = Gravity.END;
            toolbar.addView(v);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    protected void showToast(String str) {
        if (mToast == null) {
            mToast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(str);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    protected void showLongToast(String str) {
        if (mToast == null) {
            mToast = Toast.makeText(this, str, Toast.LENGTH_LONG);
        } else {
            mToast.setText(str);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
