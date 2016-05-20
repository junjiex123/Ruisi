package xyz.yluo.ruisiapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by free2 on 16-5-20.
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="xidianrs.db";
    //更改版本后数据库将重新创建
    private static final int  DATABASE_VERSION=1;
    private static final String TABLE_NAME="rs_article_list";



    public SQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);//继承父类
    }


    /**
     * 该函数是在第一次创建数据库时执行，只有当其调用getreadabledatebase()
     */
    public void onCreate(SQLiteDatabase db)
    {

        String sql = "CREATE TABLE " + TABLE_NAME + "("
                + "tid VARCHAR(10) primary key,"
                + "title VARCHAR(50),"
                + "uid VARCHAR(10),"
                + "author VARCHAR(15),"
                + "time DATETIME,"
                + "read_time DATETIME,"
                + "view VARCHAR(10),"
                + "reply VARCHAR(10)"
                + ")";
        db.execSQL(sql);
        Log.e("create","数据库创建成功");
    }


    /**
     *数据库更新函数，当数据库更新时会执行此函数
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        this.onCreate(db);
        System.out.println("数据库已经更新");
    }

}