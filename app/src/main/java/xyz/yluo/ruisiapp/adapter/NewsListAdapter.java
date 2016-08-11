package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.SingleNewsActivity;
import xyz.yluo.ruisiapp.data.SchoolNewsData;
import xyz.yluo.ruisiapp.database.MyDB;

/**
 * Created by free2 on 16-3-31.
 * 支持 gallery
 */
public class NewsListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_NEWS_LIST = 0;
    private static final int TYPE_PLACE_HOLDER = 2;
    private List<SchoolNewsData> DataSet;
    private Activity activity;
    private String placeHolderString = "Loading......";

    public NewsListAdapter(Activity activity, List<SchoolNewsData> DataSet) {
        this.DataSet = DataSet;
        this.activity = activity;
    }

    public void setPlaceHolderString(String placeHolderString) {
        this.placeHolderString = placeHolderString;
        notifyItemChanged(0);
    }

    @Override
    public int getItemCount() {
        if (DataSet.size() == 0) {
            return 1;
        }
        return DataSet.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItemCount()==1){
            return TYPE_PLACE_HOLDER;
        }

        if (position > 0 && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        } else {
            return TYPE_NEWS_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_item, parent, false));
            case TYPE_PLACE_HOLDER:
                return new PlaceHolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.place_holder_recycler_item, parent, false));
            default: // TYPE_NEWS_LIST
                return new NewsViewHolderMe(LayoutInflater.from(parent.getContext()).inflate(R.layout.school_news_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    //加载更多ViewHolder
    private class LoadMoreViewHolder extends BaseViewHolder {

        private ProgressBar progressBar;
        private TextView textView;
        LoadMoreViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.load_more_progress);
            textView = (TextView) itemView.findViewById(R.id.load_more_text);
        }

        @Override
        void setData(int position) {
            progressBar.setVisibility(View.GONE);
            textView.setText("已无更多");
        }
    }


    //新闻列表
    private class NewsViewHolderMe extends BaseViewHolder {
        TextView article_title;
        TextView post_time;
        TextView is_patch;
        View message_badge;

        //构造
        NewsViewHolderMe(View v) {
            super(v);
            article_title = (TextView) v.findViewById(R.id.article_title);
            post_time = (TextView) v.findViewById(R.id.time);
            is_patch = (TextView) v.findViewById(R.id.is_patch);
            message_badge = v.findViewById(R.id.message_badge);

            v.findViewById(R.id.main_window).setOnClickListener(new View.OnClickListener() {
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
            message_badge.setVisibility(single.isRead()?View.GONE:View.VISIBLE);
            article_title.setTextColor(single.isRead()? 0xff888888 : 0xff000000);
            article_title.setText(single.getTitle());
            post_time.setText(single.getPost_time());
            is_patch.setVisibility(single.is_patch() ? View.VISIBLE : View.GONE);
        }

        void onBtnItemClick() {
            SchoolNewsData single = DataSet.get(getAdapterPosition());
            single.setRead(true);
            message_badge.setVisibility(View.GONE);
            article_title.setTextColor(0xff888888);

            MyDB myDB = new MyDB(activity, MyDB.MODE_WRITE);
            myDB.setSingleNewsRead(single.getUrl());
            SingleNewsActivity.open(activity, single.getUrl(), single.getTitle());
        }
    }


    //place holder
    private class PlaceHolderViewHolder extends BaseViewHolder{
        private TextView textView;
        public PlaceHolderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        void setData(int position) {
            textView.setText(placeHolderString);
        }
    }
}
