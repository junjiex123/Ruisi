package xyz.yluo.ruisiapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.PostsActivity;
import xyz.yluo.ruisiapp.database.MyDB;
import xyz.yluo.ruisiapp.model.ForumListData;
import xyz.yluo.ruisiapp.utils.DimmenUtils;

/**
 * Created by free2 on 16-3-19.
 * 板块列表
 */
public class ForumsAdapter extends BaseAdapter {

    protected Context context;
    private List<ForumListData> datas = null;
    private List<ForumListData> starDatas = null;

    public ForumsAdapter(Context context, List<ForumListData> datasStar, List<ForumListData> datas) {
        this.context = context;
        this.datas = datas;
        this.starDatas = datasStar;

        disableLoadMore();
        setIsenablePlaceHolder(false);
    }

    @Override
    protected int getDataCount() {
        if (starDatas.size() == 0) {
            return datas.size();
        } else {
            if (starDatas.size() > 0 && !starDatas.get(0).isheader()) {
                starDatas.add(0, new ForumListData(true, "我的收藏", null, -1));
            }
            return datas.size() + starDatas.size();
        }
    }

    @Override
    protected int getItemType(int pos) {
        if (pos < starDatas.size()) {
            if (starDatas.get(pos).isheader()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            pos = pos - starDatas.size();
            if (datas.get(pos).isheader()) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    @Override
    protected BaseViewHolder getItemViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new HeadView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_h, parent, false));
        } else {
            return new ChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum_n, parent, false));
        }
    }

    private class HeadView extends BaseViewHolder {

        TextView head;

        HeadView(View itemView) {
            super(itemView);
            head = (TextView) itemView.findViewById(R.id.header_title);
        }

        @Override
        void setData(int position) {
            if (position < starDatas.size()) {
                head.setText(starDatas.get(position).getTitle());
            } else {
                position = position - starDatas.size();
                head.setText(datas.get(position).getTitle());
            }
        }
    }

    private class ChildViewHolder extends BaseViewHolder {

        ImageView img;
        TextView title;
        TextView today_count;
        View container;
        int size = 42;

        ChildViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            title = (TextView) itemView.findViewById(R.id.title);
            today_count = (TextView) itemView.findViewById(R.id.today_count);
            container = itemView.findViewById(R.id.forum_list_item);
            size = DimmenUtils.dip2px(context, 42);
        }

        @Override
        void setData(final int position) {
            final ForumListData single;
            if (position < starDatas.size()) {
                single = starDatas.get(position);
            } else {
                single = datas.get(position - starDatas.size());
            }

            title.setText(single.getTitle());
            if (!single.getTodayNew().isEmpty()) {
                today_count.setVisibility(View.VISIBLE);
                today_count.setText(single.getTodayNew());
            } else {
                today_count.setVisibility(View.GONE);
            }

            int fid = single.getFid();
            String name = "file:///android_asset/forumlogo/common_" + fid + "_icon.gif";
            Picasso.with(context).load(name).resize(size, size).error(R.drawable.image_placeholder).into(img);

            itemView.setOnClickListener(view -> {
                int fid1 = single.getFid();
                String title1 = single.getTitle();
                PostsActivity.open(context, fid1, title1);
            });

            itemView.setOnLongClickListener(view -> {
                int fid1 = single.getFid();
                String title12 = single.getTitle();

                MyDB myDB = new MyDB(context);
                if (myDB.isFormStar(fid)) {
                    Dialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("取消收藏")
                            .setMessage("你要取消收藏" + title12 + "吗？？")
                            .setPositiveButton("确定", (dialogInterface, i) -> {
                                myDB.setFormStar(title12, fid1, false);
                                changeStar(single, false);
                            })
                            .setNegativeButton("关闭", null)
                            .setCancelable(true)
                            .create();
                    alertDialog.show();
                } else {
                    Dialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("收藏版块")
                            .setMessage("你要收藏" + title12 + "吗？？")
                            .setPositiveButton("收藏", (dialogInterface, i) -> {
                                myDB.setFormStar(title12, fid1, true);
                                changeStar(single, true);
                            })
                            .setNegativeButton("关闭", null)
                            .setCancelable(true)
                            .create();
                    alertDialog.show();
                }

                return true;
            });
        }

        private void changeStar(ForumListData d, boolean star) {
            if (star) {
                if (starDatas.size() == 0) {
                    starDatas.add(new ForumListData(true, "我的收藏", null, -1));
                    starDatas.add(d);
                    notifyItemRangeInserted(0, 2);
                } else {
                    starDatas.add(d);
                    notifyItemInserted(starDatas.size() - 1);
                }
            } else {
                for (int i = 0; i < starDatas.size(); i++) {
                    ForumListData p = starDatas.get(i);
                    if (p.getFid() == d.getFid()) {
                        starDatas.remove(i);
                        notifyItemRemoved(i);
                        if (starDatas.size() == 1) {
                            starDatas.remove(0);
                            notifyItemRemoved(0);
                        }
                        break;
                    }
                }
            }

        }
    }
}
