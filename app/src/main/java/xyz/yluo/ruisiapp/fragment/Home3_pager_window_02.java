package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.Home3RecylerAdapter;
import xyz.yluo.ruisiapp.data.MyTopicReplyListData;


//第一页预览页面
public class Home3_pager_window_02 extends Fragment {

    @Bind(R.id.refresh_view)
    protected SwipeRefreshLayout refresh_view;
    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    private static RecyclerView.LayoutManager layoutManager;
    private static Home3RecylerAdapter adapter;
    private static List<MyTopicReplyListData> datas = new ArrayList<>();

    private static final String ARG_POSITION = "position";

    public static Home3_pager_window_02 newInstance(int position) {
        Home3_pager_window_02 f = new Home3_pager_window_02();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int position = getArguments().getInt(ARG_POSITION);
        View rootView = inflater.inflate(R.layout.layout_home3_pager_window_02_03, container, false);
        ButterKnife.bind(this,rootView);

        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new Home3RecylerAdapter(getActivity(),datas);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);

        //int type, String title, String titleUrl, String author, String time, String froumName
        for (int i =0;i<10;i++){
            datas.add(new MyTopicReplyListData(0,"title","titleUrl","author","time","froumName"));
        }

        adapter.notifyItemRangeInserted(0,datas.size());

        return rootView;
    }
}