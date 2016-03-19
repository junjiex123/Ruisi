package xyz.yluo.ruisiapp.utils;

/**
 * Created by free2 on 16-3-12.
 * 通过userUrl 获得头像URL
 */
public class GetUserImage {
    public static String getimageurl(String userurl){
        //12345
        http://rs.xidian.edu.cn/ucenter/data/avatar/000/29/84/87_avatar_small.jpg
        //String stringArray[]={"abc", "How", "you"};
        switch(userurl.split("=")[2].length()){
            //     00/00/00
            case 1:
                return "00/00/0"+userurl.split("=")[2];
            case 2:
                return "00/00/"+userurl.split("=")[2];
            case 3:
                return "00/0"+userurl.split("=")[2].charAt(0)+"/"+userurl.split("=")[2].charAt(1)+userurl.split("=")[2].charAt(2);

            case 4:
                return "00/"+userurl.split("=")[2].charAt(0)+userurl.split("=")[2].charAt(1)+"/"+userurl.split("=")[2].charAt(2)+userurl.split("=")[2].charAt(3);
            case 5:
                return "0"+userurl.split("=")[2].charAt(0)+"/"+userurl.split("=")[2].charAt(1)+userurl.split("=")[2].charAt(2)+"/"+userurl.split("=")[2].charAt(3)+userurl.split("=")[2].charAt(4);
            case 6:
                return userurl.split("=")[2].substring(0,2)+"/"+userurl.split("=")[2].substring(2,4)+"/"+userurl.split("=")[2].substring(4,6);
            default:
                return "00/00/00";

        }



    }
}
