package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.View.MyHtmlView.HtmlView;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;
import xyz.yluo.ruisiapp.model.SingleArticleData;
import xyz.yluo.ruisiapp.model.SingleType;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-7.
 * 单篇文章adapter
 * 评论 文章 loadmore
 */

public class PostAdapter extends BaseAdapter {

    private static final int CONTENT = 0;
    private static final int COMENT = 1;
    private static final int HEADER = 3;


    //数据
    private List<SingleArticleData> datalist;
    private Activity activity;

    public PostAdapter(
            Activity activity, ListItemClickListener itemListener,
            List<SingleArticleData> datalist) {

        this.datalist = datalist;
        this.activity = activity;
        setItemListener(itemListener);
    }


    @Override
    protected int getDataCount() {
        return datalist.size();
    }

    @Override
    protected int getItemType(int pos) {
        if (datalist.get(pos).getType() == SingleType.CONTENT) {
            return CONTENT;
        } else if (datalist.get(pos).getType() == SingleType.HEADER) {
            return HEADER;
        } else {
            return COMENT;
        }
    }

    @Override
    protected BaseAdapter.BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CONTENT:
                return new ArticleContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false));
            case HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_h, parent, false));
            default: // TYPE_COMMENT
                return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
        }
    }


    //文章内容 楼主ViewHolder
    private class ArticleContentViewHolder extends BaseViewHolder {
        TextView article_title;
        CircleImageView article_user_image;
        TextView article_username;
        TextView article_post_time, tv_remove, tv_edit;
        // MyWebView myWebView;
        HtmlView htmlView;

        ArticleContentViewHolder(View itemView) {
            super(itemView);
            tv_remove = (TextView) itemView.findViewById(R.id.tv_remove);
            tv_edit = (TextView) itemView.findViewById(R.id.tv_edit);
            article_title = (TextView) itemView.findViewById(R.id.article_title);
            article_user_image = (CircleImageView) itemView.findViewById(R.id.article_user_image);
            article_username = (TextView) itemView.findViewById(R.id.article_username);
            article_post_time = (TextView) itemView.findViewById(R.id.article_post_time);
            htmlView = (HtmlView) itemView.findViewById(R.id.html_text);
            article_user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDetailActivity.openWithAnimation(
                            activity, datalist.get(0).getUsername(), article_user_image, datalist.get(0).getUid());
                }
            });

            tv_remove.setOnClickListener(this);
            tv_edit.setOnClickListener(this);
        }

        @Override
        void setData(int position) {
            final SingleArticleData single = datalist.get(position);
            article_title.setText(single.getTitle());
            article_username.setText(single.getUsername());
            String img_url = UrlUtils.getAvaterurlm(single.getImg());
            Picasso.with(activity).load(img_url).placeholder(R.drawable.image_placeholder).into(article_user_image);
            String post_time = "发表于:" + single.getPostTime();
            article_post_time.setText(post_time);
            htmlView.setHtmlText(single.getCotent(), true);
            //判断是不是自己
            if (App.ISLOGIN(activity) && App.getUid(activity).equals(single.getUid())) {
                tv_edit.setVisibility(View.VISIBLE);
                if (getItemCount() > 2) {
                    tv_remove.setVisibility(View.GONE);
                } else {
                    tv_remove.setVisibility(View.VISIBLE);
                }
            } else {
                tv_remove.setVisibility(View.GONE);
                tv_edit.setVisibility(View.GONE);
            }
        }
    }

    private class CommentViewHolder extends BaseViewHolder {
        ImageView replay_image;
        ImageView btn_reply_2;
        TextView replay_author;
        TextView replay_index;
        TextView replay_time;
        HtmlView htmlTextView;
        TextView bt_lable_lz, tv_remove, tv_edit;

        CommentViewHolder(View itemView) {
            super(itemView);
            tv_remove = (TextView) itemView.findViewById(R.id.tv_remove);
            tv_edit = (TextView) itemView.findViewById(R.id.tv_edit);
            replay_image = (ImageView) itemView.findViewById(R.id.article_user_image);
            btn_reply_2 = (ImageView) itemView.findViewById(R.id.btn_reply_2);
            replay_author = (TextView) itemView.findViewById(R.id.replay_author);
            replay_index = (TextView) itemView.findViewById(R.id.replay_index);
            replay_time = (TextView) itemView.findViewById(R.id.replay_time);
            htmlTextView = (HtmlView) itemView.findViewById(R.id.html_text);
            bt_lable_lz = (TextView) itemView.findViewById(R.id.bt_lable_lz);

            replay_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDetailActivity.openWithAnimation(
                            activity, datalist.get(getAdapterPosition()).getUsername(),
                            replay_image, datalist.get(getAdapterPosition()).getUid());
                }
            });

            tv_remove.setOnClickListener(this);
            tv_edit.setOnClickListener(this);

            btn_reply_2.setOnClickListener(this);
        }

        //设置listItem的数据
        @Override
        void setData(int position) {
            final SingleArticleData single = datalist.get(position);
            replay_author.setText(single.getUsername());
            //判断是不是楼主
            boolean islz = datalist.get(position).getUsername().equals(datalist.get(0).getUsername());
            bt_lable_lz.setVisibility(islz ? View.VISIBLE : View.GONE);
            boolean isreply = single.getReplyUrlTitle().contains("action=reply");
            btn_reply_2.setVisibility(isreply ? View.VISIBLE : View.GONE);
            String img_url = UrlUtils.getAvaterurlm(single.getImg());
            Picasso.with(activity).load(img_url).placeholder(R.drawable.image_placeholder).into(replay_image);
            replay_time.setText(single.getPostTime());
            replay_index.setText(single.getIndex());
            htmlTextView.setHtmlText(single.getCotent(), true);

            //判断是不是自己
            if (App.ISLOGIN(activity) && App.getUid(activity).equals(single.getUid())) {
                tv_remove.setVisibility(View.VISIBLE);
                tv_edit.setVisibility(View.VISIBLE);
            } else {
                tv_remove.setVisibility(View.GONE);
                tv_edit.setVisibility(View.GONE);
            }
        }

    }

    //header
    private class HeaderViewHolder extends BaseViewHolder {

        HeaderViewHolder(View itemView) {
            super(itemView);

        }

        @Override
        void setData(int position) {

        }
    }
}
