package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-21.
 * 我的帖子
 * 我的回复 list data
 */
public class MyTopicReplyListData {
    private int type =0;//0 我的主题 1我的回复
    private String title;
    private String titleUrl;
    private String author;
    private String time;  //在我的回复当作内容
    private String froumName;
    private String view_count;
    private String replycount;

    //我的回复
    public MyTopicReplyListData(int type, String title, String titleUrl, String author, String time, String froumName) {
        this.type = type;
        this.title = title;
        this.titleUrl = titleUrl;
        this.author = author;
        this.time = time;
        this.froumName = froumName;
    }

    //我的主题
    public MyTopicReplyListData(int type, String title, String titleUrl, String replycount) {
        this.type = type;
        this.title = title;
        this.titleUrl = titleUrl;
        this.replycount = replycount;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getReplycount() {
        return replycount;
    }

    public void setReplycount(String replycount) {
        this.replycount = replycount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFroumName() {
        return froumName;
    }

    public void setFroumName(String froumName) {
        this.froumName = froumName;
    }
}
