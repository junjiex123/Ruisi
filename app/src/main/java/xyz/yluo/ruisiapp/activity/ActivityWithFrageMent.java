package xyz.yluo.ruisiapp.activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.fragment.FrageFriends;
import xyz.yluo.ruisiapp.fragment.FrageMessage;


/**
 * 带fragemnt的Activity
 */
public class ActivityWithFrageMent extends BaseActivity {

    public static final int FRAGE_MESSAGE = 0;
    public static final int FRAGE_FRIEND = 1;

    private FrageMessage frageMessage;
    private FrageFriends frageFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_with_fragement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int id =  getIntent().getExtras().getInt("type",-1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBar actionBar = getDelegate().getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        switch (id){
            case FRAGE_MESSAGE:
                if(actionBar!=null)
                actionBar.setTitle("我的消息");
                changeFragement(FRAGE_MESSAGE);
                break;
            case FRAGE_FRIEND:
                if(actionBar!=null)
                actionBar.setTitle("我的好友");
                changeFragement(FRAGE_FRIEND);
                break;
        }

    }

    private void changeFragement(int id){
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (id)
        {
            case FRAGE_MESSAGE:
                if (frageMessage == null)
                {
                    frageMessage = new FrageMessage();
                }
                // 使用当前Fragment的布局替代id_content的控件
                transaction.add(R.id.fragment, frageMessage);
                break;
            case FRAGE_FRIEND:
                if (frageFriends == null)
                {
                    frageFriends = new FrageFriends();
                }
                transaction.add(R.id.fragment, frageFriends);
                break;
        }


        // 事务提交
        transaction.commit();
    }

}
