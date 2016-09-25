package xyz.yluo.ruisiapp.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-4-11.
 * 所有activity的基类
 */
public class BaseActivity extends AppCompatActivity {

    private static Toast mToast;

    //判断是否需要弹出登录dialog
    public boolean isLogin() {
        if (!TextUtils.isEmpty(App.getUid(this))) {
            return true;
        } else {
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("需要登陆").
                    setMessage("你还没有登陆，要去登陆吗？？").
                    setPositiveButton("登陆", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        }
                    })
                    .setNegativeButton("取消",null)
                    .setCancelable(true)
                    .create();

            alertDialog.show();
        }
        return false;
    }


    protected void initToolBar(boolean isshowBack,String text){
        View toolbar = findViewById(R.id.myToolBar);
        if(toolbar!=null){
            ((TextView)toolbar.findViewById(R.id.title)).setText(text);
            if(isshowBack){
                findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }else{
                findViewById(R.id.logo).setVisibility(View.GONE);
            }
        }
    }

    protected ImageView addToolbarMenu(int resid){
        View toolbar = findViewById(R.id.myToolBar);
        if(toolbar!=null){
            ImageView i =  (ImageView)toolbar.findViewById(R.id.menu);
            i.setImageResource(resid);
            i.setVisibility(View.VISIBLE);
            return i;
        }
        return null;
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
}
