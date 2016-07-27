package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.MyGuildView;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.data.GalleryData;

/**
 * Created by free2 on 16-3-31.
 * 支持 gallery
 */
public class HotNewListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_ARTICLE_LIST = 3;
    private static final int TYPE_ARTICLE_HEADER = 2;

    private List<ArticleListData> DataSet;
    private List<GalleryData> galleryDatas;
    private Activity activity;

    public HotNewListAdapter(Activity activity, List<ArticleListData> DataSet, @Nullable List<GalleryData> galleryDatas) {
        this.DataSet = DataSet;
        this.activity = activity;
        this.galleryDatas = galleryDatas;
    }

    @Override
    public int getItemCount() {
        if (galleryDatas!=null&&galleryDatas.size() > 0) {
            if (DataSet.size() == 0) {
                return 1;
            }
            return DataSet.size() + 2;
        }
        if (DataSet.size() == 0) {
            return 0;
        }
        return DataSet.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (galleryDatas != null && position == 0 && galleryDatas.size() > 0) {
            return TYPE_ARTICLE_HEADER;
        } else if (position > 0 && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }else {
            return TYPE_ARTICLE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ARTICLE_LIST:
                return new NormalViewHolderMe(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item_me, parent, false));
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_item, parent, false));
            default:
                return new HeadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    //加载更多ViewHolder
    private class LoadMoreViewHolder extends BaseViewHolder {

        LoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void setData(int position) {
            //TODO
            //load more 现在没有数据填充
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
            ArticleListData single = DataSet.get(position);
            article_title.setTextColor(single.isRead() ? 0xff888888 : 0xff000000);
            article_title.setText(single.getTitle());
            author_name.setText(single.getAuthor());
            reply_count.setText(single.getReplayCount());
            is_image.setVisibility(single.ishaveImage() ? View.VISIBLE : View.GONE);
        }

        void onBtnItemClick() {
            ArticleListData single_data = DataSet.get(getAdapterPosition());
            if (!single_data.isRead()) {
                single_data.setRead(true);
                notifyItemChanged(getAdapterPosition());
            }
            SingleArticleActivity.open(activity, single_data.getTitleUrl(), single_data.getAuthor());
        }
    }

    //图片切换view
    private class HeadViewHolder extends BaseViewHolder{
        private MyGuildView guildView;
        public HeadViewHolder(View itemView) {
            super(itemView);
            guildView  = (MyGuildView) itemView.findViewById(R.id.myGuideView);

        }
        @Override
        void setData(int position) {
            guildView.setData(galleryDatas);
            guildView.setListener(new MyGuildView.OnItemClickListener() {
                @Override
                public void onBannerItemClick(View view, int position) {
                    String titleUrl = galleryDatas.get(position).getTitleUrl();
                    if(!TextUtils.isEmpty(titleUrl)){
                        SingleArticleActivity.open(activity,titleUrl,null);
                    }
                }
            });
        }
    }
}
