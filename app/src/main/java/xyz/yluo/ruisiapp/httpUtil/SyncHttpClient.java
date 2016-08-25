package xyz.yluo.ruisiapp.httpUtil;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import xyz.yluo.ruisiapp.App;

public class SyncHttpClient {
    public static final String DEFAULT_USER_AGENT = "myRuisiAsyncLiteHttp/1.0";
    static final String UTF8 = "UTF-8";
    private static PersistentCookieStore store;
    private final int dataRetrievalTimeout = 8000;
    private int connectionTimeout = 8000;
    private Map<String, String> headers;
    //重定向地址
    private String Location = null;

    SyncHttpClient() {
        headers = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        setUserAgent(DEFAULT_USER_AGENT);
        Location = null;
    }

    public void setStore(PersistentCookieStore store) {
        if (SyncHttpClient.store == null) {
            SyncHttpClient.store = store;
        }
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setUserAgent(String userAgent) {
        headers.put("User-Agent", userAgent);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void removeHeader(String name) {
        headers.remove(name);
    }

    private void getCookie(HttpURLConnection conn) {
        String fullCookie = "";
        String cookieVal;
        String key;
        //取cookie
        for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
            if (key.equalsIgnoreCase("set-cookie")) {
                cookieVal = conn.getHeaderField(i);
                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                fullCookie = fullCookie + cookieVal + ";";
            }
        }
        store.addCookie(fullCookie);
    }

    private HttpURLConnection buildURLConnection(String url, Method method) throws IOException {
        URL resourceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();

        // Settings
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(dataRetrievalTimeout);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method.toString());
        connection.setDoInput(true);

        //加入cookie
        if (store != null) {
            connection.setRequestProperty("Cookie", store.getCookie());
        }

        // Headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
        return connection;
    }

    private byte[] encodeParameters(Map<String, String> map) {
        if (map == null) {
            map = new TreeMap<>();
        }
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (encodedParams.length() > 0) {
                    encodedParams.append("&");
                }
                encodedParams.append(URLEncoder.encode(entry.getKey(), UTF8));
                encodedParams.append('=');
                String v = entry.getValue() == null ? "" : entry.getValue();
                encodedParams.append(URLEncoder.encode(v, UTF8));
            }
            return encodedParams.toString().getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + UTF8, e);
        }
    }

    public void get(final String url, final ResponseHandler handler) {
        request(url, Method.GET, null, handler);
    }


    public void post(final String url, final Map<String, String> map, final ResponseHandler handler) {
        request(url, Method.POST, map, handler);
    }

    public void head(final String url,final Map<String, String> map, final ResponseHandler handler) {
        request(url, Method.HEAD, map, handler);
    }

    void request(final String url, final Method method, final Map<String, String> map,
                 final ResponseHandler handler) {
        HttpURLConnection connection = null;
        Log.i("httputil", "request url " + url);
        try {
            connection = buildURLConnection(url, method);

            // Request start
            handler.sendStartMessage();

            if (method == Method.POST) {
                // Send content as form-urlencoded
                byte[] content = encodeParameters(map);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + UTF8);
                connection.setRequestProperty("Content-Length", Long.toString(content.length));
                connection.setFixedLengthStreamingMode(content.length);
                OutputStream os = connection.getOutputStream();
                os.write(content);
                os.flush();
                os.close();
            } else if (method == Method.HEAD) {
                Log.i("head",connection.getHeaderFields().toString());
                String location = connection.getHeaderField("Location");
                handler.sendSuccessMessage(location.getBytes());
                connection.connect();
                return;
            }
//            处理重定向
            int code = connection.getResponseCode();
            if (code == 302||code==301) {
                //如果会重定向，保存302 301重定向地址,然后重新发送请求(模拟请求)
                String location = connection.getHeaderField("Location");
                Log.i("httputil", "302 new location is " + location);
                if(TextUtils.isEmpty(Location)||(!Location.equals(location))){
                    Location = location;
                    request(App.getBaseUrl() + location, Method.GET, map, handler);
                }else{
                    handler.sendFailureMessage(new Throwable("重定向错误"));
                }
                connection.disconnect();
                return;
            } else {
                handler.processResponse(connection);
                //获取cookie
                if (store != null) {
                    getCookie(connection);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendFailureMessage(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            // Request finished
            handler.sendFinishMessage();
        }
    }
}
