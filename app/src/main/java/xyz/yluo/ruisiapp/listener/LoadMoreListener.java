package xyz.yluo.ruisiapp.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class LoadMoreListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;
    private OnLoadMoreListener onLoadMoreListener;
    private int limit = 9;

    public LoadMoreListener(@NonNull LinearLayoutManager linearLayoutManager, @NonNull OnLoadMoreListener onLoadMoreListener,int limit) {
        super();
        this.linearLayoutManager = linearLayoutManager;
        this.onLoadMoreListener = onLoadMoreListener;
        this.limit = limit;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //-1最后  -2 倒数第二
        if (linearLayoutManager.findLastVisibleItemPosition()>limit&&linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.getItemCount() - 1) {

                // 向下滑动，判断最后一个item是不是显示中
            //最后一个体验不好
            //向前移几个
                onLoadMoreListener.onLoadMore();
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
