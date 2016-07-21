package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.data.GalleryData;
import xyz.yluo.ruisiapp.listener.RecyclerPageChangeListener;

/**
 * Created by free2 on 16-3-31.
 * 支持 gallery
 */
public class HotNewListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int TYPE_HEADER_GALLERY = 0;
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_ARTICLE_LIST = 3;
    private List<GalleryData> DataSet_gallery;
    private List<ArticleListData> DataSet;
    private GalleryAdapter galleryAdapter;
    private Activity activity;

    public HotNewListAdapter(Activity activity, List<GalleryData> dataSetg, List<ArticleListData> DataSet) {
        DataSet_gallery = dataSetg;
        this.DataSet = DataSet;
        this.activity = activity;

        galleryAdapter = new GalleryAdapter(activity, DataSet_gallery);
    }

    @Override
    public int getItemCount() {

        if (DataSet_gallery.size() > 0) {
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
        if (DataSet_gallery != null && position == 0 && DataSet_gallery.size() > 0) {
            return TYPE_HEADER_GALLERY;
        } else if (position > 0 && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        } else {
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
            default: // TYPE_HEADER_GALLERY
                return new GalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_hot_gallery_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    //Gallery viewHolder
    private class GalleryViewHolder extends BaseViewHolder implements RecyclerPageChangeListener.OnPageChange {
        private RecyclerView recyclerGallery;
        private TextView pageInfo;

        GalleryViewHolder(View itemView) {
            super(itemView);
            recyclerGallery = (RecyclerView) itemView.findViewById(R.id.recycler_view_gallery);
            pageInfo = (TextView) itemView.findViewById(R.id.gallery_page_info);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true);
            recyclerGallery.setLayoutManager(linearLayoutManager);
            recyclerGallery.addOnScrollListener(new RecyclerPageChangeListener(linearLayoutManager, this));
            recyclerGallery.setAdapter(galleryAdapter);
        }

        void setData(int position) {
            String txt = getAdapterPosition() % DataSet.size() + 1 + "/" + DataSet.size();
            pageInfo.setText(txt);
        }

        @Override
        public void onPageChange(int page) {
            String txt = page % DataSet.size() + 1 + "/" + DataSet.size();
            pageInfo.setText(txt);
        }
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
}
