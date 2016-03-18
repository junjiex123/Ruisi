package xyz.yluo.ruisiapp.api;

/**
 * Created by free2 on 16-3-17.
 * 首页数据类型 板块列表和最新贴
 */
public class MainListArticleDataHome {
    private String name; //or title
    private String image;//imgurl
    private String url;//or titleurl
    private String todaypost; //todaypost
    private String viewCount;
    private String user;



    //板块列表
    public MainListArticleDataHome(String name, String image, String url, String todaypost) {
        this.name = name;
        this.image = image;
        this.url = url;
        this.todaypost = todaypost;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

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


    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    //热帖新帖
    //title titleurl User ReplyCount ViewCount
    public MainListArticleDataHome(String name, String url, String user, String replaycount, String viewcount) {
        this.name = name;
        this.url = url;
        this.user = user;
        this.todaypost = replaycount;
        this.viewCount = viewcount;
    }
}
