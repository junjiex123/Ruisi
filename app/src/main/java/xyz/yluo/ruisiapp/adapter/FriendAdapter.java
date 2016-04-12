package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ChatActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.FriendData;
import xyz.yluo.ruisiapp.utils.CircleImageView;

/**
 * Created by free2 on 16-4-12.
 * 好友列表
 *
 */
public class FriendAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private List<FriendData> datas;
    private Activity activity;

    public FriendAdapter(List<FriendData> datas, Activity activity) {
        this.datas = datas;
        this.activity = activity;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_friend_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    protected class FriendViewHolder extends BaseViewHolder{
        @Bind(R.id.main_item_btn_item)
        protected LinearLayout main_item_btn_item;
        @Bind(R.id.user_image)
        protected CircleImageView user_image;
        @Bind(R.id.user_name)
        protected TextView user_name;
        @Bind(R.id.user_info)
        protected TextView user_info;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @Override
        void setData(int position) {
            FriendData single = datas.get(position);
            user_name.setText(single.getUserName());
            user_info.setText(single.getInfo());
            Picasso.with(activity).load(single.getImgUrl()).placeholder(R.drawable.image_placeholder).into(user_image);
        }

        @OnClick(R.id.user_image)
        protected void userImage_click(){
            FriendData single = datas.get(getAdapterPosition());
            String username= single.getUserName();
            String imgUrl = single.getImgUrl();
            UserDetailActivity.openWithTransitionAnimation(activity,username,user_image,imgUrl);
        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_item_click(){
            String uid = datas.get(getAdapterPosition()).getUid();
            String username = datas.get(getAdapterPosition()).getUserName();
            String url = "home.php?mod=space&do=pm&subop=view&touid="+uid+"&mobile=2";
            ChatActivity.open(activity,username,url);
        }

    }
}
