package xyz.yluo.ruisiapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;
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
    private static final int TYPE_EMPTY = 102;

    boolean enableEmpty = false;
    boolean enableLoadMore = false;

    private int height = 0;
    protected String placeHolderString = "加载中...";
    protected String loadmoreString = "加载中...";

    @Override
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case TYPE_PLACE:
                return new PlaceHolderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_place_holder_item,parent,false));
            case TYPE_EMPTY:
                View view =new View(parent.getContext());
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        DimmenUtils.dip2px(parent.getContext(),height)));
                return new EmptyViewHolder(view);
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
            if(enableEmpty){
                return TYPE_EMPTY;
            }else if(enableLoadMore){
                return TYPE_LOADMORE;
            }
        }else if(position==getItemCount()-2){
            if(enableLoadMore&& enableEmpty){
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


        //end
        if(enableEmpty){
            count++;
        }

        return count;
    }

    protected abstract int getDataCount();
    protected abstract int getItemType(int pos);
    protected abstract BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType);


    public void enableEmptyHolder(int height){
        enableEmpty = true;
        this.height = height;
    }

    public void setLoadMoreEnable(boolean b,String s){
        if(b){
            enableLoadMore = true;
            loadmoreString = s;
        }else{
            if(enableLoadMore){
                int i = getItemCount()-1;
                if(enableEmpty){
                    i--;
                }
                if(i>=0){
                    notifyItemRemoved(i);
                }
            }
        }
    }

    public void setPlaceHolderString(String text){
        placeHolderString = text;
        if(getDataCount()==0){
            notifyItemChanged(0);
        }
    }


    //加载更多ViewHolder
    private class LoadMoreViewHolder extends BaseViewHolder {

        LoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void setData(int position) {
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

    private class EmptyViewHolder extends BaseViewHolder{

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void setData(int position) {

        }
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        abstract void setData(int position);
    }
}
