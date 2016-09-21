package xyz.yluo.ruisiapp.model;

import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-11.
 * 单篇文章数据包括评论
 */
public class SingleArticleData {

    //用来标识是楼主还是内容还是loadmore
    private SingleType type;
    private String username;
    private String postTime;
    private String uid;
    private String pid;
    //楼层
    private String index;
    //回复链接
    private String replyUrlTitle;
    private String cotent;
    private String title;
    private String editTime;

    public SingleArticleData(SingleType type, String title, String uid, String username, String postTime, String index, String replyUrl, String cotent,String pid) {
        this.type = type;
        this.username = username;
        this.postTime = postTime;
        this.index = index;
        this.replyUrlTitle = replyUrl;
        this.cotent = cotent;
        this.title = title;
        this.pid = pid;
        this.uid = uid;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg(){
        return UrlUtils.getAvaterurlm(uid);
    }

    public String getPid() {
        return pid;
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

    public void setEditTime(String editTime) {
        this.editTime = editTime;
    }

    public String getEditTime() {
        return editTime;
    }

    public String getUid() {
        return uid;
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

    public void setCotent(String cotent) {
        this.cotent = cotent;
    }

    public void setType(SingleType type) {
        this.type = type;
    }
}
