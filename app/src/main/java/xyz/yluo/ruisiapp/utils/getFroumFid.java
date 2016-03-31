package xyz.yluo.ruisiapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by free2 on 16-3-23.
 * 一些地址在这儿修正
 */
public class getFroumFid {
    public static String getFid(String url){

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

    }
}
