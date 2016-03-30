package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ChatListAdapter;
import xyz.yluo.ruisiapp.data.ChatListData;

/**
 * Created by free2 on 16-3-30.
 * 聊天activity
 */
public class ChatActivity extends AppCompatActivity{

    @Bind(R.id.topic_recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private List<ChatListData> datas = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle("聊天");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        for(int i=0;i<30;i++){
            ChatListData temp = new ChatListData(0,"","","","");
            datas.add(temp);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        ChatListAdapter adapter = new ChatListAdapter(getApplicationContext(),datas);

        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);

    }
}
