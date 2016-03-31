package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleNormalActivity;
import xyz.yluo.ruisiapp.data.ImageArticleListData;
import xyz.yluo.ruisiapp.utils.ConfigClass;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-31.
 *
 */
public class ImagListAdapter extends RecyclerView.Adapter<ImagListAdapter.ImageCardViewHolder> {
    private List<ImageArticleListData> DataSet;
    private Activity activity;

    public ImagListAdapter(Activity activity,List<ImageArticleListData> dataSet) {
        DataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public ImageCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_image_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageCardViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    //图片板块ViewHolder
    public class ImageCardViewHolder extends RecyclerView.ViewHolder{

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

        void setData(int position) {
            img_card_author.setText(DataSet.get(position).getAuthor());
            img_card_title.setText(DataSet.get(position).getTitle());
            img_card_like.setText(DataSet.get(position).getReplyCount());
            if(DataSet.get(position).getImage()!=""){
                Picasso.with(activity).load(ConfigClass.BBS_BASE_URL+DataSet.get(position).getImage()).placeholder(R.drawable.image_placeholder).into(img_card_image);
            }else{
                img_card_image.setImageResource(R.drawable.image_placeholder);
            }

        }

        @OnClick(R.id.card_list_item)
        protected void card_list_item() {
            ImageArticleListData single_data =  DataSet.get(getAdapterPosition());
            String tid = GetId.getTid(single_data.getTitleUrl());
            //Context context, String tid,String title,String replycount,String type
            //String title, String titleUrl, String image, String author, String authorUrl, String viewCount

            ArticleNormalActivity.open(activity,tid,single_data.getTitle(),single_data.getReplyCount(),"");
        }
    }
}
