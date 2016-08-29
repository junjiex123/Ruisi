package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SmileyAdapter;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;
import xyz.yluo.ruisiapp.utils.ImageUtils;
import xyz.yluo.ruisiapp.utils.PostHandler;

/**
 * Created by free2 on 16-7-19.
 *
 */

public class MySmileyPicker extends PopupWindow{

    private Context mContext;
    private OnItemClickListener listener;
    private RecyclerView recyclerView;
    private SmileyAdapter adapter;
    private List<Drawable> ds = new ArrayList<>();
    private String[] nameList;

    private static final int SMILEY_TB = 1;
    private static final int SMILEY_LDB = 2;
    private static final int SMILEY_ACN = 3;

    private int smiley_type = SMILEY_TB;


    public MySmileyPicker(Context context)
    {
        super(context);
        mContext = context;
        init();
    }


    private void init()
    {

        View v = LayoutInflater.from(mContext).inflate(R.layout.my_smiley_view,null);
        TabLayout tab = (TabLayout) v.findViewById(R.id.mytab);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        ds = getSmileys();
        tab.addTab(tab.newTab().setText("贴吧"));
        tab.addTab(tab.newTab().setText("林大b"));
        tab.addTab(tab.newTab().setText("AC娘"));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 7, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SmileyAdapter(new ListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                smileyClick(position);
                dismiss();
            }
        }, ds);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        smiley_type = SMILEY_TB;
                        break;
                    case 1:
                        smiley_type = SMILEY_LDB;
                        break;
                    case 2:
                        smiley_type = SMILEY_ACN;
                        break;
                }
                changeSmiley();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        recyclerView.setAdapter(adapter);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.rec_solid_primary_bg));
        setFocusable(true);
        setContentView(v);
    }


    private void changeSmiley() {
        ds.clear();
        ds = getSmileys();
        adapter.notifyDataSetChanged();
    }


    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void itemClick(String str,Drawable a);
    }

    private List<Drawable> getSmileys() {
        String smiley_dir = "static/image/smiley/";
        if (smiley_type == SMILEY_TB) {
            smiley_dir += "smiley_tieba";
        } else if (smiley_type == SMILEY_LDB) {
            smiley_dir += "lindab";
        } else if (smiley_type == SMILEY_ACN) {
            smiley_dir += "acn";
        }

        try {
            nameList = mContext.getAssets().list(smiley_dir);
            for (String temp : nameList) {
                InputStream in = mContext.getAssets().open(smiley_dir + "/" + temp);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                d.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                ds.add(d);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ds;
    }

    private void smileyClick(int position) {
        if (position > nameList.length) {
            return;
        }

        String name = nameList[position].split("\\.")[0];
        String insertName = ImageUtils.getSmileyName(smiley_type,name);
        if(listener!=null){
            listener.itemClick(insertName,ds.get(position));
        }

    }
}