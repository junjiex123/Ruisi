package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyAlertDialog.MyAlertDialog;
import xyz.yluo.ruisiapp.adapter.BaseAdapter;
import xyz.yluo.ruisiapp.adapter.FriendAdapter;
import xyz.yluo.ruisiapp.data.FriendData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.httpUtil.TextResponseHandler;
import xyz.yluo.ruisiapp.listener.ListItemLongClickListener;
import xyz.yluo.ruisiapp.listener.LoadMoreListener;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-4-12.
 * 好友列表
 * 数据{@link FriendData}
 * adapter{@link FriendAdapter}
 * //todo 查找好友
 */
public class FrageFriends extends BaseFragment
        implements LoadMoreListener.OnLoadMoreListener,
        ListItemLongClickListener{
    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;
    private FriendAdapter adapter;
    private List<FriendData> datas;
    private int CurrentPage = 1;
    private boolean isEnableLoadMore = true;
    private boolean isHaveMore = true;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        initToolbar(true,"我的好友");
        recycler_view = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        datas = new ArrayList<>();
        adapter = new FriendAdapter(getActivity(),datas,this);
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(lm);
        recycler_view.addOnScrollListener(new LoadMoreListener(lm, this, 12));
        recycler_view.setAdapter(adapter);

        final String url = "home.php?mod=space&do=friend&mobile=2";

        refreshLayout.setEnabled(false);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        new GetDataTask().execute(url);

        return mRootView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_toolbar;
    }


    @Override
    public void onLoadMore() {
        //加载更多被电击
        if (isEnableLoadMore&&isHaveMore) {
            isEnableLoadMore = false;
            CurrentPage++;
            String url = "home.php?mod=space&do=friend&mobile=2"+"&page="+CurrentPage;
            new GetDataTask().execute(url);
        }
    }

    @Override
    public void onItemLongClick(View v, final int position) {
        new MyAlertDialog(getActivity(),MyAlertDialog.WARNING_TYPE)
                .setTitleText("删除好友")
                .setContentText("你要删除"+datas.get(position).getUserName()+"吗？")
                .setConfirmClickListener(new MyAlertDialog.OnConfirmClickListener() {
                    @Override
                    public void onClick(MyAlertDialog myAlertDialog) {
                        removeFriend(datas.get(position).getUid(),position);
                    }
                }).show();
    }


    private class GetDataTask extends AsyncTask<String, Void, List<FriendData>> {
        @Override
        protected List<FriendData> doInBackground(String... params) {
            final List<FriendData> temp = new ArrayList<>();
            HttpUtil.SyncGet(getActivity(), params[0], new TextResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Document document = Jsoup.parse(response);
                    Elements lists = document.select("#friend_ul").select("li");
                    if(lists.size()<=0){
                        isHaveMore =false;
                    }else{
                        for (Element element : lists) {
                            String imgurl = element.select(".avt").select("img").attr("src");
                            String lastOnline = element.select(".avt").select(".gol").attr("title");
                            String userName = element.select("h4").select("a[href^=home.php?mod=space&uid=]").text();
                            String uid = GetId.getid("uid=",imgurl);
                            String info = element.select("p.maxh").text();
                            //userName,imgUrl,info,uid,lastOnlineTime
                            temp.add(new FriendData(userName, imgurl, info, uid, lastOnline));
                        }
                    }

                }

                @Override
                public void onFailure(Throwable e) {
                    super.onFailure(e);
                }
            });
            return temp;
        }

        @Override
        protected void onPostExecute(List<FriendData> s) {
            super.onPostExecute(s);
            if(isHaveMore){
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOADING);
            }else{
                adapter.changeLoadMoreState(BaseAdapter.STATE_LOAD_NOTHING);
            }

            int i = datas.size();
            datas.addAll(s);
            if(i==0){
                adapter.notifyDataSetChanged();
            }else{
                adapter.notifyItemRangeInserted(i,s.size());
            }
            isEnableLoadMore = true;

            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            },500);
        }
    }


    private void removeFriend(String uid, final int pos){
        //操作成功
        String url = "home.php?mod=spacecp&ac=friend&op=ignore&uid="+uid+"&confirm=1";
        if(App.ISLOGIN(getActivity())){
            url = url+"&mobile=2";
        }
        HashMap<String,String> pa = new HashMap<>();
        pa.put("friendsubmit","true");
        HttpUtil.post(getActivity(), url, pa, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String s = new String(response);
                if(s.contains("操作成功")){
                    removeRes(true,pos);
                }else{
                    removeRes(false,pos);
                }
            }
            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
                removeRes(false,pos);
            }
        });
    }

    private void removeRes(boolean b,int pos){
        if(b){
            datas.remove(pos);
            adapter.notifyItemRemoved(pos);
            Snackbar.make(refreshLayout,"删除好友成功！",Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(refreshLayout,"删除好友失败！",Snackbar.LENGTH_SHORT).show();
        }
    }

}
