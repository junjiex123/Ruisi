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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.utils.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.utils.GetUserImage;
import xyz.yluo.ruisiapp.activity.ArticleNormalActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;

/**
 * Created by free2 on 16-3-5.
 *
 */
public class MainArticleListAdapter extends RecyclerView.Adapter<MainArticleListAdapter.BaseViewHolder>{

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_IMAGE_CARD = 2;
    //数据
    private List<ArticleListData> DataSet;

    //上下文
    private Activity activity;
    public MainArticleListAdapter(Activity activity, List<ArticleListData> data) {
        DataSet = data;
        this.activity =activity;
    }

    @Override
    public int getItemViewType(int position) {

        //判断listItem类型
        //图片列表
        if(position!= getItemCount() - 1 && DataSet.get(position).isImageCard()) {
            return TYPE_IMAGE_CARD;
        }

        //加载更多
        else if (position>0&& position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }
        //普通列表
        else {
            return TYPE_NORMAL;
        }
        //TODO 普通文章类型再分类
        //int type   =  DataSet.get(position).getType();
    }
    //设置view
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_load_more_list_item, viewGroup, false));
            case TYPE_IMAGE_CARD:
                return new ImageCardViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_image_list_item, viewGroup, false));
            default: // TYPE_NORMAL
                return new CardViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_main_list_item, viewGroup, false));
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
    public  class CardViewHolder extends BaseViewHolder {
        @Bind(R.id.image_good)
        protected ImageView image_good;
        @Bind(R.id.article_type)
        protected TextView article_type;
        @Bind(R.id.article_title)
        protected TextView article_title;
        @Bind(R.id.author_img)
        protected ImageView author_img;
        @Bind(R.id.author_name)
        protected TextView author_name;
        @Bind(R.id.post_time)
        protected TextView post_time;
        @Bind(R.id.reply_count)
        protected TextView reply_count;
        @Bind(R.id.view_count)
        protected TextView view_count;
        //构造
        public CardViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            //imageView = (ImageView) v.findViewById(R.id.main_item_icon_good);
            //textViewTitle = (TextView) v.findViewById(R.id.main_item_tv_title);
        }
        //设置listItem的数据
        @Override
        void setData(int position) {
            String type = DataSet.get(position).getType();
            if(type.equals("zhidin")){
                article_type.setText("置顶");
            }else if(type.equals("normal")){
                article_type.setText("水贴");
            }else if(type.startsWith("gold")){
                String gold = type.substring(5);
                article_type.setText("金币:"+gold);
            }


            if(DataSet.get(position).getType().equals("zhidin")){
                image_good.setVisibility(View.VISIBLE);
            }else {
                image_good.setVisibility(View.INVISIBLE);
            }
            article_title.setText(DataSet.get(position).getTitle());
            author_name.setText(DataSet.get(position).getAuthor());
            post_time.setText(DataSet.get(position).getPostTime());
            reply_count.setText(DataSet.get(position).getReplayCount());
            view_count.setText(DataSet.get(position).getViewCount());

            String imageUrl = "http://rs.xidian.edu.cn/ucenter/data/avatar/000/"+GetUserImage.getimageurl(DataSet.get(position).getAuthorUrl())+"_avatar_small.jpg";
            //if(userUID.length())
            // 00/00/00

            //home.php?mod=space&uid=277268
            //http://rs.xidian.edu.cn/ucenter/data/avatar/000/29/84/87_avatar_small.jpg
            //298487
            //System.out.println("@@@@@@@@@@@@@@@user url>>\n"+DataSet.get(position).getAuthorUrl()+"\n@@@@@@@@userUID>>\n"+GetUserImage.getimageurl(DataSet.get(position).getAuthorUrl()));

            Picasso.with(activity).load(imageUrl).resize(36,36).centerCrop().placeholder(R.drawable.image_placeholder).into(author_img);
        }

        @OnClick(R.id.author_img)
        protected void onBtnAvatarClick() {
            UserDetailActivity.openWithTransitionAnimation(activity, "name", author_img,"222");
            //ArticleNormalActivity.open(activity, "id12345");
        }

        @OnClick(R.id.main_item_btn_item)
        protected void onBtnItemClick() {
            //传递一些参数过去 | 分割到时候分割   url|标题|回复|类型|author

            List<String> messagelist = new ArrayList<>();
            ArticleListData single_data =  DataSet.get(getPosition());
            messagelist.add(single_data.getTitleUrl());
            messagelist.add(single_data.getTitle());
            messagelist.add(single_data.getReplayCount());
            messagelist.add(single_data.getType());
            messagelist.add(single_data.getAuthor());

            ArticleNormalActivity.open(activity,messagelist);
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

    //图片刘ViewHolder
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
    }

}
