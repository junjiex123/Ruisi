package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-19.
 * 首页第三页fragement 管理4个页面
 *
 */
public class FragementUser extends Fragment{

    @Bind(R.id.mytab)
    protected TabLayout mytab;
    private String uid = "";
    private Fragment curentFrag;
    private Fragment fragment1,fragment2,fragment3,fragment4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);

        mytab.addTab(mytab.newTab().setText("最新回复"));
        mytab.addTab(mytab.newTab().setText("我的主题"));
        mytab.addTab(mytab.newTab().setText("私人消息"));
        mytab.addTab(mytab.newTab().setText("我的收藏"));
        uid = PublicData.USER_UID;
        mytab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeFrageMent(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        changeFrageMent(0);
        return view;
    }

    private void changeFrageMent(int position){
        //回复我的
        String url0 ="home.php?mod=space&do=notice&mobile=2";
        //主题
        String url1 = "home.php?mod=space&uid="+uid+"&do=thread&view=me&mobile=2";
        //我的消息
        String url2 = "home.php?mod=space&do=pm&mobile=2";
        //我的收藏
        String url3 = "home.php?mod=space&uid="+uid+"&do=favorite&view=me&type=thread&mobile=2";
        if(curentFrag==null){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if(curentFrag ==null) {
                fragment1 = FrageReplyMe.newInstance(url0);
                curentFrag =fragment1;
                ft.replace(R.id.fragment_container_me, curentFrag).commit();
            }
            return;
        }

        switch (position){
            case 0:
                if(fragment1==null){
                    fragment1 = FrageReplyMe.newInstance(url0);
                }
                switchContent(fragment1);
                break;
            case 1:
                if(fragment2==null){
                    fragment2= FrageMyArticle.newInstance(url1);
                }
                switchContent(fragment2);
                break;
            case 2:
                if(fragment3==null){
                    fragment3 = FrageMessage.newInstance(url2);
                }
                switchContent(fragment3);
                break;
            case 3:
                if(fragment4==null){
                    fragment4= FrageMyStar.newInstance(url3);
                }
                switchContent(fragment4);
                break;
        }
    }

    /**
     * 当fragment进行切换时，采用隐藏与显示的方法加载fragment以防止数据的重复加载
     */
    public void switchContent(Fragment to) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (curentFrag != to) {
            if (!to.isAdded()) {    // 先判断是否被add过
                ft.hide(curentFrag).add(R.id.fragment_container_me, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(curentFrag).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            curentFrag = to;
        }
    }
}
