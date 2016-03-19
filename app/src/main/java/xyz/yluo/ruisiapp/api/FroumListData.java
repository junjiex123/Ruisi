package xyz.yluo.ruisiapp.api;

/**
 * Created by free2 on 16-3-19.
 * 单个板块数据
 */
public class FroumListData {
    //title,img,url,actualnew
    private String title;
    private String imgUrl;
    private String titleUrl;
    private String todayNew;

    public FroumListData(String title, String todayNew, String imgUrl, String titleUrl) {
        this.title = title;
        this.todayNew = todayNew;
        this.imgUrl = imgUrl;
        this.titleUrl = titleUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public String getTodayNew() {
        return todayNew;
    }

    public void setTodayNew(String todayNew) {
        this.todayNew = todayNew;
    }
}
