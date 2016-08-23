package xyz.yluo.ruisiapp.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyToolBar;
import xyz.yluo.ruisiapp.activity.BaseActivity;
import xyz.yluo.ruisiapp.activity.HomeActivity;


/**
 * A simple {@link Fragment} subclass.
 */

public abstract class BaseFragment extends Fragment {
    protected View mRootView;
    protected MyToolBar toolBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null == mRootView){
            mRootView = inflater.inflate(getLayoutId(), container, false);
            toolBar = (MyToolBar) mRootView.findViewById(R.id.myToolBar);
            if(toolBar!=null){
                toolBar.setTitle(getTitle());

            }

        }
        return mRootView;
    }

    protected void setCloseIcon(){
        if(toolBar!=null){
            toolBar.setIcon(R.drawable.ic_clear_24dp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getActivity() instanceof HomeActivity){
                        getFragmentManager().popBackStack();
                    }
                }
            });
        }
    }

    protected abstract int getLayoutId();
    protected abstract String getTitle();

    protected boolean isLogin() {
        return App.ISLOGIN || ((BaseActivity) getActivity()).isLogin();
    }

    protected void setToolBarMenuClick(MyToolBar myToolBar) {
        ((BaseActivity) getActivity()).setToolBarMenuClick(myToolBar);
    }

    protected void switchActivity(Class<?> cls){
        getActivity().startActivity(new Intent(getActivity(),cls));
    }
}
