package xyz.yluo.ruisiapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by free2 on 16-4-11.
 * 获得楼层
 */
public  class GetIndex {
    public static int getIndex(String index){
        int num = 1;
        if(index.contains("楼主")||index.contains("收藏")){
            num=1;
        }else if(index.contains("沙发")){
            num =2 ;
        }else if(index.contains("板凳")){
            num =3;
        }else if(index.contains("地板")){
            num =4;
        }else {
            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher(index);
            if (matcher.find()) {
                String temps = index.substring(matcher.start(), matcher.end());
                num = Integer.parseInt(temps);
            }
        }

        return num;
    }
}
