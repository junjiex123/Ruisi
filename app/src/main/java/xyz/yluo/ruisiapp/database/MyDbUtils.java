package xyz.yluo.ruisiapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import xyz.yluo.ruisiapp.data.ArticleListData;
import xyz.yluo.ruisiapp.utils.GetId;

/**
 * Created by free2 on 16-5-20.
 *+ "tid VARCHAR(10) primary key,"
 + "title VARCHAR(50),"
 + "uid VARCHAR(10),"
 + "author VARCHAR(15),"
 + "time DATETIME,"
 + "read_time DATETIME,"
 + "view VARCHAR(10),"
 + "reply VARCHAR(10)"
 */
public class MyDbUtils {

    //tid title uid author  time  view reply read_time
    //要操作的数据表的名称
    private static final String TABLE_NAME = "rs_article_list";
    private SQLiteDatabase db=null;	//数据库操作

    private String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());
        return format.format(curDate);
    }

    //构造函数
    public MyDbUtils(Context context,boolean isRead)
    {
        if(isRead){
            this.db = new SQLiteHelper(context).getReadableDatabase();
        }else{
            this.db = new SQLiteHelper(context).getWritableDatabase();
        }
    }

    //处理单个点击事件
    public void handleSingle(ArticleListData data){

        String tid = GetId.getTid(data.getTitleUrl());
        if(isRead(tid)){
            update(tid,data.getTitle(),data.getViewCount(),data.getReplayCount());
        }else{
            String uid = GetId.getUid(data.getAuthorUrl());
            insert(tid,data.getTitle(),uid,data.getAuthor(),data.getPostTime(),data.getViewCount(),data.getReplayCount());
        }
    }

    //判断list<> 是否为已读并修改返回
    public List<ArticleListData> handleList(List<ArticleListData> datas){
        String sql="SELECT tid from " + TABLE_NAME + " where tid = ?";
        for(ArticleListData data:datas){
            String tid = GetId.getTid(data.getTitleUrl());
            String args[] =new String[]{String.valueOf(tid)};

            Cursor result=db.rawQuery(sql,args);
            int count = result.getCount();
            result.close();
            if(count!=0)//判断得到的返回数据是否为空
            {
                data.setRead(true);
                Log.e("mydb",tid+"is read");
            }
        }
        db.close();
        return datas;
    }

    //判断插入数据的ID是否已经存在数据库中。
    private boolean isRead(String tid)
    {
        String sql="SELECT tid from " + TABLE_NAME + " where tid = ?";
        String args[] =new String[]{String.valueOf(tid)};
        Cursor result=db.rawQuery(sql,args);
        int count = result.getCount();
        result.close();
        if(count==0)//判断得到的返回数据是否为空
        {
            //db.close();
            return false;
        }else{
            //db.close();
            return true;
        }
    }
    //	//插入操作
    private void insert(String tid,String title,String uid,String author,String time,String view,String reply)
    {
        String sql = "INSERT INTO " + TABLE_NAME + " (tid,title,uid,author,time,view,reply,read_time)"
                + " VALUES(?,?,?,?,?,?,?,?)";

        String read_time_str = getTime();
        Object args[]=new Object[]{tid,title,uid,author,time,view,reply,read_time_str};
        this.db.execSQL(sql, args);
        this.db.close();
        Log.e("mydb",tid+"insert");
    }

    //更新操作
    private void update(String tid,String title,String view,String reply)
    {
        String read_time_str = getTime();
        String sql = "UPDATE " + TABLE_NAME + " SET title=?,view =?,reply =?,read_time=? WHERE tid=?";
        Object args[]=new Object[]{title,view,reply,read_time_str,tid};
        this.db.execSQL(sql, args);
        this.db.close();

        Log.e("mydb",tid+"update");
    }

    //删除操作,删除
    public void delete(int tid)
    {
        String sql = "DELETE FROM " + TABLE_NAME +" WHERE tid=?";
        Object args[]=new Object[]{tid};
        this.db.execSQL(sql, args);
        this.db.close();
    }

    public void showDatabase(){
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
        for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
         {
             System.out.println(result.getString(0)+","+result.getString(1)+","+result.getString(2)+","+result.getString(3)+","
                     +result.getString(4)+","+result.getString(5)+","+result.getString(6)+","+result.getString(7));
        }
        result.close();
        this.db.close();
    }

    public List<ArticleListData> getHistory(int num){
        List<ArticleListData> datas = new ArrayList<>();
        String sql = "SELECT * FROM "+TABLE_NAME+" order by read_time desc limit "+num;
        Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
        for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
        {
            /**
             * tid VARCHAR(10) primary key,"
             + "title VARCHAR(50),"
             + "uid VARCHAR(10),"
             + "author VARCHAR(15),"
             + "time DATETIME,"
             + "read_time DATETIME,"
             + "view VARCHAR(10),"
             + "reply VARCHAR(10)"
             */
            datas.add(new ArticleListData(false,result.getString(1),result.getString(0),result.getString(3),result.getString(7)));
        }
        result.close();
        this.db.close();
        return datas;
    }
}