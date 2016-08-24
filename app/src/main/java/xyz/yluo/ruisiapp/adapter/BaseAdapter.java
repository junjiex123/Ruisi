package xyz.yluo.ruisiapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.data.LoadMoreType;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;
import xyz.yluo.ruisiapp.utils.DimmenUtils;

/**
 * Created by yang on 16-8-23.
 * adapter 简单封装
 * //0 ---- placeholder
 //允许空 则最后一个就是
 //允许加载更多 不允许空则是最后一个否则倒数第二
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder>{

    private static final int TYPE_PLACE = 100;
    private static final int TYPE_LOADMORE =101;

    public static final int STATE_LOADING = 1;
    public static final int STATE_LOAD_FAIL = 2;
    public static final int STATE_LOAD_NOTHING = 3;


    boolean enableLoadMore = false;
    int loadeState = STATE_LOADING;

    private String placeHolderString = "加载中...";
    private String loadmoreString = "加载中...";
    private ListItemClickListener itemListener;

    @Override
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case TYPE_PLACE:
                return new PlaceHolderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_place_holder_item,parent,false));
            case TYPE_LOADMORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.load_more_item, parent, false));
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
            return TYPE_PLACE;
        }

        if(position==getItemCount()-1){
            if(enableLoadMore){
                return TYPE_LOADMORE;
            }
        }

        return getItemType(position);
    }

    @Override
    public final int getItemCount() {
        int count = getDataCount();
        if(count==0){
            //1 是placeholder
            return 1;
        }

        //center
        if(enableLoadMore){
            count++;
        }

        return count;
    }

    protected abstract int getDataCount();
    protected abstract int getItemType(int pos);
    protected abstract BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType);

    public void setLoadMoreEnable(boolean b){
        if(b!=enableLoadMore){
            enableLoadMore = b;
            if(b){
                //之前是false
                int i  = getItemCount()-1;
                if(i>0&&getItemViewType(i)!=TYPE_LOADMORE){
                    notifyItemInserted(i);
                }

            }else{
                //之前是开启状态
                //false 检查是否有 有则移除
                int i = getItemCount()-1;
                if(i>=0&&getItemViewType(i)==TYPE_LOADMORE){
                    notifyItemRemoved(i);
                }
            }
        }
    }

    public void setLoadMoreState(int i){
        if(enableLoadMore){
            this.loadeState = i;
            int ii = getItemCount()-1;
            if(ii>=0&&getItemViewType(ii)==TYPE_LOADMORE){
                notifyItemChanged(ii);
            }
        }
    }


    public void setPlaceHolderString(String text){
        placeHolderString = text;
        if(getDataCount()==0){
            notifyItemChanged(0);
        }
    }


    public void setItemListener(ListItemClickListener l){
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
            switch (loadeState){
                case STATE_LOAD_FAIL:
                    loadmore_progress.setVisibility(View.GONE);
                    loadmore_text.setText("加载失败");
                    break;
                case STATE_LOAD_NOTHING:
                    loadmore_progress.setVisibility(View.GONE);
                    loadmore_text.setText("暂无更多");
                    break;
                default:
                    loadmore_progress.setVisibility(View.VISIBLE);
                    loadmore_text.setText(loadmoreString);
                    break;
            }
        }
    }
    //placeholder ViewHolder
    private class PlaceHolderViewHolder extends BaseViewHolder {
        TextView textView;
        PlaceHolderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        void setData(int position) {
            textView.setText(placeHolderString);
        }
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public BaseViewHolder(View itemView) {
            super(itemView);
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
