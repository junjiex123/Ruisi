package xyz.yluo.ruisiapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-21.
 *
 */
public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder>{

    List<Pair<String,String >> datas = new ArrayList<Pair<String,String >>();
    public UserInfoAdapter(List<Pair<String,String >> datas) {
        this.datas =datas;
    }

    @Override
    public UserInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.userinfo_article_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(UserInfoViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class UserInfoViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.key)
        protected TextView key;

        @Bind(R.id.value)
        protected TextView value;

        public UserInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setData(int position){
            key.setText(datas.get(position).first);
            value.setText(datas.get(position).second);
        }
    }
}
