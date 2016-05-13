package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.ArticleListImageActivity;
import xyz.yluo.ruisiapp.activity.ArticleListNormalActivity;
import xyz.yluo.ruisiapp.data.FroumListData;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.GetLogoUtils;

/**
 * Created by free2 on 16-3-19.
 * 板块列表
 *
 */
public class ForumListAdapter extends BaseExpandableListAdapter{

    protected Activity activity;
    private List<FroumListData> listParents = null;
    private Map<Integer, List<FroumListData>> mapDatas = null;

    public ForumListAdapter(List<FroumListData> dataSet, Activity activity) {
        this.activity = activity;
        listParents = new ArrayList<>();
        mapDatas = new HashMap<>();
        initData(dataSet);
    }

    public void initData(List<FroumListData> dataSet){
        listParents.clear();
        mapDatas.clear();
        for(FroumListData temp:dataSet){
            if(temp.isheader()){
                listParents.add(temp);
            }else {
                List<FroumListData> datas = mapDatas.get(listParents.size()-1);
                if(datas==null) {
                    datas = new ArrayList<>();
                }
                datas.add(temp);
                mapDatas.put(listParents.size()-1,datas);
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return listParents.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mapDatas.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listParents.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mapDatas.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int position) {
        return position;
    }

    @Override
    public long getChildId(int groupposition, int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.forums_list_item_header, viewGroup, false);
        TextView head = (TextView) v.findViewById(R.id.header_title);
        head.setText(listParents.get(i).getTitle());
        return v;
    }


    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.forums_list_item, parent, false);
        final FroumListData single = mapDatas.get(groupPosition).get(childPosition);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView today_count = (TextView) v.findViewById(R.id.today_count);
        View container = v.findViewById(R.id.forum_list_item);

        title.setText(single.getTitle());
        if(!single.getTodayNew().isEmpty()){
            today_count.setVisibility(View.VISIBLE);
            today_count.setText(single.getTodayNew());
        }else{
            today_count.setVisibility(View.GONE);
        }
        String url = single.getTitleUrl();
        Drawable dra = GetLogoUtils.getlogo(activity, url);
        img.setImageDrawable(dra);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fid = GetId.getFroumFid(single.getTitleUrl());
                //几个特殊的板块
                if(PublicData.IS_SCHOOL_NET &&(fid.equals("561")||fid.equals("157")||fid.equals("13"))){
                    ArticleListImageActivity.open(activity,Integer.parseInt(fid),single.getTitle());
                }else{
                    ArticleListNormalActivity.open(activity, Integer.parseInt(fid), single.getTitle());
                }
            }
        });


        return v;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
