package xyz.yluo.ruisiapp.main;

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
import xyz.yluo.ruisiapp.api.NewAndTopListData;
import xyz.yluo.ruisiapp.article.ArticleNormalActivity;

/**
 * Created by free2 on 16-3-19.
 *首页获取新帖 热帖
 */
public class FragmentNewTopAdapter extends RecyclerView.Adapter<FragmentNewTopAdapter.NewAndTopViewHolder>{

    //数据
    private List<NewAndTopListData> DataSet;
    protected Activity activity;
    int type;

    public FragmentNewTopAdapter(Activity activity, List<NewAndTopListData> dataSet, int type) {
        DataSet = dataSet;
        this.activity = activity;
        this.type = type;
    }

    @Override
    public NewAndTopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewAndTopViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_home_list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(NewAndTopViewHolder holder, int position) {
        //set data here
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }

    //首页新帖列表ViewHolder
    public class NewAndTopViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.article_title)
        protected TextView article_title;

        @Bind(R.id.author_name)
        protected TextView author_name;

        @Bind(R.id.reply_count)
        protected TextView reply_count;

        @Bind(R.id.view_count)
        protected TextView view_count;

        //url
        public NewAndTopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(int position) {
            article_title.setText(DataSet.get(position).getTitle());
            author_name.setText(DataSet.get(position).getUser());
            reply_count.setText(DataSet.get(position).getReplyCount());
            view_count.setText(DataSet.get(position).getViewCount());
        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_item_btn_item_click(){
            //传递一些参数过去 | 分割到时候分割   url|标题|回复|类型|author

            List<String> messagelist = new ArrayList<>();
            NewAndTopListData single_data =  DataSet.get(getPosition());
            messagelist.add(single_data.getTitleUrl());
            messagelist.add(single_data.getTitle());
            messagelist.add(single_data.getReplyCount());
            messagelist.add("");
            messagelist.add(single_data.getUser());

            ArticleNormalActivity.open(activity, messagelist);
        }
    }
}
