package xyz.yluo.ruisiapp.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yang on 2015/4/20 0020.
 * http_client post 和get 方法类
 * 复用类
 */
public class Http_client_post_get {
    public Http_client_post_get(String url, String[] list){

        this.url = url;
        this.list = list;

    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String url = "http://me.yluo.xyz/login.php";
    public String[] list;

    public String Resoult;

    //http get 方法
    public String  HttpURLConnection_GET() {
        HttpURLConnection conn = null;
        try {
            URL myurl = new URL(url);
            //1.得到HttpURLConnection实例化对象
            conn = (HttpURLConnection) myurl.openConnection();
            //2.设置请求信息（请求方式... ...）
            //设置请求方式和响应时间
            conn.setRequestMethod("GET");
            //conn.setRequestProperty("encoding","UTF-8"); //可以指定编码
            conn.setConnectTimeout(5000);
            //不使用缓存
            conn.setUseCaches(false);
            //3.读取相应
            if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    os.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                os.close();
                // 返回字符串
                String result = new String(os.toByteArray());
                System.out.println("***************" + result + "******************");
                return result;
            } else {
                System.out.println("请求失败！");
                return "error";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //4.释放资源
            if (conn != null) {
                //关闭连接 即设置 http.keepAlive = false;
                conn.disconnect();
            }
        }
        return "error";
    }

    //http post 方法 表单
    public String  HttpURLConnection_POST(String para) {
        HttpURLConnection conn = null;
        try {
            URL myurl = new URL("url");
            //TODO para 以后要处理
            //String para = new String("username=admin&password=admin");

            //1.得到HttpURLConnection实例化对象
            conn = (HttpURLConnection) myurl.openConnection();
            //2.设置请求方式
            conn.setRequestMethod("POST");
            //3.设置post提交内容的类型和长度
		/*
		 * 只有设置contentType为application/x-www-form-urlencoded，
		 * servlet就可以直接使用request.getParameter("username");直接得到所需要信息
		 */
            conn.setRequestProperty("contentType", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(para.getBytes().length));
            //默认为false
            conn.setDoOutput(true);
            //4.向服务器写入数据
            conn.getOutputStream().write(para.getBytes());

            //5.得到服务器相应
            if (conn.getResponseCode() == 200) {
                System.out.println("服务器已经收到表单数据！");
                InputStream inStream = conn.getInputStream();
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取

                // 创建字节输出流对象
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int len = 0;
                while ((len = inStream.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    os.write(buffer, 0, len);
                }

                // 释放资源
                inStream.close();
                os.close();
                // 返回字符串
                String result = new String(os.toByteArray());
                System.out.println("***************" + result + "******************");
                return result;
            } else {
                System.out.println("请求失败！");
                return "error";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //6.释放资源
            if (conn != null) {
                //关闭连接 即设置 http.keepAlive = false;
                conn.disconnect();
            }
        }

        return "error";
    }


}
