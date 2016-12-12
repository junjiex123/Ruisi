package xyz.yluo.ruisiapp.widget.emotioninput;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.DimmenUtils;


public class SmileyView extends LinearLayout implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private Context context;
    private PageAdapter adapter;
    private int dotImageResourseId;
    private LinearLayout dotContainer;
    private LinearLayout tabContainer;
    private List<SmileyDataSet> smileys;
    private EmotionInputHandler emotionInputHandler;
    private int currentTabPosition = -1;
    private int totalPageSize = 0;
    private int SIZE_8 = 0;

    private static final int LMP = LayoutParams.MATCH_PARENT;
    private static final int LWC = LayoutParams.WRAP_CONTENT;
    private boolean isInitSize = false;
    private static int ROW_COUNT = 4;
    private static int COLOUM_COUNT = 7;
    private int COLOR_TAB = Color.WHITE;
    private int COLOR_TAB_SEL = Color.GRAY;

    public SmileyView(Context context) {
        super(context);
        init(context);
    }

    public SmileyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmileyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        SIZE_8 = DimmenUtils.dip2px(context, 8);
        setOrientation(VERTICAL);
        setBackgroundColor(ContextCompat.getColor(context, R.color.bg_primary));
        COLOR_TAB = ContextCompat.getColor(context, R.color.bg_primary);
        COLOR_TAB_SEL = ContextCompat.getColor(context, R.color.bg_select);

        viewPager = new ViewPager(context);
        viewPager.setLayoutParams(new LayoutParams(LMP, LWC, 1));
        viewPager.addOnPageChangeListener(this);
        dotImageResourseId = R.drawable.dot_bg;
        addView(viewPager);

        dotContainer = new LinearLayout(context);
        dotContainer.setOrientation(LinearLayout.HORIZONTAL);
        dotContainer.setLayoutParams(new LayoutParams(LMP, DimmenUtils.dip2px(context, 24)));
        dotContainer.setGravity(Gravity.CENTER);
        addView(dotContainer);


        View gap = new View(context);
        gap.setLayoutParams(new LayoutParams(LMP, DimmenUtils.dip2px(context, 0.6f)));
        gap.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDivider));
        addView(gap);

        tabContainer = new LinearLayout(context);
        tabContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabContainer.setGravity(Gravity.CENTER_VERTICAL);
        tabContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_primary));
        tabContainer.setLayoutParams(new LayoutParams(LMP, DimmenUtils.dip2px(context, 36)));
        addView(tabContainer);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isInitSize) {
            isInitSize = true;
            int width = DimmenUtils.px2dip(context, r - l);
            int height = DimmenUtils.px2dip(context, b - t);
            COLOUM_COUNT = width / 60;
            ROW_COUNT = height / 60;
            Log.e("onLayout", "width: " + width + " height:" + height);

            adapter = new PageAdapter();
            List<SmileyDataSet> smileys = new ArrayList<>();
            SmileyDataSet setTieba = SmileyDataSet.getDataSet(context, "贴吧", true, R.array.smiley_tieba);
            SmileyDataSet setAcn = SmileyDataSet.getDataSet(context, "ac娘", true, R.array.smiley_acn);
            SmileyDataSet setJgz = SmileyDataSet.getDataSet(context, "金馆长", true, R.array.smiley_jgz);
            SmileyDataSet setYwz = SmileyDataSet.getDataSet(context, "颜文字", false, R.array.smiley_ywz);

            smileys.add(setTieba);
            smileys.add(setAcn);
            smileys.add(setJgz);
            // TODO: 2016/12/11 睿思不支持emoji
            //smileys.add(SmileyEmoji.getEmojis());
            smileys.add(setYwz);
            setSmileys(smileys);
            viewPager.setAdapter(adapter);
        }
    }


    public void setInputView(EmotionInputHandler handler) {
        emotionInputHandler = handler;
    }


    public void setSmileys(List<SmileyDataSet> smileys) {
        if (smileys == null) return;
        this.smileys = smileys;
        totalPageSize = getTotalPageSize();
        adapter.notifyDataSetChanged();
        initTabs();
        setDots(0);
        switchDot(0);
    }


    //获得某一类表情的页数
    private int getPageSize(int pos) {
        if (smileys == null) {
            return 0;
        } else {
            int singlePageCount = ROW_COUNT * COLOUM_COUNT;
            int size = smileys.get(pos).getCount();
            int page = size / singlePageCount;
            if (size % singlePageCount != 0) {
                page++;
            }
            return page;
        }
    }

    //获得中的页数
    private int getTotalPageSize() {
        if (smileys == null) {
            return 0;
        } else {
            int count = 0;
            for (int i = 0; i < smileys.size(); i++) {
                count += getPageSize(i);
            }
            return count;
        }
    }


    private int getPageCountBefore(int tabpos) {
        int p = 0;

        for (int i = 0; i < tabpos; i++) {
            p += getPageSize(i);
        }

        return p;
    }


    //页转tab
    private int pageToTabPos(int pageIndex) {
        if (pageIndex <= 0) return 0;
        if (pageIndex >= totalPageSize - 1) return smileys.size() - 1;
        int p = 0;
        for (int i = 0; i < smileys.size(); i++) {
            p += getPageSize(i);
            if (pageIndex < p) {
                return i;
            }
        }
        return smileys.size() - 1;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int page) {
        int tabpos = pageToTabPos(page);
        int dotindex = page - getPageCountBefore(tabpos);
        switchTab(tabpos);
        switchDot(dotindex);
    }

    private void switchTab(int pos) {
        if (currentTabPosition == -1 || pos != currentTabPosition) {
            if (currentTabPosition == -1) currentTabPosition = 0;
            tabContainer.getChildAt(currentTabPosition).setBackgroundColor(COLOR_TAB);
            currentTabPosition = pos;
            tabContainer.getChildAt(currentTabPosition).setBackgroundColor(COLOR_TAB_SEL);
            setDots(currentTabPosition);
        }
    }

    private void switchDot(int index) {
        for (int i = 0; i < dotContainer.getChildCount(); i++) {
            if (i == index) {
                dotContainer.getChildAt(i).setEnabled(true);
                dotContainer.getChildAt(i).setScaleX(1.35f);
                dotContainer.getChildAt(i).setScaleY(1.35f);
            } else {
                dotContainer.getChildAt(i).setEnabled(false);
                dotContainer.getChildAt(i).setScaleX(1.0f);
                dotContainer.getChildAt(i).setScaleY(1.0f);
            }
        }
    }

    public void initTabs() {
        LayoutParams params = new LayoutParams(LWC, LMP);
        for (int i = 0; i < smileys.size(); i++) {
            View itemView;
            SmileyDataSet set = smileys.get(i);
            if (set.isImage()) {
                itemView = new ImageView(context);
                Picasso.with(context).load(set.getLogo()).resize(SIZE_8 * 2, SIZE_8 * 2).into((ImageView) itemView);
                ((ImageView) itemView).setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                itemView = new TextView(context);
                ((TextView) itemView).setText(set.getLogo());
                ((TextView) itemView).setTextSize(TypedValue.COMPLEX_UNIT_SP, set.textSize);
                ((TextView) itemView).setGravity(Gravity.CENTER);
            }

            itemView.setPadding(SIZE_8 * 2, SIZE_8 / 2, SIZE_8 * 2, SIZE_8 / 2);
            itemView.setClickable(true);
            final int finalI = i;
            if (i == 0) itemView.setBackgroundColor(COLOR_TAB_SEL);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchTab(finalI);
                    switchDot(0);
                    int pageStart = getPageCountBefore(finalI);
                    viewPager.setCurrentItem(pageStart, true);
                }
            });

            tabContainer.addView(itemView, params);
        }

        View v = new View(context);
        v.setLayoutParams(new LayoutParams(LWC, LMP, 1));
        tabContainer.addView(v);

        ImageView delIcon = new ImageView(context);
        delIcon.setPadding(SIZE_8 * 2, SIZE_8 / 2, SIZE_8 * 2, SIZE_8 / 2);
        delIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        delIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_back_space));
        delIcon.setClickable(true);
        delIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                emotionInputHandler.backSpace();
            }
        });
        tabContainer.addView(delIcon, params);
    }

    public void setDots(int tabpos) {
        dotContainer.removeAllViews();
        LayoutParams lpp = new LayoutParams(LWC, LWC);
        lpp.setMargins(SIZE_8 / 2, 0, SIZE_8 / 2, 0);
        lpp.gravity = Gravity.CENTER_VERTICAL;

        for (int i = 0; i < getPageSize(tabpos); i++) {
            ImageView dotImageView = new ImageView(context);
            dotImageView.setImageResource(dotImageResourseId);
            dotImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            dotImageView.setEnabled(false);
            dotContainer.addView(dotImageView, lpp);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class PageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return totalPageSize;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int page) {
            GridView v = (GridView) container.findViewWithTag(page);

            if (v == null) {
                v = new GridView(context);
                v.setNumColumns(COLOUM_COUNT);
                //v.setSelector(android.R.color.transparent);
                final int tabpos = pageToTabPos(page);
                v.setAdapter(new SmileyAdapter(tabpos, page, smileys.get(tabpos)));
                v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (view instanceof ImageView) {
                            ImageView v = (ImageView) view;
                            SmileyDataSet set = smileys.get(tabpos);
                            int pageStart = page - getPageCountBefore(tabpos);
                            int index = pageStart * ROW_COUNT * COLOUM_COUNT + i;
                            emotionInputHandler.insertSmiley(set, index, v.getDrawable());
                        } else if (view instanceof TextView) {
                            TextView v = (TextView) view;
                            emotionInputHandler.insertString(v.getText().toString());
                        }

                    }
                });
                v.setLayoutParams(new LayoutParams(LMP, LMP));
                v.setTag(page);
                container.addView(v);
            }
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private class SmileyAdapter extends BaseAdapter {
        private SmileyDataSet set;
        private int startIndex;
        private int pageStart = 0;
        private int tab = 0;

        SmileyAdapter(int tabpos, int page, SmileyDataSet set) {
            this.set = set;
            this.tab = tabpos;
            pageStart = page - getPageCountBefore(tabpos);
            startIndex = pageStart * ROW_COUNT * COLOUM_COUNT;
        }

        @Override
        public int getCount() {
            if (set == null) {
                return 0;
            }
            int pages = getPageSize(tab);
            if (pageStart < pages - 1) {
                return ROW_COUNT * COLOUM_COUNT;
            } else if (pageStart == pages - 1) {
                return set.getCount() - startIndex;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return smileys.get(startIndex + i);
        }

        @Override
        public long getItemId(int i) {
            return i + startIndex;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            final int pos = startIndex + i;
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                if (set.isImage()) {
                    convertView = new ImageView(context);
                    convertView.setPadding(SIZE_8 / 2 * 3, SIZE_8 / 2 * 3, SIZE_8 / 2 * 3, SIZE_8 / 2 * 3);
                } else {
                    convertView = new TextView(context);
                    ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP, set.textSize);
                    ((TextView) convertView).setTextColor(ContextCompat.getColor(context, R.color.text_color_pri));
                    convertView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    ((TextView) convertView).setGravity(Gravity.CENTER);
                    convertView.setPadding(SIZE_8 / 4, 0, SIZE_8 / 4, 0);
                }
                holder.emoticon = convertView;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int realItemHeight = viewPager.getMeasuredHeight() / ROW_COUNT;
            holder.emoticon.setLayoutParams(new LinearLayoutCompat.LayoutParams(LMP, realItemHeight));
            if (realItemHeight > 0) {
                if (set.isImage()) {
                    Picasso.with(context).load(set.getSmileys().get(pos).first).resize(realItemHeight / 2, realItemHeight / 2).into((ImageView) holder.emoticon);
                } else {
                    ((TextView) holder.emoticon).setText(set.getSmileys().get(pos).first);
                }
            }
            return convertView;
        }

        private class ViewHolder {
            View emoticon;
        }
    }

}
