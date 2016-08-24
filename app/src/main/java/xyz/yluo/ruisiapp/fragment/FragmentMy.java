package xyz.yluo.ruisiapp.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.activity.AboutActivity;
import xyz.yluo.ruisiapp.activity.LoginActivity;
import xyz.yluo.ruisiapp.activity.UserDakaActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 *  TODO: 16-8-23  打开的时候检查是否签到显示在后面
 *  基础列表后面可以显示一些详情，如收藏的数目等...
 */
public class FragmentMy extends BaseFragment implements View.OnClickListener{

    private String username,uid;
    private CircleImageView user_img;
    private TextView user_name,user_grade;
    //记录上次创建时候是否登录
    private boolean isLoginLast = false;
    private LinearLayout containerlist;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isLoginLast = App.ISLOGIN(getActivity());
        super.onCreateView(inflater,container,savedInstanceState);
        username = App.getName(getActivity());
        uid = App.getUid(getActivity());

        user_img = (CircleImageView) mRootView.findViewById(R.id.user_img);
        user_name = (TextView) mRootView.findViewById(R.id.user_name);
        user_grade = (TextView) mRootView.findViewById(R.id.user_grade);
        user_img.setOnClickListener(this);
        mRootView.findViewById(R.id.setting).setOnClickListener(this);
        containerlist = (LinearLayout) mRootView.findViewById(R.id.container);
        for(int i =0;i<containerlist.getChildCount();i++){
            View ii = containerlist.getChildAt(i);
            if(ii instanceof LinearLayout){
                ii.setOnClickListener(this);
            }
        }
        freshView();
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if((isLoginLast!=App.ISLOGIN(getActivity()))){
            isLoginLast = !isLoginLast;
            freshView();
        }
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden&&(isLoginLast!=App.ISLOGIN(getActivity()))){
            isLoginLast = !isLoginLast;
            freshView();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    private void freshView(){
        if(isLoginLast){
            user_name.setText(username);
            user_grade.setVisibility(View.VISIBLE);
            user_grade.setText(App.getGrade(getActivity()));
            Picasso.with(getActivity()).load(UrlUtils.getAvaterurlm(uid))
                    .placeholder(R.drawable.image_placeholder).into(user_img);
        }else{
            user_name.setText("点击头像登陆");
            user_grade.setVisibility(View.GONE);
            user_img.setImageResource(R.drawable.image_placeholder);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_img:
                if(App.ISLOGIN(getActivity())){
                    UserDetailActivity.openWithAnimation(
                            getActivity(),username,user_img,uid);
                }else{
                    switchActivity(LoginActivity.class);
                }
                break;
            case R.id.about:
                switchActivity(AboutActivity.class);
                break;
            case R.id.sign:
                if (App.IS_SCHOOL_NET&&isLogin()) {
                    switchActivity(UserDakaActivity.class);
                } else {
                    Snackbar.make(containerlist,"校园网环境下才可以签到",Snackbar.LENGTH_SHORT).show();
                }
                break;
            /**
            case R.id.setting:
                //changeFragement(FrageType.SETTING);
                //// TODO: 16-8-23
                break;
            case R.id.message:
                changeFragement(FrageType.MESSAGE);
                messageHandler.sendEmptyMessage(0);
                break;
            case R.id.post:
                if (isLogin()) {
                    changeFragement(FrageType.TOPIC);
                }
                break;
            case R.id.star:
                if (isLogin()) {
                    changeFragement(FrageType.START);
                }
                break;
            case R.id.history:
                if (isLogin()) {
                    changeFragement(FrageType.HISTORY);
                }
                break;
            case R.id.help:
                changeFragement(FrageType.HELP);
                break;
            case R.id.friend:
                changeFragement(FrageType.FRIEND);
                break;
             */
        }
    }
}
