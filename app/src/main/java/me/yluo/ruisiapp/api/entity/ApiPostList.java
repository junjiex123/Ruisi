package me.yluo.ruisiapp.api.entity;

import java.util.List;

public class ApiPostList {
    private String cookiepre;
    private String auth;
    private String saltkey;
    private String member_uid;
    private String member_username;
    private String groupid;
    private String formhash;
    private String ismoderator;
    private String readaccess;
    private Notice notice;
    private Thread thread;
    private String fid;
    private List<Postlist> postlist;
    private String ppp;
    private Setting_rewriterule setting_rewriterule;
    private List<String> setting_rewritestatus;
    private String forum_threadpay;
    private List<String> cache_custominfo_postno;
    private Forum forum;
    public void setCookiepre(String cookiepre) {
        this.cookiepre = cookiepre;
    }
    public String getCookiepre() {
        return cookiepre;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
    public String getAuth() {
        return auth;
    }

    public void setSaltkey(String saltkey) {
        this.saltkey = saltkey;
    }
    public String getSaltkey() {
        return saltkey;
    }

    public void setMember_uid(String member_uid) {
        this.member_uid = member_uid;
    }
    public String getMember_uid() {
        return member_uid;
    }

    public void setMember_username(String member_username) {
        this.member_username = member_username;
    }
    public String getMember_username() {
        return member_username;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
    public String getGroupid() {
        return groupid;
    }

    public void setFormhash(String formhash) {
        this.formhash = formhash;
    }
    public String getFormhash() {
        return formhash;
    }

    public void setIsmoderator(String ismoderator) {
        this.ismoderator = ismoderator;
    }
    public String getIsmoderator() {
        return ismoderator;
    }

    public void setReadaccess(String readaccess) {
        this.readaccess = readaccess;
    }
    public String getReadaccess() {
        return readaccess;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }
    public Notice getNotice() {
        return notice;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
    public Thread getThread() {
        return thread;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }
    public String getFid() {
        return fid;
    }

    public void setPostlist(List<Postlist> postlist) {
        this.postlist = postlist;
    }
    public List<Postlist> getPostlist() {
        return postlist;
    }

    public void setPpp(String ppp) {
        this.ppp = ppp;
    }
    public String getPpp() {
        return ppp;
    }

    public void setSetting_rewriterule(Setting_rewriterule setting_rewriterule) {
        this.setting_rewriterule = setting_rewriterule;
    }
    public Setting_rewriterule getSetting_rewriterule() {
        return setting_rewriterule;
    }

    public void setSetting_rewritestatus(List<String> setting_rewritestatus) {
        this.setting_rewritestatus = setting_rewritestatus;
    }
    public List<String> getSetting_rewritestatus() {
        return setting_rewritestatus;
    }

    public void setForum_threadpay(String forum_threadpay) {
        this.forum_threadpay = forum_threadpay;
    }
    public String getForum_threadpay() {
        return forum_threadpay;
    }

    public void setCache_custominfo_postno(List<String> cache_custominfo_postno) {
        this.cache_custominfo_postno = cache_custominfo_postno;
    }
    public List<String> getCache_custominfo_postno() {
        return cache_custominfo_postno;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }
    public Forum getForum() {
        return forum;
    }

}
