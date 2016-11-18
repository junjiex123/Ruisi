package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyGuildView;
import xyz.yluo.ruisiapp.activity.PostActivity;
import xyz.yluo.ruisiapp.model.ArticleListData;
import xyz.yluo.ruisiapp.model.GalleryData;

/**
 * Created by free2 on 16-3-31.
 * 支持 gallery
 */
public class HotNewListAdapter extends BaseAdapter {
    private static final int TYPE_ARTICLE_LIST = 3;
    private static final int TYPE_ARTICLE_HEADER = 2;

    private List<ArticleListData> DataSet;
    private List<GalleryData> galleryDatas;
    private Activity activity;
    private int readcolor;

    public HotNewListAdapter(Activity activity, List<ArticleListData> DataSet, @Nullable List<GalleryData> galleryDatas) {
        this.DataSet = DataSet;
        this.activity = activity;
        this.galleryDatas = galleryDatas;
        readcolor = ContextCompat.getColor(activity, R.color.text_color_sec);
    }


    @Override
    protected int getDataCount() {
        int count = DataSet.size();
        if (galleryDatas.size() > 0) {
            count++;
        }
        return count;
    }

    @Override
    protected int getItemType(int position) {
        if (position == 0 && galleryDatas.size() > 0) {
            return TYPE_ARTICLE_HEADER;
        } else {
            return TYPE_ARTICLE_LIST;
        }
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ARTICLE_LIST:
                return new NormalViewHolderMe(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_me, parent, false));
            default:
                return new HeadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false));
        }
    }

    //手机版文章列表
    private class NormalViewHolderMe extends BaseViewHolder {
        TextView article_title;
        TextView author_name;
        TextView is_image;
        TextView reply_count;

        //构造
        NormalViewHolderMe(View v) {
            super(v);
            article_title = (TextView) v.findViewById(R.id.article_title);
            author_name = (TextView) v.findViewById(R.id.author_name);
            is_image = (TextView) v.findViewById(R.id.is_image);
            reply_count = (TextView) v.findViewById(R.id.reply_count);
            v.findViewById(R.id.main_item_btn_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnItemClick();
                }
            });
        }

        //设置listItem的数据
        @Override
        void setData(int position) {
            if (galleryDatas.size() > 0 && position > 0) {
                position--;
            }
            ArticleListData single = DataSet.get(position);
            int color = single.titleColor;
            article_title.setTextColor(single.isRead ? readcolor : color);
            article_title.setText(single.title);
            author_name.setText(single.author);
            reply_count.setText(single.replayCount);
            is_image.setVisibility(single.ishaveImage ? View.VISIBLE : View.GONE);
        }

        void onBtnItemClick() {
            int pos = getAdapterPosition();
            if (pos > 0 && galleryDatas.size() > 0) {
                pos--;
            }
            ArticleListData single_data = DataSet.get(pos);
            if (!single_data.isRead) {
                single_data.isRead = true;
                notifyItemChanged(getAdapterPosition());
            }
            PostActivity.open(activity, single_data.titleUrl, single_data.author);
        }
    }

    //图片切换view
    private class HeadViewHolder extends BaseViewHolder {
        private MyGuildView guildView;

        HeadViewHolder(View itemView) {
            super(itemView);
            guildView = (MyGuildView) itemView.findViewById(R.id.myGuideView);

        }

        @Override
        void setData(int position) {
            guildView.setData(galleryDatas);
            guildView.setListener((view, position1) -> {
                String titleUrl = galleryDatas.get(position1).getTitleUrl();
                if (!TextUtils.isEmpty(titleUrl)) {
                    PostActivity.open(activity, titleUrl, null);
                }
            });
        }
    }
}
