package xyz.yluo.ruisiapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.DimmenUtils;

/**
 * Created by free2 on 16-7-18.
 * my tab
 */

public class MyBottomTab extends LinearLayout implements OnClickListener {
    private Context context;
    private int currentSelected = 0;
    private int[] icons_unselect = {
            R.drawable.ic_home_24dp,
            R.drawable.ic_whatshot_white_24dp,
            R.drawable.ic_notifications_white_24dp,
            R.drawable.ic_person_white_24dp
    };

    private String[] tab_names = {"板块", "看贴", "消息", "个人"};
    private OnTabChangeListener listener;
    private boolean ishaveMessage = false;

    //遵循md 设计规范
    private int PADDING_8 = 8;
    private int BADGE_SIZE = 6;
    private int PADDING_12 = 12;
    private int SIZE_ICON = 24;
    private int COLOR_SELECT;
    private int COLOR_UNSELECT;
    private int CLICK_BG_RES;

    public MyBottomTab(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MyBottomTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setMessage(boolean b) {
        if (b != ishaveMessage) {
            ishaveMessage = b;
            drawableStateChanged();
            invalidate();
        }
    }

    public void setSelect(int pos) {
        if (pos >= tab_names.length) {
            return;
        }

        if (pos != currentSelected) {
            setTabSelect(currentSelected, pos);
            currentSelected = pos;
        }
    }

    /**
     * 初始化视图
     */
    private void init() {
        COLOR_SELECT = ContextCompat.getColor(context, R.color.colorAccent);
        COLOR_UNSELECT = ContextCompat.getColor(context, R.color.colorDisableHintIcon);
        PADDING_8 = DimmenUtils.dip2px(context, PADDING_8);
        PADDING_12 = DimmenUtils.dip2px(context, PADDING_12);
        SIZE_ICON = DimmenUtils.dip2px(context, SIZE_ICON);
        BADGE_SIZE = DimmenUtils.dip2px(context, 3);
        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        CLICK_BG_RES = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        setOrientation(LinearLayout.HORIZONTAL);// 水平布局
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        setBackgroundResource(R.drawable.bottom_tab_bg);

        for (int i = 0; i < tab_names.length; i++) {
            View v = getSingleTab(i);
            v.setTag(i);
            v.setOnClickListener(this);
            addView(v);
        }
        setTabSelect(-1, 0);

        paint_badge.setColor(COLOR_SELECT);
        paint_badge.setStyle(Paint.Style.FILL);
        paint_badge.setStrokeWidth(PADDING_8 / 2);
        paint_badge.setAntiAlias(true);
    }

    private void setTabSelect(int from, int to) {
        if (from != -1) {
            ViewGroup tab_item_from = (ViewGroup) this.findViewWithTag(from);
            ImageView pre_img = (ImageView) tab_item_from.getChildAt(0);
            TextView pre_text = (TextView) tab_item_from.getChildAt(1);
            pre_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            pre_img.setColorFilter(COLOR_UNSELECT);
            pre_text.setTextColor(COLOR_UNSELECT);
        }

        ViewGroup tab_item_to = (ViewGroup) this.findViewWithTag(to);
        ImageView to_img = (ImageView) tab_item_to.getChildAt(0);
        TextView to_text = (TextView) tab_item_to.getChildAt(1);
        to_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        to_img.setImageResource(icons_unselect[to]);
        to_img.setColorFilter(COLOR_SELECT);
        to_text.setTextColor(COLOR_SELECT);
        invalidate();
    }

    @Override
    public void onClick(View v) {
        int tag = (Integer) v.getTag();
        boolean change = (currentSelected != tag);
        if (listener != null) {
            listener.tabClicked(v, tag, change);
        }

        if (change) {
            setTabSelect(currentSelected, tag);
            currentSelected = tag;
        }
    }

    private View getSingleTab(int position) {
        LinearLayout view = new LinearLayout(getContext());
        view.setClickable(true);
        view.setBackgroundResource(CLICK_BG_RES);

        view.setOrientation(LinearLayout.VERTICAL);
        // 设置宽高和权重
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1));
        view.setGravity(Gravity.CENTER);
        view.setPadding(PADDING_12, PADDING_8, PADDING_12, PADDING_8);

        /**
         * 图标
         */
        ImageView iconView = new ImageView(getContext());
        //三个参数的构造可以设置权重
        iconView.setLayoutParams(new LayoutParams(SIZE_ICON, SIZE_ICON));
        iconView.setImageResource(icons_unselect[position]);
        iconView.setColorFilter(COLOR_UNSELECT);
        /**
         * 标题
         */
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(COLOR_UNSELECT);
        textView.setText(tab_names[position]);
        view.addView(iconView);
        view.addView(textView);
        // 返回布局视图
        return view;
    }

    private Paint paint_badge = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (ishaveMessage) {
            int len = getWidth();
            int end = len / 4 * 3;
            int start = len / 2;
            int center = (end - start) / 2 + start;
            int centx = center + PADDING_12;
            int centy = PADDING_12;
            canvas.drawCircle(centx, centy, BADGE_SIZE, paint_badge);
        }

    }

    public interface OnTabChangeListener {
        void tabClicked(View v, int position, boolean isChange);
    }

    public void setOnTabChangeListener(OnTabChangeListener linstener) {
        this.listener = linstener;
    }
}