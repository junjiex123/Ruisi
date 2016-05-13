package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.View.MyHtmlTextView;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.ChatListData;

/**
 * Created by free2 on 16-3-30.
 * 私人消息 adapter
 */
public class ChatListAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private final int LEFT_ITEM = 0;
    private final int RIGHT_ITEM = 1;
    private final int EMPTY_ITEM = 2;

    private List<ChatListData> DataSets;
    private Activity context;

    public ChatListAdapter(Activity context, List<ChatListData> datas) {
        DataSets = datas;
        this.context  = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==getItemCount()-1){
            return EMPTY_ITEM;
        }
        if(DataSets.get(position).getType()==0){
            return LEFT_ITEM;
        }else{
            return RIGHT_ITEM;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case LEFT_ITEM:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left_list_item, parent, false));
            case RIGHT_ITEM:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right_list_item, parent, false));
            case EMPTY_ITEM:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_list_item,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }



    @Override
    public int getItemCount() {
        return DataSets.size()+1;
    }

    public class MyViewHolder extends BaseViewHolder{

        @BindView(R.id.content)
        protected MyHtmlTextView content;
        @BindView(R.id.user_image)
        protected CircleImageView user_image;
        @BindView(R.id.post_time)
        protected TextView post_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void setData(final int position){
            ChatListData single = DataSets.get(position);
            Picasso.with(context).load(single.getUserImage()).into(user_image);
            post_time.setText(single.getTime());
            content.mySetText(context, single.getContent());
        }

        @OnClick(R.id.user_image)
        protected void user_image_click(){
            String imageUrl = DataSets.get(getAdapterPosition()).getUserImage();
            UserDetailActivity.openWithTransitionAnimation(context, "username", user_image,imageUrl);
        }
    }

    protected class EmptyViewHolder extends BaseViewHolder{

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void setData(int position) {

        }
    }


}
