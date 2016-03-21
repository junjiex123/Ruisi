package xyz.yluo.ruisiapp.adapter;



import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import xyz.yluo.ruisiapp.fragment.Home3_pager_window_01;
import xyz.yluo.ruisiapp.fragment.Home3_pager_window_02;
import xyz.yluo.ruisiapp.fragment.Home3_pager_window_03;

/**
 * Created by yluo on 2015/10/5 0005.
 *
 */
public class Home3ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT;
    private String titles[] ;

    public Home3ViewPagerAdapter(FragmentManager fm, String[] titles2) {
        super(fm);
        titles=titles2;
        PAGE_COUNT =titles2.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            // Open FragmentTab1.java
            case 0:
                return Home3_pager_window_01.newInstance(position);
            case 1:
                return Home3_pager_window_02.newInstance(position);
            case 2:
                return Home3_pager_window_03.newInstance(position);
        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}