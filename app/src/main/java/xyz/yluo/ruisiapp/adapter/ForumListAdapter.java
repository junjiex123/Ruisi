package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleList;
import xyz.yluo.ruisiapp.activity.ArticleListImage;
import xyz.yluo.ruisiapp.data.ForumListData;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.utils.ImageUtils;

/**
 * Created by free2 on 16-3-19.
 * 板块列表
 */
public class ForumListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    protected Context context;
    private List<ForumListData> datas = null;
    private RecyclerViewClickListener listener;

    public ForumListAdapter(List<ForumListData> dataSet, Context context,RecyclerViewClickListener listener) {
        this.context = context;
        this.datas = dataSet;
        this.listener = listener;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new HeadView(LayoutInflater.from(parent.getContext()).inflate(R.layout.forums_list_item_header, parent, false));
        } else {
            return new ChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.forums_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (datas.get(position).isheader()) {
            return 0;
        } else {
            return 1;
        }
    }

    protected class HeadView extends BaseViewHolder {

        TextView head;

        public HeadView(View itemView) {
            super(itemView);
            head = (TextView) itemView.findViewById(R.id.header_title);
        }

        @Override
        void setData(int position) {
            head.setText(datas.get(position).getTitle());
        }
    }


    protected class ChildViewHolder extends BaseViewHolder {

        ImageView img;
        TextView title;
        TextView today_count;
        View container;

        public ChildViewHolder(View itemView) {
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

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.recyclerViewListClicked(view,position);
                }
            });
        }
    }



}
