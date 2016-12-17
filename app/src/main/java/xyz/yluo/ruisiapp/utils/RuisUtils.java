package xyz.yluo.ruisiapp.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

public class RuisUtils {

    /**
     * 获得板块图标
     */
    public static Drawable getForunlogo(Context contex, int fid) {
        try {
            InputStream ims = contex.getAssets().open("forumlogo/common_" + fid + "_icon.gif");
            return Drawable.createFromStream(ims, null);
        } catch (IOException ex) {
            return null;
        }
    }

    public static String getLevel(int a) {
        if (a >= 0 && a < 100) {
            return "西电托儿所";
        } else if (a < 200) {
            return " 西电幼儿园";
        } else if (a < 500) {
            return " 西电附小";
        } else if (a < 1000) {
            return " 西电附中";
        } else if (a < 2000) {
            return " 西电大一";
        } else if (a < 2500) {
            return " 西电大二";
        } else if (a < 3000) {
            return " 西电大三";
        } else if (a < 3500) {
            return " 西电大四";
        } else if (a < 6000) {
            return " 西电研一";
        } else if (a < 10000) {
            return " 西电研二";
        } else if (a < 14000) {
            return " 西电研三";
        } else if (a < 20000) {
            return " 西电博一";
        } else if (a < 25000) {
            return " 西电博二";
        } else if (a < 30000) {
            return " 西电博三";
        } else if (a < 35000) {
            return " 西电博四";
        } else if (a < 40000) {
            return " 西电博五";
        } else if (a >= 40000 && a < 100000) {
            return " 西电博士后";
        } else {
            return "新手上路";
        }
    }

    public static float getLevelProgress(String s) {
        int a = Integer.parseInt(s);
        if (a >= 0 && a < 100) {
            return a / 100f;
        } else if (a < 200) {
            return (a - 100) / 100f;
        } else if (a < 500) {
            return (a - 200) / 300f;
        } else if (a < 1000) {
            return (a - 500) / 500f;
        } else if (a < 2000) {
            return (a - 1000) / 1000f;
        } else if (a < 2500) {
            return (a - 2000) / 500f;
        } else if (a < 3000) {
            return (a - 2500) / 500f;
        } else if (a < 3500) {
            return (a - 3000) / 500f;
        } else if (a < 6000) {
            return (a - 3500) / 2500f;
        } else if (a < 10000) {
            return (a - 6000) / 4000f;
        } else if (a < 14000) {
            return (a - 10000) / 4000f;
        } else if (a < 20000) {
            return (a - 14000) / 6000f;
        } else if (a < 25000) {
            return (a - 20000) / 15000f;
        } else if (a < 30000) {
            return (a - 25000) / 5000f;
        } else if (a < 35000) {
            return (a - 30000) / 5000f;
        } else if (a < 40000) {
            return (a - 35000) / 5000f;
        } else if (a >= 40000) {
            float b = (a - 40000) / 60000f;
            if (b > 1) b = 1;
            return b;
        } else {
            return 0;
        }


    }

    public static String toHtml(String s) {
        s = s.replace("[b]", "<b>");
        s = s.replace("[/b]", "</b>");

        s = s.replace("[i]", "<i>");
        s = s.replace("[/i]", "</i>");

        s = s.replace("[quote]", "<blockquote>");
        s = s.replace("[/quote]", "</blockquote>");

        s = s.replace("[size=1]", "<font size=\"1\">");//<font size="6">哈哈</font>
        s = s.replace("[size=2]", "<font size=\"2\">");
        s = s.replace("[size=3]", "<font size=\"3\">");
        s = s.replace("[size=4]", "<font size=\"4\">");
        s = s.replace("[size=5]", "<font size=\"5\">");
        s = s.replace("[size=6]", "<font size=\"6\">");
        s = s.replace("[size=7]", "<font size=\"7\">");
        s = s.replace("[/size]", "</size>");

        return s;
    }
}
