package xyz.yluo.ruisiapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by free2 on 16-4-7.
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    abstract void setData(int position);
}
