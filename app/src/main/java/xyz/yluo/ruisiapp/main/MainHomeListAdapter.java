package xyz.yluo.ruisiapp.main;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.api.MainListArticleData;
import xyz.yluo.ruisiapp.api.MainListArticleDataHome;
import xyz.yluo.ruisiapp.article.ArticleNormalActivity;

/**
 * Created by free2 on 16-3-17.
 * 首页adapter
 *
 */
public class MainHomeListAdapter extends RecyclerView.Adapter<MainHomeListAdapter.BaseViewHolder>{

    private static final int TYPE_HOME_1 = 0;
    private static final int TYPE_HOME_2 = 1;
    //数据
    private List<MainListArticleDataHome> DataSet;
    protected Activity activity;
    int type;

    public MainHomeListAdapter(Activity activity,List<MainListArticleDataHome> dataSet,int type) {

        DataSet = dataSet;
        this.type =type;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        if(type==0){
            return TYPE_HOME_1;
        }else{
            return TYPE_HOME_2;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_HOME_2:
                return new FroumsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_forums_list_item, parent, false));
            default:
                return new HomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_home_list_item, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        //set data here
        holder.setData(position);
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

    //首页板块列表ViewHolder
    public class FroumsListViewHolder extends BaseViewHolder{

        @Bind(R.id.img)
        protected ImageView img;

        @Bind(R.id.title)
        protected TextView title;

        @Bind(R.id.today_count)
        protected TextView today_count;

        public FroumsListViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            today_count.setVisibility(View.GONE);
        }

        @OnClick(R.id.forum_list_item)
        protected void forum_list_item_click(){
            String url = DataSet.get(getPosition()).getUrl();
            Pattern pattern = Pattern.compile("[0-9]{2,}");
            Matcher matcher = pattern.matcher(url);
            String fid ="";
            while (matcher.find()) {
                fid = url.substring(matcher.start(),matcher.end());
                //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
            }
            System.out.print("\ntitle>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+DataSet.get(getPosition()).getName());
            MainActivity.open(activity,Integer.parseInt(fid),DataSet.get(getPosition()).getName());
        }


        @Override
        void setData(int position) {
            title.setText(DataSet.get(position).getName());
            if(DataSet.get(position).getTodaypost()!=""){
                today_count.setVisibility(View.VISIBLE);
                today_count.setText(DataSet.get(position).getTodaypost());
            }

            Picasso.with(activity).load(ConfigClass.BBS_BASE_URL+DataSet.get(position).getImage()).placeholder(R.drawable.image_placeholder).into(img);

        }

    }

    //首页新帖列表ViewHolder
    public class HomeViewHolder extends BaseViewHolder{

        @Bind(R.id.article_title)
        protected TextView article_title;

        @Bind(R.id.author_name)
        protected TextView author_name;

        @Bind(R.id.reply_count)
        protected TextView reply_count;

        @Bind(R.id.view_count)
        protected TextView view_count;

        //url
        public HomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void setData(int position) {
            article_title.setText(DataSet.get(position).getName());
            author_name.setText(DataSet.get(position).getUser());
            reply_count.setText(DataSet.get(position).getTodaypost());
            view_count.setText(DataSet.get(position).getViewCount());
        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_item_btn_item_click(){
            //传递一些参数过去 | 分割到时候分割   url|标题|回复|类型|author

            List<String> messagelist = new ArrayList<>();
            MainListArticleDataHome single_data =  DataSet.get(getPosition());
            messagelist.add(single_data.getUrl());
            messagelist.add(single_data.getName());
            messagelist.add(single_data.getTodaypost());
            messagelist.add("");
            messagelist.add(single_data.getUser());

            ArticleNormalActivity.open(activity, messagelist);
        }
    }
}
