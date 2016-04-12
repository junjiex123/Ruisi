package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.adapter.ReplyMessageAdapter;
import xyz.yluo.ruisiapp.data.ReplyMessageData;

//我的消息
//回复我的
public class FragementMessageReply extends BaseFragement{

    private ReplyMessageAdapter adapter;
    private List<ReplyMessageData> datas;

    //0回复我的 //1消息
    private static int type;
    private static String url;

    public FragementMessageReply() {
        super(url);
        datas = new ArrayList<>();
    }


    public static FragementMessageReply newInstance(int type, String url) {
        FragementMessageReply.type = type;
        FragementMessageReply.url = url;

        return new FragementMessageReply();
    }

    @Override
    protected void initView() {
        adapter  = new ReplyMessageAdapter(getActivity(),datas,type);
        recycler_view.setAdapter(adapter);
        if(type==0){
            currentIndex = 0;
        }else {
            currentIndex = 2;
        }
    }

    @Override
    protected void finishGetData(String res) {
        if(type==0){
            new GetUserReplyTask(res).execute();
        }else{
            new GetUserMessageTask(res).execute();
        }
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

    //获得消息
    public class GetUserMessageTask extends AsyncTask<Void, Void, String> {

        private String res;
        public GetUserMessageTask(String res) {
            this.res = res;
        }
        @Override
        protected String doInBackground(Void... params) {
            //pmbox
            Elements lists = Jsoup.parse(res).select(".pmbox").select("ul").select("li");
            for(Element tmp:lists){
                String title = tmp.select(".cl").select(".name").text();
                String time = tmp.select(".cl.grey").select(".time").text();
                tmp.select(".cl.grey").select(".time").remove();
                String content = tmp.select(".cl.grey").text();
                String authorImage = tmp.select("img").attr("src");
                String titleUrl =tmp.select("a").attr("href");
                datas.add(new ReplyMessageData(title,titleUrl,authorImage,time,content));
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
