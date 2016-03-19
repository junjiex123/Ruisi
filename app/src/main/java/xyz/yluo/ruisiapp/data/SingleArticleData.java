package xyz.yluo.ruisiapp.data;

/**
 * Created by free2 on 16-3-11.
 * 单篇文章数据包括评论
 * 0-文章
 * >0评论
 */
public class SingleArticleData {
    private String username;
    private String userUrl;
    private String imgUrl;
    private String title;
    private String postTime;
    private String userGroup;
    private String cotent;
    private String articletype;//normal zhidin gold:100
    private String replayCount;

    public boolean isGetGold;
    //金币贴获得了金币
    private String goldnum;

    public boolean isGetpingfen;
    private String pingfen;

    public boolean isGetDianpin;
    private String dianpin;

    public String getPingfen() {
        return pingfen;
    }

    public void setPingfen(String pingfen) {
        this.pingfen = pingfen;
    }


    public String getDianpin() {
        return dianpin;
    }

    public void setDianpin(String dianpin) {
        this.dianpin = dianpin;
    }

    public String getGoldnum() {
        return goldnum;
    }

    public void setGoldnum(String goldnum) {
        this.goldnum = goldnum;
    }



    public String getTitle() {
        return title;
    }

    public String getArticletype() {
        return articletype;
    }

    public String getReplayCount() {
        return replayCount;
    }

    //goldnum 不是金币贴可以设置为null
    public SingleArticleData(String title, String type , String username, String userUrl, String imgUrl, String postTime, String userGroup, String replaycount , String cotent) {
        this.title = title;
        articletype = type;
        this.replayCount = replaycount;
        this.username = username;

        this.userUrl = userUrl;
        this.imgUrl = imgUrl;
        this.postTime = postTime;
        this.userGroup = userGroup;
        this.cotent = cotent;
        this.goldnum = goldnum;
    }

    public String getUsername() {
        return username;
    }

    public String getUserUrl() {
        return userUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getPostTime() {
        return postTime;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public String getCotent() {
        return cotent;
    }
}
