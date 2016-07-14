package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-23.
 * 根据fid获得板块logo
 */
public class GetLogoUtils {

    public static Drawable getlogo(Context contex, String url) {
        try {

            String fid = GetId.getFroumFid(url);
            InputStream ims = contex.getAssets().open("forumlogo/common_" + fid + "_icon.gif");
            return Drawable.createFromStream(ims, null);
        } catch (IOException ex) {
            return ContextCompat.getDrawable(contex, R.drawable.image_placeholder);
        }
    }

}
