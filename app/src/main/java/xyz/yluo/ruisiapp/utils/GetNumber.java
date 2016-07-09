package xyz.yluo.ruisiapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by free2 on 16-7-9.
 * 从字符串中获得数字
 */
public class GetNumber {
    public static int getNumber(String text){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(text);
        String num ="0";
        if (matcher.find()) {
            num = text.substring(matcher.start(),matcher.end());
        }
        return Integer.parseInt(num);
    }
}
