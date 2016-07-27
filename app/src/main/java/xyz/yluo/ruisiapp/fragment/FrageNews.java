package xyz.yluo.ruisiapp.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.NewsListAdapter;
import xyz.yluo.ruisiapp.data.SchoolNewsData;
import xyz.yluo.ruisiapp.database.MyDbUtils;

/**
 * Created by free2 on 16-3-19.
 * 简单的fragment 首页第二页 展示最新的帖子等
 */
public class FrageNews extends Fragment{

    public static final String TAG = FrageNews.class.getSimpleName();
    protected RecyclerView recycler_view;
    protected SwipeRefreshLayout refreshLayout;
    private List<SchoolNewsData> mydataset = new ArrayList<>();
    private NewsListAdapter adapter;

    public static FrageNews newInstance(boolean isNeedUpdate) {
        Bundle args = new Bundle();
        args.putBoolean("isneedupdate",isNeedUpdate);
        FrageNews fragment = new FrageNews();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frage_news_list, container, false);
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);

        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        //首先从数据库读入数据
        MyDbUtils myDbUtils = new MyDbUtils(getActivity(),MyDbUtils.MODE_READ);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        adapter = new NewsListAdapter(getActivity(), myDbUtils.getNewsList(null));
        recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 300);
        return view;
    }

    private void refresh() {
        new GetNewsListTask().execute();
    }


    private class GetNewsListTask extends AsyncTask<String, Void, List<SchoolNewsData>> {
        @Override
        protected List<SchoolNewsData> doInBackground(String... params) {
            List<SchoolNewsData> dataset = new ArrayList<>();
            // write your code here
            Document document = null;
            try {
                document = Jsoup.connect("http://jwc.xidian.edu.cn/tzgg1.htm").timeout(3000).get();
            } catch (IOException e) {
                e.printStackTrace();
                return dataset;
            }

            Elements articlelists = document.select("table.winstyle49756").select("tr[height=20]");
            for (Element article : articlelists) {
                Elements title = article.select("a");
                String url = title.attr("href");
                String titleStr = title.text();
                boolean is_fj = !article.select("img[src=images/fj.gif]").isEmpty();
                boolean is_image = !article.select("img[src=images/tu-hz.gif]").isEmpty();
                String time = article.select("span.timestyle49756").text();
                Log.i("news task", titleStr + " " + url + " " + is_fj + " " + is_image + " " + time);
                //String url,String title, boolean is_image, boolean is_patch, String post_time
                dataset.add(new SchoolNewsData(url, titleStr, is_image, is_fj, time,false));
            }

            //数据加载完毕，和数据库比对
            MyDbUtils myDbUtils = new MyDbUtils(getActivity(),MyDbUtils.MODE_WRITE);
            dataset = myDbUtils.getNewsList(dataset);
            return dataset;
        }

        @Override
        protected void onPostExecute(List<SchoolNewsData> dataset) {
            mydataset.clear();
            mydataset.addAll(dataset);
            adapter.notifyDataSetChanged();
            refreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            }, 500);
        }
    }

}
