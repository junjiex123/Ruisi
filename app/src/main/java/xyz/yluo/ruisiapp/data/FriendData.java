package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-4-12.
 */
public class FriendData {

    private String userName;
    private String imgUrl;
    private String info;
    private String uid;
    private String lastOnlineTime;

    public FriendData(String userName, String imgUrl, String info, String uid, String lastOnlineTime) {
        this.userName = userName;
        this.imgUrl = imgUrl;
        this.info = info;
        this.uid = uid;
        this.lastOnlineTime = lastOnlineTime;
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

    public String getLastOnlineTime() {
        return lastOnlineTime;
    }
}
