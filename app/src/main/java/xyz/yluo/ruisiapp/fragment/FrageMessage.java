package xyz.yluo.ruisiapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.MessageAdapter;
import xyz.yluo.ruisiapp.data.ListType;
import xyz.yluo.ruisiapp.data.ReplyMessageData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;

//回复我的
public class FrageMessage extends Fragment{

    @Bind(R.id.recycler_view)
    protected RecyclerView recycler_view;
    @Bind(R.id.refresh_layout)
    protected SwipeRefreshLayout refreshLayout;
    private MessageAdapter adapter;
    private List<ReplyMessageData> datas;
    private int index =0;

    public FrageMessage() {
        datas = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_list_view, container, false);
        ButterKnife.bind(this,view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(ListType.REPLAYME, getActivity(), datas, new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                if(position!=index){
                    index = position;
                    refresh();
                }

            }
        });
        recycler_view.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();

        return view;
    }


    protected void refresh() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        getData();
    }

    private void getData(){
        //reply
        String url = "home.php?mod=space&do=notice&mobile=2";
        if(index!=0){
            //pm
            url = "home.php?mod=space&do=pm&mobile=2";
        }
        HttpUtil.get(getActivity(), url, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res= new String(response);
                if(index!=0){
                    new GetUserPmTask(res).execute();
                }else{
                    new GetUserReplyTask(res).execute();
                }

            }
            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    //获得回复我的
    public class GetUserReplyTask extends AsyncTask<Void, Void, String> {
        private String res;
        private List<ReplyMessageData> tempdatas = new ArrayList<>();
        public GetUserReplyTask(String res) {
            this.res = res;
        }
        @Override
        protected String doInBackground(Void... params) {
            //pmbox
            Elements lists = Jsoup.parse(res).select(".nts").select("dl.cl");
            for(Element tmp:lists){
                boolean isRead = true;
                if(tmp.select(".ntc_body").attr("style").contains("bold")){
                    isRead = false;
                }
                String content = tmp.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").text().replace("查看","");;
                if(content.isEmpty()){
                    continue;
                }
                String authorImage = tmp.select(".avt").select("img").attr("src");
                String authorTitle = tmp.select(".ntc_body").select("a[href^=home.php]").text()+" 回复了我";
                String time = tmp.select(".xg1.xw0").text();
                String titleUrl =tmp.select(".ntc_body").select("a[href^=forum.php?mod=redirect]").attr("href");
                tempdatas.add(new ReplyMessageData(authorTitle,titleUrl,authorImage,time,isRead,content));
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            datas.clear();
            datas.addAll(tempdatas);
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        }
    }

    //获得pm消息
    public class GetUserPmTask extends AsyncTask<Void, Void, String> {

        private String res;
        private List<ReplyMessageData> temdatas = new ArrayList<>();
        public GetUserPmTask(String res) {
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
                temdatas.add(new ReplyMessageData(title,titleUrl,authorImage,time,isRead,content));
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String res) {
            datas.clear();
            datas.addAll(temdatas);
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        }
    }
}
