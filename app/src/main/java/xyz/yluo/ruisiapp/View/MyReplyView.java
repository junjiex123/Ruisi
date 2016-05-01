package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SmileyAdapter;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
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
    @Bind(R.id.smileys_container)
    protected RecyclerView smiley_container;

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

        List<Drawable> ds = getSmileys();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3,HORIZONTAL,false);
        SmileyAdapter adapter = new SmileyAdapter(new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                insertSmiley(position);
            }
        }, ds);
        smiley_container.setLayoutManager(layoutManager);
        smiley_container.setAdapter(adapter);

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

    private String[] nameList;
    private List<Drawable> ds = new ArrayList<>();

    private List<Drawable> getSmileys(){
        try {
            nameList =  getContext().getAssets().list("static/image/smiley/tieba");
            for(String temp:nameList){
                System.out.println(temp);
                InputStream in = getContext().getAssets().open("static/image/smiley/tieba/"+temp);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                Drawable d = new BitmapDrawable(getContext().getResources(),bitmap);
                d.setBounds(0,0,bitmap.getWidth(),bitmap.getHeight());
                ds.add(d);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ds;
    }

    private void insertSmiley(int position){
        if(position>nameList.length){
            return;
        }
        String name = nameList[position];
        String insertName = "";
        switch (name){
            case "tb001.png":
                insertName = "_1014";
                break;
            case "tb002.png":
                insertName = "_1006";
                break;
            case "tb003.png":
                insertName = "_1018";
                break;
            case "tb004.png":
                insertName = "_1001";
                break;
            case "tb005.png":
                insertName = "_1019";
                break;
            case "tb006.png":
                insertName = "_1025";
                break;
            case "tb007.png":
                insertName = "_9999";
                break;
            case "tb008.png":
                insertName = "_1013";
                break;
            case "tb009.png":
                insertName = "_1024";
                break;
            case "tb010.png":
                insertName = "_1020";
                break;
            case "tb011.png":
                insertName = "_1022";
                break;
            case "tb012.png":
                insertName = "_1000";
                break;
            case "tb013.png":
                insertName = "_999";
                break;
            case "tb014.png":
                insertName = "_998";
                break;
            case "tb015.png":
                insertName = "_1028";
                break;
            case "tb016.png":
                insertName = "_1017";
                break;
            case "tb017.png":
                insertName = "_1023";
                break;
            case "tb018.png":
                insertName = "_1009";
                break;
            case "tb019.png":
                insertName = "_1030";
                break;
            case "tb020.png":
                insertName = "_1007";
                break;
            case "tb021.png":
                insertName = "_1015";
                break;
            case "tb022.png":
                insertName = "_1011";
                break;
            case "tb023.png":
                insertName = "_1029";
                break;
            case "tb024.png":
                insertName = "_1003";
                break;
            case "tb025.png":
                insertName = "_1016";
                break;
            case "tb026.png":
                insertName = "_1008";
                break;
            case "tb027.png":
                insertName = "_1002";
                break;
            case "tb028.png":
                insertName = "_1005";
                break;
            case "tb029.png":
                insertName = "_1027";
                break;
            case "tb030.png":
                insertName = "_1010";
                break;
            case "tb031.png":
                insertName = "_1012";
                break;
            case "tb032.png":
                insertName = "_1021";
                break;
            case "tb033.png":
                insertName = "_9998";
                break;
        }

        PostHander hander = new PostHander(input);
        hander.insertSmiley("{:16" + insertName + ":}", ds.get(position));

    }


}
