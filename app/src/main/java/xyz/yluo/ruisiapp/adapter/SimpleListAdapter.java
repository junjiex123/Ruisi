package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
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
import xyz.yluo.ruisiapp.data.SimpleListData;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-4-7.
 *
 */
public class SimpleListAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private List<SimpleListData> Datas = new ArrayList<>();
    private Activity activity;

    public SimpleListAdapter(Activity activity, List<SimpleListData> datas) {
        Datas = datas;
        this.activity = activity;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleVivwHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return Datas.size();
    }

    protected class SimpleVivwHolder extends BaseViewHolder {
        @Bind(R.id.key)
        protected TextView key;
        @Bind(R.id.value)
        protected TextView value;

        public SimpleVivwHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        void setData(int position) {
            key.setText(Datas.get(position).getKey());
            String values = Datas.get(position).getValue();
            if(values.length()>0){
                value.setVisibility(View.VISIBLE);
                value.setText(values);
            }else {
                value.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.main_item_btn_item)
        protected void main_container_click(){
            SimpleListData single_data =  Datas.get(getAdapterPosition());
            String url = single_data.getExtradata();
            if(!url.equals("")){
                String tid = GetId.getTid(url);
                if(!tid.equals(""))
                    SingleArticleNormalActivity.open(activity,tid,single_data.getKey(),"","");
            }
        }
    }


}
