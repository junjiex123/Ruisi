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
    private String userImgUrl;
    private String postTime;
    //楼层
    private String index;
    private String cotent;
    //楼中楼回复链接
    private String replyUrl;


    //index>0 的数据也就是评论
    public SingleArticleData(String username, String userImgUrl, String postTime,String index,String replyUrl,String cotent) {
        this.index = index;
        this.username = username;
        this.userImgUrl = userImgUrl;
        this.postTime = postTime;
        this.cotent = cotent;
        this.replyUrl = replyUrl;
    }

    //index==0 内容
    public SingleArticleData(String title, String type, String replyCount, String username, String userImgUrl, String postTime, String cotent) {
        this.title = title;
        this.type = type;
        this.replyCount = replyCount;
        this.username = username;
        this.userImgUrl = userImgUrl;
        this.postTime = postTime;
        this.cotent = cotent;
    }

    public String getUsername() {
        return username;
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

    public String getIndex() {
        return index;
    }
    public String getReplyUrl() {
        return replyUrl;
    }
}
