package xyz.yluo.ruisiapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.data.ForumListData;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;
import xyz.yluo.ruisiapp.utils.ImageUtils;

/**
 * Created by free2 on 16-3-19.
 * 板块列表
 */
public class ForumListAdapter extends BaseAdapter {

    protected Context context;
    private List<ForumListData> datas = null;

    public ForumListAdapter(List<ForumListData> dataSet, Context context,ListItemClickListener listener) {
        this.context = context;
        this.datas = dataSet;
        setItemListener(listener);
        disableLoadMore();
        setIsenablePlaceHolder(false);
    }

    @Override
    protected int getDataCount() {
        return datas.size();
    }

    @Override
    protected int getItemType(int pos) {
        if (datas.get(pos).isheader()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new HeadView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_h, parent, false));
        } else {
            return new ChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_n, parent, false));
        }
    }

    private class HeadView extends BaseViewHolder {

        TextView head;
        HeadView(View itemView) {
            super(itemView);
            head = (TextView) itemView.findViewById(R.id.header_title);
        }

        @Override
        void setData(int position) {
            head.setText(datas.get(position).getTitle());
        }
    }
    private class ChildViewHolder extends BaseViewHolder {

        ImageView img;
        TextView title;
        TextView today_count;
        View container;

        ChildViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            title = (TextView) itemView.findViewById(R.id.title);
            today_count = (TextView) itemView.findViewById(R.id.today_count);
            container = itemView.findViewById(R.id.forum_list_item);
        }

        @Override
        void setData(final int position) {
            final ForumListData single = datas.get(position);
            title.setText(single.getTitle());
            if (!single.getTodayNew().isEmpty()) {
                today_count.setVisibility(View.VISIBLE);
                today_count.setText(single.getTodayNew());
            } else {
                today_count.setVisibility(View.GONE);
            }
            int fid = single.getFid();
            Drawable dra = ImageUtils.getForunlogo(context, fid);
            img.setImageDrawable(dra);
        }
    }
}
