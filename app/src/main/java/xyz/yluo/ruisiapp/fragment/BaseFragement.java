package xyz.yluo.ruisiapp.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;

/**
 * Created by free2 on 16-4-7.
 * 首页我 的基类
 * 另外几个个继承他 {@link FragementMyArticle} {@link FragementReplyMe} {@link FragementMyStar}
 */
public abstract class BaseFragement extends Fragment implements LoadMoreListener.OnLoadMoreListener{

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.main_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    protected int CurrentPage = 0;
    protected boolean isEnableLoadMore = true;
    protected  String url = "";
    protected int currentIndex = 0;
    protected boolean isHaveMore = true;
    public BaseFragement(String url) {
        this.url = url;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_list, container, false);
        ButterKnife.bind(this,view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
        initView();
        startGetdata();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CurrentPage =0;
                isHaveMore = true;
                isEnableLoadMore = true;
                refresh();
                startGetdata();
            }
        });

        recycler_view.addOnScrollListener(new LoadMoreListener((LinearLayoutManager) layoutManager, this,15));
        return view;
    }

    private void startGetdata(){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        getStringFromInternet(url);

    }

    private void getStringFromInternet(String url){

        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res= new String(response);
                finishGetData(res);
            }
            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onLoadMore() {
        if(currentIndex==1||currentIndex==3){
            if(isEnableLoadMore&&isHaveMore){
                int a = CurrentPage;
                String newurl = url+"&page="+(a+1);
                getStringFromInternet(newurl);
                System.out.println("===load more=="+newurl);
                isEnableLoadMore = false;
            }

        }

    }

    protected abstract void initView();
    protected abstract void finishGetData(String res);
    protected abstract void refresh();

}
