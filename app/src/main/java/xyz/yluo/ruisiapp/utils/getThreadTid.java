package xyz.yluo.ruisiapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by free2 on 16-3-19.
 * 通过链接获得tid
 */
public class getThreadTid {

    public static String getTid(String url){
        //http://rs.xidian.edu.cn/forum.php?mod=viewthread&tid=840272&extra=

        Pattern pattern = Pattern.compile("[0-9]{3,}");
        Matcher matcher = pattern.matcher(url);
        String tid ="";
        while (matcher.find()) {
            tid = url.substring(matcher.start(),matcher.end());
            break;
            //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
        }
        return tid;
    }
}
