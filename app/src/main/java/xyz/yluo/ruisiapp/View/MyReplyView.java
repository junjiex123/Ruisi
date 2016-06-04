package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SmileyAdapter;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.listener.ReplyBarListner;
import xyz.yluo.ruisiapp.utils.PostHander;

/**
 * Created by free2 on 16-4-28.
 * 自定义回复框View
 */
public class MyReplyView extends LinearLayout implements View.OnClickListener{
    private ReplyBarListner listener;
    private final int SMILEY_TB = 1;
    private final int SMILEY_LDB = 2;
    private final int SMILEY_ACN = 3;
    int smiley_type  =SMILEY_TB;
    EditText input;
    LinearLayout smiley_container;
    RecyclerView smiley_listv;
    ImageView btn_send;
    RadioGroup smiley_change;
    SmileyAdapter adapter;

    public MyReplyView(Context context) {
        super(context);
    }

    public MyReplyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyReplyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(ReplyBarListner listener) {
        this.listener = listener;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View v =  LayoutInflater.from(getContext()).inflate(R.layout.reply_bar,this,false);

        input = (EditText) v.findViewById(R.id.input_aera);
        smiley_change =  (RadioGroup) v.findViewById(R.id.smiley_change);
        smiley_change.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                Log.i("smiley check","id "+id);

                switch (id){
                    case R.id.smiley_tb:
                        smiley_type = SMILEY_TB;
                        break;
                    case R.id.smiley_ldb:
                        smiley_type = SMILEY_LDB;
                        break;
                    case R.id.smiley_acn:
                        smiley_type = SMILEY_ACN;
                        break;
                }

                changeSmiley();
            }
        });
        smiley_listv = (RecyclerView) v.findViewById(R.id.smiley_list);
        smiley_container = (LinearLayout) v.findViewById(R.id.smileys_container);
        btn_send = (ImageView) v.findViewById(R.id.action_send);
        input.setOnClickListener(this);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               if(!TextUtils.isEmpty(input.getText())){
                   btn_send.setImageResource(R.drawable.ic_menu_send_active);
                }else{
                   btn_send.setImageResource(R.drawable.ic_menu_send);
               }
            }
        });
        v.findViewById(R.id.action_smiley).setOnClickListener(this);
        btn_send.setOnClickListener(this);

        addView(v);

        ds = getSmileys();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3,HORIZONTAL,false);
        adapter = new SmileyAdapter(new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                insertSmiley(position);
            }
        }, ds);
        smiley_listv.setLayoutManager(layoutManager);
        smiley_listv.setAdapter(adapter);

    }

    private void changeSmiley(){

        Log.i("smiley type","type "+smiley_type);
        ds.clear();
        ds = getSmileys();
        adapter.notifyDataSetChanged();
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
        String smiley_dir = "static/image/smiley/";
        if(smiley_type==SMILEY_TB){
            smiley_dir+="tieba";
        }else if(smiley_type==SMILEY_LDB){
            smiley_dir+="lindab";
        }else if(smiley_type==SMILEY_ACN){
            smiley_dir+="acn";
        }

        try {
            nameList =  getContext().getAssets().list(smiley_dir);
            for(String temp:nameList){
                InputStream in = getContext().getAssets().open(smiley_dir+"/"+temp);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                Drawable d = new BitmapDrawable(getContext().getResources(),bitmap);
                d.setBounds(0,0, (int) (bitmap.getWidth()*1.2f), (int) (bitmap.getHeight()*1.2f));
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

        Log.i("click smiley name","name "+name);
        if(smiley_type==SMILEY_TB){
            switch (name){
                case "tb001.png":
                    insertName = "16_1014";
                    break;
                case "tb002.png":
                    insertName = "16_1006";
                    break;
                case "tb003.png":
                    insertName = "16_1018";
                    break;
                case "tb004.png":
                    insertName = "16_1001";
                    break;
                case "tb005.png":
                    insertName = "16_1019";
                    break;
                case "tb006.png":
                    insertName = "16_1025";
                    break;
                case "tb007.png":
                    insertName = "16_9999";
                    break;
                case "tb008.png":
                    insertName = "16_1013";
                    break;
                case "tb009.png":
                    insertName = "16_1024";
                    break;
                case "tb010.png":
                    insertName = "16_1020";
                    break;
                case "tb011.png":
                    insertName = "16_1022";
                    break;
                case "tb012.png":
                    insertName = "16_1000";
                    break;
                case "tb013.png":
                    insertName = "16_999";
                    break;
                case "tb014.png":
                    insertName = "16_998";
                    break;
                case "tb015.png":
                    insertName = "16_1028";
                    break;
                case "tb016.png":
                    insertName = "16_1017";
                    break;
                case "tb017.png":
                    insertName = "16_1023";
                    break;
                case "tb018.png":
                    insertName = "16_1009";
                    break;
                case "tb019.png":
                    insertName = "16_1030";
                    break;
                case "tb020.png":
                    insertName = "16_1007";
                    break;
                case "tb021.png":
                    insertName = "16_1015";
                    break;
                case "tb022.png":
                    insertName = "16_1011";
                    break;
                case "tb023.png":
                    insertName = "16_1029";
                    break;
                case "tb024.png":
                    insertName = "16_1003";
                    break;
                case "tb025.png":
                    insertName = "16_1016";
                    break;
                case "tb026.png":
                    insertName = "16_1008";
                    break;
                case "tb027.png":
                    insertName = "16_1002";
                    break;
                case "tb028.png":
                    insertName = "16_1005";
                    break;
                case "tb029.png":
                    insertName = "16_1027";
                    break;
                case "tb030.png":
                    insertName = "16_1010";
                    break;
                case "tb031.png":
                    insertName = "16_1012";
                    break;
                case "tb032.png":
                    insertName = "16_1021";
                    break;
                case "tb033.png":
                    insertName = "16_9998";
                    break;
            }
        }else if(smiley_type==SMILEY_LDB){
            switch (name){
                case "ldb033.jpg":
                    insertName = "14_860";
                    break;
                case "ldb074.jpg":
                    insertName = "14_871";
                    break;
                case "ldb028.jpg":
                    insertName = "14_872";
                    break;
                case "ldb079.jpg":
                    insertName = "14_873";
                    break;
                case "ldb038.jpg":
                    insertName = "14_874";
                    break;
                case "ldb061.jpg":
                    insertName = "14_875";
                    break;
                case "ldb036.jpg":
                    insertName = "14_876";
                    break;
                case "ldb062.jpg":
                    insertName = "14_877";
                    break;
                case "ldb023.jpg":
                    insertName = "14_878";
                    break;
                case "ldb007.jpg":
                    insertName = "14_870";
                    break;
                case "ldb052.jpg":
                    insertName = "14_869";
                    break;
                case "ldb050.jpg":
                    insertName = "14_861";
                    break;


                case "ldb032.jpg":
                    insertName = "14_862";
                    break;
                case "ldb016.jpg":
                    insertName = "14_863";
                    break;
                case "ldb082.jpg":
                    insertName = "14_864";
                    break;
                case "ldb039.jpg":
                    insertName = "14_865";
                    break;
                case "ldb043.jpg":
                    insertName = "14_866";
                    break;
                case "ldb019.jpg":
                    insertName = "14_867";
                    break;
                case "ldb060.jpg":
                    insertName = "14_868";
                    break;
                case "ldb046.jpg":
                    insertName = "14_879";
                    break;
                case "ldb002.jpg":
                    insertName = "14_880";
                    break;
                case "ldb055.jpg":
                    insertName = "14_881";
                    break;
                case "ldb080.jpg":
                    insertName = "14_882";
                    break;
                case "ldb073.jpg":
                    insertName = "14_883";
                    break;


                case "ldb040.jpg":
                    insertName = "14_894";
                    break;
                case "ldb020.jpg":
                    insertName = "14_895";
                    break;
                case "ldb008.jpg":
                    insertName = "14_896";
                    break;
                case "ldb024.jpg":
                    insertName = "14_897";
                    break;
                case "ldb042.jpg":
                    insertName = "14_898";
                    break;
                case "ldb018.jpg":
                    insertName = "14_899";
                    break;

                case "ldb034.jpg":
                    insertName = "14_891";
                    break;
                case "ldb041.jpg":
                    insertName = "14_890";
                    break;
                case "ldb083.jpg":
                    insertName = "14_882";
                    break;
                case "ldb067.jpg":
                    insertName = "14_883";
                    break;
                case "ldb025.jpg":
                    insertName = "14_884";
                    break;
                case "ldb063.jpg":
                    insertName = "14_885";
                    break;



                case "ldb078.jpg":
                    insertName = "14_886";
                    break;
                case "ldb065.jpg":
                    insertName = "14_887";
                    break;
                case "ldb004.jpg":
                    insertName = "14_888";
                    break;
                case "ldb081.jpg":
                    insertName = "14_889";
                    break;
                case "ldb006.jpg":
                    insertName = "14_900";
                    break;
                case "ldb026.jpg":
                    insertName = "14_859";
                    break;
                case "ldb017.jpg":
                    insertName = "14_818";
                    break;
                case "ldb011.jpg":
                    insertName = "14_829";
                    break;
                case "ldb014.jpg":
                    insertName = "14_830";
                    break;
                case "ldb070.jpg":
                    insertName = "14_831";
                    break;
                case "ldb056.jpg":
                    insertName = "14_832";
                    break;
                case "ldb021.jpg":
                    insertName = "14_833";
                    break;



                case "ldb010.jpg":
                    insertName = "14_834";
                    break;
                case "ldb045.jpg":
                    insertName = "14_835";
                    break;
                case "ldb076.jpg":
                    insertName = "14_836";
                    break;
                case "ldb005.jpg":
                    insertName = "14_828";
                    break;
                case "ldb069.jpg":
                    insertName = "14_827";
                    break;
                case "ldb030.jpg":
                    insertName = "14_819";
                    break;
                case "ldb071.jpg":
                    insertName = "14_820";
                    break;
                case "ldb044.jpg":
                    insertName = "14_821";
                    break;
                case "ldb027.jpg":
                    insertName = "14_822";
                    break;
                case "ldb013.jpg":
                    insertName = "14_823";
                    break;
                case "ldb015.jpg":
                    insertName = "14_824";
                    break;
                case "ldb072.jpg":
                    insertName = "14_825";
                    break;



                case "ldb054.jpg":
                    insertName = "14_826";
                    break;
                case "ldb049.jpg":
                    insertName = "14_837";
                    break;
                case "ldb068.jpg":
                    insertName = "14_838";
                    break;
                case "ldb059.jpg":
                    insertName = "14_839";
                    break;
                case "ldb075.jpg":
                    insertName = "14_850";
                    break;
                case "ldb031.jpg":
                    insertName = "14_851";
                    break;
                case "ldb064.jpg":
                    insertName = "14_852";
                    break;
                case "ldb037.jpg":
                    insertName = "14_853";
                    break;
                case "ldb003.jpg":
                    insertName = "14_854";
                    break;
                case "ldb012.jpg":
                    insertName = "14_855";
                    break;
                case "ldb035.jpg":
                    insertName = "14_856";
                    break;
                case "ldb022.jpg":
                    insertName = "14_857";
                    break;


                case "ldb001.jpg":
                    insertName = "14_849";
                    break;
                case "ldb057.jpg":
                    insertName = "14_848";
                    break;
                case "ldb048.jpg":
                    insertName = "14_840";
                    break;
                case "ldb077.jpg":
                    insertName = "14_841";
                    break;
                case "ldb009.jpg":
                    insertName = "14_842";
                    break;
                case "ldb053.jpg":
                    insertName = "14_843";
                    break;
                case "ldb029.jpg":
                    insertName = "14_844";
                    break;
                case "ldb047.jpg":
                    insertName = "14_845";
                    break;
                case "ldb066.jpg":
                    insertName = "14_846";
                    break;
                case "ldb051.jpg":
                    insertName = "14_847";
                    break;
                case "ldb058.jpg":
                    insertName = "14_858";
                    break;

            }
        }else if(smiley_type==SMILEY_ACN){
            //
        }


        PostHander hander = new PostHander(input);
        hander.insertSmiley("{:" + insertName + ":}", ds.get(position));
    }

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
        }else{
            if(len<13){
                int need = 14-len;
                for(int i=0;i<need;i++){
                    text+=" ";
                }
            }
            listener.btnSendClick(text);
        }
    }


    protected void smiley_click(){
        if(smiley_container.getVisibility()==View.VISIBLE){
            smiley_container.setVisibility(View.GONE);
        }else{
            smiley_container.setVisibility(View.VISIBLE);
        }
    }

    protected void input_aera_click(){
        smiley_container.setVisibility(View.GONE);
    }

    public void clearText(){
        input.setText("");
        smiley_container.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.action_send:
                send_click();
                break;
            case R.id.action_smiley:
                smiley_click();
                break;
            case R.id.input_aera:
                input_aera_click();
                break;
            default:
                break;
        }
    }
}
