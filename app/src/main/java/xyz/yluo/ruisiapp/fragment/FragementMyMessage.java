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

//我的消息
public class FragementMyMessage extends BaseFragement {

    private ReplyMessageAdapter adapter;
    private List<ReplyMessageData> datas;
    private static String url;

    public FragementMyMessage() {
        super(url);
        datas = new ArrayList<>();
    }


    public static FragementMyMessage newInstance(String url) {
        FragementMyMessage.url = url;

        return new FragementMyMessage();
    }

    @Override
    protected void initView() {
        adapter  = new ReplyMessageAdapter(ListType.MYMESSAGE,getActivity(),datas);
        recycler_view.setAdapter(adapter);
        currentIndex = 2;
    }

    @Override
    protected void finishGetData(String res) {
        new GetUserMessageTask(res).execute();
    }

    @Override
    protected void refresh() {
        datas.clear();
        adapter.notifyDataSetChanged();
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
                boolean isRead = true;
                if(tmp.select(".num").text().length()>0){
                    isRead = false;
                }
                String title = tmp.select(".cl").select(".name").text();
                String time = tmp.select(".cl.grey").select(".time").text();
                tmp.select(".cl.grey").select(".time").remove();
                String content = tmp.select(".cl.grey").text();
                String authorImage = tmp.select("img").attr("src");
                String titleUrl =tmp.select("a").attr("href");
                //todo
                datas.add(new ReplyMessageData(title,titleUrl,authorImage,time,isRead,content));
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
