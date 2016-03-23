package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleNormalActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.utils.ConfigClass;
import xyz.yluo.ruisiapp.utils.GetUserImage;
import xyz.yluo.ruisiapp.utils.getThreadTid;

/**
 * Created by free2 on 16-3-5.
 *
 */
public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.BaseViewHolder>{

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_IMAGE_CARD = 2;
    //校外网 手机版
    private static final int TYPE_NORMAL_MOBILE = 3;
    //数据
    private List<ArticleListData> DataSet;


    private int type =3;
    //上下文
    private Activity activity;
    public ArticleListAdapter(Activity activity, List<ArticleListData> data,int type) {
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
        if(!ConfigClass.CONFIG_IS_INNER||type==TYPE_NORMAL_MOBILE){
            return TYPE_NORMAL_MOBILE;
        }else{
            //图片列表
            if(DataSet.get(position).isImageCard()) {
                return TYPE_IMAGE_CARD;
            }
            else {
                //一般板块
                return TYPE_NORMAL;
            }

        }


        //判断listItem类型

        //TODO 普通文章类型再分类
        //int type   =  DataSet.get(position).getType();
    }
    //设置view
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_NORMAL_MOBILE:
                return new NormalViewHolderMe(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_list_item_me, viewGroup, false));
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_load_more_list_item, viewGroup, false));
            case TYPE_IMAGE_CARD:
                return new ImageCardViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_image_list_item, viewGroup, false));
            default: // TYPE_NORMAL
                return new NormalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_list_item, viewGroup, false));
        }
    }

    //设置data
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }
    //为了让 list item可以变化，我们可以重写这个方法
    //他的返回值是onCreateViewHolder 的参数viewType
    //这儿可以 知道position 和 data

    @Override
    public int getItemCount() {
        if (DataSet.size()==0){
            return 0;
        }
        return DataSet.size() + 1;
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder{

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
        abstract void setData(int position);
    }

    //文章列表ViewHolder 如果想创建别的样式还可以创建别的houlder继承自RecyclerView.ViewHolder
    public  class NormalViewHolder extends BaseViewHolder {
        @Bind(R.id.image_good)
        protected ImageView image_good;
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
        @Bind(R.id.reply_count)
        protected TextView reply_count;
        @Bind(R.id.view_count)
        protected TextView view_count;
        //构造
        public NormalViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            //imageView = (ImageView) v.findViewById(R.id.main_item_icon_good);
            //textViewTitle = (TextView) v.findViewById(R.id.main_item_tv_title);
        }
        //设置listItem的数据
        @Override
        void setData(int position) {
            String type = DataSet.get(position).getType();
            if (type.equals("zhidin")) {
                article_type.setText("置顶");
            } else if (type.equals("normal")) {
                article_type.setText("水贴");
            } else if (type.startsWith("gold")) {
                String gold = type.substring(5);
                article_type.setText("金币:" + gold);
            }

            if(DataSet.get(position).getType().equals("zhidin")){
                image_good.setVisibility(View.VISIBLE);
            }else {
                image_good.setVisibility(View.INVISIBLE);
            }

            post_time.setText(DataSet.get(position).getPostTime());
            view_count.setText(DataSet.get(position).getViewCount());
            String imageUrl = ConfigClass.BBS_BASE_URL+"ucenter/data/avatar/000/"+GetUserImage.getimageurl(DataSet.get(position).getAuthorUrl())+"_avatar_small.jpg";
            Picasso.with(activity).load(imageUrl).resize(36,36).centerCrop().placeholder(R.drawable.image_placeholder).into(author_img);

            article_title.setText(DataSet.get(position).getTitle());
            author_name.setText(DataSet.get(position).getAuthor());
            reply_count.setText(DataSet.get(position).getReplayCount());

        }

        @OnClick(R.id.author_img)
        protected void onBtnAvatarClick() {
            UserDetailActivity.openWithTransitionAnimation(activity, "name", author_img,"222");
            //ArticleNormalActivity.open(activity, "id12345");
        }

        @OnClick(R.id.main_item_btn_item)
        protected void onBtnItemClick() {
            ArticleListData single_data =  DataSet.get(getAdapterPosition());

            //Context context, String tid,String title,String replycount,String type
            ArticleNormalActivity.open(activity, getThreadTid.getTid(single_data.getTitleUrl()),single_data.getTitle(),single_data.getReplayCount(),single_data.getType());
            //System.out.print("$$$$$$$$$>>"+DataSet.get(getPosition()).getTitleUrl()+"|"+article_title.getText()+"|"+reply_count.getText()+"|"+article_type.getText()+"|"+author_name.getText()+"\n");
        }
    }

    //加载更多ViewHolder
    public class LoadMoreViewHolder extends BaseViewHolder{

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

    //图片板块ViewHolder
    public class ImageCardViewHolder extends BaseViewHolder{

        @Bind(R.id.img_card_image)
        protected ImageView img_card_image;

        @Bind(R.id.img_card_title)
        protected TextView img_card_title;

        @Bind(R.id.img_card_author)
        protected TextView img_card_author;

        @Bind(R.id.img_card_like)
        protected TextView img_card_like;

        public ImageCardViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        void setData(int position) {
            img_card_author.setText(DataSet.get(position).getAuthor());
            img_card_title.setText(DataSet.get(position).getTitle());
            img_card_like.setText(DataSet.get(position).getViewCount());
            if(DataSet.get(position).getImage()!=""){
                Picasso.with(activity).load(ConfigClass.BBS_BASE_URL+DataSet.get(position).getImage()).placeholder(R.drawable.image_placeholder).into(img_card_image);
            }else{
                img_card_image.setImageResource(R.drawable.image_placeholder);
            }

        }

        @OnClick(R.id.card_list_item)
        protected void card_list_item() {
            ArticleListData single_data =  DataSet.get(getAdapterPosition());
            String tid = getThreadTid.getTid(single_data.getTitleUrl());
            //Context context, String tid,String title,String replycount,String type
            //String title, String titleUrl, String image, String author, String authorUrl, String viewCount

            ArticleNormalActivity.open(activity,tid,single_data.getTitle(),single_data.getViewCount(),"");
        }
    }

    //手机版文章列表
    public  class NormalViewHolderMe extends BaseViewHolder {

        @Bind(R.id.article_title)
        protected TextView article_title;

        @Bind(R.id.author_name)
        protected TextView author_name;

        @Bind(R.id.is_image)
        protected ImageView is_image;
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
            article_title.setText(DataSet.get(position).getTitle());
            author_name.setText(DataSet.get(position).getAuthor());
            reply_count.setText(DataSet.get(position).getReplayCount());

            if(DataSet.get(position).isImageCard()){
                is_image.setVisibility(View.VISIBLE);
            }else {
                is_image.setVisibility(View.GONE);
            }
        }
        @OnClick(R.id.main_item_btn_item)
        protected void onBtnItemClick() {
            ArticleListData single_data =  DataSet.get(getAdapterPosition());
            String tid = getThreadTid.getTid(single_data.getTitleUrl());
            String title = single_data.getTitle();
            String replyCount = single_data.getReplayCount();

            System.out.print("\ntid"+tid);
            //Context context, String tid,String title,String replycount,String type
            ArticleNormalActivity.open(activity,tid,title,replyCount,"");
        }
    }

}
