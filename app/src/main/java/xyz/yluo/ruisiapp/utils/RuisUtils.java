package xyz.yluo.ruisiapp.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.model.Category;
import xyz.yluo.ruisiapp.model.Forum;

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

    public static Map<String, String> getForms(Document document, String id) {
        Element element = document.getElementById(id);
        Map<String, String> params = new HashMap<>();
        if (element == null) return params;
        Elements inputs = element.select("input");
        for (Element ee : inputs) {
            String key = ee.attr("name");
            String type = ee.attr("type");
            String value = ee.attr("value");
            if (!TextUtils.isEmpty(key) && !"submit".equals(type)) {
                params.put(key, value);
            }
        }

        Elements textareas = element.select("textarea");
        for (Element ee : textareas) {
            String key = ee.attr("name");
            String value = ee.html();
            params.put(key, value);
        }

        return params;
    }

    public static List<Category> getForums(Context context, boolean isLogin) {
        InputStream in = null;
        String s;
        try {
            in = context.getAssets().open("forums.json");
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            s = new String(buffer);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Category> cates = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                boolean cateLogin = o.getBoolean("login");
                if (!isLogin && cateLogin) {//false true
                    continue;
                }
                List<Forum> fs = new ArrayList<>();
                JSONArray forums = o.getJSONArray("forums");
                for (int j = 0; j < forums.length(); j++) {
                    JSONObject oo = forums.getJSONObject(j);
                    boolean forumLogin = oo.getBoolean("login");
                    if (!isLogin && forumLogin) {//false true
                        continue;
                    }
                    fs.add(new Forum(oo.getString("name"), oo.getInt("fid"), forumLogin));
                }
                cates.add(new Category(o.getString("name"), o.getInt("gid"), cateLogin, fs));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cates;
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
