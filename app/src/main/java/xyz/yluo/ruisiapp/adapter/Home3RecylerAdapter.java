package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleNormalActivity;
import xyz.yluo.ruisiapp.data.MyTopicReplyListData;

/**
 * Created by free2 on 16-3-21.
 * 首页第三页的第二和第三小页
 */
public class Home3RecylerAdapter extends RecyclerView.Adapter<Home3RecylerAdapter.GetListHolder>{

    //数据
    private List<MyTopicReplyListData> DataSet;
    protected Activity activity;

    public Home3RecylerAdapter(Activity activity, List<MyTopicReplyListData> dataSet) {
        DataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public GetListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GetListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home3_pager_window_02_03_list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(GetListHolder holder, int position) {
        //set data here
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }

    //首页新帖列表ViewHolder
    public class GetListHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.article_title)
        protected TextView article_title;

        @Bind(R.id.author_name)
        protected TextView author_name;

        @Bind(R.id.article_time)
        protected TextView article_time;

        @Bind(R.id.article_from)
        protected TextView article_from;

        //url
        public GetListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(int position) {
            article_title.setText(DataSet.get(position).getTitle());
            author_name.setText(DataSet.get(position).getAuthor());
            article_time.setText(DataSet.get(position).getTime());
            if(DataSet.get(position).getType()==0){
                article_from.setVisibility(View.VISIBLE);
                article_from.setText(DataSet.get(position).getFroumName());
            }else{
                article_from.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_item_btn_item_click(){
            //传递一些参数过去 | 分割到时候分割   url|标题|回复|类型|author

            List<String> messagelist = new ArrayList<>();
            MyTopicReplyListData single_data =  DataSet.get(getPosition());
            //
            messagelist.add(single_data.getTitleUrl());
            messagelist.add(single_data.getTitle());
            messagelist.add(" ");
            messagelist.add(" ");
            messagelist.add(single_data.getAuthor());
            ArticleNormalActivity.open(activity, messagelist);
        }
    }
}