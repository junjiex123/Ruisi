package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-21.
 * 我的帖子
 * 我的回复 list data
 */
public class MyTopicReplyListData {
    private int type =0;//0 我的主题 1我的消息

    private String title;
    private String titleUrl;
    private String authorImage;
    private String time;  //在我的回复当作内容

    private String content;
    private String replycount;

    //我的消息
    public MyTopicReplyListData(int type, String title, String titleUrl, String authorImage, String time,String content) {
        this.type = type;
        this.title = title;
        this.titleUrl = titleUrl;
        this.authorImage = authorImage;
        this.time = time;
        this.content = content;

    }

    //我的主题
    public MyTopicReplyListData(int type, String title, String titleUrl, String replycount) {
        this.type = type;
        this.title = title;
        this.titleUrl = titleUrl;
        this.replycount = replycount;
    }

    public String getcontent() {
        return content;
    }

    public void setcontent(String content) {
        this.content = content;
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

    public String getauthorImage() {
        return authorImage;
    }

    public void setauthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
