package xyz.yluo.ruisiapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.AboutActivity;
import xyz.yluo.ruisiapp.activity.FragementActivity;
import xyz.yluo.ruisiapp.activity.FriendActivity;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.SettingActivity;
import xyz.yluo.ruisiapp.activity.SignActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.model.FrageType;
import xyz.yluo.ruisiapp.utils.IntentUtils;
import xyz.yluo.ruisiapp.utils.UrlUtils;
import xyz.yluo.ruisiapp.widget.CircleImageView;

/**
 * TODO: 16-8-23  打开的时候检查是否签到显示在后面
 * 基础列表后面可以显示一些详情，如收藏的数目等...
 */
public class FragmentMy extends BaseLazyFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private String username, uid;
    private CircleImageView userAvater;
    private TextView userName, userGrade;
    //记录上次创建时候是否登录
    private boolean isLoginLast = false;

    private final int[] icons = new int[]{
            R.drawable.ic_autorenew_black_24dp,
            R.drawable.ic_info_24dp,
            R.drawable.ic_menu_share_24dp,
            R.drawable.ic_favorite_white_12dp,
            R.drawable.ic_settings_24dp,
    };

    private final String[] titles = new String[]{
            "签到中心",
            "关于本程序",
            "分享手机睿思",
            "到商店评分",
            "设置",
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        username = App.getName(getActivity());
        uid = App.getUid(getActivity());

        userAvater = (CircleImageView) mRootView.findViewById(R.id.user_img);
        userName = (TextView) mRootView.findViewById(R.id.user_name);
        userGrade = (TextView) mRootView.findViewById(R.id.user_grade);
        userAvater.setOnClickListener(this);
        mRootView.findViewById(R.id.history).setOnClickListener(this);
        mRootView.findViewById(R.id.star).setOnClickListener(this);
        mRootView.findViewById(R.id.friend).setOnClickListener(this);
        mRootView.findViewById(R.id.post).setOnClickListener(this);

        ListView listView = (ListView) mRootView.findViewById(R.id.function_list);
        List<Map<String, Object>> fs = new ArrayList<>();
        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> d = new HashMap<>();
            d.put("icon", icons[i]);
            d.put("title", titles[i]);
            fs.add(d);
        }
        listView.setOnItemClickListener(this);
        Log.d("onItemClick", "=====");
        listView.setAdapter(new SimpleAdapter(getActivity(), fs, R.layout.item_function,
                new String[]{"icon", "title"}, new int[]{R.id.icon, R.id.title}));
        return mRootView;
    }

    @Override
    public void onFirstUserVisible() {
        isLoginLast = App.ISLOGIN(getActivity());
        refreshAvaterView();
    }

    @Override
    public void onUserVisible() {
        if (isLoginLast != App.ISLOGIN(getActivity())) {
            isLoginLast = !isLoginLast;
            refreshAvaterView();
        }
    }

    @Override
    public void ScrollToTop() {
        //do noting
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    private void refreshAvaterView() {
        if (isLoginLast) {
            uid = App.getUid(getActivity());
            userName.setText(App.getName(getActivity()));
            userGrade.setVisibility(View.VISIBLE);
            userGrade.setText(App.getGrade(getActivity()));
            Picasso.with(getActivity()).load(UrlUtils.getAvaterurlm(uid))
                    .placeholder(R.drawable.image_placeholder).into(userAvater);
        } else {
            userName.setText("点击头像登陆");
            userGrade.setVisibility(View.GONE);
            userAvater.setImageResource(R.drawable.image_placeholder);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_img:
                if (App.ISLOGIN(getActivity())) {
                    UserDetailActivity.openWithAnimation(
                            getActivity(), username, userAvater, uid);
                } else {
                    switchActivity(LoginActivity.class);
                }
                break;
            case R.id.post:
                if (isLogin()) {
                    FragementActivity.open(getActivity(), FrageType.TOPIC);
                }
                break;
            case R.id.star:
                if (isLogin()) {
                    FragementActivity.open(getActivity(), FrageType.START);
                }
                break;
            case R.id.history:
                FragementActivity.open(getActivity(), FrageType.HISTORY);
                break;
            case R.id.friend:
                if (isLogin()) {
                    switchActivity(FriendActivity.class);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("onItemClick", "onItemClick" + position);
        switch (position) {
            case 0:
                if (isLogin()) {
                    if (App.IS_SCHOOL_NET) {
                        switchActivity(SignActivity.class);
                    } else {
                        Snackbar.make(mRootView, "校园网环境下才可以签到", Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            case 1:
                switchActivity(AboutActivity.class);
                break;
            case 2:
                String data = "这个手机睿思客户端非常不错，分享给你们。" +
                        "\n下载地址(校园网): http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=" + App.POST_TID +
                        "\n下载地址2(校外网): http://bbs.rs.xidian.me/forum.php?mod=viewthread&tid=" + App.POST_TID + "&mobile=2";
                IntentUtils.shareApp(getActivity(), data);
                break;
            case 3:
                if (!IntentUtils.openStore(getActivity())) {
                    Toast.makeText(getActivity(), "确保你的手机安装了相关应用商城", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
        }
    }
}
