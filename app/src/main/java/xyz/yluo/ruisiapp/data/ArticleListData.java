package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-7.
 *
 *  各个板块列表数据 当个数据
 */
public class ArticleListData {

    //主页面文章列表item

    //判断是否为 图片文章数据列表 例如摄影专区
    private boolean isImageCard = false;

    private String title;

    public boolean isImageCard() {
        return isImageCard;
    }

    private String titleUrl;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;//图片文章特有 手机版有么有图

    private String type;// normal zhidin gold:100
    private String author;
    private String authorUrl;

    private String postTime;

    private String viewCount;
    private String replayCount;

    //图片列表特有
    public void setImageCard(boolean imageCard) {
        isImageCard = imageCard;
    }

    public String getReplayCount() {
        return replayCount;
    }

    public void setReplayCount(String replayCount) {
        this.replayCount = replayCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
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

    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
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

    public void setAuthor(String author) {
        this.author = author;
    }

    //一般文章构造器
    public ArticleListData(String title, String titleUrl, String type, String author, String authorUrl, String postTime, String viewCount, String replayCount) {
        this.title = title;
        this.titleUrl = titleUrl;
        this.type = type;
        this.author = author;
        this.authorUrl = authorUrl;
        this.postTime = postTime;
        this.viewCount = viewCount;
        this.replayCount = replayCount;
    }
    //图片文章构造
    public ArticleListData(String title, String titleUrl, String image, String author, String authorUrl, String viewCount) {
        this.title = title;
        this.titleUrl = titleUrl;
        this.image = image;
        this.author = author;
        this.authorUrl = authorUrl;
        this.viewCount = viewCount;
    }

    //手机版构造器
    public ArticleListData(boolean isImageCard,String title, String titleUrl, String author, String replayCount) {
        this.isImageCard = isImageCard;
        this.title = title;
        this.titleUrl = titleUrl;
        this.author = author;
        this.replayCount = replayCount;
    }
}
