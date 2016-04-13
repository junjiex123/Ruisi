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

//我的主题页面
public class FragementMyArticle extends BaseFragement{

    private List<SimpleListData> datas;
    private SimpleListAdapter adapter;
    private static String url = "";

    public FragementMyArticle() {
        super(url);
        datas = new ArrayList<>();
    }

    public static FragementMyArticle newInstance(String url) {
        FragementMyArticle.url = url;
        return new FragementMyArticle();
    }

    @Override
    protected void initView() {
        adapter = new SimpleListAdapter(ListType.ARTICLE,getActivity(),datas);
        recycler_view.setAdapter(adapter);
        currentIndex = 1;
    }

    @Override
    protected void finishGetData(String res) {
        new GetUserArticleask(res).execute();
    }

    @Override
    protected void refresh() {
        datas.clear();
        adapter.notifyDataSetChanged();
    }

    //获得主题
    public class GetUserArticleask extends AsyncTask<Void, Void, String> {
        private String res;
        public GetUserArticleask(String res) {
            this.res = res;
        }

        @Override
        protected String doInBackground(Void... params) {
            Elements lists = Jsoup.parse(res).select(".threadlist").select("ul").select("li");
            for(Element tmp:lists){
                String title = tmp.select("a").text();
                if(title.isEmpty()){
                    datas.add(new SimpleListData("暂无更多","",""));
                    isHaveMore = false;
                    break;
                }
                String titleUrl =tmp.select("a").attr("href");
                String num = tmp.select(".num").text();
                datas.add(new SimpleListData(title,num,titleUrl));
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
