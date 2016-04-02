package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleNormalActivity;
import xyz.yluo.ruisiapp.data.ArticleListBtData;
import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.GetLogoUtils;

/**
 * Created by free2 on 16-4-2.
 *
 */
public class ArticleListBtAdapter extends RecyclerView.Adapter<ArticleListBtAdapter.MyBtViewHolder>{

    private List<ArticleListBtData> Datas;
    private Activity activity;

    public ArticleListBtAdapter(Activity activity,List<ArticleListBtData> datas ) {
        Datas = datas;
        this.activity = activity;
    }

    @Override
    public MyBtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyBtViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyBtViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }


    protected class MyBtViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.bt_isfree)
        protected TextView bt_isfree;
        @Bind(R.id.bt_lable_1)
        protected TextView bt_lable_1;
        @Bind(R.id.bt_lable_2)
        protected TextView bt_lable_2;
        @Bind(R.id.bt_size)
        protected TextView bt_size;
        @Bind(R.id.bt_num)
        protected TextView bt_num;
        @Bind(R.id.bt_logo)
        protected ImageView bt_logo;
        @Bind(R.id.bt_title)
        protected TextView bt_title;
        @Bind(R.id.bt_author)
        protected TextView bt_author;
        @Bind(R.id.bt_time)
        protected TextView bt_time;

        public MyBtViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        private void setData(int position){
            ArticleListBtData single = Datas.get(position);
            if(single.isFree()){
                bt_isfree.setVisibility(View.VISIBLE);
            }else{
                bt_isfree.setVisibility(View.GONE);
            }

            String title_b = single.getTitle();
            Pattern p = Pattern.compile("\\[([^\\[\\]]*)\\]");
            Matcher matcher = p.matcher(title_b);
            int i =0;int end = 0;
            while (matcher.find()) {
                if(i==0){
                    bt_lable_1.setText(title_b.substring(matcher.start(),matcher.end()).replace("[","").replace("]",""));
                }else{
                    bt_lable_2.setText(title_b.substring(matcher.start(),matcher.end()).replace("[","").replace("]",""));
                    end = matcher.end();
                    break;
                }
                i++;
            }

            bt_title.setText(single.getTitle().substring(end));
            bt_author.setText(single.getAuthor());
            bt_size.setText(single.getBtSize());
            bt_time.setText(single.getTime());

            Drawable dra = GetLogoUtils.getBtLogo(activity, single.getLogoUrl());
            bt_logo.setImageDrawable(dra);

        }

        @OnClick(R.id.main_item_btn_item)
        protected void item_click(){
            ArticleListBtData single_data =  Datas.get(getAdapterPosition());
            ArticleNormalActivity.open(activity, GetId.getTid(single_data.getTitleUrl()),single_data.getTitle(),"null","");

        }


    }
}
