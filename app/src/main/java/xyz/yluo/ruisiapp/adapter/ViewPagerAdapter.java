package xyz.yluo.ruisiapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import xyz.yluo.ruisiapp.fragment.FrageForumList;
import xyz.yluo.ruisiapp.fragment.FrageFriends;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageMessage;
import xyz.yluo.ruisiapp.fragment.FrageNews;

/**
 * Created by free2 on 16-5-3.
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter{
    private String[] titles;
    private  Fragment fragment1,fragment2,fragment3;

    public ViewPagerAdapter(FragmentManager fm,String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(fragment1==null){
                    fragment1 = new FrageForumList();
                }
                return fragment1;
            case 1:
                if(fragment2==null){
                    fragment2 = new FrageHotNew();
                }
                return fragment2;
            case 2:
                if(fragment3==null){
                    fragment3 = new FrageNews();
                }
                return fragment3;
        }

        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
