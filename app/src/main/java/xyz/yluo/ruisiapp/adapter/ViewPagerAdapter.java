package xyz.yluo.ruisiapp.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import xyz.yluo.ruisiapp.fragment.FrageForumList;
import xyz.yluo.ruisiapp.fragment.FrageHotNew;
import xyz.yluo.ruisiapp.fragment.FrageNews;

/**
 * Created by free2 on 16-5-3.
 *
 */
public class ViewPagerAdapter extends PagerAdapter {
    private String[] titles;
    private Fragment fragment1, fragment2, fragment3,current;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    public ViewPagerAdapter(FragmentManager fm, String[] titles) {
        this.titles = titles;
        mFragmentManager = fm;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), position);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), position));
        }
        if (fragment != current) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != current) {
            if (current != null) {
                current.setMenuVisibility(false);
                current.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            current = fragment;
        }
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }


    private Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (fragment1 == null) {
                    fragment1 = FrageForumList.newInstance(true);
                }
                return fragment1;
            case 1:
                if (fragment2 == null) {
                    fragment2 = FrageHotNew.newInstance(0);
                }
                return fragment2;
            case 2:
                if (fragment3 == null) {
                    fragment3 = new FrageNews();
                }
                return fragment3;
        }

        return null;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment)object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            Log.d("finish","finishUpdate");
            //commitNowAllowingStateLoss
            mCurTransaction.commit();
            mCurTransaction = null;
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
