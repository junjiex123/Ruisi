package me.yluo.ruisiapp.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.utils.DimenUtils;


public class MySpinner<T> extends PopupWindow implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ListView listView;
    private OnItemSelectListener listener;
    private MySpinnerListAdapter<T> adapter;

    private int currentSelect = -1;

    public MySpinner(Context context) {
        super(context);
        mContext = context;
        init();
    }


    private void init() {
        listView = new ListView(mContext);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        listView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bg_secondary));
        listView.setOnItemClickListener(this);

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.my_spinner_bg));

        setFocusable(true);
        setContentView(listView);
    }

    public void setData(List<T> datas) {
        adapter = new MySpinnerListAdapter<>(mContext, datas);
        listView.setAdapter(adapter);
    }

    public T getSelectItem() {
        if (currentSelect > adapter.getCount() - 1 || currentSelect < 0) {
            return null;
        }
        return adapter.getItem(currentSelect);
    }

    public void setListener(OnItemSelectListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        dismiss();
        if (listener != null && currentSelect != pos) {
            currentSelect = pos;
            listener.onItemSelectChanged(pos, view);
        }

    }

    public interface OnItemSelectListener {
        void onItemSelectChanged(int pos, View v);
    }

    private static class MySpinnerListAdapter<T> extends BaseAdapter {

        private List<T> datas;
        private Context context;


        MySpinnerListAdapter(Context context, List<T> datas) {
            this.datas = datas;
            this.context = context;
        }


        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public T getItem(int i) {
            return datas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            TextView v = new TextView(context);
            v.setTag(i);
            v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            v.setText(datas.get(i).toString());
            v.setGravity(Gravity.CENTER);
            v.setLayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            //textView.setTextColor(COLOR_UNSELECT);
            int padding1 = DimenUtils.dip2px(context, 8);
            int padding2 = DimenUtils.dip2px(context, 6);

            v.setPadding(padding1, padding2, padding1, padding2);
            return v;
        }
    }
}