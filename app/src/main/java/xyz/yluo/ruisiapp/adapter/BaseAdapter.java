package xyz.yluo.ruisiapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;

/**
 * Created by yang on 16-8-23.
 * adapter 简单封装
 * //0 ---- placeholder
 //允许空 则最后一个就是
 //允许加载更多 不允许空则是最后一个否则倒数第二
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder>{

    private static final int TYPE_LOADMORE =101;

    public static final int STATE_LOADING = 1;
    public static final int STATE_LOAD_FAIL = 2;
    public static final int STATE_LOAD_NOTHING = 3;
    public static final int STATE_LOAD_OK = 4;
    public static final int STATE_NEED_LOGIN = 5;

    private int loadeState = STATE_LOADING;

    private ListItemClickListener itemListener;
    private boolean enableLoadMore = true;

    @Override
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case TYPE_LOADMORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item_load_more, parent, false));
            default:
                return getItemViewHolder(parent,viewType);
        }
    }

    @Override
    public final void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public final int getItemViewType(int position) {
        if(position==0&&getDataCount()==0){
            return TYPE_LOADMORE;
        }
        if(enableLoadMore&&position==getItemCount()-1){
            return TYPE_LOADMORE;
        }
        return getItemType(position);
    }


    @Override
    public final int getItemCount() {
        int count = getDataCount();
        if(count==0){
            //1 是placeholder
            return 1;
        }else{
            if(enableLoadMore){
                //++是有一个loadmore item
                count++;
            }
            return count;
        }
    }


    public void disableLoadMore(){
        if(enableLoadMore){
            enableLoadMore = false;
            //之前是开启状态
            //false 检查是否有 有则移除
            int i = getItemCount()-1;
            if(i>=0&&getItemViewType(i)==TYPE_LOADMORE){
                notifyItemRemoved(i);
            }
        }
    }

    protected abstract int getDataCount();
    protected abstract int getItemType(int pos);
    protected abstract BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType);

    //改变状态
    public void changeLoadMoreState(int i){
        this.loadeState = i;
        int ii = getItemCount()-1;
        if(ii>=0&&getItemViewType(ii)==TYPE_LOADMORE){
            notifyItemChanged(ii);
        }
    }

    void setItemListener(ListItemClickListener l){
        this.itemListener = l;
    }


    //加载更多ViewHolder
    private class LoadMoreViewHolder extends BaseViewHolder {
        TextView loadmore_text;
        ProgressBar loadmore_progress;
        View container;
        LoadMoreViewHolder(View itemView) {
            super(itemView);
            loadmore_progress = (ProgressBar) itemView.findViewById(R.id.load_more_progress);
            loadmore_text = (TextView) itemView.findViewById(R.id.load_more_text);
            container = itemView.findViewById(R.id.main_container);
        }

        @Override
        void setData(int position) {
            //不是第一次加载
            container.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            switch (loadeState){
                case STATE_LOAD_FAIL:
                    loadmore_progress.setVisibility(View.GONE);
                    loadmore_text.setText("加载失败");
                    break;
                case STATE_NEED_LOGIN:
                    loadmore_progress.setVisibility(View.GONE);
                    loadmore_text.setText("需要登录");
                    //没有数据填充第一次加载......
                    if(getDataCount()==0){
                        container.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    break;
                case STATE_LOAD_OK:
                    loadmore_text.setText("");
                    loadmore_progress.setVisibility(View.GONE);
                    break;
                case STATE_LOADING:
                    loadmore_progress.setVisibility(View.VISIBLE);
                    loadmore_text.setText("加载中...");
                    //没有数据填充第一次加载......
                    if(getDataCount()==0){
                        loadmore_progress.setVisibility(View.GONE);
                        container.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    break;
                default:
                    loadmore_progress.setVisibility(View.GONE);
                    loadmore_text.setText("暂无更多");
                    break;
            }
        }
    }


    abstract class BaseViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        BaseViewHolder(View itemView) {
            super(itemView);
            if(itemListener!=null)
                itemView.setOnClickListener(this);
        }

        void setData(int position){

        }

        @Override
        public void onClick(View view) {
            if(itemListener!=null){
                itemListener.onListItemClick(view, this.getAdapterPosition());
            }
        }
    }
}
