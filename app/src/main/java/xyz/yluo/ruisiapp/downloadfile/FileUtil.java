package xyz.yluo.ruisiapp.downloadfile;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * 类描述：FileUtil
 * 创建下载的文件
 */
public class FileUtil {
    /***********保存升级APK的目录***********/
    public static final String path = "Download/手机睿思下载";
    /**
     * 方法描述：createFile方法
     */
    public static File createFile(String filename) {
        boolean isCreateFileSucess = true;
        File fileDir,downFile = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            fileDir = new File(Environment.getExternalStorageDirectory()+ "/" + path +"/");
            downFile = new File(fileDir + "/" + filename);

            if (!fileDir.exists()) {
                boolean b =  fileDir.mkdirs();
                Log.i("create directory",b+fileDir.getPath()+"");
            }
            if (downFile.exists()) {
                if(downFile.delete()){
                    try {
                        boolean b =  downFile.createNewFile();

                        Log.i("create file",b+downFile.getPath()+"");
                    } catch (IOException e) {
                        isCreateFileSucess = false;
                        e.printStackTrace();
                    }
                }
            }
        }else{
            isCreateFileSucess = false;
        }

        if(isCreateFileSucess){
            return  downFile;
        }else{
            return null;
        }
    }

    public static boolean  deleteFile(String filename) {
        File fileDir = new File(Environment.getExternalStorageDirectory() + "/" + path + "/");
        File file = new File(fileDir + "/" + filename);
        Log.i("file","delete file");
        return !file.exists() || file.delete();

    }

    public static void requestHandleFile(Context context,String fileName){
        File fileDir = new File(Environment.getExternalStorageDirectory() + "/" + path + "/");
        File file = new File(fileDir + "/" + fileName);
        if(fileName.endsWith(".apk")){
            /*********下载完成，点击安装***********/
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            /**********加这个属性是因为使用Context的startActivity方法的话，就需要开启一个新的task**********/
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri,"application/vnd.android.package-archive");
            context.startActivity(intent);
        }else{
            String filetype = fileExt(fileName).substring(1);
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension(filetype);
            newIntent.setDataAndType(Uri.fromFile(file),mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(newIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "无法打开此类型文件~~~~~~", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return "";
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            Log.i("type",ext.toLowerCase());
            return ext.toLowerCase();
        }
    }

    public static String getFileName(String url){

        String fileName = url;
        if(url.contains("/")){
            fileName =url.substring(url.lastIndexOf(".") + 1);
        }
        if(fileName.contains(".")){
            String txt = fileName.substring(fileName.lastIndexOf(".")+1);
            if(txt.length()>4){
                return "null";
            }
            return fileName;
        }else{
            return "null";
        }
    }
}