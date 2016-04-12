package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.MyPublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.utils.CircleImageView;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-5.
 *
 */
public class ArticleListNormalAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOAD_MORE = 1;
    //校外网 手机版
    private static final int TYPE_NORMAL_MOBILE = 3;
    //数据
    private List<ArticleListData> DataSet;
    private int type =3;

    //上下文
    private Activity activity;
    public ArticleListNormalAdapter(Activity activity, List<ArticleListData> data, int type) {
        DataSet = data;
        this.activity =activity;
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {

        if (position>0&& position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }
        //手机版
        if(!MyPublicData.IS_SCHOOL_NET ||type==TYPE_NORMAL_MOBILE){
            return TYPE_NORMAL_MOBILE;
        }else{
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
                return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_load_more_item, viewGroup, false));
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
        if (DataSet.size()==0){
            return 0;
        }
        return DataSet.size() + 1;
    }

    //文章列表ViewHolder 如果想创建别的样式还可以创建别的houlder继承自RecyclerView.ViewHolder
    protected  class NormalViewHolder extends BaseViewHolder {
        @Bind(R.id.article_type)
        protected TextView article_type;
        @Bind(R.id.article_title)
        protected TextView article_title;
        @Bind(R.id.author_img)
        protected CircleImageView author_img;
        @Bind(R.id.author_name)
        protected TextView author_name;
        @Bind(R.id.post_time)
        protected TextView post_time;
        @Bind(R.id.last_reply_time)
        protected TextView last_reply_time;
        @Bind(R.id.reply_count)
        protected TextView reply_count;
        @Bind(R.id.view_count)
        protected TextView view_count;
        //构造
        public NormalViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
        //设置listItem的数据
        @Override
        void setData(int position) {
            ArticleListData single = DataSet.get(position);
            String type = single.getType();
            if (type.equals("zhidin")) {
                article_type.setText("置顶");
                article_type.setVisibility(View.VISIBLE);
            } else if (type.startsWith("gold")) {
                String gold = type.substring(5);
                article_type.setVisibility(View.VISIBLE);
                String golds ="金币:" + gold ;
                article_type.setText(golds);
            }else {
                article_type.setVisibility(View.GONE);
            }

            String postTime = "发表于:"+single.getPostTime();
            post_time.setText(postTime);
            view_count.setText(single.getViewCount());

            String imageUrl = UrlUtils.getimageurl(single.getAuthorUrl(),false);
            Picasso.with(activity).load(imageUrl).resize(36,36).centerCrop().placeholder(R.drawable.image_placeholder).into(author_img);
            article_title.setText(single.getTitle());
            author_name.setText(single.getAuthor());
            reply_count.setText(single.getReplayCount());
        }

        @OnClick(R.id.author_img)
        protected void onBtnAvatarClick() {
            //Activity activity, String loginName, ImageView imgAvatar, String avatarUrl
            String imageUrl = UrlUtils.getimageurl(DataSet.get(getAdapterPosition()).getAuthorUrl(),false);
            UserDetailActivity.openWithTransitionAnimation(activity, DataSet.get(getAdapterPosition()).getAuthor(), author_img,imageUrl);
        }

        @OnClick(R.id.main_item_btn_item)
        protected void onBtnItemClick() {
            ArticleListData single_data =  DataSet.get(getAdapterPosition());
            SingleArticleActivity.open(activity, GetId.getTid(single_data.getTitleUrl()),single_data.getTitle());
        }
    }

    //加载更多ViewHolder
    protected class LoadMoreViewHolder extends BaseViewHolder{

        protected EditText loadmoreText;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void setData(int position) {
            //TODO
            //load more 现在没有数据填充
        }
    }


    //手机版文章列表
    protected   class NormalViewHolderMe extends BaseViewHolder {

        @Bind(R.id.article_title)
        protected TextView article_title;
        @Bind(R.id.author_name)
        protected TextView author_name;
        @Bind(R.id.is_image)
        protected TextView is_image;
        @Bind(R.id.reply_count)
        protected TextView reply_count;

        //构造
        public NormalViewHolderMe(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
        //设置listItem的数据
        @Override
        void setData(int position) {
            ArticleListData single = DataSet.get(position);
            article_title.setText(single.getTitle());
            author_name.setText(single.getAuthor());
            reply_count.setText(single.getReplayCount());

            if(single.getType().equals("0")){
                is_image.setVisibility(View.VISIBLE);
            }else {
                is_image.setVisibility(View.GONE);
            }
        }
        @OnClick(R.id.main_item_btn_item)
        protected void onBtnItemClick() {
            ArticleListData single_data =  DataSet.get(getAdapterPosition());
            String tid = GetId.getTid(single_data.getTitleUrl());
            String title = single_data.getTitle();

            SingleArticleActivity.open(activity,tid,title);
        }
    }

}
