package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyProgressDialog;
import xyz.yluo.ruisiapp.database.MyDbUtils;

public class TestActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_1:
                new MyProgressDialog(this)
                        .setLoadingText("加 载 中 . . . . . .")
                        .show();
                break;
            case R.id.btn_2:
                new MyAlertDialog(this, MyAlertDialog.ERROR_TYPE)
                        .setTitleText("Good job!")
                        .setContentText("You clicked the btn_blue_bg!")
                        .show();
                break;
            case R.id.btn_3:
                new MyAlertDialog(this, MyAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Good job!")
                        .setContentText("You clicked the btn_blue_bg!")
                        .show();
                break;
            case R.id.btn_4:
                new MyAlertDialog(this, MyAlertDialog.NORMAL_TYPE)
                        .setTitleText("Good job!")
                        .setContentText("You clicked the btn_blue_bg!")
                        .setCancelText("取消")
                        .show();
                break;
            case R.id.btn_5:
                new MyAlertDialog(this, MyAlertDialog.WARNING_TYPE)
                        .setTitleText("Good job!")
                        .setContentText("You clicked the btn_blue_bg!")
                        .show();
                break;
            case R.id.btn_6:
                MyDbUtils myDbUtils = new MyDbUtils(getApplicationContext(), MyDbUtils.MODE_WRITE);
                myDbUtils.insertMessage("http://www.baidu.com/tid=64645645","这是描述");
                Log.e("插入","success");
                break;


        }
    }
}
