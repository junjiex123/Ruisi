package me.yluo.ruisiapp.myhttp;

/**
 * Created by free2 on 16-4-4.
 * 缓存cookie类 每次http请求后写入cookie到首选项
 * 每次http请求前从首选项读出cookie
 * 注意只缓存rs的cookie
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;


class PersistentCookieStore {

    private static final String COOKIE_PREFS = "Rs_Cookies";
    private final SharedPreferences cookiePrefs;
    private Map<String, String> listCookie = new HashMap<>();


    PersistentCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        Map<String, ?> allContent = cookiePrefs.getAll();

        //注意遍历map的方法
        for (Map.Entry<String, ?> entry : allContent.entrySet()) {
            listCookie.put(entry.getKey(), (String) entry.getValue());
        }
    }

    void addCookie(String cookies) {
        //cookies持久化到本地
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        for (String tmp : cookies.split(";")) {
            if (tmp.contains("=")) {
                String key = tmp.split("=")[0];
                if (key.startsWith("Q8qA_2132")) {
                    String value = tmp.split("=")[1];
                    listCookie.put(key, value);
                    prefsWriter.putString(key, value);
                }
            }
        }
        prefsWriter.apply();
    }

    String getCookie() {
        StringBuilder fulcookie = new StringBuilder();
        for (Map.Entry<String, String> entry : listCookie.entrySet()) {
            String temp = entry.getKey() + "=" + entry.getValue() + ";";
            fulcookie.append(temp);
        }
        return fulcookie.toString();
    }


    void clearCookie() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        listCookie.clear();
    }

}