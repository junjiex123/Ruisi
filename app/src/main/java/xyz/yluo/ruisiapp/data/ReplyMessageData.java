package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-21.
 * 我的帖子
 * 我的回复 list data
 */
public class ReplyMessageData {

    private String title;
    private String titleUrl;
    private String authorImage;
    private String time;  //在我的回复当作内容
    private String content;

    //我的消息 ////回复我的
    public ReplyMessageData(String title, String titleUrl, String authorImage, String time, String content) {
        this.title = title;
        this.titleUrl = titleUrl;
        this.authorImage = authorImage;
        this.time = time;
        this.content = content;
    }

    public String getcontent() {
        return content;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
