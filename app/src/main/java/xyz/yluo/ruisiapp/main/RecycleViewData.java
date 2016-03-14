package xyz.yluo.ruisiapp.main;

/**
 * Created by free2 on 16-3-5.
 *
 */
public class RecycleViewData {

    private int type;
    private String title;


    //这个构造生成每列数据
    public RecycleViewData(int type,  String title) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }
}
