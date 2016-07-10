package xyz.yluo.ruisiapp.listener;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 *监听recyclerview 页数变化回掉函数
 */

public class RecyclerPageChangeListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;
    private OnPageChange onPageChangeListener;

    public RecyclerPageChangeListener(@NonNull LinearLayoutManager linearLayoutManager, @NonNull OnPageChange listener) {
        super();
        this.linearLayoutManager = linearLayoutManager;
        this.onPageChangeListener = listener;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        onPageChangeListener.onPageChange(linearLayoutManager.findLastVisibleItemPosition());
    }

    public interface OnPageChange {
        void onPageChange(int page);
    }

}
