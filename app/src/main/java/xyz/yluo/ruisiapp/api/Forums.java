package xyz.yluo.ruisiapp.api;

/**
 * Created by free2 on 16-3-17.
 * 论坛版块 名称 fid type
 */
public class Forums {
    private   String name;
    private   int fid;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private   int type;

    public Forums(String name, int fid, int type) {
        this.name = name;
        this.fid = fid;
        this.type = type;
    }


}
