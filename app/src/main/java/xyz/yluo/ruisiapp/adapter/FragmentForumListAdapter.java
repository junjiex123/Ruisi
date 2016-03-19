package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.utils.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.data.FroumListData;
import xyz.yluo.ruisiapp.activity.ArticleListActivity;

/**
 * Created by free2 on 16-3-19.
 *
 */
public class FragmentForumListAdapter extends RecyclerView.Adapter<FragmentForumListAdapter.FroumsListViewHolder>{

    //数据
    private List<FroumListData> DataSet;
    protected Activity activity;
    int type;

    public FragmentForumListAdapter(Activity activity, List<FroumListData> dataSet, int type) {
        DataSet = dataSet;
        this.activity = activity;
        this.type = type;
    }

    @Override
    public FroumsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FroumsListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_forums_list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(FroumsListViewHolder holder, int position) {
        //set data here
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }

    //首页板块列表ViewHolder
    public class FroumsListViewHolder extends RecyclerView.ViewHolder{

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
            String url = DataSet.get(getPosition()).getTitleUrl();
            Pattern pattern = Pattern.compile("[0-9]{2,}");
            Matcher matcher = pattern.matcher(url);
            String fid ="";
            while (matcher.find()) {
                fid = url.substring(matcher.start(),matcher.end());
                //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
            }
            System.out.print("\ntitle>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+DataSet.get(getPosition()).getTitle());
            ArticleListActivity.open(activity, Integer.parseInt(fid), DataSet.get(getPosition()).getTitle());
        }

        void setData(int position) {
            title.setText(DataSet.get(position).getTitle());
            if(DataSet.get(position).getTodayNew()!=""){
                today_count.setVisibility(View.VISIBLE);
                today_count.setText(DataSet.get(position).getTodayNew());
            }

            //TODO
            Picasso.with(activity).load(ConfigClass.BBS_BASE_URL+DataSet.get(position).getImgUrl()).placeholder(R.drawable.image_placeholder).into(img);

        }

    }
}
