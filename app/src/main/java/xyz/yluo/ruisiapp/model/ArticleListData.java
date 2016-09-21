package xyz.yluo.ruisiapp.model;

/**
 * Created by free2 on 16-3-7.
 * <p>
 * 各个板块文章列表数据
 */
public class ArticleListData {

    //主页面文章列表item
    private String title;
    private String titleUrl;
    private String type; //置顶 金币 普通
    private String author;
    private String authorUrl;
    private String postTime;
    private String viewCount;
    private String replayCount;
    private boolean isRead;
    private boolean ishaveImage;
    private String imUrl;

    private int titleColor = 0xff000000;//文章颜色


    //一般文章构造器
    public ArticleListData(String type, String title, String titleUrl, String author, String authorUrl, String postTime, String viewCount, String replayCount,int titleColor) {
        this.type = type;//置顶 精华 金币。。。
        this.title = title;
        this.titleUrl = titleUrl;
        this.author = author;
        this.authorUrl = authorUrl;
        this.postTime = postTime;
        this.viewCount = viewCount;
        this.replayCount = replayCount;
        this.titleColor = titleColor;
    }

    //手机版构造器
    public ArticleListData(boolean haveImage, String title, String titleUrl, String author, String replayCount,int titleColor) {
        this.ishaveImage = haveImage;//0--have image
        this.title = title;
        this.titleUrl = titleUrl;
        this.author = author;
        this.replayCount = replayCount;
        this.titleColor = titleColor;
    }

    //图片分类构造
    public ArticleListData(String title, String titleUrl, String imageurl, String author, String replyCount){
        this.title = title;
        this.titleUrl = titleUrl;
        this.imUrl = imageurl;
        this.author = author;
        this.replayCount = replyCount;
    }

    public boolean ishaveImage() {
        return ishaveImage;
    }

    public boolean isRead() {
        return isRead;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getReplayCount() {
        return replayCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public String getPostTime() {
        return postTime;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return imUrl;
    }
}
