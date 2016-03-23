package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.view.View;

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

            String fid = getFroumFid.getFid(url);
            //./data/attachment/common/32/common_72_icon.gif
            //forum.php?mod=forumdisplay&fid=72
            // get input stream
            //file:///android_asset/forumlogo/xxx.png
            InputStream ims = contex.getAssets().open("forumlogo/common_"+fid+"_icon.gif");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);

            return d;
        }
        catch(IOException ex) {
          //nothing
            return ContextCompat.getDrawable(contex,R.drawable.image_placeholder);
        }
    }


}
