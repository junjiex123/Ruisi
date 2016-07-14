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
 *
 */
public class FrageHome extends Fragment{

    private final String[] titles = {"板块","看帖","新闻"};
    private ViewPager viewpager;
    private TabLayout tabLayout;


    public FrageHome() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_home, container, false);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.mytab);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager(), titles);
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.setOffscreenPageLimit(titles.length-1);
        tabLayout.setupWithViewPager(viewpager);


        return view;
    }
}
