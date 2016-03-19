package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.HomeActivity;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class LaunchActivity extends AppCompatActivity implements Runnable{

    private SharedPreferences perPreferences;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        //TODO  perPreferences
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        perPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //TODO
        //editor = perPreferences.edit();
        //isFirstIn = perPreferences.getBoolean("isFirstIn", true);
        //isAutoLogin = perPreferences.getBoolean("ISAUTO", false);
        postDelayed(this,1000);
    }

    @Override
    public void run() {

        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }


    public static boolean postDelayed(Runnable r, long delayMillis) {
        return handler.postDelayed(r, delayMillis);
    }
}
