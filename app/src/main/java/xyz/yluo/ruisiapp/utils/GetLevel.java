package xyz.yluo.ruisiapp.utils;

/**
 * Created by free2 on 16-3-12.
 * 根据积分获得等级
 */
public class GetLevel {
    public static String getUserLevel(int a){
        if(a>=0&&a<100) {
            return "西电托儿所";
        } else if(a<200){
            return " 西电幼儿园";
        }else if(a<500){
            return " 西电附小";
        }
        else if(a<1000){
            return " 西电附中";
        }
        else if(a<2000){
            return " 西电大一";
        }
        else if(a<2500){
            return " 西电大二";
        }
        else if(a<3000){
            return " 西电大三";
        }
        else if(a<3500){
            return " 西电大四";
        }
        else if(a<6000){
            return " 西电研一";
        }
        else if(a<10000){
            return " 西电研二";
        }
        else if(a<14000){
            return " 西电研三";
        }else if(a<20000){
            return " 西电博一";
        }else if(a<25000){
            return " 西电博二";
        }else if(a<30000){
            return " 西电博三";
        }else if(a<35000){
            return " 西电博四";
        }else if(a<40000){
            return " 西电博五";
        }else if(a>=40000&&a<100000){
            return " 西电博士后";
        }
        else{
                return "新手上路";
            }

    }
}
