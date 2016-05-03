package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleListImageActivity;
import xyz.yluo.ruisiapp.activity.ArticleListNormalActivity;
import xyz.yluo.ruisiapp.data.FroumListData;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.GetLogoUtils;

/**
 * Created by free2 on 16-3-19.
 * 板块列表
 *
 */
public class ForumListAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    //数据
    private List<FroumListData> DataSet;
    protected Activity activity;

    private final int TYPE_NORMAL = 0;
    private final int TYPE_HEADER = 1;

    public ForumListAdapter(Activity activity, List<FroumListData> dataSet) {
        DataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if(DataSet.get(position).isheader()){
            return TYPE_HEADER;
        }else{
            return TYPE_NORMAL;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_NORMAL){
            return new FroumsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.forums_list_item, parent, false));
        }else{
            return new FroumsListHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.forums_item_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        //set data here
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }

    //首页板块列表ViewHolder
    protected class FroumsListViewHolder extends BaseViewHolder{

        @Bind(R.id.img)
        protected ImageView img;
        @Bind(R.id.title)
        protected TextView title;
        @Bind(R.id.today_count)
        protected TextView today_count;

        public FroumsListViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            today_count.setVisibility(View.GONE);
        }
        @OnClick(R.id.forum_list_item)
        protected void item_click(){
            FroumListData single = DataSet.get(getAdapterPosition());

            String fid = GetId.getFroumFid(single.getTitleUrl());
            //几个特殊的板块
            if(PublicData.IS_SCHOOL_NET &&(fid.equals("561")||fid.equals("157")||fid.equals("13"))){
                ArticleListImageActivity.open(activity,Integer.parseInt(fid),single.getTitle());
            }else{
                ArticleListNormalActivity.open(activity, Integer.parseInt(fid), single.getTitle());
            }

        }
        void setData(int position) {
            FroumListData single = DataSet.get(position);
            title.setText(single.getTitle());
            if(!single.getTodayNew().isEmpty()){
                today_count.setVisibility(View.VISIBLE);
                today_count.setText(single.getTodayNew());
            }

            String url = single.getTitleUrl();
            Drawable dra = GetLogoUtils.getlogo(activity, url);
            img.setImageDrawable(dra);
        }

    }

    protected class FroumsListHeaderViewHolder extends BaseViewHolder{

        @Bind(R.id.header_title)
        protected TextView header_title;

        public FroumsListHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @Override
        void setData(int position) {
            header_title.setText(DataSet.get(position).getTitle());
        }

        @OnClick(R.id.forum_list_item)
        protected void item_click(){

        }
    }
}
