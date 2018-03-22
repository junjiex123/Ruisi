package me.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.activity.UserDetailActivity;
import me.yluo.ruisiapp.listener.ListItemClickListener;
import me.yluo.ruisiapp.model.SingleArticleData;
import me.yluo.ruisiapp.model.SingleType;
import me.yluo.ruisiapp.utils.DimmenUtils;
import me.yluo.ruisiapp.utils.UrlUtils;
import me.yluo.ruisiapp.widget.CircleImageView;
import me.yluo.ruisiapp.widget.htmlview.HtmlView;

/**
 * Created by free2 on 16-3-7.
 * 单篇文章adapter
 * 评论 文章 loadmore
 */

public class PostAdapter extends BaseAdapter {

    private static final int CONTENT = 0;
    private static final int COMENT = 1;
    private static final int HEADER = 3;
    private int size = 0;


    //数据
    private List<SingleArticleData> datalist;
    private Activity activity;

    public PostAdapter(
            Activity activity, ListItemClickListener itemListener,
            List<SingleArticleData> datalist) {

        this.datalist = datalist;
        this.activity = activity;
        size = DimmenUtils.dip2px(activity, 42);
        setItemListener(itemListener);
    }


    @Override
    protected int getDataCount() {
        return datalist.size();
    }

    @Override
    protected int getItemType(int pos) {
        if (datalist.get(pos).type == SingleType.CONTENT) {
            return CONTENT;
        } else if (datalist.get(pos).type == SingleType.HEADER) {
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
        CircleImageView userAvatar;
        TextView title, postTime, userName, content, btnRemove, btnEdit;
        TextView btnClose, btnBlock, btnWarn;

        ArticleContentViewHolder(View itemView) {
            super(itemView);
            btnRemove = itemView.findViewById(R.id.tv_remove);
            btnEdit = itemView.findViewById(R.id.tv_edit);
            title = itemView.findViewById(R.id.article_title);
            userAvatar = itemView.findViewById(R.id.article_user_image);
            userName = itemView.findViewById(R.id.article_username);
            postTime = itemView.findViewById(R.id.article_post_time);
            content = itemView.findViewById(R.id.content);


            btnBlock = itemView.findViewById(R.id.tv_block);
            btnClose = itemView.findViewById(R.id.tv_close);
            btnWarn = itemView.findViewById(R.id.tv_warn);

            userAvatar.setOnClickListener(v -> UserDetailActivity.openWithAnimation(
                    activity, datalist.get(0).username, userAvatar, datalist.get(0).uid));


            btnRemove.setOnClickListener(this);
            btnEdit.setOnClickListener(this);

            btnBlock.setOnClickListener(this);
            btnClose.setOnClickListener(this);
            btnWarn.setOnClickListener(this);
        }

        @Override
        void setData(int position) {
            final SingleArticleData single = datalist.get(position);
            title.setText(single.title);
            userName.setText(single.username);
            String img_url = UrlUtils.getAvaterurlm(single.getImg());
            Picasso.with(activity)
                    .load(img_url)
                    .resize(size, size)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(userAvatar);
            String post_time = "发表于:" + single.postTime;
            postTime.setText(post_time);
            HtmlView.parseHtml(single.content).into(content);

            //判断是不是自己
            if (App.ISLOGIN(activity) && App.getUid(activity).equals(single.uid)) {
                btnEdit.setVisibility(View.VISIBLE);
            } else {
                // 只能使用INVISIBLE，请勿设置成GONE，否则会对管理界面UI产生一定影响
                btnEdit.setVisibility(View.INVISIBLE);
            }

            //如果有管理权限，则显示全部按钮
            if (single.canManage) {
                btnEdit.setVisibility(View.VISIBLE);
                btnWarn.setVisibility(View.VISIBLE);
                btnBlock.setVisibility(View.VISIBLE);
                btnClose.setVisibility(View.VISIBLE);
                btnRemove.setVisibility(View.VISIBLE);
            }
        }
    }

    private class CommentViewHolder extends BaseViewHolder {
        ImageView avatar;
        TextView username, index, replyTime, comment, btnRemove, btnEdit, labelLz;
        View btnReplyCz;
        TextView btnClose, btnBlock, btnWarn;

        CommentViewHolder(View itemView) {
            super(itemView);
            btnRemove = itemView.findViewById(R.id.tv_remove);
            btnEdit = itemView.findViewById(R.id.tv_edit);
            avatar = itemView.findViewById(R.id.article_user_image);
            btnReplyCz = itemView.findViewById(R.id.btn_reply_cz);
            username = itemView.findViewById(R.id.replay_author);
            index = itemView.findViewById(R.id.replay_index);
            replyTime = itemView.findViewById(R.id.replay_time);
            comment = itemView.findViewById(R.id.html_text);
            labelLz = itemView.findViewById(R.id.bt_lable_lz);

            btnBlock = itemView.findViewById(R.id.tv_block);
            btnClose = itemView.findViewById(R.id.tv_close);
            btnWarn = itemView.findViewById(R.id.tv_warn);

            comment.setOnLongClickListener(view -> {
                String user = datalist.get(getAdapterPosition()).username;
                String content = comment.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText(null, content));
                Toast.makeText(activity, "已复制" + user + "的评论", Toast.LENGTH_SHORT).show();
                return true;
            });

            avatar.setOnClickListener(v -> UserDetailActivity.openWithAnimation(
                    activity, datalist.get(getAdapterPosition()).username,
                    avatar, datalist.get(getAdapterPosition()).uid));

            btnRemove.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
            btnReplyCz.setOnClickListener(this);

            btnBlock.setOnClickListener(this);
            btnClose.setOnClickListener(this);
            btnWarn.setOnClickListener(this);
        }

        //设置listItem的数据
        @Override
        void setData(int position) {
            final SingleArticleData single = datalist.get(position);
            username.setText(single.username);
            //判断是不是楼主
            boolean isLz = datalist.get(position).username.equals(datalist.get(0).username);
            labelLz.setVisibility(isLz ? View.VISIBLE : View.GONE);
            boolean isReply = single.replyUrlTitle.contains("action=reply");
            btnReplyCz.setVisibility(isReply ? View.VISIBLE : View.GONE);
            String img_url = UrlUtils.getAvaterurlm(single.getImg());
            Picasso.with(activity)
                    .load(img_url)
                    .resize(size, size)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(avatar);
            replyTime.setText(single.postTime);
            index.setText(single.index);

            HtmlView.parseHtml(single.content).into(comment);

            btnClose.setVisibility(View.GONE);
            //判断是不是自己
            if (App.ISLOGIN(activity) && App.getUid(activity).equals(single.uid)) {
                btnEdit.setVisibility(View.VISIBLE);
            } else {
                btnEdit.setVisibility(View.GONE);
            }

            //如果有管理权限，则显示除了关闭之外的全部按钮
            if (single.canManage) {
                btnWarn.setVisibility(View.VISIBLE);
                btnBlock.setVisibility(View.VISIBLE);
                btnRemove.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.VISIBLE);
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
