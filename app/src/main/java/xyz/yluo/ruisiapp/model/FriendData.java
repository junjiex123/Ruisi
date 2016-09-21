package xyz.yluo.ruisiapp.model;

/**
 * Created by free2 on 16-4-12.
 * 好友列表data
 */
public class FriendData {

    private String userName;
    private String imgUrl;
    private String info;
    private String uid;
    private boolean isOnline;

    public FriendData(String userName, String imgUrl, String info, String uid,boolean isOnline) {
        this.userName = userName;
        this.imgUrl = imgUrl;
        this.info = info;
        this.uid = uid;
        this.isOnline = isOnline;
    }

    public String getUserName() {
        return userName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getInfo() {
        return info;
    }

    public String getUid() {
        return uid;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
