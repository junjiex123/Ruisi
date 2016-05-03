package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.NeedLoginDialogFragment;

/**
 * Created by free2 on 16-4-11.
 * 所有activity的基类
 */
public class BaseActivity extends AppCompatActivity{

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_setting:
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                return true;
            case R.id.new_topic:
                startActivity(new Intent(getApplicationContext(),NewArticleActivity_2.class));
                return true;
            case R.id.menu_search:
                if(isneed_login()){
                    startActivity(new Intent(getApplicationContext(),ActivitySearch.class));
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);

    }

    //判断是否需要弹出登录dialog
    protected boolean isneed_login(){
        if(PublicData.ISLOGIN){
            return true;
        }else{
            NeedLoginDialogFragment dialogFragment = new NeedLoginDialogFragment();
            dialogFragment.show(getFragmentManager(), "needlogin");
        }
        return false;
    }
}
