package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.Home3ViewPagerAdapter;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class HomeFragement_3 extends Fragment {

    @Bind(R.id.mytab)
    protected TabLayout mytab;

    @Bind(R.id.viewpager)
    protected ViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_3, container, false);

        ButterKnife.bind(this,view);

        //监听tab事件 tab改变 使viewpager改变
        mytab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        String titles[] = new String[]{"个人信息", "我的帖子", "我的收藏"};
        Home3ViewPagerAdapter viewPagerAdapter = new Home3ViewPagerAdapter(getFragmentManager(), titles);
        viewPager.setAdapter(viewPagerAdapter);
        //设置Tab和ViewPager绑定
        mytab.setupWithViewPager(viewPager);

        return view;
    }
}
