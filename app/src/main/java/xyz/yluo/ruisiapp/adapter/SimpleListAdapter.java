package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.SingleArticleActivity;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.SimpleListData;

/**
 * Created by free2 on 16-4-7.
 * 简单的adapter 比如用户信息
 * 我的收藏 我的帖子,搜索结果
 * 等都用这个
 */
public class SimpleListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int CONTENT = 0;
    private static final int LOAD_MORE = 1;
    private boolean showLoadMore = false;
    private List<SimpleListData> Datas = new ArrayList<>();
    private Activity activity;
    private ListType type;

    public SimpleListAdapter(ListType type, Activity activity, List<SimpleListData> datas) {
        Datas = datas;
        this.activity = activity;
        this.type = type;
    }

    public void setShowLoadMore(boolean showLoadMore) {
        this.showLoadMore = showLoadMore;
    }

    @Override
    public int getItemCount() {
        if (Datas.size() == 0) {
            return 0;
        }
        return Datas.size() + 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return LOAD_MORE;
        } else {
            return CONTENT;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CONTENT:
                return new SimpleVivwHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false));
            default:
                return new SimpleLoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_load_more_item, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }


    private class SimpleVivwHolder extends BaseViewHolder {
        protected TextView key;
        protected TextView value;

        SimpleVivwHolder(View itemView) {
            super(itemView);
            key = (TextView) itemView.findViewById(R.id.key);
            value = (TextView) itemView.findViewById(R.id.value);
            itemView.findViewById(R.id.main_item_btn_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_click();
                }
            });

            System.out.println(type);
        }

        @Override
        void setData(int position) {
            String keystr = Datas.get(position).getKey();
            key.setText(Html.fromHtml(keystr));
            String values = Datas.get(position).getValue();
            if (values.length() > 0) {
                value.setVisibility(View.VISIBLE);
                value.setText(values);
            } else {
                value.setVisibility(View.GONE);
            }
        }

        void item_click() {
            SimpleListData single_data = Datas.get(getAdapterPosition());
            String url = single_data.getExtradata();
            if (url != null && url.length() > 0) {
                SingleArticleActivity.open(activity, url, single_data.getKey(), single_data.getValue());
            }
        }
    }

    private class SimpleLoadMoreViewHolder extends BaseViewHolder {

        public SimpleLoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        void setData(int position) {
            itemView.setVisibility(showLoadMore ? View.VISIBLE : View.GONE);
        }
    }


}
