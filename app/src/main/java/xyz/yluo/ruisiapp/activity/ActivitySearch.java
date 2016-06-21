package xyz.yluo.ruisiapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.SimpleListData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;

/**
 * Created by free2 on 16-4-6.
 * 搜索activity
 * todo 支持更多的搜索，支持翻页
 */
public class ActivitySearch extends BaseActivity {

    private RecyclerView recycler_view;
    private EditText search_input;
    private LinearLayout main_window;
    private SwipeRefreshLayout refreshLayout;
    private SimpleListAdapter adapter;

    private ActionBar actionBar;
    private List<SimpleListData> datas = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        search_input = (EditText) findViewById(R.id.search_input);
        main_window = (LinearLayout) findViewById(R.id.main_window);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        findViewById(R.id.start_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_search_click();
            }
        });


        adapter = new SimpleListAdapter(ListType.SERRCH,this, datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);

        actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("搜索");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datas.clear();
                if(search_input.getText().toString().equals("")){
                    refreshLayout.setRefreshing(false);
                }else {
                    start_search_click();
                }
            }
        });

        search_input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    start_search_click();
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void start_search_click(){
        datas.clear();
        if (search_input.getText().toString().isEmpty()){
            Snackbar.make(main_window,"你还没写内容呢",Snackbar.LENGTH_SHORT).show();
        }else {
            getData(search_input.getText().toString());
        }
    }

    private void getData(String str){

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        String url = "search.php?mod=forum&mobile=2";
        Map<String,String> paras = new HashMap<>();
        paras.put("formhash", PublicData.FORMHASH);
        paras.put("searchsubmit","yes");
        paras.put("srchtxt",str);

        HttpUtil.post(this,url,paras,new ResponseHandler(){
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                if(res.contains("秒内只能进行一次搜索")){
                    Snackbar.make(main_window,"抱歉，您在 15 秒内只能进行一次搜索",Snackbar.LENGTH_SHORT).show();
                }else {
                    actionBar.setTitle("搜索结果");
                    new GetResultListTaskMe().execute(new String(response));
                }

            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                Snackbar.make(main_window,"网络错误",Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                },500);

            }
        });
    }

    private class GetResultListTaskMe extends AsyncTask<String, Void, List<SimpleListData>> {
        @Override
        protected List<SimpleListData> doInBackground(String... params) {
            String res = params[0];
            List<SimpleListData> dataset = new ArrayList<>();
            Document doc = Jsoup.parse(res);
            Elements body = doc.select("div[class=threadlist]"); // 具有 href 属性的链接
            Elements links = body.select("li");
            for (Element src : links) {
                String url = src.select("a").attr("href");
                String title = src.select("a").text();
                dataset.add(new SimpleListData(title,"",url));
            }
            return dataset;
        }

        @Override
        protected void onPostExecute(List<SimpleListData> dataset) {
            datas.addAll(dataset);
            if(datas.size()==0){
                datas.add(new SimpleListData("没有搜索到结果","",""));
            }
            adapter.notifyDataSetChanged();

            recycler_view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            },500);
        }
    }
}
