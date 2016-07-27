package xyz.yluo.ruisiapp.View;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.DimmenUtils;

/**
 * Created by free2 on 16-7-27.
 *
 */


public class MyToolBar extends LinearLayout implements View.OnClickListener{

    private boolean isHomeEnable = false;
    private boolean isNaviEnable = false;
    private boolean isSetTitleMargin  = false;

    private Context context;
    private int toolBarheight = 0;
    private int toolBarPadding = 0;
    private int toolBarSmallPadding = 0;
    private TextView title;
    private OnToolBarItemClick listener;

    public MyToolBar(Context context) {
        super(context);
        init(context);
    }

    public MyToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        toolBarPadding = DimmenUtils.dip2px(context,12);
        toolBarheight = DimmenUtils.dip2px(context,48);
        toolBarSmallPadding = DimmenUtils.dip2px(context,8);

        setOrientation(LinearLayout.HORIZONTAL);// 水平布局
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);
        setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams lpt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,1);
        title = new TextView(context);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setSingleLine(true);
        lpt.setMarginStart(toolBarSmallPadding);
        isSetTitleMargin = true;
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setTextColor(Color.WHITE);
        title.setText(context.getString(R.string.app_name));
        addView(title,lpt);
    }


    public void addView(View v){
        if(v!=null){
            if(isHomeEnable||isNaviEnable){
                addView(v,1);
            }else{
                addView(v,0);
            }
        }
    }

    public void setTitle(String title) {
        if(!isNaviEnable&&!isHomeEnable&&!isSetTitleMargin){
            LinearLayout.LayoutParams lpt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,1);
            lpt.setMarginStart(toolBarSmallPadding);
            this.title.setLayoutParams(lpt);
            isSetTitleMargin = true;
        }
        this.title.setText(title);
    }

    public ImageView setIcon(int resId){
        if(isSetTitleMargin){
            LinearLayout.LayoutParams lpt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,1);
            this.title.setLayoutParams(lpt);
            isSetTitleMargin = false;
        }
        isNaviEnable = true;
        ImageView iv ;
        if(isHomeEnable){
            iv = (ImageView) findViewWithTag("HOME");
            iv.setTag("NAVIGATION");
            iv.setImageResource(resId);
        }else{
            iv  = new ImageView(context);
            iv.setLayoutParams(new LinearLayout.LayoutParams(toolBarheight, toolBarheight));
            iv.setPadding(toolBarPadding,toolBarPadding,toolBarSmallPadding,toolBarPadding);
            int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            iv.setBackgroundResource(backgroundResource);
            typedArray.recycle();
            iv.setTag("NAVIGATION");
            iv.setImageResource(resId);
            addView(iv,0);
        }
        iv.setOnClickListener(this);
        return iv;
    }

    public void setHomeEnable(final Activity activity) {
        if(isSetTitleMargin){
            LinearLayout.LayoutParams lpt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,1);
            this.title.setLayoutParams(lpt);
            isSetTitleMargin = false;
        }

        isHomeEnable = true;
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(toolBarheight, toolBarheight));
        imageView.setPadding(toolBarPadding,toolBarPadding,toolBarPadding,toolBarPadding);
        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        imageView.setBackgroundResource(backgroundResource);
        imageView.setTag("HOME");
        imageView.setImageResource(R.drawable.ic_arraw_back_white);
        typedArray.recycle();
        addView(imageView,0);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
    }

    public void addMenu(int resId,String Tag){
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(toolBarheight, toolBarheight));
        imageView.setPadding(toolBarSmallPadding,toolBarPadding,toolBarPadding,toolBarPadding);
        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        imageView.setBackgroundResource(backgroundResource);
        imageView.setTag(Tag);
        imageView.setImageResource(resId);
        typedArray.recycle();
        int count = getChildCount();
        addView(imageView,count);
        imageView.setOnClickListener(this);
    }

    public void addButton(String name,int bg,String Tag){
        Button button = new Button(context);
        button.setText(name);
        button.setTag(Tag);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        button.setTextColor(Color.WHITE);
        button.setBackgroundResource(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DimmenUtils.dip2px(context,52),DimmenUtils.dip2px(context,30));
        lp.setMarginEnd(toolBarPadding);
        button.setLayoutParams(lp);
        int count = getChildCount();
        addView(button,count);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String Tag = (String) view.getTag();
        if(listener!=null){
            listener.OnItemClick(view,Tag);
        }
    }

    public void setToolBarClickListener(OnToolBarItemClick listener){
        this.listener = listener;
    }

    public interface OnToolBarItemClick{
        void OnItemClick(View v,String Tag);
    }
}

