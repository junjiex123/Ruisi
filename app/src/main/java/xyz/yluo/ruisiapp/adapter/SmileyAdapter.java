package xyz.yluo.ruisiapp.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;

/**
 * Created by free2 on 16-5-1.
 * 表情adapter
 */
public class SmileyAdapter extends RecyclerView.Adapter<SmileyAdapter.SmileyViewHolder> {

    private List<Drawable> images = new ArrayList<>();
    private ListItemClickListener itemListener;

    public SmileyAdapter(ListItemClickListener itemListener, List<Drawable> images) {
        this.images = images;
        this.itemListener = itemListener;
    }


    @Override
    public SmileyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SmileyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_smiley, parent, false));
    }

    @Override
    public void onBindViewHolder(SmileyViewHolder holder, int position) {
        holder.setSmiley(position);
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    class SmileyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        SmileyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.smiley);
            image.setOnClickListener(view -> itemListener.onListItemClick(image, getAdapterPosition()));
        }


        private void setSmiley(int position) {
            image.setImageDrawable(images.get(position));
        }


    }


}
