package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.ChatListData;

/**
 * Created by free2 on 16-3-30.
 *
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>{

    private final int LEFT_ITEM = 0;
    private final int RIGHT_ITEM = 1;

    private List<ChatListData> DataSets;
    private Activity context;

    public ChatListAdapter(Activity context, List<ChatListData> datas) {
        DataSets = datas;
        this.context  = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(DataSets.get(position).getType()==0){
            return LEFT_ITEM;
        }else {
            return RIGHT_ITEM;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case LEFT_ITEM:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left_list_item, parent, false));

            case RIGHT_ITEM:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right_list_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(position);
    }


    @Override
    public int getItemCount() {
        return DataSets.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.content)
        protected TextView content;
        @Bind(R.id.user_image)
        protected CircleImageView user_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        private void setData(int position){
            content.setText(Html.fromHtml(DataSets.get(position).getContent()));
            Picasso.with(context).load(DataSets.get(position).getUserImage()).into(user_image);

        }

        @OnClick(R.id.user_image)
        protected void user_image_click(){
            UserDetailActivity.openWithTransitionAnimation(context, "name", user_image,"222");
        }
    }


}
