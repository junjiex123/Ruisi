package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.Home3RecylerAdapter;
import xyz.yluo.ruisiapp.data.MyTopicReplyListData;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class HomeFragement_3 extends Fragment {

    @Bind(R.id.mytab)
    protected TabLayout mytab;
    @Bind(R.id.recycler_view)
    protected RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager ;
    private List<MyTopicReplyListData> datas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_3, container, false);

        ButterKnife.bind(this, view);
        layoutManager =new LinearLayoutManager(getActivity());
        Home3RecylerAdapter adapter = new Home3RecylerAdapter(getActivity(),datas);

        //int type, String title, String titleUrl, String author, String time, String froumName
        for(int i =0;i<20;i++){
            datas.add(new MyTopicReplyListData(0,"title","titleUrl","author","time","froumname"));
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mytab.addTab(mytab.newTab().setText("Tab 1"));
        mytab.addTab(mytab.newTab().setText("Tab 2"));
        mytab.addTab(mytab.newTab().setText("Tab 3"));

        mytab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeRecyclerViewData(mytab.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    private void changeRecyclerViewData(int position){
        switch (position){
            case 1:

                break;
            case 2:

                break;
            default:
        }
    }
}
