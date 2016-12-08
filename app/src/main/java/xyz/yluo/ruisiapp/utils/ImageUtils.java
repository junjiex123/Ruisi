package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-7-13.
 * 获得板块图标
 */
public class ImageUtils {

    /**
     * 获得板块图标
     */
    public static Drawable getForunlogo(Context contex, int fid) {
        try {
            InputStream ims = contex.getAssets().open("forumlogo/common_" + fid + "_icon.gif");
            return Drawable.createFromStream(ims, null);
        } catch (IOException ex) {
            return ContextCompat.getDrawable(contex, R.drawable.image_placeholder);
        }
    }
}
