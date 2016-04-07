package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.adapter.SimpleListAdapter;
import xyz.yluo.ruisiapp.data.SimpleListData;

//我的主题页面
//我的收藏
public class FragementArticlestar extends BaseFragement{

    private List<SimpleListData> datas;
    private SimpleListAdapter adapter;
    private static int type = 0;
    private static String url = "";

    public FragementArticlestar() {
        super(url);
        datas = new ArrayList<>();
    }

    public static FragementArticlestar newInstance(int type, String url) {

        FragementArticlestar.type = type;
        FragementArticlestar.url = url;
        return new FragementArticlestar();
    }

    @Override
    protected void initView() {
        adapter = new SimpleListAdapter(getActivity(),datas);
        recycler_view.setAdapter(adapter);
    }

    @Override
    protected void finishGetData(String res) {
        if(type==0){
            //我的主题
            new GetUserArticleask(res).execute();
        }else {
            //我的收藏
            new GetUserStarTask(res).execute();
        }
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
            if(lists!=null){
                for(Element tmp:lists){
                    String title = tmp.select("a").text();
                    String titleUrl =tmp.select("a").attr("href");
                    String num = tmp.select(".num").text();
                    datas.add(new SimpleListData(title,num,titleUrl));
                }
            }else{
                datas.add(new SimpleListData("你还没有发过贴","",""));
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
        }
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
            if(lists!=null){
                for(Element tmp:lists){
                    String key = tmp.select("a").text();
                    String link = tmp.select("a").attr("href");
                    datas.add(new SimpleListData(key,"",link));
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {

            refreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();

        }
    }


}
