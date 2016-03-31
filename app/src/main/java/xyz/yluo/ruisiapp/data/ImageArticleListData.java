package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-31.
 * 各板块图片列表item
 */
public class ImageArticleListData {

    private String title;
    private String titleUrl;
    private String image;
    private String author;
    private String authorUrl;
    private String likeCount;
    private String ReplyCount;

    public ImageArticleListData(String title, String titleUrl, String image, String author, String authorUrl, String likeCount, String replyCount) {
        this.title = title;
        this.titleUrl = titleUrl;
        this.image = image;
        this.author = author;
        this.authorUrl = authorUrl;
        this.likeCount = likeCount;
        ReplyCount = replyCount;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public String getImage() {
        return image;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public String getReplyCount() {
        return ReplyCount;
    }
}
