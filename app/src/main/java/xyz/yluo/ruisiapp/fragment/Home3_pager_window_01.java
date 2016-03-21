package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.yluo.ruisiapp.R;


//第一页预览页面
public class Home3_pager_window_01 extends Fragment {

    private static final String ARG_POSITION = "position";

    //存储数据 需要填充的列表
    //TODO 动态获取
    private int position;

    public static Home3_pager_window_01 newInstance(int position) {
        Home3_pager_window_01 f = new Home3_pager_window_01();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        position = getArguments().getInt(ARG_POSITION);
        View rootView = inflater.inflate(R.layout.layout_home3_pager_window_01, container, false);

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