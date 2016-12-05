package xyz.yluo.ruisiapp.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import xyz.yluo.ruisiapp.fragment.FrageForumList;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageMessage;
import xyz.yluo.ruisiapp.fragment.FragmentMy;

public class MainPageAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments;

    public MainPageAdapter(FragmentManager fm) {
        super(fm);
        fragments = new Fragment[4];
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            Fragment to = null;
            switch (position) {
                case 0:
                    to = new FrageForumList();
                    break;
                case 1:
                    to = new FrageHotNew();
                    break;
                case 2:
                    to = new FrageMessage();
                    break;
                case 3:
                    to = new FragmentMy();
                    break;
            }

            fragments[position] = to;
        }

        return fragments[position];
    }

    @Override
    public int getCount() {
        return 4;
    }
}
