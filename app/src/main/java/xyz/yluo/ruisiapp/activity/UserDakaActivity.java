package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-15.
 * 签到activity
 */
public class UserDakaActivity extends BaseActivity {

    protected TextView input;
    protected CoordinatorLayout main_window;
    protected CircleImageView user_image;
    protected TextView user_name;
    protected ProgressBar progressBar;
    protected Toolbar toolbar;
    private Spinner spinner_select;
    private Button btn_start_sign;
    private LinearLayout have_sign_view, not_sign_view, container;
    private TextView total_sign_day, total_sign_month, info_title;
    private int spinner__select = 0;
    private String qdxq = "kx";
    private boolean isSign = true;
    private String hash = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_daka);

        input = (TextView) findViewById(R.id.input);
        main_window = (CoordinatorLayout) findViewById(R.id.main_window);
        spinner_select = (Spinner) findViewById(R.id.spinner_select);
        btn_start_sign = (Button) findViewById(R.id.btn_start_sign);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        user_name = (TextView) findViewById(R.id.user_name);
        not_sign_view = (LinearLayout) findViewById(R.id.not_sign_view);
        have_sign_view = (LinearLayout) findViewById(R.id.have_sign_view);
        container = (LinearLayout) findViewById(R.id.container);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        total_sign_day = (TextView) findViewById(R.id.total_sign_day);
        total_sign_month = (TextView) findViewById(R.id.total_sign_month);
        info_title = (TextView) findViewById(R.id.info_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_start_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_click();
            }
        });
        user_name.setText(App.USER_NAME);
        Picasso.with(this).load(UrlUtils.getAvaterurlb(App.USER_UID)).placeholder(R.drawable.image_placeholder).into(user_image);
        init();
        isHaveDaka();
        final String[] mItems = {"开心", "难过", "郁闷", "无聊", "怒", "擦汗", "奋斗", "慵懒", "衰"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        spinner_select.setAdapter(adapter);
    }

    private void init() {
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

    //看看是否已经签到
    private void isHaveDaka() {
        container.setVisibility(View.GONE);
        not_sign_view.setVisibility(View.GONE);
        have_sign_view.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String urlget = "plugin.php?id=dsu_paulsign:sign";
        HttpUtil.get(this, urlget, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                Document doc = Jsoup.parse(res);
                String temphash = doc.select("input[name=formhash]").attr("value");
                if (!temphash.isEmpty()) {
                    hash = temphash;
                }
                if (res.contains("您今天已经签到过了或者签到时间还未开始")) {
                    //您今天已经签到过了
                    //获得时间
                    have_sign_view.setVisibility(View.VISIBLE);
                    Calendar c = Calendar.getInstance();
                    int HOUR_OF_DAY = c.get(Calendar.HOUR_OF_DAY);
                    if (7 <= HOUR_OF_DAY && HOUR_OF_DAY < 23) {
                        info_title.setText("今日已签到");
                    } else {
                        info_title.setText("不在签到时间内");
                    }

                    for (Element temp : doc.select(".mn").select("p")) {
                        String temptext = temp.text();
                        if (temptext.contains("您累计已签到")) {
                            int pos = temptext.indexOf("您累计已签到");
                            total_sign_day.setText(temptext.substring(pos));
                        } else if (temptext.contains("您本月已累计签到")) {
                            total_sign_month.setText(temptext);
                        }
                    }

                    isSign = true;
                    not_sign_view.setVisibility(View.GONE);
                    btn_start_sign.setText("返回");
                } else {
                    info_title.setText("今日未签到");
                    isSign = false;
                    not_sign_view.setVisibility(View.VISIBLE);
                    have_sign_view.setVisibility(View.GONE);
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

    //点击签到按钮
    private void sign_click() {
        if (isSign) {
            finish();
        } else {
            String xinqin = getGroup1_select();
            String formhash = hash;
            String qdmode;
            String todaysay = "";
            String fastreplay = "0";

            if (!input.getText().toString().isEmpty()) {
                qdmode = "1";
                todaysay = input.getText().toString() + "  --来自睿思手机客户端";
            } else {
                qdmode = "3";
            }
            Map<String, String> params = new HashMap<>();
            params.put("formhash", formhash);
            params.put("qdxq", xinqin);
            params.put("qdmode", qdmode);
            params.put("todaysay", todaysay);
            params.put("fastreplay", fastreplay);

            String url = UrlUtils.getSignUrl();
            HttpUtil.post(this, url, params, new ResponseHandler() {
                @Override
                public void onSuccess(byte[] response) {
                    String res = new String(response);
                    if (res.contains("恭喜你签到成功")) {
                        showNtice("签到成功,点击按钮返回");
                        info_title.setText("签到成功");
                        isSign = true;
                        btn_start_sign.setText("返回");
                    } else {
                        showNtice("未知错误");
                    }

                    isHaveDaka();
                }

                @Override
                public void onFailure(Throwable e) {
                    showNtice("网络错误!!!!!");
                }
            });
        }
    }

    //获得选择的心情
    private String getGroup1_select() {
        switch (spinner__select) {
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

    private void showNtice(String res) {
        progressBar.setVisibility(View.GONE);
        Snackbar.make(main_window, res, Snackbar.LENGTH_LONG).show();
    }
}
