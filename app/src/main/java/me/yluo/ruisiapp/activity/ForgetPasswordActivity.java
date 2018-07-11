package me.yluo.ruisiapp.activity;

import android.os.Bundle;

import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.utils.UrlUtils;

public class ForgetPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initToolBar(true, "找回密码");

        String url = UrlUtils.getForgetPasswordUrl();
        //TODO
        // method post
        // params ["handlekey":"lostpwform","email": email]
        // 可选 ["username": username]
    }
}
