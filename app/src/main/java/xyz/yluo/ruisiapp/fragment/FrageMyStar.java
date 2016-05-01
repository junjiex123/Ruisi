package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.SimpleListData;

/**
 *我的收藏fragement
 */

public class FrageMyStar extends BaseFragement{

    private List<SimpleListData> datas;
    private SimpleListAdapter adapter;
    private static String url = "";

    public FrageMyStar() {
        super(url);
        datas = new ArrayList<>();
    }

    public static FrageMyStar newInstance(String url) {
        FrageMyStar.url = url;
        return new FrageMyStar();
    }

    @Override
    protected void initView() {
        adapter = new SimpleListAdapter(ListType.STAR,getActivity(),datas);
        recycler_view.setAdapter(adapter);
        currentIndex = 3;
    }

    @Override
    protected void finishGetData(String res) {
        new GetUserStarTask(res).execute();
    }

    @Override
    protected void refresh() {
        datas.clear();
        adapter.notifyDataSetChanged();
    }

    //获得用户收藏
    public class GetUserStarTask extends AsyncTask<Void, Void, String> {

        private String res;
        public GetUserStarTask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            for(Element tmp:lists){
                String key = tmp.select("a").text();
                if(key.isEmpty()){
                    datas.add(new SimpleListData("暂无更多","",""));
                    isHaveMore = false;
                    break;
                }
                String link = tmp.select("a").attr("href");
                datas.add(new SimpleListData(key,"",link));
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            isEnableLoadMore = true;
            CurrentPage++;
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
    }


}
