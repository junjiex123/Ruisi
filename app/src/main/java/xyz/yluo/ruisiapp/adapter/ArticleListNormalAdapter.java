package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.yluo.ruisiapp.Config;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-5.
 * 一般文章列表adapter分校园网和外网
 */
public class ArticleListNormalAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOAD_MORE = 1;
    //校外网 手机版
    private static final int TYPE_NORMAL_MOBILE = 3;
    //数据
    private List<ArticleListData> DataSet;
    private int type = 3;

    //上下文
    private Activity activity;

    public ArticleListNormalAdapter(Activity activity, List<ArticleListData> data, int type) {
        DataSet = data;
        this.activity = activity;
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {

        if (position > 0 && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }
        //手机版
        if (!Config.IS_SCHOOL_NET || type == TYPE_NORMAL_MOBILE) {
            return TYPE_NORMAL_MOBILE;
        } else {
            //一般板块
            return TYPE_NORMAL;
        }
    }

    //设置view
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_NORMAL_MOBILE:
                return new NormalViewHolderMe(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_list_item_me, viewGroup, false));
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.load_more_item, viewGroup, false));
            default: // TYPE_NORMAL
                return new NormalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_list_item, viewGroup, false));
        }
    }

    //设置data
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        if (DataSet.size() == 0) {
            return 0;
        }
        return DataSet.size() + 1;
    }

    private class NormalViewHolder extends BaseViewHolder {
        protected TextView article_title;
        protected TextView post_time;
        TextView article_type;
        CircleImageView author_img;
        TextView author_name;
        TextView reply_count;
        TextView view_count;

        //构造
        NormalViewHolder(View v) {
            super(v);
            article_type = (TextView) v.findViewById(R.id.article_type);
            article_title = (TextView) v.findViewById(R.id.article_title);
            author_img = (CircleImageView) v.findViewById(R.id.author_img);
            author_name = (TextView) v.findViewById(R.id.author_name);
            post_time = (TextView) v.findViewById(R.id.post_time);
            reply_count = (TextView) v.findViewById(R.id.reply_count);
            view_count = (TextView) v.findViewById(R.id.view_count);

            author_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnAvatarClick();
                }
            });

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
            String type = single.getType();
            if (!type.equals("normal")) {
                article_type.setText(type);
                article_type.setVisibility(View.VISIBLE);
            } else {
                article_type.setVisibility(View.GONE);
            }
            String postTime = "发表于:" + single.getPostTime();
            post_time.setText(postTime);
            view_count.setText(single.getViewCount());

            String imageUrl = UrlUtils.getAvaterurlm(single.getAuthorUrl());
            Picasso.with(activity).load(imageUrl).placeholder(R.drawable.image_placeholder).into(author_img);

            int color = single.getTitleColor();
            article_title.setTextColor(single.isRead()?0xff888888 :color);

            article_title.setText(single.getTitle());
            author_name.setText(single.getAuthor());
            reply_count.setText(single.getReplayCount());
        }

        void onBtnAvatarClick() {
            String imageUrl = UrlUtils.getAvaterurlb(DataSet.get(getAdapterPosition()).getAuthorUrl());
            UserDetailActivity.openWithTransitionAnimation(activity, DataSet.get(getAdapterPosition()).getAuthor(), author_img, imageUrl);
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

    //加载更多ViewHolder
    private class LoadMoreViewHolder extends BaseViewHolder {

        LoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void setData(int position) {
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
            int color = single.getTitleColor();
            article_title.setTextColor(single.isRead()?0xff888888 :color);
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
