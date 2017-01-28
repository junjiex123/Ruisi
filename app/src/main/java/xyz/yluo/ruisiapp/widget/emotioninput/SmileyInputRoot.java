package xyz.yluo.ruisiapp.widget.emotioninput;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import xyz.yluo.ruisiapp.utils.DimmenUtils;
import xyz.yluo.ruisiapp.utils.ViewUtil;


public class SmileyInputRoot extends LinearLayout {

    private int mOldHeight = -1;
    private int mStatusBarHeight;
    private PanelViewRoot mPanelLayout;
    private boolean mIsTranslucentStatus;


    public SmileyInputRoot(Context context) {
        super(context);
        init();
    }

    public SmileyInputRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SmileyInputRoot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        this.mStatusBarHeight = ViewUtil.getStatusBarHeight(getContext());
        final Activity activity = (Activity) getContext();
        this.mIsTranslucentStatus = ViewUtil.isTranslucentStatus(activity);

        mPanelLayout = new PanelViewRoot(activity);
        mPanelLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DimmenUtils.dip2px(activity, 200)));
        mPanelLayout.setBackgroundColor(Color.parseColor("#fffefefe"));
        mPanelLayout.setVisibility(GONE);
        addView(mPanelLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        handleBeforeMeasure(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 记录总高度
        int mTotalHeight = 0;
        // 遍历所有子视图
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (!(childView instanceof PanelViewRoot)) {
                // 获取在onMeasure中计算的视图尺寸
                int measureHeight = childView.getMeasuredHeight();
                int measuredWidth = childView.getMeasuredWidth();
                childView.layout(l, mTotalHeight, measuredWidth, mTotalHeight + measureHeight);
                mTotalHeight += measureHeight;
            }
        }

        if (mPanelLayout != null) {
            mPanelLayout.layout(l, mTotalHeight, r, b);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void handleBeforeMeasure(final int width, int height) {
        if (mIsTranslucentStatus) {
            if (getFitsSystemWindows()) {
                final Rect rect = new Rect();
                getWindowVisibleDisplayFrame(rect);
                height = rect.bottom - rect.top;
            }
        }

        if (height < 0) {
            return;
        }

        if (mOldHeight < 0) {
            mOldHeight = height;
            return;
        }

        final int offset = mOldHeight - height;

        if (offset == 0) {
            return;
        }

        if (Math.abs(offset) == mStatusBarHeight) {
            return;
        }

        mOldHeight = height;

        if (mPanelLayout == null) {
            return;
        }

        // 检测到布局变化非键盘引起
        if (Math.abs(offset) < DimmenUtils.dip2px(getContext(), 80)) {
            return;
        }

        if (offset > 0) {
            //键盘弹起 (offset > 0，高度变小)
            mPanelLayout.handleHide();
        } else if (mPanelLayout.isKeyboardShowing()) {
            if (mPanelLayout.isVisible()) {
                mPanelLayout.handleShow();
            }
        }
    }

    public PanelViewRoot getmPanelLayout() {
        return mPanelLayout;
    }
}
