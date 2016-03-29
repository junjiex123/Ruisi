package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleNormalActivity;
import xyz.yluo.ruisiapp.data.MyTopicReplyListData;
import xyz.yluo.ruisiapp.utils.getThreadTid;

/**
 * Created by free2 on 16-3-21.
 * 首页第三页用户主题 回复 消息 收藏
 */
public class UserArticleReplyStarAdapter extends RecyclerView.Adapter<UserArticleReplyStarAdapter.BaseViewHolder>{

    private final int TYPE_MY_ARTICLE =0;
    private final int TYPE_MY_REPLY = 1;
    //数据
    private List<MyTopicReplyListData> DataSet;
    protected Activity activity;

    public UserArticleReplyStarAdapter(Activity activity, List<MyTopicReplyListData> dataSet) {
        DataSet = dataSet;
        this.activity = activity;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_MY_ARTICLE){
            //我的主题
            return new GetListArticleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.userinfo_article_list_item, parent, false));
        }else{
            //我的回复
            return new GetListReplyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_reply_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemViewType(int position) {
        switch (DataSet.get(position).getType()){
            case 0:
                return TYPE_MY_ARTICLE;
            default:
                return TYPE_MY_REPLY;

        }
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder{
        public BaseViewHolder(View itemView) {
            super(itemView);

        }
        abstract void setData(int position);

    }

    //用户的帖子的回复holder
    public class GetListReplyHolder extends BaseViewHolder{

        @Bind(R.id.article_title)
        protected TextView article_title;

        @Bind(R.id.author)
        protected TextView author_name;

        @Bind(R.id.reply_content)
        protected TextView reply_content;

        @Bind(R.id.froun_name)
        protected TextView froun_name;

        //url
        public GetListReplyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void setData(int position) {

        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_item_btn_item_click(){

            MyTopicReplyListData single_data =  DataSet.get(getAdapterPosition());
            ArticleNormalActivity.open(activity, getThreadTid.getTid(single_data.getTitleUrl()),single_data.getTitle(),single_data.getReplycount()," ");
        }
    }

    //用户主题
    public class GetListArticleHolder extends BaseViewHolder{

        @Bind(R.id.key)
        protected TextView article_title;

        @Bind(R.id.value)
        protected TextView reply_num;

        @Bind(R.id.main_item_btn_item)
        protected LinearLayout main_item_btn_item;

        public GetListArticleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        //int type, String title, String titleUrl, String replycount
        void setData(int position) {
            article_title.setText( DataSet.get(position).getTitle());
            reply_num.setText("回复:"+DataSet.get(position).getReplycount());
        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_item_btn_item_click(){
            MyTopicReplyListData single_data =  DataSet.get(getAdapterPosition());
            if(single_data.getTitleUrl()!=""){
                ArticleNormalActivity.open(activity, getThreadTid.getTid(single_data.getTitleUrl()),single_data.getTitle(),single_data.getReplycount()," ");
            }

        }
    }
}