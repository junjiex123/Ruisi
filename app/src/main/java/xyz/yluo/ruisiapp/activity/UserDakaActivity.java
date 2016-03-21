package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;
import xyz.yluo.ruisiapp.utils.ConfigClass;

/**
 * Created by free2 on 16-3-15.
 *
 */
public class UserDakaActivity extends AppCompatActivity{

    @Bind(R.id.group_1)
    protected RadioGroup group_1;

    @Bind(R.id.group_2)
    protected RadioGroup group_2;

    @Bind(R.id.input)
    protected TextView input;

    @Bind(R.id.spinner_select)
    protected Spinner spinner_select;
    @Bind(R.id.btn_submit)
    protected Button btn_submit;

    @Bind(R.id.ll_daka)
    protected LinearLayout ll_daka;

    @Bind(R.id.text_have_daka)
    protected TextView text_have_daka;

    @Bind(R.id.test)
    protected TextView testView;

    private int group1_select = 1;
    private int group2_select = 1;
    private int spinner__select = 0;

    private String qdxq = "ng";
    private boolean isdaka = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_daka);
        ButterKnife.bind(this);

        ll_daka.setVisibility(View.GONE);
        text_have_daka.setVisibility(View.GONE);
        isHaveDaka();
        group_2.check(R.id.btn1);
        group_1.check(R.id.radiobtn_01);
        spinner_select.setVisibility(View.GONE);


        final String[] mItems = {"新的一天，新的开始~","今天要更加努力哦~~","我要成为BT大神~~~","今天要怒冲水神榜！！","有点小忧伤~","每天的太阳都是新的！","我又回来了！！！","来吧骚年，战个痛快！！"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_select.setAdapter(adapter);
        spinner_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                spinner__select = pos;
                //Toast.makeText(getApplicationContext(), "你点击的是:" + mItems[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        //RadioButton radioButton = (RadioButton)findViewById(group_1.getCheckedRadioButtonId());

        group_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.btn1:
                        input.setVisibility(View.VISIBLE);
                        spinner_select.setVisibility(View.GONE);
                        group2_select = 1;
                    break;
                    case R.id.btn2:
                        input.setVisibility(View.GONE);
                        spinner_select.setVisibility(View.VISIBLE);
                        group2_select = 2;
                    break;
                    case R.id.btn3:
                        input.setVisibility(View.GONE);
                        spinner_select.setVisibility(View.GONE);
                        group2_select = 3;
                    break;
                }

            }
        });
    }

    @OnClick(R.id.btn_submit)
    protected void btn_submit_click(){
        boolean isok = false;
        getGroup1_select();
        Toast.makeText(getApplicationContext(),"group1:"+group1_select+"group2:"+group2_select+"spin:"+spinner__select,Toast.LENGTH_SHORT).show();

        String formhash = ConfigClass.CONFIG_FORMHASH;
        qdxq = "ng";
        String qdmode = "1";
        String todaysay = "";
        String fastreplay = "0";


        switch (group2_select){
            case 1:
                //自己填写
//                qdmode:1
//                todaysay:这是一个测试签到
                qdmode = "1";
                try {
                    String tmp =   input.getText().toString();
                    int len  = tmp.getBytes("UTF-8").length;
                    if(len<3){
                        //len = text.getBytes("UTF-8").length;
                        Toast.makeText(getApplicationContext(),"字数不够",Toast.LENGTH_SHORT).show();
                        isok = false;
                    }else{
                        todaysay = input.getText().toString()+"  --来自睿思手机客户端";
                        isok = true;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                //快速选择
//                qdmode:2
//                fastreply:5
                qdmode = "2";
                fastreplay = ""+spinner__select;
                isok = true;
                break;

            case 3:
                //不想填写
//                qdmode:3
                qdmode = "3";
                isok = true;
                break;
        }

        if(isok){
            RequestParams params = new RequestParams();
            params.add("formhash",formhash);
            params.add("qdxq",qdxq);
            params.add("qdmode",qdmode);
            params.add("todaysay",todaysay);
            params.add("fastreplay",fastreplay);
            String url = "plugin.php?id=dsu_paulsign:sign&operation=qiandao&infloat=1&inajax=1";
            AsyncHttpCilentUtil.post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String res = new String(responseBody);
                    if(res.contains("恭喜你签到成功")){
                        Document doc = Jsoup.parse(res);
                        String get = doc.select("div[class=c]").text();
                        Toast.makeText(getApplicationContext(),get,Toast.LENGTH_SHORT).show();
                        ConfigClass.CONFIG_ISDAKA = true;

                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"未知错误",Toast.LENGTH_SHORT).show();
                    }
                    //testView.setText(new String(responseBody));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void isHaveDaka(){
        String urlget =   "plugin.php?id=dsu_paulsign:sign";
        AsyncHttpCilentUtil.get(getApplicationContext(), urlget, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);

                if(res.contains("您今天已经签到过了或者签到时间还未开始")){
                    isdaka = true;
                    ll_daka.setVisibility(View.GONE);
                    ConfigClass.CONFIG_ISDAKA = true;
                    text_have_daka.setVisibility(View.VISIBLE);

                }else{
                    isdaka = false;
                    ll_daka.setVisibility(View.VISIBLE);
                    ConfigClass.CONFIG_ISDAKA = false;
                    text_have_daka.setVisibility(View.GONE);
                }

                Toast.makeText(getApplicationContext(),"      "+isdaka,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getGroup1_select(){
        switch (group_1.getCheckedRadioButtonId()){
            case R.id.radiobtn_01:
                group1_select =1;
                qdxq = "kx";
                break;
            case R.id.radiobtn_02:
                group1_select =2;
                qdxq = "ng";
                break;
            case R.id.radiobtn_03:
                group1_select =3;
                qdxq = "ym";
                break;
            case R.id.radiobtn_04:
                group1_select =4;
                qdxq = "wl";
                break;
            case R.id.radiobtn_05:
                group1_select =5;
                qdxq = "nu";
                break;
            case R.id.radiobtn_06:
                group1_select =6;
                qdxq = "ch";
                break;
            case R.id.radiobtn_07:
                group1_select =7;
                qdxq = "fd";
                break;
            case R.id.radiobtn_08:
                group1_select =8;
                qdxq = "yl";
                break;
            case R.id.radiobtn_09:
                qdxq = "shuai";
                group1_select =9;
                break;
        }
    }
}
