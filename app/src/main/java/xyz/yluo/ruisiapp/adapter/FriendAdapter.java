package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.activity.ChatActivity;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.FriendData;

/**
 * Created by free2 on 16-4-12.
 * 好友列表
 */
public class FriendAdapter extends BaseAdapter {

    private List<FriendData> datas;
    private Activity activity;

    public FriendAdapter(List<FriendData> datas, Activity activity) {
        this.datas = datas;
        this.activity = activity;
    }

    @Override
    protected int getDataCount() {
        return datas.size();
    }

    @Override
    protected int getItemType(int pos) {
        return 0;
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item, parent, false));
    }

    private class FriendViewHolder extends BaseViewHolder {
        protected CircleImageView user_image;
        protected TextView user_name;
        TextView user_info;

        FriendViewHolder(View itemView) {
            super(itemView);
            user_image = (CircleImageView) itemView.findViewById(R.id.user_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_info = (TextView) itemView.findViewById(R.id.user_info);

            user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userImage_click();
                }
            });

            itemView.findViewById(R.id.main_item_btn_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_click();
                }
            });
        }

        @Override
        void setData(int position) {
            FriendData single = datas.get(position);
            user_name.setText(single.getUserName());
            user_info.setText(single.getInfo());
            Picasso.with(activity).load(single.getImgUrl()).placeholder(R.drawable.image_placeholder).into(user_image);
        }

        void userImage_click() {
            FriendData single = datas.get(getAdapterPosition());
            String username = single.getUserName();
            UserDetailActivity.openWithAnimation(activity, username, user_image, single.getUid());
        }

        void item_click() {
            String uid = datas.get(getAdapterPosition()).getUid();
            String username = datas.get(getAdapterPosition()).getUserName();
            String url = "home.php?mod=space&do=pm&subop=view&touid=" + uid + "&mobile=2";
            ChatActivity.open(activity, username, url);
        }

    }
}
