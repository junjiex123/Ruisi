package xyz.yluo.ruisiapp.api;

/**
 * Created by free2 on 16-3-17.
 * 首页数据类型
 */
public class MainHomeListData {
    private String name;
    private String image;
    private String url;
    private String todaypost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTodaypost() {
        return todaypost;
    }

    public void setTodaypost(String todaypost) {
        this.todaypost = todaypost;
    }

    public String getTotalarticle() {
        return totalarticle;
    }

    public void setTotalarticle(String totalarticle) {
        this.totalarticle = totalarticle;
    }

    public String getTotalreplay() {
        return totalreplay;
    }

    public void setTotalreplay(String totalreplay) {
        this.totalreplay = totalreplay;
    }

    private String totalarticle;
    private String totalreplay;

    public MainHomeListData(String name, String image, String url, String todaypost, String totalarticle, String totalreplay) {
        this.name = name;
        this.image = image;
        this.url = url;
        this.todaypost = todaypost;
        this.totalarticle = totalarticle;
        this.totalreplay = totalreplay;
    }
}
