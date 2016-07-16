package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.View.MyHtmlView.CustomQuoteSpan;
import xyz.yluo.ruisiapp.View.MyHtmlView.MyHtmlTextView;
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

    private final int CONTENT = 0;
    private final int COMENT = 1;
    private final int LOAD_MORE = 2;
    private LoadMoreType loadMoreType = LoadMoreType.LOADING;

    private ScrollToSomePosition scrollToSomePosition = null;
    //数据
    private List<SingleArticleData> datalist;
    private RecyclerViewClickListener itemListener;
    private Activity activity;
    public SingleArticleAdapter(Activity activity, RecyclerViewClickListener itemListener, List<SingleArticleData> datalist) {
        this.datalist = datalist;
        this.activity = activity;
        this.itemListener = itemListener;
    }

    public void setScrollToSomePosition(ScrollToSomePosition scrollToSomePosition) {
        this.scrollToSomePosition = scrollToSomePosition;
    }

    public void setLoadMoreType(LoadMoreType loadMoreType) {
        this.loadMoreType = loadMoreType;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return LOAD_MORE;
        } else if (datalist.get(position).getType() == SingleType.CONTENT) {
            return CONTENT;
        } else {
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
            return 0;
        }
        return datalist.size() + 1;
    }

    public interface ScrollToSomePosition {
        void scroolto(int position);
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
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }

    }

    //文章内容 楼主ViewHolder
    private class ArticleContentViewHolder extends BaseViewHolder {
        TextView article_title;
        CircleImageView article_user_image;
        TextView article_username;
        TextView article_post_time;
        MyHtmlTextView htmlTextView;

        ArticleContentViewHolder(View itemView) {
            super(itemView);
            article_title = (TextView) itemView.findViewById(R.id.article_title);
            article_user_image = (CircleImageView) itemView.findViewById(R.id.article_user_image);
            article_username = (TextView) itemView.findViewById(R.id.article_username);
            article_post_time = (TextView) itemView.findViewById(R.id.article_post_time);
            htmlTextView = (MyHtmlTextView) itemView.findViewById(R.id.html_text);

            article_user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDetailActivity.openWithTransitionAnimation(activity, datalist.get(0).getUsername(), article_user_image, datalist.get(0).getImg());
                }
            });
        }

        @Override
        void setData(int position) {
            SingleArticleData single = datalist.get(position);
            article_title.setText(single.getTitle());
            article_username.setText(single.getUsername());
            String img_url = UrlUtils.getAvaterurlm(single.getImg());
            Picasso.with(activity).load(img_url).placeholder(R.drawable.image_placeholder).into(article_user_image);
            String post_time = "发表于:" + single.getPostTime();
            article_post_time.setText(post_time);
            htmlTextView.mySetText(activity, single.getCotent());
        }

    }

    private class CommentViewHolder extends BaseViewHolder {
        ImageView replay_image;
        ImageView btn_reply_2;
        TextView replay_author;
        TextView replay_index;
        TextView replay_time;
        MyHtmlTextView htmlTextView;
        TextView bt_lable_lz;

        CommentViewHolder(View itemView) {
            super(itemView);
            replay_image = (ImageView) itemView.findViewById(R.id.article_user_image);
            btn_reply_2 = (ImageView) itemView.findViewById(R.id.btn_reply_2);
            replay_author = (TextView) itemView.findViewById(R.id.replay_author);
            replay_index = (TextView) itemView.findViewById(R.id.replay_index);
            replay_time = (TextView) itemView.findViewById(R.id.replay_time);
            htmlTextView = (MyHtmlTextView) itemView.findViewById(R.id.html_text);
            bt_lable_lz = (TextView) itemView.findViewById(R.id.bt_lable_lz);

            replay_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserDetailActivity.openWithTransitionAnimation(activity, datalist.get(getAdapterPosition()).getUsername(), replay_image, datalist.get(getAdapterPosition()).getImg());
                }
            });

            btn_reply_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(v, getLayoutPosition());
                }
            });
        }

        //设置listItem的数据
        @Override
        void setData(final int position) {
            SingleArticleData single = datalist.get(position);

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
            htmlTextView.mySetText(activity, single.getCotent());

            //处理回复点击问题 跳转
            htmlTextView.setQuoteSpanClickListener(new CustomQuoteSpan.OnQuoteSpanClick() {
                @Override
                public void quoteSpanClick(String res) {
                    if (scrollToSomePosition != null && res.contains("回复") && res.contains("\n")) {
                        String usernaec = res.split("\\n")[0].split(" ")[1].trim();
                        String contentc = res.split("\\n")[1].trim();
                        if (contentc.length() > 3) {
                            contentc = contentc.substring(0, 3);
                        }

                        System.out.println("user:" + usernaec + "  content:" + contentc);
                        for (int i = position; i > 0; i--) {
                            SingleArticleData singlec = datalist.get(i);
                            if (singlec.getUsername().equals(usernaec) && singlec.getCotent().contains(contentc)) {
                                System.out.println("i find" + i);
                                scrollToSomePosition.scroolto(i);
                            }
                        }
                    }
                }
            });
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
}
