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

    private Paint paint;
    private RectF rectF;
    private float arrowInHeight = 10;
    private Path path;

    public ArrowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArrowTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ArrowTextView(Context context) {
        super(context);
        init();
    }

    private void init(){
        paint=new Paint();
        rectF = new RectF(0, arrowInHeight, getWidth(),getHeight());
        path=new Path();
    }


    private int color = ContextCompat.getColor(getContext(), R.color.bluegrey50);

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(color == 0 ? Color.RED : color);
        paint.setAntiAlias(true);



        canvas.drawRoundRect(rectF, 4, 4, paint);

        //画三角形
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(80,0);
        path.lineTo(68, arrowInHeight);
        path.lineTo(92, arrowInHeight);
        path.lineTo(80, 0);
        path.close();
        canvas.drawPath(path,paint);

        super.onDraw(canvas);

    }

}
