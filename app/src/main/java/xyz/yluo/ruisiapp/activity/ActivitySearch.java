package xyz.yluo.ruisiapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.MyPublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.data.SimpleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;

/**
 * Created by free2 on 16-4-6.
 * 搜索
 */
public class ActivitySearch extends BaseActivity {

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.search_input)
    protected EditText search_input;
    @Bind(R.id.main_window)
    protected LinearLayout main_window;
    @Bind(R.id.refresh_view)
    protected SwipeRefreshLayout refresh_view;

    private SimpleListAdapter adapter;

    private ActionBar actionBar;
    private List<SimpleListData> datas = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        adapter = new SimpleListAdapter(this, datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);

        String search_string = "";
        try {
            search_string = getIntent().getExtras().getString("res");
        }catch (Exception e){
            e.printStackTrace();
        }
        if (search_string!=null){
            getData(search_string);
            actionBar = getSupportActionBar();
            if(actionBar!=null){
                actionBar.setTitle("搜索");
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            search_input.setText(search_string);
        }

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datas.clear();
                if(search_input.getText().toString().equals("")){
                    refresh_view.setRefreshing(false);
                }else {
                    start_search_click();
                }
            }
        });

    }

    @OnClick(R.id.start_search)
    protected void start_search_click(){
        datas.clear();
        if (search_input.getText().toString().isEmpty()){
            Snackbar.make(main_window,"你还没写内容呢",Snackbar.LENGTH_SHORT).show();
        }else {
            getData(search_input.getText().toString());
        }
    }

    private void getData(String str){

        refresh_view.post(new Runnable() {
            @Override
            public void run() {
                refresh_view.setRefreshing(true);
            }
        });

        String url = "search.php?mod=forum&mobile=2";
        Map<String,String> paras = new HashMap<>();
        paras.put("formhash", MyPublicData.FORMHASH);
        paras.put("searchsubmit","yes");
        paras.put("srchtxt",str);

        HttpUtil.post(this,url,paras,new ResponseHandler(){
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if(res.contains("秒内只能进行一次搜索")){
                    Snackbar.make(main_window,"抱歉，您在 15 秒内只能进行一次搜索",Snackbar.LENGTH_SHORT).show();
                    refresh_view.setRefreshing(false);
                }else {
                    actionBar.setTitle("搜索结果");
                    new GetResultListTaskMe(new String(response)).execute();
                }

            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                refresh_view.setRefreshing(false);
                Snackbar.make(main_window,"网络错误",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class GetResultListTaskMe extends AsyncTask<Void, Void, String> {

        private List<SimpleListData> dataset = new ArrayList<>();
        private String res;

        public GetResultListTaskMe(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            //chiphell
            Document doc = Jsoup.parse(res);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接

            Pair<String,String> temp;
            Elements links = body.select("li");
            for (Element src : links) {
                String url = src.select("a").attr("href");
                String title = src.select("a").text();
                dataset.add(new SimpleListData(title,"",url));
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            refresh_view.setRefreshing(false);
            datas.addAll(dataset);
            if(datas.size()==0){
                datas.add(new SimpleListData("没有搜索到结果","",""));
            }
            adapter.notifyDataSetChanged();
        }
    }
}
