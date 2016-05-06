package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-11.
 * 单篇文章数据包括评论
 */
public class SingleArticleData {

    //用来标识是楼主还是内容还是loadmore
    private SingleType type;
    private String Img;
    private String username;
    private String postTime;
    //楼层
    private String index;
    //回复链接
    private String replyUrlTitle;
    private String cotent;
    private String title;

    //层主
    public SingleArticleData(SingleType type,String title, String Img, String username, String postTime, String index, String replyUrl, String cotent) {
        this.type = type;
        this.Img = Img;
        this.username = username;
        this.postTime = postTime;
        this.index = index;
        this.replyUrlTitle = replyUrl;
        this.cotent = cotent;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public String getReplyUrlTitle() {
        return replyUrlTitle;
    }
    public String getUsername() {
        return username;
    }

    public String getImg() {
        return Img;
    }

    public String getPostTime() {
        return postTime;
    }

    public String getCotent() {
        return cotent;
    }

    public String getIndex() {
        return index;
    }

    public SingleType getType() {
        return type;
    }

    public void setType(SingleType type) {
        this.type = type;
    }
}
