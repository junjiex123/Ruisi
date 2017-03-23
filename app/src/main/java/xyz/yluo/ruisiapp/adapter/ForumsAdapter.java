package xyz.yluo.ruisiapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.PostsActivity;
import xyz.yluo.ruisiapp.model.Category;
import xyz.yluo.ruisiapp.model.Forum;
import xyz.yluo.ruisiapp.model.ForumListData;
import xyz.yluo.ruisiapp.utils.DimmenUtils;
import xyz.yluo.ruisiapp.utils.RuisUtils;

/**
 * Created by free2 on 16-3-19.
 * 板块列表
 */
public class ForumsAdapter extends BaseAdapter {

    protected Context context;
    List<ForumListData> datas = new ArrayList<>();

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    public ForumsAdapter(Context context) {
        this.context = context;
        disableLoadMore();
        setIsenablePlaceHolder(false);
    }

    public void setDatas(List<Category> ds) {
        if (ds == null) {
            ds = new ArrayList<>();
        }
        for (Category c : ds) {
            datas.add(new ForumListData(true, c.name, c.gid));
            for (Forum f : c.forums) {
                datas.add(new ForumListData(false, f.name, f.fid));
            }
        }

        notifyDataSetChanged();
    }


    @Override
    protected int getDataCount() {
        return datas.size();
    }

    @Override
    protected int getItemType(int pos) {
        return datas.get(pos).isheader ? TYPE_HEADER : TYPE_NORMAL;
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
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
            head.setText(datas.get(position).title);
        }
    }

    private class ChildViewHolder extends BaseViewHolder {
        ImageView img;
        TextView title;
        int size = 42;

        ChildViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            title = (TextView) itemView.findViewById(R.id.title);
            size = DimmenUtils.dip2px(context, 42);
        }

        @Override
        void setData(final int position) {
            final ForumListData s = datas.get(position);
            title.setText(s.title);
            //todo
            //today_count.setVisibility(View.VISIBLE);
            //today_count.setText(s.todaynew);
            Drawable a = RuisUtils.getForunlogo(context, s.fid);
            if (a != null) {
                img.setImageDrawable(a);
            } else {
                img.setImageResource(R.drawable.image_placeholder);
            }
            itemView.setOnClickListener(view -> PostsActivity.open(context, s.fid, s.title));
        }
    }
}
