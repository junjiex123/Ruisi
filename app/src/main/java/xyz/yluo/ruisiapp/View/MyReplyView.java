package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.listener.ReplyBarListner;
import xyz.yluo.ruisiapp.utils.PostHander;

/**
 * Created by free2 on 16-4-28.
 * 自定义回复框View
 */
public class MyReplyView extends LinearLayout{
    private ReplyBarListner listener;
    @Bind(R.id.input_aera)
    protected EditText input;
    @Bind(R.id.smiley_container)
    protected LinearLayout smiley_container;

    public MyReplyView(Context context) {
        super(context);
        init();
    }

    public MyReplyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyReplyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setListener(ReplyBarListner listener) {
        this.listener = listener;
    }

    private void init(){

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View v =  LayoutInflater.from(getContext()).inflate(R.layout.reply_bar,this,false);
        ButterKnife.bind(this,v);
        addView(v);
    }

    @OnClick(R.id.action_send)
    protected void send_click(){
        smiley_container.setVisibility(View.GONE);
        String text = input.getText().toString();
        int len =0;
        try {
            len = text.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(len==0){
            input.setError("你还没写内容呢!");
        }else if(len<13){
            input.setError("字数不够啊,最少13个字节!");
        }else {
            listener.btnSendClick(text);
        }
    }

    @OnClick({R.id._1000, R.id._1001,R.id._1002,R.id._1003,R.id._1005,
            R.id._1006,R.id._1007,R.id._1008,R.id._1009,R.id._1010,
            R.id._1011,R.id._1012,R.id._1013,R.id._1014,R.id._1015,
            R.id._1016,R.id._1017,R.id._1018,R.id._1019,R.id._1020,
            R.id._1021,R.id._1022,R.id._1023,R.id._1024,R.id._1025,
            R.id._1027,R.id._1028,R.id._1029,R.id._1030, R.id._998,
            R.id._999,R.id._9998,R.id._9999
    })
    protected void smiley_click(ImageButton btn){
        //插入表情
        //{:16_1021:}
        String tmp = btn.getTag().toString();
        PostHander hander = new PostHander(getContext(),input);
        hander.insertSmiley("{:16" + tmp + ":}", btn.getDrawable());
    }

    @OnClick(R.id.action_smiley)
    protected void smiley_click(){
        if(smiley_container.getVisibility()==View.VISIBLE){
            smiley_container.setVisibility(View.GONE);
        }else{
            smiley_container.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.input_aera)
    protected void input_aera_click(){
        smiley_container.setVisibility(View.GONE);
    }

    public void clearText(){
        input.setText("");
        smiley_container.setVisibility(View.GONE);
    }

    public boolean hideSmiley() {
        if (smiley_container.getVisibility() == View.VISIBLE) {
            smiley_container.setVisibility(View.GONE);
            return true;
        }else{
            return false;
        }
    }


}
