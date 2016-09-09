package xyz.yluo.ruisiapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.ScaleImageView;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;

public class ViewImgActivity extends BaseActivity implements ViewPager.OnPageChangeListener {


    private ViewPager pager;
    private List<String> datas;
    private String aid = "0";
    private TextView index;
    private MyAdapter adapter;
    private int position = 0;


    public static void open(Context context, String url) {
        Intent intent = new Intent(context, ViewImgActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_img);
        datas = new ArrayList<>();


        pager = (ViewPager) findViewById(R.id.pager);
        index = (TextView) findViewById(R.id.index);
        findViewById(R.id.nav_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pager.addOnPageChangeListener(this);
        adapter = new MyAdapter();
        pager.setAdapter(adapter);

        Bundle b = getIntent().getExtras();
        String url = b.getString("url");

        String tid = GetId.getid("tid=",url);
        aid = GetId.getid("aid=",url);
        String urll = "forum.php?mod=viewthread&tid="
                +tid+"&aid="+aid+"&from=album&mobile=2";
        HttpUtil.get(ViewImgActivity.this, urll, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document doc = Jsoup.parse(new String(response));
                Elements elements = doc.select("ul.postalbum_c").select("li");
                int i = 0;
                for(Element e:elements){
                    String zsrc = e.select("img").attr("zsrc");
                    if(zsrc.contains(aid)){
                        position = i;
                        Log.e("position",position+"====1");
                    }

                    String src = e.select("img").attr("orig");
                    if(TextUtils.isEmpty("src")){
                        continue;
                    }
                    if(src.startsWith("./")){
                        src =  src.substring(2);
                    }
                    if(!src.startsWith("http")){
                        src = App.getBaseUrl()+src;
                    }

                    i++;
                    datas.add(src);
                }
                adapter.notifyDataSetChanged();
                changeIndex(position);
                pager.setCurrentItem(position);
            }
        });
    }

    private void changeIndex(int pos){
        index.setText((pos+1)+"/"+datas.size());
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        position = position % datas.size();
        changeIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    private class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ScaleImageView v = (ScaleImageView) container.findViewWithTag(position);
            if(v==null){
                v = new ScaleImageView(ViewImgActivity.this);
                v.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                v.setLayoutParams(params);
                Picasso.with(ViewImgActivity.this).load(datas.get(position))
                        .placeholder(R.drawable.image_placeholder)
                        .into(v);
                v.setTag(position);
                container.addView(v);
            }
            return v;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }
}
