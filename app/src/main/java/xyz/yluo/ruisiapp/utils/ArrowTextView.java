package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-21.
 *
 */
public class ArrowTextView extends TextView{


    public ArrowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArrowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ArrowTextView(Context context) {
        super(context);
    }


    private int color = ContextCompat.getColor(getContext(), R.color.bluegrey50);;

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setColor(color == 0 ? Color.RED : color);
        paint.setAntiAlias(true);


        float arrowInHeight = 10;
        canvas.drawRoundRect(new RectF(0, arrowInHeight, getWidth(),getHeight()), 4, 4, paint);

        //画三角形
        Path path=new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        float xMiddle = getWidth()/2;

        path.moveTo(80,0);
        path.lineTo(68, arrowInHeight);
        path.lineTo(92, arrowInHeight);
        path.lineTo(80, 0);
        path.close();
        canvas.drawPath(path,paint);

        super.onDraw(canvas);

    }

}
