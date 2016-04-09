package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-19.
 * 单个板块数据
 */
public class FroumListData {
    //title,img,url,actualnew
    private String title;
    private String titleUrl;
    private String todayNew;

    //是不是头
    private boolean isheader;

    public FroumListData(boolean isheader,String title, String todayNew, String titleUrl) {
        this.title = title;
        this.todayNew = todayNew;
        this.titleUrl = titleUrl;

        this.isheader = isheader;
    }

    //板块分类头
    public FroumListData(boolean isheader,String title) {
        this.title = title;
        this.isheader = isheader;
    }

    public boolean isheader() {
        return isheader;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

}
