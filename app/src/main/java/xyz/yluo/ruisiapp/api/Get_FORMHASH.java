package xyz.yluo.ruisiapp.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.http.MyHttpConnection;

/**
 * Created by free2 on 16-3-14.
 *
 * 获得FORMHASH
 */
public class Get_FORMHASH {

    //bool 参数是否强制更新 FORMHASH
    public static String get_Hash(boolean isForce){
        if(!isForce){
            if(ConfigClass.CONFIG_FORMHASH!=""){
                return ConfigClass.CONFIG_FORMHASH;
            }else{
                return doget();
            }
        }else {
            return doget();
        }
    }
    private static String doget(){

        String url = ConfigClass.BBS_BASE_URL+"forum.php";
        String get_response = MyHttpConnection.Http_get(url);
        Document doc = Jsoup.parse(get_response);
        // 具有 formhash 属性的链接
        if (doc.select("input[name=formhash]").first() != null) {
            ConfigClass.CONFIG_FORMHASH = doc.select("input[name=formhash]").first().attr("value");
            return ConfigClass.CONFIG_FORMHASH;
        }else{
            return "";
        }
    }
}
