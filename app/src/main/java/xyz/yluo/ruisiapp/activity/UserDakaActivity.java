package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.CircleImageView;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-15.
 *
 */
public class UserDakaActivity extends AppCompatActivity{

    @Bind(R.id.input)
    protected TextView input;
    @Bind(R.id.main_window)
    protected CoordinatorLayout main_window;
    @Bind(R.id.spinner_select)
    protected Spinner spinner_select;
    @Bind(R.id.btn_start_sign)
    protected FloatingActionButton btn_start_sign;
    @Bind(R.id.user_image)
    protected CircleImageView user_image;
    @Bind(R.id.user_name)
    protected TextView user_name;
    @Bind(R.id.View_have_sign)
    protected LinearLayout View_have_sign;
    @Bind(R.id.View_have_sign_2)
    protected LinearLayout View_have_sign_2;
    @Bind(R.id.View_not_sign)
    protected LinearLayout View_not_sign;
    @Bind(R.id.container)
    protected LinearLayout container;
    @Bind(R.id.progressBar)
    protected ProgressBar progressBar;
    @Bind(R.id.total_sign_day)
    protected TextView total_sign_day;
    @Bind(R.id.total_sign_month)
    protected TextView total_sign_month;
    @Bind(R.id.information)
    protected TextView information;
    @Bind(R.id.info_title)
    protected TextView info_title;

    private int spinner__select = 0;
    private String qdxq  = "kx";
    private boolean isSign = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_daka);
        ButterKnife.bind(this);
        user_name.setText(MySetting.CONFIG_USER_NAME);
        Picasso.with(this).load(UrlUtils.getimageurl(MySetting.CONFIG_USER_UID,true)).placeholder(R.drawable.image_placeholder).into(user_image);
        init();
        isHaveDaka();
        final String[] mItems = {"开心","难过","郁闷","无聊","怒","擦汗","奋斗","慵懒","衰"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_select.setAdapter(adapter);
    }

    private void init(){
        container.setVisibility(View.GONE);
        View_not_sign.setVisibility(View.GONE);
        View_have_sign.setVisibility(View.GONE);
        View_have_sign_2.setVisibility(View.GONE);

        spinner_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                spinner__select = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @OnClick(R.id.btn_start_sign)
    protected void btn_start_sign_click(){
        if(isSign){
            finish();
        }else{
            String xinqin = getGroup1_select();
            String formhash = MySetting.CONFIG_FORMHASH;
            String qdmode = "1";
            String todaysay = "";
            String fastreplay = "0";

            if(!input.getText().toString().isEmpty()){
                qdmode = "1";
                todaysay = input.getText().toString()+"  --来自睿思手机客户端";
            }else {
                qdmode = "3";
            }
            Map<String,String> params = new HashMap<>();
            params.put("formhash",formhash);
            params.put("qdxq",xinqin);
            params.put("qdmode",qdmode);
            params.put("todaysay",todaysay);
            params.put("fastreplay",fastreplay);

            String url = UrlUtils.getSignUrl();
            HttpUtil.post(this, url, params, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    String res = new String(response);
                    if(res.contains("恭喜你签到成功")){
                        Document doc = Jsoup.parse(res);
                        String get = doc.select("div[class=c]").text();
                        showNtice("签到成功!!!");
                        info_title.setText("恭喜你签到成功");
                        isSign = true;
                        System.out.println(get);
                        btn_start_sign.setImageResource(R.drawable.ic_arrow_back_24dp);
                    }else{
                        showNtice("未知错误");
                    }
                }
                @Override
                public void onFailure(Throwable e) {
                    showNtice("网络错误!!!!!");
                }
            });
        }
    }

    private void isHaveDaka(){
        String urlget =   "plugin.php?id=dsu_paulsign:sign";
        HttpUtil.get(this, urlget, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                Document doc = Jsoup.parse(res);
                if (doc.select("input[name=formhash]").first() != null) {
                    String temphash = doc.select("input[name=formhash]").attr("value");
                    if(!temphash.isEmpty()){
                        MySetting.CONFIG_FORMHASH = temphash;
                    }
                }

                if(res.contains("您今天已经签到过了或者签到时间还未开始")){
                    //您今天已经签到过了
                    //获得时间
                    Calendar c = Calendar.getInstance();
                    int HOUR_OF_DAY = c.get(Calendar.HOUR_OF_DAY);
                    if(7<=HOUR_OF_DAY&&HOUR_OF_DAY<23){
                        info_title.setText("您今天已经签到过了");
                    }else {
                        info_title.setText("今天的签到还没开始呢");
                    }
                    System.out.println(HOUR_OF_DAY);

                    for(Element temp:doc.select(".mn").select("p")){
                        String temptext = temp.text();
                        if(temptext.contains("您累计已签到")){
                            int pos = temptext.indexOf("您累计已签到");
                            total_sign_day.setText(temptext.substring(pos));
                        }else if(temptext.contains("您本月已累计签到")){
                            total_sign_month.setText(temptext);
                        }else {
                            String newString = information.getText().toString()+"\n"+temptext;
                            information.setText(newString);
                        }
                    }

                    isSign = true;
                    View_have_sign.setVisibility(View.VISIBLE);
                    View_have_sign_2.setVisibility(View.VISIBLE);
                    View_not_sign.setVisibility(View.GONE);
                    btn_start_sign.setImageResource(R.drawable.ic_arrow_back_24dp);
                }else{
                    isSign = false;
                    View_not_sign.setVisibility(View.VISIBLE);
                    View_have_sign.setVisibility(View.GONE);
                    View_have_sign_2.setVisibility(View.GONE);
                }
                container.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable e) {
                showNtice("网络错误");
            }
        });
    }

    private String getGroup1_select(){
        switch (spinner__select){
            case 0:
                qdxq = "kx";
                break;
            case 1:
                qdxq = "ng";
                break;
            case 2:
                qdxq = "ym";
                break;
            case 3:
                qdxq = "wl";
                break;
            case 4:
                qdxq = "nu";
                break;
            case 5:
                qdxq = "ch";
                break;
            case 6:
                qdxq = "fd";
                break;
            case 7:
                qdxq = "yl";
                break;
            case 8:
                qdxq = "shuai";
                break;
        }
        return qdxq;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNtice(String res){

        progressBar.setVisibility(View.GONE);
        Snackbar.make(main_window, res, Snackbar.LENGTH_LONG).show();
    }
}
