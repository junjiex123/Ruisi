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
import java.util.Objects;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleListImage;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.data.ImageArticleListData;

/**
 * Created by free2 on 16-3-31.
 * 图片文章列表adapter 例如摄影天地板块{@link ArticleListImage}
 *
 */
public class ArticleListImageAdapter extends RecyclerView.Adapter<ArticleListImageAdapter.ImageCardViewHolder> {
    private List<ImageArticleListData> DataSet;
    private Activity activity;

    public ArticleListImageAdapter(Activity activity, List<ImageArticleListData> dataSet) {
        DataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public ImageCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageCardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageCardViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }


    //图片板块ViewHolder
    class ImageCardViewHolder extends RecyclerView.ViewHolder{

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
            img_card_like.setText(DataSet.get(position).getReplyCount());
            if(!Objects.equals(DataSet.get(position).getImage(), "")){
                Picasso.with(activity).load(PublicData.getBaseUrl()+DataSet.get(position).getImage()).placeholder(R.drawable.image_placeholder).into(img_card_image);
            }else{
                img_card_image.setImageResource(R.drawable.image_placeholder);
            }

        }

        protected void item_click() {
            ImageArticleListData single_data =  DataSet.get(getAdapterPosition());
            SingleArticleActivity.open(activity,single_data.getTitleUrl());
        }
    }
}
