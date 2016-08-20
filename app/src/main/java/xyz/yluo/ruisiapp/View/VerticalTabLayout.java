package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-7-15.
 * 我的自定竖直tablayout
 */

public class VerticalTabLayout extends ViewGroup {

    /**
     * onmeasure>>onlayout>>ondraw
     */

    private int indicateColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
    private int indicateWidth = 8;
    private int currentSelect = 0;


    private Paint paint = new Paint();
    public VerticalTabLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public VerticalTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }


    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        //chat_bg_left、top以及右下角right、bottom
        // 动态获取子View实例
        left = left+getPaddingStart();
        right = right-getPaddingEnd();
        top = top+getPaddingTop();
        bottom = bottom-getPaddingBottom();

        int singleHeight = (bottom-top)/getChildCount();

        for (int index = 0, size = getChildCount(); index < size; index++) {
            View view = getChildAt(index);
            view.layout(left, top+index*singleHeight, right-indicateWidth, top+(index+1)*singleHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //viewgroup 默认不会调用此方法
        //setWillNotDraw(false) 打开
        int count = getChildCount();
        float singleHeigle = getHeight()/count;
        paint.setColor(indicateColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawRect(getWidth()-indicateWidth,currentSelect*singleHeigle,getWidth(),((currentSelect+1)*singleHeigle),paint);
        super.onDraw(canvas);
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int index);
        void onTabSelectedChanged(int index);
    }

    public void setOnTabSelectedListener(@NonNull final OnTabSelectedListener listener) {
        for(int i=0;i<getChildCount();i++){
            View cv = getChildAt(i);
            final int id = i;
            cv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onTabSelected(id);
                    if(currentSelect!=id){
                        currentSelect = id;
                        listener.onTabSelectedChanged(id);
                        invalidate();
                    }
                }
            });
        }
    }
}
