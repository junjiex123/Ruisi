package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ChatActivity;
import xyz.yluo.ruisiapp.activity.SingleArticleNormalActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.ReplyMessageData;
import xyz.yluo.ruisiapp.utils.ArrowTextView;
import xyz.yluo.ruisiapp.utils.CircleImageView;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-21.
 * 首页第三页用户主题 回复 消息 收藏
 */
public class ReplyMessageAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    //数据
    private List<ReplyMessageData> DataSet;
    protected Activity activity;
    private int type; //0回复我的 //1我的消息

    public ReplyMessageAdapter(Activity activity, List<ReplyMessageData> dataSet, int type) {
        DataSet = dataSet;
        this.activity = activity;
        this.type = type;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageReplyListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }


    //用户消息holder //回复我的
    protected class MessageReplyListHolder extends BaseViewHolder{
        @Bind(R.id.title)
        protected TextView title;
        @Bind(R.id.time)
        protected TextView time;
        @Bind(R.id.article_user_image)
        protected CircleImageView article_user_image;
        @Bind(R.id.reply_content)
        protected ArrowTextView reply_content;
        //url
        public MessageReplyListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(int position) {
            ReplyMessageData single_data = DataSet.get(position);
            title.setText(single_data.getTitle());
            time.setText(single_data.getTime());
            String imageUrl = single_data.getauthorImage();
            Picasso.with(activity).load(imageUrl).resize(40,40).centerCrop().placeholder(R.drawable.image_placeholder).into(article_user_image);
            reply_content.setText(single_data.getcontent());
        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_item_btn_item_click(){
            ReplyMessageData single_data =  DataSet.get(getAdapterPosition());
            if(type==1){
                String username = single_data.getTitle().replace("我对 ","").replace("说:","").replace(" 对我","");
                ChatActivity.open(activity,username,single_data.getTitleUrl(),false);
            }else{
                String fid = GetId.getTid(single_data.getTitleUrl());
                String title = single_data.getcontent();
                System.out.println(single_data.getTitleUrl());
                System.out.println(fid);
                //Context context, String tid,String title,String replycount,String type
                SingleArticleNormalActivity.open(activity,fid,title,"","");
            }

        }
        @OnClick(R.id.article_user_image)
        protected void article_user_image_click(){
            ReplyMessageData single_data =  DataSet.get(getAdapterPosition());
            String username = single_data.getTitle().replace("我对 ","").replace("说:","").replace(" 对我","");
            UserDetailActivity.openWithTransitionAnimation(activity, username, article_user_image,DataSet.get(getAdapterPosition()).getauthorImage());
        }
    }

}