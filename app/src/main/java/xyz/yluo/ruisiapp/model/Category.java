package xyz.yluo.ruisiapp.model;

import java.util.List;

/**
 * Created by yangluo on 2017/3/23.
 * 一个板块
 */

public class Category {
    public String name;
    public boolean login;
    public int gid;
    public List<Forum> forums;

    public Category() {
    }

    public Category(String name, int gid, boolean login, List<Forum> forums) {
        this.name = name;
        this.login = login;
        this.forums = forums;
        this.gid = gid;
    }
}
