package xyz.yluo.ruisiapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.activity.ActivitySearch;
import xyz.yluo.ruisiapp.activity.HomeActivity;
import xyz.yluo.ruisiapp.activity.NewArticleActivity;
import xyz.yluo.ruisiapp.adapter.ViewPagerAdapter;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-7-14.
 * 首页fragment
 */
public class FrageHome extends BaseFragment implements MyToolBar.OnToolBarItemClick{

    private final String[] titles = {"板块列表", "看帖", "教务新闻"};
    //新消息小红点
    private View message_bage;

    @Override
    protected int getLayoutId() {
        return R.layout.fragement_home;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.app_name);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        MyToolBar myToolBar = (MyToolBar)mRootView.findViewById(R.id.myToolBar);
        ViewPager viewpager = (ViewPager) mRootView.findViewById(R.id.viewpager);
        final TabLayout tabLayout = (TabLayout) mRootView.findViewById(R.id.mytab);
        myToolBar.setIcon(R.drawable.ic_menu_24dp);
        myToolBar.addMenu(R.drawable.ic_search_white_24dp,"SEARCH");
        myToolBar.addMenu(R.drawable.ic_edit,"POST");
        View imgContainer = LayoutInflater.from(getActivity()).inflate(R.layout.user_img_with_meessage,null,false);
        myToolBar.addView(imgContainer,"USERIMG");
        CircleImageView userImage = (CircleImageView) imgContainer.findViewById(R.id.toolbar_user_image);
        message_bage = imgContainer.findViewById(R.id.toolbar_message_bage);
        message_bage.setVisibility(View.INVISIBLE);

        if(App.ISLOGIN&& !TextUtils.isEmpty(App.USER_NAME)) {
            myToolBar.setTitle(App.USER_NAME);
            String url = UrlUtils.getAvaterurlm(App.USER_UID);
            Picasso.with(getActivity()).load(url).placeholder(R.drawable.image_placeholder).into(userImage);
        }else{
            userImage.setImageResource(R.drawable.image_placeholder);
        }
        myToolBar.setToolBarClickListener(this);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), titles);
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(titles.length - 1);
        tabLayout.setupWithViewPager(viewpager);
        return mRootView;
    }


    @Override
    public void OnItemClick(View v, String Tag) {
        switch (Tag){
            case "POST":
                if(isLogin())
                switchActivity(NewArticleActivity.class);
                break;
            case "SEARCH":
                if(isLogin())
                switchActivity(ActivitySearch.class);
                break;
            case "USERIMG":
            case "NAVIGATION":
                ((HomeActivity)getActivity()).opemDraw();
                break;
        }
    }
}
