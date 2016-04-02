package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-23.
 *
 */
public class GetLogoUtils {

    public static Drawable getlogo(Context contex,String url){
        try {

            String fid = GetId.getFroumFid(url);
            //./data/attachment/common/32/common_72_icon.gif
            //forum.php?mod=forumdisplay&fid=72
            // get input stream
            //file:///android_asset/forumlogo/xxx.png
            InputStream ims = contex.getAssets().open("forumlogo/common_"+fid+"_icon.gif");
            // load image as Drawable

            return Drawable.createFromStream(ims, null);
        }
        catch(IOException ex) {
          //nothing
            return ContextCompat.getDrawable(contex,R.drawable.image_placeholder);
        }
    }

    public static Drawable getBtLogo(Context context,String url){

        Pattern pattern = Pattern.compile("[0-9]{2,}");
        Matcher matcher = pattern.matcher(url);
        String tid ="";
        String trueid  ="";
        while (matcher.find()) {
            tid = url.substring(matcher.start(),matcher.end());
            trueid = tid.substring(0,2);
            break;
            //System.out.println("\ntid is------->>>>>>>>>>>>>>:" +  articleUrl.substring(matcher.start(),matcher.end()));
        }

        try {

            InputStream ims = context.getAssets().open("btlogo/"+trueid+"00.png");
            // load image as Drawable

            return Drawable.createFromStream(ims, null);
        }
        catch(IOException ex) {
            //nothing
            return ContextCompat.getDrawable(context,R.drawable.image_placeholder);
        }
    }


}
