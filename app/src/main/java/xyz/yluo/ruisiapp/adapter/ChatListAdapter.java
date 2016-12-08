package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.model.ChatListData;
import xyz.yluo.ruisiapp.view.CircleImageView;
import xyz.yluo.ruisiapp.view.myhtmlview.HtmlView;

/**
 * Created by free2 on 16-3-30.
 * 私人消息 adapter
 */
public class ChatListAdapter extends BaseAdapter {

    private final int LEFT_ITEM = 0;
    private final int RIGHT_ITEM = 1;

    private List<ChatListData> DataSets;
    private Activity context;

    public ChatListAdapter(Activity context, List<ChatListData> datas) {
        DataSets = datas;
        this.context = context;
    }

    @Override
    protected int getDataCount() {
        return DataSets.size();
    }

    @Override
    protected int getItemType(int pos) {
        if (DataSets.get(pos).getType() == 0) {
            return LEFT_ITEM;
        } else {
            return RIGHT_ITEM;
        }
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case LEFT_ITEM:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_l, parent, false));
            case RIGHT_ITEM:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_r, parent, false));
        }
        return null;
    }

    private class MyViewHolder extends BaseViewHolder {

        protected HtmlView content;
        protected CircleImageView user_image;
        protected TextView post_time;

        MyViewHolder(View itemView) {
            super(itemView);
            content = (HtmlView) itemView.findViewById(R.id.content);
            user_image = (CircleImageView) itemView.findViewById(R.id.user_image);
            post_time = (TextView) itemView.findViewById(R.id.post_time);

            user_image.setOnClickListener(v -> {
                String imageUrl = DataSets.get(getAdapterPosition()).getUserImage();
                UserDetailActivity.openWithAnimation(context, "username", user_image, imageUrl);
            });
        }

        void setData(final int position) {
            final ChatListData single = DataSets.get(position);
            Picasso.with(context).load(single.getUserImage()).into(user_image);
            post_time.setText(single.getTime());
            content.setHtmlText(single.getContent(), true);
        }
    }

}
