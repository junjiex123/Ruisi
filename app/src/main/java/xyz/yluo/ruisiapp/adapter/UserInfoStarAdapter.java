package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
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
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.SingleArticleNormalActivity;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-3-21.
 *
 */
public class UserInfoStarAdapter extends RecyclerView.Adapter<UserInfoStarAdapter.UserInfoViewHolder>{

    List<Pair<String,String >> datas = new ArrayList<>();
    private int type;
    private Activity activity;
    //0---用户信息
    //1---用户收藏
    //当为收藏时 url 在value里
    public UserInfoStarAdapter(Activity activity, List<Pair<String,String >> datas, int type) {
        this.datas =datas;
        this.type =type;
        this.activity =activity;
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
            if(type==0){
                value.setText(datas.get(position).second);
            }else{
                value.setVisibility(View.GONE);
            }

        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_container_click(){
            if(type==1){
                Pair<String,String > single_data =  datas.get(getAdapterPosition());
                String tid = GetId.getTid(single_data.second);
                if(tid.length()>=2){
                    SingleArticleNormalActivity.open(activity,tid,single_data.first,"","");
                }

            }
        }
    }
}
