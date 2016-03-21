package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-21.
 * 我的帖子
 * 我的回复 list data
 */
public class MyTopicReplyListData {
    private int type =0;//0 我的帖子 1我的回复
    private String title;
    private String titleUrl;
    private String author;
    private String time;

    private String froumName;//板块 我的帖子特有

    public MyTopicReplyListData(int type, String title, String titleUrl, String author, String time, String froumName) {
        this.type = type;
        this.title = title;
        this.titleUrl = titleUrl;
        this.author = author;
        this.time = time;
        this.froumName = froumName;
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
