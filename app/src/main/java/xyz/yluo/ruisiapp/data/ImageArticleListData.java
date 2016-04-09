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
    private String ReplyCount;

    public ImageArticleListData(String title, String titleUrl, String image, String author,String replyCount) {
        this.title = title;
        this.titleUrl = titleUrl;
        this.image = image;
        this.author = author;
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

    public String getReplyCount() {
        return ReplyCount;
    }
}
