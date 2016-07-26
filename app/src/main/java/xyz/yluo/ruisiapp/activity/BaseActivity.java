package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import xyz.yluo.ruisiapp.Config;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.checknet.NetworkReceiver;

/**
 * Created by free2 on 16-4-11.
 * 所有activity的基类
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            //返回按钮点击事件
            case android.R.id.home:
                finish();
                break;
            case R.id.new_topic:
                startActivity(new Intent(getApplicationContext(), NewArticleActivity_2.class));
                return true;
            case R.id.menu_search:
                if (isneed_login()) {
                    startActivity(new Intent(getApplicationContext(), ActivitySearch.class));
                    return true;
                }
                break;
            case R.id.menu_test:
                startActivity(new Intent(getApplicationContext(),TestActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    //判断是否需要弹出登录dialog
    boolean isneed_login() {
        if (Config.ISLOGIN) {
            return true;
        } else {
            new MyAlertDialog(this,MyAlertDialog.WARNING_TYPE)
                    .setTitleText("需要登录登陆")
                    .setCancelText("取消")
                    .setContentText("你还没有登陆，要去登陆吗？？")
                    .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                        @Override
                        public void onClick(MyAlertDialog myAlertDialog) {
                            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        }
                    }).show();
        }
        return false;
    }
}
