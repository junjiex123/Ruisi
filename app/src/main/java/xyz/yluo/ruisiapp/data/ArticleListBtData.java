package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-4-2.
 *
 */
public class ArticleListBtData {
    private String title;
    private String titleUrl;
    private String author;
    private String logoUrl;
    private String time;
    private String btSize;
    private String btNum;
    private String btDownLoadNum;
    private String btCompeteNum;
    private boolean isFree;

    public ArticleListBtData(String title,String titleUrl, String author, String logoUrl, String time, String btSize, String btNum, String btDownLoadNum, String btCompeteNum, boolean isFree) {
        this.title = title;
        this.author = author;
        this.logoUrl = logoUrl;
        this.time = time;
        this.btSize = btSize;
        this.btNum = btNum;
        this.btDownLoadNum = btDownLoadNum;
        this.btCompeteNum = btCompeteNum;
        this.isFree = isFree;
        this.titleUrl = titleUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getTime() {
        return time;
    }

    public String getBtSize() {
        return btSize;
    }

    public String getBtNum() {
        return btNum;
    }

    public String getBtDownLoadNum() {
        return btDownLoadNum;
    }

    public String getBtCompeteNum() {
        return btCompeteNum;
    }

    public boolean isFree() {
        return isFree;
    }

    public String getTitleUrl() {
        return titleUrl;
    }
}
