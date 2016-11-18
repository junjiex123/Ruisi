package xyz.yluo.ruisiapp.model;

/**
 * Created by free2 on 16-3-19.
 * 单个板块数据
 */
public class ForumListData {
    //title,img,url,actualnew
    private String title;
    private int fid;
    private String todayNew;
    //是不是头
    private boolean isheader;


    public ForumListData(boolean isheader, String title, String todayNew, int fid) {
        this.title = title;
        this.todayNew = todayNew;
        this.fid = fid;
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

    public int getFid() {
        return fid;
    }

    public String getTodayNew() {
        return todayNew;
    }

}
