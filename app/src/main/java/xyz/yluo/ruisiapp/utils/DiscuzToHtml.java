package xyz.yluo.ruisiapp.utils;

/**
 * Created by yang on 2016/9/5.
 * todo
 */

public class DiscuzToHtml {
    public static String toHtml(String s){
        s = s.replace("[b]","<b>");
        s =  s.replace("[/b]","</b>");

        s = s.replace("[i]","<i>");
        s =  s.replace("[/i]","</i>");

        s = s.replace("[quote]","<blockquote>");
        s =  s.replace("[/quote]","</blockquote>");

        s = s.replace("[size=1]","<font size=\"1\">");//<font size="6">哈哈</font>
        s = s.replace("[size=2]","<font size=\"2\">");
        s = s.replace("[size=3]","<font size=\"3\">");
        s = s.replace("[size=4]","<font size=\"4\">");
        s = s.replace("[size=5]","<font size=\"5\">");
        s = s.replace("[size=6]","<font size=\"6\">");
        s = s.replace("[size=7]","<font size=\"7\">");
        s =  s.replace("[/size]","</size>");

        return s;
    }
}
