package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-7-13.
 * 教务处新闻数据
 */
public class SchoolNewsData {
    private String title;
    private String url;
    private boolean is_image;
    private boolean is_patch;
    private String post_time;
    private boolean isRead;


    public SchoolNewsData(String url,String title, boolean is_image, boolean is_patch, String post_time) {
        this.title = title;
        this.is_image = is_image;
        this.is_patch = is_patch;
        this.post_time = post_time;
        this.url = url;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getTitle() {
        return title;
    }

    public boolean is_image() {
        return is_image;
    }

    public boolean is_patch() {
        return is_patch;
    }

    public String getPost_time() {
        return post_time;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getUrl() {
        return url;
    }
}
