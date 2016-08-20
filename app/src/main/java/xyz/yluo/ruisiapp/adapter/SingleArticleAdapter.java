package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.View.MyHtmlView.HtmlView;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.LoadMoreType;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.data.SingleType;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-7.
 * 单篇文章adapter
 * 评论 文章 loadmore
 */

public class SingleArticleAdapter extends RecyclerView.Adapter<SingleArticleAdapter.BaseViewHolder> {

    private static final int CONTENT = 0;
    private static final int COMENT = 1;
    private static final int LOAD_MORE = 2;
    private static final int HEADER = 3;
    private static final int PLACEHOLDER = 4;

    private String placeHolderString = "Loading......";

    public void setPlaceHolderString(String placeHolderString) {
        this.placeHolderString = placeHolderString;
        notifyItemChanged(0);
    }

    private LoadMoreType loadMoreType = LoadMoreType.LOADING;
    /**
     * 缓存楼主的一层
     */
    private SpannableStringBuilder strBuilderContent;


    //数据
    private List<SingleArticleData> datalist;
    private RecyclerViewClickListener itemListener;
    private Activity activity;

    public SingleArticleAdapter(Activity activity, RecyclerViewClickListener itemListener, List<SingleArticleData> datalist) {
        this.datalist = datalist;
        this.activity = activity;
        this.itemListener = itemListener;
    }


    public void setLoadMoreType(LoadMoreType loadMoreType) {
        this.loadMoreType = loadMoreType;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItemCount()==1){//nodata
            return PLACEHOLDER;
        }
        if (position == getItemCount() - 1) {
            return LOAD_MORE;
        } else if (datalist.get(position).getType() == SingleType.CONTENT) {
            return CONTENT;
        } else if(datalist.get(position).getType()==SingleType.HEADER){
            return HEADER;
        }else{
            return COMENT;
        }
    }

    //设置view
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case CONTENT:
                return new ArticleContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_list_item, viewGroup, false));
            case LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.load_more_item, viewGroup, false));
            case HEADER:
                return new HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.need_load_content_item, viewGroup, false));
            case PLACEHOLDER:
                return new PlaceHolderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_place_holder_item,viewGroup,false));
            default: // TYPE_COMMENT
                return new CommentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false));
        }
    }

    //设置data
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        if (datalist.size() == 0) {
            return 1;
        }
        return datalist.size() + 1;
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        abstract void setData(int position);

        @Override
        public void onClick(View v) {
            //getItemCount()
            itemListener.recyclerViewListClicked(v, this.getAdapterPosition());
        }

    }

    //文章内容 楼主ViewHolder
    private class ArticleContentViewHolder extends BaseViewHolder {
        TextView article_title;
        CircleImageView article_user_image;
        TextView article_username;
        TextView article_post_time,tv_remove,tv_edit;
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
            //myWebView = (MyWebView) itemView.findViewById(R.id.mywebview);
            htmlView = (HtmlView) itemView.findViewById(R.id.html_text);
            article_user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDetailActivity.openWithTransitionAnimation(activity, datalist.get(0).getUsername(), article_user_image, datalist.get(0).getImg());
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
            //myWebView.setContent(single.getCotent());
            if(strBuilderContent==null){
                htmlView.setHtmlText(single.getCotent(), true);
                strBuilderContent = htmlView.getStrBuilderContent();
            }else{
                htmlView.setSpannedHtmlText(strBuilderContent);
            }

            //判断是不是自己
            if(!TextUtils.isEmpty(App.USER_UID)&&single.getUid().equals(App.USER_UID)){
                tv_edit.setVisibility(View.VISIBLE);
                if(getItemCount()>2){
                    tv_remove.setVisibility(View.GONE);
                }else{
                    tv_remove.setVisibility(View.VISIBLE);
                }
            }else{
                tv_remove.setVisibility(View.GONE);
                tv_edit.setVisibility(View.GONE);
            }

        }


    }

    private class CommentViewHolder extends BaseViewHolder{
        ImageView replay_image;
        ImageView btn_reply_2;
        TextView replay_author;
        TextView replay_index;
        TextView replay_time;
        HtmlView htmlTextView;
        TextView bt_lable_lz,tv_remove,tv_edit;

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
                    UserDetailActivity.openWithTransitionAnimation(activity, datalist.get(getAdapterPosition()).getUsername(), replay_image, datalist.get(getAdapterPosition()).getImg());
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
            String timeText = "发表于:" + single.getPostTime();
            replay_time.setText(timeText);
            replay_index.setText(single.getIndex());
            htmlTextView.setHtmlText(single.getCotent(), true);

            //判断是不是自己
            if(!TextUtils.isEmpty(App.USER_UID)&&single.getUid().equals(App.USER_UID)){
                tv_remove.setVisibility(View.VISIBLE);
                tv_edit.setVisibility(View.VISIBLE);
            }else{
                tv_remove.setVisibility(View.GONE);
                tv_edit.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
        }
    }

    //加载更多ViewHolder
    private class LoadMoreViewHolder extends BaseViewHolder {
        ProgressBar progressBar;
        TextView load_more_text;
        View load_more_empty;

        LoadMoreViewHolder(View itemView) {
            super(itemView);
            load_more_text = (TextView) itemView.findViewById(R.id.load_more_text);
            progressBar = (ProgressBar) itemView.findViewById(R.id.load_more_progress);
            load_more_empty = itemView.findViewById(R.id.load_more_empty);
        }

        @Override
        void setData(int position) {
            if (position == 1) {
                loadMoreType = LoadMoreType.SECOND;
            }
            if (loadMoreType == LoadMoreType.LOADING) {
                load_more_text.setText("正在加载");
                progressBar.setVisibility(View.VISIBLE);
                load_more_empty.setVisibility(View.VISIBLE);
            } else if (loadMoreType == LoadMoreType.SECOND) {
                load_more_text.setText("还没有人回复快来抢沙发吧！！");
                progressBar.setVisibility(View.GONE);
                load_more_empty.setVisibility(View.VISIBLE);
            } else if (loadMoreType == LoadMoreType.NOTHING) {
                load_more_text.setText("暂无更多");
                progressBar.setVisibility(View.GONE);
                load_more_empty.setVisibility(View.VISIBLE);
            } else {
                load_more_text.setText("加载失败");
                progressBar.setVisibility(View.GONE);
                load_more_empty.setVisibility(View.VISIBLE);
            }
        }
    }

    //header
    private class HeaderViewHolder extends BaseViewHolder{

        HeaderViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.need_loading_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.recyclerViewListClicked(view, getLayoutPosition());
                }
            });
        }

        @Override
        void setData(int position) {

        }
    }

    //placeholder ViewHolder
    private class PlaceHolderViewHolder extends BaseViewHolder{
        TextView textView;
        PlaceHolderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        void setData(int position) {
            textView.setText(placeHolderString);
        }
    }
}
