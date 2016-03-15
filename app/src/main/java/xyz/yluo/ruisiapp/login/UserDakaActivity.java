package xyz.yluo.ruisiapp.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-15.
 *
 */
public class UserDakaActivity extends AppCompatActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_daka);

        ButterKnife.bind(this);
    }
}
