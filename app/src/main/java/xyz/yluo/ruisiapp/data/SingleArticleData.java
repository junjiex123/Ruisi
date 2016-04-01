package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-11.
 * 单篇文章数据包括评论
 * 0-文章
 * >0评论
 */
//单层楼数据 0 楼主 > 0 回复
public class SingleArticleData {

    //作者的内容特有 0 位置特有
    private String title;
    private String type;
    private String replyCount;

    //通用
    private String username;
    private String userUrl;
    private String userImgUrl;
    private String postTime;
    private String cotent;


    //index>0 的数据也就是评论
    public SingleArticleData(String username, String userUrl, String userImgUrl, String postTime,String cotent) {
        this.username = username;
        this.userUrl = userUrl;
        this.userImgUrl = userImgUrl;
        this.postTime = postTime;
        this.cotent = cotent;
    }

    //index==0 内容
    public SingleArticleData(String title, String type, String replyCount, String username, String userUrl, String userImgUrl, String postTime, String cotent) {
        this.title = title;
        this.type = type;
        this.replyCount = replyCount;
        this.username = username;
        this.userUrl = userUrl;
        this.userImgUrl = userImgUrl;
        this.postTime = postTime;
        this.cotent = cotent;
    }

    public String getUsername() {
        return username;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public String getPostTime() {
        return postTime;
    }

    public String getCotent() {
        return cotent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReplyCount() {
        return replyCount;
    }
}
