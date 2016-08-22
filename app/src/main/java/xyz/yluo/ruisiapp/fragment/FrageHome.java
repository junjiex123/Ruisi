package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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

    private final String[] titles = {"板块列表", "看帖", "教务新闻"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_home, container, false);
        ViewPager viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.mytab);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), titles);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(titles.length - 1);
        tabLayout.setupWithViewPager(viewpager);
        return view;
    }
}
