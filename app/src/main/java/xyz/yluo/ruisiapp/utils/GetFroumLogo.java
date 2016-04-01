package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-23.
 *
 */
public class GetFroumLogo {

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


}
