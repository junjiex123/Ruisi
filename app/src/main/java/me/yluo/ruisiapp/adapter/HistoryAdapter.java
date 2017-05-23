package me.yluo.ruisiapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.activity.PostActivity;
import me.yluo.ruisiapp.model.ReadHistoryData;

/**
 * Created by free2 on 16-12-10.
 * 浏览历史adapter
 */
public class HistoryAdapter extends BaseAdapter {

    private static final int CONTENT = 0;
    private List<ReadHistoryData> Datas;
    private Context context;

    public HistoryAdapter(Context context, List<ReadHistoryData> datas) {
        Datas = datas;
        this.context = context;
    }

    @Override
    protected int getDataCount() {
        return Datas.size();
    }

    @Override
    protected int getItemType(int pos) {
        return CONTENT;
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        return new HistoryVivwHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false));
    }


    private class HistoryVivwHolder extends BaseViewHolder {
        protected TextView title, author, time;

        HistoryVivwHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            author = (TextView) itemView.findViewById(R.id.author);
            time = (TextView) itemView.findViewById(R.id.time);
            itemView.findViewById(R.id.main_item_btn_item).setOnClickListener(v -> item_click());
        }

        @Override
        void setData(int position) {
            title.setText(Datas.get(position).title);
            author.setText(Datas.get(position).author);
            time.setText(Datas.get(position).readTime);
        }

        void item_click() {
            String tid = Datas.get(getAdapterPosition()).tid;
            if (!TextUtils.isEmpty(tid))
                PostActivity.open(context, "tid=" + tid, Datas.get(getAdapterPosition()).author);
        }
    }
}
