package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.activity.SingleNewsActivity;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.data.GalleryData;
import xyz.yluo.ruisiapp.data.SchoolNewsData;
import xyz.yluo.ruisiapp.listener.RecyclerPageChangeListener;

/**
 * Created by free2 on 16-3-31.
 * 支持 gallery
 */
public class NewsListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<SchoolNewsData> DataSet;
    private Activity activity;
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_NEWS_LIST = 0;

    public NewsListAdapter(Activity activity, List<SchoolNewsData> DataSet) {
        this.DataSet = DataSet;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        if(DataSet.size()==0){
            return 0;
        }
        return DataSet.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position>0&& position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }else{
            return TYPE_NEWS_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_item, parent, false));
            default: // TYPE_NEWS_LIST
                return new NewsViewHolderMe(LayoutInflater.from(parent.getContext()).inflate(R.layout.school_news_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    //加载更多ViewHolder
    private class LoadMoreViewHolder extends BaseViewHolder{

        LoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void setData(int position) {
            //TODO
            //load more 现在没有数据填充
        }
    }


    //手机版文章列表
    private class NewsViewHolderMe extends BaseViewHolder {
        TextView article_title;
        TextView post_time;
        TextView is_image;
        TextView is_patch;

        //构造
        NewsViewHolderMe(View v) {
            super(v);
            article_title = (TextView) v.findViewById(R.id.article_title);
            post_time = (TextView) v.findViewById(R.id.time);
            is_image = (TextView) v.findViewById(R.id.is_image);
            is_patch = (TextView) v.findViewById(R.id.is_patch);
            v.findViewById(R.id.main_item_btn_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBtnItemClick();
                }
            });
        }
        //设置listItem的数据
        @Override
        void setData(int position) {
            SchoolNewsData single = DataSet.get(position);
            article_title.setTextColor(single.isRead()?0xff888888:0xff000000);
            article_title.setText(single.getTitle());
            post_time.setText(single.getPost_time());
            is_image.setVisibility(single.is_image()?View.VISIBLE:View.GONE);
            is_patch.setVisibility(single.is_patch()?View.VISIBLE:View.GONE);
        }

        void onBtnItemClick() {
            SchoolNewsData single =  DataSet.get(getAdapterPosition());
            //todo  数据库支持
//            if(!single_data.isRead()){
//                single_data.setRead(true);
//                notifyItemChanged(getAdapterPosition());
//            }
            SingleNewsActivity.open(activity,single.getUrl(),single.getTitle());
        }
    }
}
