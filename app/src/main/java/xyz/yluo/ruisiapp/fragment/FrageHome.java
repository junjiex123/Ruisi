package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.ViewPagerAdapter;

/**
 * Created by free2 on 16-7-14.
 * 首页fragment
 */
public class FrageHome extends Fragment {

    private final String[] titles = {"板块", "看帖", "新闻"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e("FrageHome","onCreateView");

        View view = inflater.inflate(R.layout.fragement_home, container, false);
        ViewPager viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.mytab);

        //getChildFragmentManager() 解决 fragment嵌套viewpager 空白问题
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), titles);
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.setOffscreenPageLimit(titles.length - 1);
        tabLayout.setupWithViewPager(viewpager);
        return view;
    }
}
