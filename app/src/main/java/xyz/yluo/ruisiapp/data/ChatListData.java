package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-30.
 *
 */
public class ChatListData {

    private int type;//left or right
    private String UserImage;

    private String content;
    private String time;

    public ChatListData(int type,String userImage, String content, String time) {
        this.type = type;
        UserImage = userImage;
        this.content = content;
        this.time = time;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public String getUserImage() {
        return UserImage;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}
