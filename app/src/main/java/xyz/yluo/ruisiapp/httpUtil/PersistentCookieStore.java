package xyz.yluo.ruisiapp.httpUtil;

/**
 * Created by free2 on 16-4-4.
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;


public class PersistentCookieStore {

    private static final String COOKIE_PREFS = "Rs_Cookies";
    private final SharedPreferences cookiePrefs;
    Map<String, String> listCookie = new HashMap<>();


    public PersistentCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        Map<String, ?> allContent = cookiePrefs.getAll();

        //注意遍历map的方法
        for (Map.Entry<String, ?> entry : allContent.entrySet()) {
            listCookie.put(entry.getKey(), (String) entry.getValue());
        }


    }

    public void addCookie(String cookies) {
        //讲cookies持久化到本地
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        for (String tmp : cookies.split(";")) {
            if (tmp.contains("=")) {
                String key = tmp.split("=")[0];
                if (key.startsWith("Q8qA")) {
                    String value = tmp.split("=")[1];
                    listCookie.put(key, value);
                    prefsWriter.putString(key, value);
                }
            }
        }
        prefsWriter.apply();
    }

    public String getCookie() {
        String fulcookie = "";
        for (Map.Entry<String, String> entry : listCookie.entrySet()) {
            String temp = entry.getKey() + "=" + entry.getValue() + ";";
            fulcookie += temp;
        }
        return fulcookie;
    }


    public void clearCookie() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        listCookie.clear();
    }

}