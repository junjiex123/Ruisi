package xyz.yluo.ruisiapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by free2 on 16-3-19.
 * 通过链接获得tid
 * uid
 * 各种id
 */
public class GetId {

    public static String getTid(String url){
        //forum.php?mod=redirect&goto=findpost&ptid=846689&pid=21330831
        Pattern pattern = Pattern.compile("tid=[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String tid ="";
        if (matcher.find()) {
            tid = url.substring(matcher.start()+4,matcher.end());
            //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
        }
        return tid;
    }

    public static String getPid(String url){
        //forum.php?mod=redirect&goto=findpost&ptid=846689&pid=21330831
        Pattern pattern = Pattern.compile("pid=[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String pid ="";
        if (matcher.find()) {
            pid = url.substring(matcher.start()+4,matcher.end());
            //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
        }
        return pid;
    }

    public static String getUid(String url){
        //http://rs.xidian.edu.cn/ucenter/avatar.php?uid=284747&size=small

        Pattern pattern = Pattern.compile("uid=[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String uid ="";
        if (matcher.find()) {
            uid = url.substring(matcher.start()+4,matcher.end());
        }
        return uid;
    }

    public static String getFroumFid(String url){

        try {
            //fid=[0-9]+
            Pattern pattern = Pattern.compile("fid=[0-9]+");
            Matcher matcher = pattern.matcher(url);
            String fid ="";
            if (matcher.find()) {
                fid = url.substring(matcher.start()+4,matcher.end());
            }
            if(fid.equals("106")){
                fid="110";
            }else if(fid.equals("553")){
                fid="554";
            }
            return fid;

        }catch (Exception e){
            e.printStackTrace();

            return "0";
        }
    }
}
