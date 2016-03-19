package xyz.yluo.ruisiapp.article;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.api.SingleArticleData;
import xyz.yluo.ruisiapp.setting.UserDetailActivity;

/**
 * Created by free2 on 16-3-7.
 * 单篇文章adapter
 */

public class ArticleRecycleAdapter extends RecyclerView.Adapter<ArticleRecycleAdapter.BaseViewHolder>{

    private static final int TYPE_COMENT = 1;
    private static final int TYPE_LOAD_MORE = 2;
    private static final int TYPE_CONTENT=0;
    //数据
    private String articleUrl;
    private List<SingleArticleData> datalist;
    private static RecyclerViewClickListener itemListener;
    //上下文
    private Activity activity;


    public ArticleRecycleAdapter(Activity activity,RecyclerViewClickListener itemListener, List<SingleArticleData> datalist) {
        this.datalist = datalist;
        this.activity =activity;
        this.itemListener = itemListener;
    }


    @Override
    public int getItemViewType(int position) {
        //判断listItem类型
        if(position==0){
            return TYPE_CONTENT;
        }else if (position!= getItemCount() - 1) {
            return TYPE_COMENT;
        } else {
            return TYPE_LOAD_MORE;
        }
        //TODO 普通文章类型再分类
        //int type   =  DataSet.get(position).getType();
    }
    //设置view
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_CONTENT:
                return new ArticleContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_article_content_item, viewGroup, false));
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_article_load_more, viewGroup, false));
            default: // TYPE_COMMENT
                return new CommentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_article_comment_item, viewGroup, false));
        }
    }

    //设置data
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }
    //为了让 list item可以变化，我们可以重写这个方法
    //他的返回值是onCreateViewHolder 的参数viewType
    //这儿可以 知道position 和 data

    @Override
    public int getItemCount() {
        if (datalist.size()==0){
            return 0;
        }
        return datalist.size()+1;
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }
        abstract void setData(int position);

        @Override
        public void onClick(View v)
        {
            //getItemCount()
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }

    }

    //文章内容ViewHolder
    public class ArticleContentViewHolder extends BaseViewHolder {
        @Bind(R.id.article_title)
        protected TextView article_title;
        @Bind(R.id.article_user_image)
        protected ImageView article_user_image;
        @Bind(R.id.article_type)
        protected ImageView article_type;
        @Bind(R.id.title_usergroup)
        protected TextView title_usergroup;
        @Bind(R.id.article_username)
        protected TextView article_username;
        @Bind(R.id.article_replaycount)
        protected TextView article_replaycount;
        @Bind(R.id.article_post_time)
        protected TextView article_post_time;
        @Bind(R.id.content_webView)
        protected MyWebView webView;

        public ArticleContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            WebSettings ws = webView.getSettings();
        }
        @OnClick(R.id.article_user_image)
        protected void authorClick(){
            UserDetailActivity.openWithTransitionAnimation(activity, "name", article_user_image,"222");
            //ArticleNormalActivity.open(activity, "id12345");
        }
        @Override
        void setData(int position){

            article_title.setText(datalist.get(0).getTitle());

            //normal zhidin gold:100
            if(datalist.get(0).getArticletype().equalsIgnoreCase("zhidin")){
                article_type.setVisibility(View.VISIBLE);
            }else{
                article_type.setVisibility(View.INVISIBLE);
            }
            title_usergroup.setText(datalist.get(0).getUserGroup());
            article_username.setText(datalist.get(0).getUsername());
            article_replaycount.setText("回复："+datalist.get(0).getReplayCount());
            Picasso.with(activity).load(datalist.get(0).getImgUrl()).resize(44,44).centerCrop().placeholder(R.drawable.image_placeholder).into(article_user_image);
                    //.error(R.drawable.user_placeholder_error)

            //TODO 文章处理

            article_post_time.setText(datalist.get(0).getPostTime());
            //String data ="<p><a href=\"http://www.ithome.com/\" target=\"_blank\">IT之家</a>讯&nbsp;&nbsp;3月8日,BioWare工作室的高级编辑Cameron Harris，近日通过推特宣布了自己将要离开这家备受好评的公司，并且宣布不再涉足于游戏领域。在推特上，Cameron Harris表示自己离开BioWare后会回到西雅图寻找一些全新的机会，对于老东家表达了自己的感谢。</p><p>Cameron Harris是BioWare的主要编辑，来到了BioWare之后，她参与开发了《质量效应：仙女座》、《龙腾世纪：审判》和《星球大战：旧共和国》这几款备受好评的游戏。之前在微软旗下也曾经开发了《战争机器3》和《神鬼寓言：旅程》这两款佳作。加上之前在任天堂的工作经历，Cameron Harris显然是游戏界中璀璨的明星。</p><p><img src=\"http://img.ithome.com/newsuploadfiles/2016/3/20160308_170543_608.jpg\" alt=\"质量效应前途未卜：重量级编剧离开BioWare\"></p><p>作为EA旗下为数不多的口碑良好的游戏工作室，BioWare与Bethesda共同被称为“最会讲故事的游戏公司”。只是最近BioWare饱受员工跳槽的困扰。据外媒gamespot的消息。此前已经有首席编辑Chris Schlerf和高级制作人Chris Wynn离开BioWare来到其他的游戏制作公司。</p><p><img class=\"lazy\" src=\"http://img.ithome.com/newsuploadfiles/2016/3/20160308_170559_583.jpg\" alt=\"质量效应前途未卜：重量级编剧离开BioWare\" data-original=\"http://img.ithome.com/newsuploadfiles/2016/3/20160308_170559_583.jpg\" style=\"display: inline;\"></p><p>之前<a href=\"http://www.ithome.com/html/game/209606.htm\" target=\"_blank\">IT之家曾经报道过《质量效应：仙女座》由于特殊原因推迟到2017年上市</a>，目前看来与员工的离职不无关系。即使《质量效应：仙女座》能够顺利地在2017年发行，随着骨干的离职，恐怕下一部的《质量效应》就前途未卜了。如果BioWare工作室为此而不得不解散，那么EA公司恐怕又要背上“北美最差游戏公司”的恶名了。</p><p class=\"yj_d\">微信搜索“<span>IT之家</span>”关注抢6s大礼！下载<span>IT之家</span>客户端（<a target=\"_blank\" href=\"http://m.ithome.com/ithome/download/\">戳这里</a>）也可参与评论抽楼层大奖！ </p>\n";
            webView.loadDataWithBaseURL("http://rs.xidian.edu.cn/",datalist.get(0).getCotent(),"text/html","UTF-8",null);
            //webView.loadDataWithBaseURL("http://rs.xidian.edu.cn/",data,"text/html","UTF-8",null);
        }

    }

    //评论列表ViewHolder 如果想创建别的样式还可以创建别的houlder继承自RecyclerView.ViewHolder
    public  class CommentViewHolder extends BaseViewHolder {
        //protected ImageView good;

        @Bind(R.id.replay_image)
        protected ImageView replay_image;

        @Bind(R.id.replay_author)
        protected TextView replay_author;

        @Bind(R.id.replay_user_group)
        protected TextView replay_user_group;

        @Bind(R.id.replay_index)
        protected TextView replay_index;

        @Bind(R.id.replay_time)
        protected TextView replay_time;

        @Bind(R.id.replay_btn_01)
        protected TextView replay_btn_01;

        @Bind(R.id.replay_btn_02)
        protected TextView replay_btn_02;

        @Bind(R.id.replay_webView)
        protected MyWebView replay_webView;

        @Bind(R.id.replay_user_gold)
        protected TextView replay_user_gold;

        @Bind(R.id.replay_user_pinfen)
        protected TextView replay_user_pinfen;

        public CommentViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }

        //设置listItem的数据
        @Override
        void setData(int position) {
            if(datalist.get(position).isGetGold){
                replay_user_gold.setVisibility(View.VISIBLE);
                replay_user_gold.setText("金币"+datalist.get(position).getGoldnum());
            }

            if(datalist.get(position).isGetpingfen){
                replay_user_pinfen.setVisibility(View.VISIBLE);
                replay_user_pinfen.setText("评分： "+datalist.get(position).getPingfen());
            }

            replay_user_group.setText(datalist.get(position).getUserGroup());
            replay_author.setText(datalist.get(position).getUsername());

            Picasso.with(activity).load(datalist.get(position).getImgUrl()).resize(36,36).centerCrop().placeholder(R.drawable.image_placeholder).into(replay_image);
            //.error(R.drawable.user_placeholder_error)
            replay_time.setText(datalist.get(position).getPostTime());

            if(position==1){
                replay_index.setText("沙发");

            }else if(position==2){
                replay_index.setText("板凳");
            }else if(position==3){
                replay_index.setText("地板");
            }else{
                replay_index.setText("第"+(position+1)+"楼");
            }

            //String data ="<p><a href=\"http://www.ithome.com/\" target=\"_blank\">IT之家</a>讯&nbsp;&nbsp;3月8日,BioWare工作室的高级编辑Cameron Harris，近日通过推特宣布了自己将要离开这家备受好评的公司，并且宣布不再涉足于游戏领域。在推特上，Cameron Harris表示自己离开BioWare后会回到西雅图寻找一些全新的机会，对于老东家表达了自己的感谢。</p><p>Cameron Harris是BioWare的主要编辑，来到了BioWare之后，她参与开发了《质量效应：仙女座》、《龙腾世纪：审判》和《星球大战：旧共和国》这几款备受好评的游戏。之前在微软旗下也曾经开发了《战争机器3》和《神鬼寓言：旅程》这两款佳作。加上之前在任天堂的工作经历，Cameron Harris显然是游戏界中璀璨的明星。</p><p><img src=\"http://img.ithome.com/newsuploadfiles/2016/3/20160308_170543_608.jpg\" alt=\"质量效应前途未卜：重量级编剧离开BioWare\"></p><p>作为EA旗下为数不多的口碑良好的游戏工作室，BioWare与Bethesda共同被称为“最会讲故事的游戏公司”。只是最近BioWare饱受员工跳槽的困扰。据外媒gamespot的消息。此前已经有首席编辑Chris Schlerf和高级制作人Chris Wynn离开BioWare来到其他的游戏制作公司。</p><p><img class=\"lazy\" src=\"http://img.ithome.com/newsuploadfiles/2016/3/20160308_170559_583.jpg\" alt=\"质量效应前途未卜：重量级编剧离开BioWare\" data-original=\"http://img.ithome.com/newsuploadfiles/2016/3/20160308_170559_583.jpg\" style=\"display: inline;\"></p><p>之前<a href=\"http://www.ithome.com/html/game/209606.htm\" target=\"_blank\">IT之家曾经报道过《质量效应：仙女座》由于特殊原因推迟到2017年上市</a>，目前看来与员工的离职不无关系。即使《质量效应：仙女座》能够顺利地在2017年发行，随着骨干的离职，恐怕下一部的《质量效应》就前途未卜了。如果BioWare工作室为此而不得不解散，那么EA公司恐怕又要背上“北美最差游戏公司”的恶名了。</p><p class=\"yj_d\">微信搜索“<span>IT之家</span>”关注抢6s大礼！下载<span>IT之家</span>客户端（<a target=\"_blank\" href=\"http://m.ithome.com/ithome/download/\">戳这里</a>）也可参与评论抽楼层大奖！ </p>\n";
            replay_webView.loadDataWithBaseURL(ConfigClass.BBS_BASE_URL,datalist.get(position).getCotent(),"text/html","UTF-8",null);
            //http://rs.xidian.edu.cn/data/attachment/forum/201301/03/220841sflvvzi8fuihbe78.png
            //webView.loadDataWithBaseURL("http://rs.xidian.edu.cn/",data,"text/html","UTF-8",null);

        }

        @OnClick(R.id.replay_image)
            protected void onBtnAvatarClick() {
                UserDetailActivity.openWithTransitionAnimation(activity, "name", replay_image,"222");
            }

    }

    //加载更多ViewHolder
    public class LoadMoreViewHolder extends BaseViewHolder{

        @Bind(R.id.aticle_load_more_text)
        protected TextView aticle_load_more_text;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }

        @Override
        void setData(int position) {
            //TODO
            //load more 现在没有数据填充
            if(position==1){
                aticle_load_more_text.setText("还没有人回复快来抢沙发吧！！");
            }else{
                aticle_load_more_text.setText("加载更多");
            }
        }

    }
}
