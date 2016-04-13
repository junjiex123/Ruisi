package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.adapter.ReplyMessageAdapter;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.ReplyMessageData;

//回复我的
public class FragementReplyMe extends BaseFragement {

    private ReplyMessageAdapter adapter;
    private List<ReplyMessageData> datas;
    private static String url;

    public FragementReplyMe() {
        super(url);
        datas = new ArrayList<>();
    }

    public static FragementReplyMe newInstance(String url) {
        FragementReplyMe.url = url;
        return new FragementReplyMe();
    }

    @Override
    protected void initView() {
        adapter  = new ReplyMessageAdapter( ListType.REPLAYME,getActivity(),datas);
        recycler_view.setAdapter(adapter);
        currentIndex = 0;
    }

    @Override
    protected void finishGetData(String res) {
        new GetUserReplyTask(res).execute();
    }

    @Override
    protected void refresh() {
        datas.clear();
        adapter.notifyDataSetChanged();
    }

    //获得回复我的
    public class GetUserReplyTask extends AsyncTask<Void, Void, String> {

        private String res;

        public GetUserReplyTask(String res) {
            this.res = res;
        }
        @Override
        protected String doInBackground(Void... params) {
            //pmbox
            Elements lists = Jsoup.parse(res).select(".nts").select("dl.cl");
            for(Element tmp:lists){
                boolean isNew = false;
                if(tmp.select(".ntc_body").attr("style").contains("bold")){
                    isNew = true;
                }
                String content = tmp.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").text().replace("查看","");;
                if(content.isEmpty()){
                    continue;
                }
                String authorImage = tmp.select(".avt").select("img").attr("src");
                String authorTitle = tmp.select(".ntc_body").select("a[href^=home.php]").text()+" 回复了我";
                String time = tmp.select(".xg1.xw0").text();
                String titleUrl =tmp.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").attr("href");

                //String title, String titleUrl, String authorImage, String time,String content
                datas.add(new ReplyMessageData(authorTitle,titleUrl,authorImage,time,content));
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        }
    }
}
