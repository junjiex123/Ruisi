package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import xyz.yluo.ruisiapp.App;
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
public class PostListAdapter extends BaseAdapter {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_NORMAL_MOBILE = 1;
    public static final int TYPE_IMAGE = 2;

    //数据
    private List<ArticleListData> DataSet;
    private int type = 3;

    //上下文
    private Activity activity;

    public PostListAdapter(Activity activity, List<ArticleListData> data, int type) {
        DataSet = data;
        this.activity = activity;
        this.type = type;
    }


    @Override
    protected int getDataCount() {
        return DataSet.size();
    }

    @Override
    protected int getItemType(int pos) {
        //手机版
        if (!App.IS_SCHOOL_NET || type == TYPE_NORMAL_MOBILE) {
            return TYPE_NORMAL_MOBILE;
        } else if(type==TYPE_IMAGE){
            //一般板块
            return TYPE_IMAGE;
        }else{
            return TYPE_NORMAL;
        }
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NORMAL_MOBILE:
                return new NormalViewHolderMe(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.main_list_item_me, parent, false));
            case TYPE_IMAGE:
                return new ImageCardViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.image_list_item, parent, false));
            default: // TYPE_NORMAL
                return new NormalViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.main_list_item, parent, false));
        }
    }


    //校园网环境 帖子列表
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
            int readcolor = ContextCompat.getColor(activity,R.color.text_color_sec);
            article_title.setTextColor(single.isRead()?readcolor :color);

            article_title.setText(single.getTitle());
            author_name.setText(single.getAuthor());
            reply_count.setText(single.getReplayCount());
        }

        void onBtnAvatarClick() {
            String imageUrl = UrlUtils.getAvaterurlb(DataSet.get(getAdapterPosition()).getAuthorUrl());
            UserDetailActivity.openWithAnimation(
                    activity, DataSet.get(getAdapterPosition()).getAuthor(), author_img, imageUrl);
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

    //校园网环境 图片板块ViewHolder
    private  class ImageCardViewHolder extends BaseViewHolder {

        ImageView img_card_image;
        TextView img_card_title;
        TextView img_card_author;
        TextView img_card_like;

        ImageCardViewHolder(View itemView) {
            super(itemView);
            img_card_image = (ImageView) itemView.findViewById(R.id.img_card_image);
            img_card_title = (TextView) itemView.findViewById(R.id.img_card_title);
            img_card_author = (TextView) itemView.findViewById(R.id.img_card_author);
            img_card_like = (TextView) itemView.findViewById(R.id.img_card_like);

            itemView.findViewById(R.id.card_list_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_click();
                }
            });
        }

        void setData(int position) {
            img_card_author.setText(DataSet.get(position).getAuthor());
            img_card_title.setText(DataSet.get(position).getTitle());
            img_card_like.setText(DataSet.get(position).getReplayCount());
            if (!Objects.equals(DataSet.get(position).getImage(), "")) {
                Picasso.with(activity).load(App.getBaseUrl() + DataSet.get(position).getImage()).placeholder(R.drawable.image_placeholder).into(img_card_image);
            } else {
                img_card_image.setImageResource(R.drawable.image_placeholder);
            }

        }
        void item_click() {
            ArticleListData single_data = DataSet.get(getAdapterPosition());
            SingleArticleActivity.open(activity, single_data.getTitleUrl(), single_data.getAuthor());
        }
    }

}
