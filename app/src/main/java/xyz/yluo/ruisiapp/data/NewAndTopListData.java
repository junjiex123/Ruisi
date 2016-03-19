package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-19.
 * 首页新热 贴data
 */
public class NewAndTopListData {
    private String title;
    private String titleUrl;
    private String user;
    private String replyCount;
    private String ViewCount;
    private int type; //0 new 1 top

    public NewAndTopListData(String title, String titleUrl, String user, String replyCount, String viewCount,int type) {
        this.title = title;
        this.titleUrl = titleUrl;
        this.user = user;
        this.replyCount = replyCount;
        ViewCount = viewCount;
        this.type = type;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public String getViewCount() {
        return ViewCount;
    }

    public void setViewCount(String viewCount) {
        ViewCount = viewCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
