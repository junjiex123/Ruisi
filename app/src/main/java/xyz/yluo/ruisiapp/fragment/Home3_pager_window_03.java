package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;


//第一页预览页面
public class Home3_pager_window_03 extends Fragment {

    @Bind(R.id.refresh_view)
    protected SwipeRefreshLayout refresh_view;
    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;

    private static final String ARG_POSITION = "position";

    public static Home3_pager_window_03 newInstance(int position) {
        Home3_pager_window_03 f = new Home3_pager_window_03();
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

//        //recylerView 用来替代listView
//        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
//        //可以设置不同样式
//        mLayoutManager = new LinearLayoutManager(getContext());
//        //第二个参数是列数
//        //mLayoutManager = new GridLayoutManager(getContext(),2);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        //TODO
//        //以后实现，现在是静态的
//        dataset = getDtaSet(30);
//        mRecyleAdapter = new recyleAdapter(dataset);
//        // Set MyRecyleAdapter as the adapter for RecyclerView.
//        mRecyclerView.setAdapter(mRecyleAdapter);
//        //设置Item增加、移除动画
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        // END_INCLUDE(initializeRecyclerView)
        switch (position) {
            case 0:
                break;
        }

        return rootView;
    }
}