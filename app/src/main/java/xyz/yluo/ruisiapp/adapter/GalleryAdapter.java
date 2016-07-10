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

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.data.GalleryData;

/**
 * Created by free2 on 16-3-31.
 * 首页gallery 的adaoter 支持无限滚动，小圆点，暂不支持自动滚动
 */
public class GalleryAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<GalleryData> DataSet;
    private Activity activity;

    public GalleryAdapter(Activity activity, List<GalleryData> dataSetg) {
        DataSet = dataSetg;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new GalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position%DataSet.size());
    }




    //图片板块ViewHolder
    private class GalleryViewHolder extends BaseViewHolder{

        ImageView img_card_image;
        TextView img_card_title;

        GalleryViewHolder(View itemView) {
            super(itemView);
            img_card_image = (ImageView) itemView.findViewById(R.id.img_card_image);
            img_card_title = (TextView) itemView.findViewById(R.id.img_card_title);

            itemView.findViewById(R.id.card_list_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_click();
                }
            });
        }

        void setData(int position) {
            img_card_title.setText(DataSet.get(position).getTitle());
            String imgUrl = PublicData.getBaseUrl()+DataSet.get(position).getImgurl().replace("./","");
            Picasso.with(activity).load(imgUrl).placeholder(R.drawable.image_placeholder).into(img_card_image);
        }
        protected void item_click() {
            GalleryData single_data =  DataSet.get(getAdapterPosition()%DataSet.size());
            SingleArticleActivity.open(activity,single_data.getTitleUrl(),"","");
        }
    }
}
