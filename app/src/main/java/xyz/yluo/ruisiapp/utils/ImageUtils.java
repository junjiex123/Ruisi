package xyz.yluo.ruisiapp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.view.MyReplyView;

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

    public static String getSmileyName(int smiley_type,String name){
        String insertName = "";
        if (smiley_type == MyReplyView.SMILEY_TB) {
            switch (name) {
                case "tb001":
                    insertName = "16_1014";
                    break;
                case "tb002":
                    insertName = "16_1006";
                    break;
                case "tb003":
                    insertName = "16_1018";
                    break;
                case "tb004":
                    insertName = "16_1001";
                    break;
                case "tb005":
                    insertName = "16_1019";
                    break;
                case "tb006":
                    insertName = "16_1025";
                    break;
                case "tb008":
                    insertName = "16_1013";
                    break;
                case "tb009":
                    insertName = "16_1024";
                    break;
                case "tb010":
                    insertName = "16_1020";
                    break;
                case "tb011":
                    insertName = "16_1022";
                    break;
                case "tb012":
                    insertName = "16_1000";
                    break;
                case "tb013":
                    insertName = "16_999";
                    break;
                case "tb014":
                    insertName = "16_998";
                    break;
                case "tb015":
                    insertName = "16_1028";
                    break;
                case "tb016":
                    insertName = "16_1017";
                    break;
                case "tb017":
                    insertName = "16_1023";
                    break;
                case "tb018":
                    insertName = "16_1009";
                    break;
                case "tb019":
                    insertName = "16_1030";
                    break;
                case "tb020":
                    insertName = "16_1007";
                    break;
                case "tb021":
                    insertName = "16_1015";
                    break;
                case "tb022":
                    insertName = "16_1011";
                    break;
                case "tb023":
                    insertName = "16_1029";
                    break;
                case "tb024":
                    insertName = "16_1003";
                    break;
                case "tb025":
                    insertName = "16_1016";
                    break;
                case "tb026":
                    insertName = "16_1008";
                    break;
                case "tb027":
                    insertName = "16_1002";
                    break;
                case "tb028":
                    insertName = "16_1005";
                    break;
                case "tb029":
                    insertName = "16_1027";
                    break;
                case "tb030":
                    insertName = "16_1010";
                    break;
                case "tb031":
                    insertName = "16_1012";
                    break;
                case "tb032":
                    insertName = "16_1021";
                    break;
            }
        } else if (smiley_type == MyReplyView.SMILEY_ALI) {
            Log.e("error",name);
            switch (name){
                case "035": insertName = "10_366"; break;
                case "075": insertName = "10_368"; break;
                case "048": insertName = "10_381"; break;
                case "087": insertName = "10_385"; break;
                case "057": insertName = "10_387"; break;
                case "019": insertName = "10_392"; break;
                case "027": insertName = "10_401"; break;
                case "007": insertName = "10_405"; break;
                case "042": insertName = "10_406"; break;
                case "016": insertName = "10_408"; break;
                case "040": insertName = "10_409"; break;
                case "020": insertName = "10_410"; break;
                case "103": insertName = "10_411"; break;
                case "045": insertName = "10_412"; break;
                case "088": insertName = "10_417"; break;
                case "005": insertName = "10_424"; break;
                case "069": insertName = "10_425"; break;
                case "009": insertName = "10_428"; break;
                case "036": insertName = "10_429"; break;
                case "073": insertName = "10_434"; break;
                case "010": insertName = "10_436"; break;
                case "090": insertName = "10_437"; break;
                case "106": insertName = "10_439"; break;
                case "037": insertName = "10_443"; break;
                case "012": insertName = "10_445"; break;
                case "100": insertName = "10_447"; break;
                case "101": insertName = "10_449"; break;
                case "033": insertName = "10_456"; break;
                case "074": insertName = "10_457"; break;
                case "003": insertName = "10_463"; break;
                case "051": insertName = "10_467"; break;
                case "008": insertName = "10_471"; break;
                case "104": insertName = "10_472"; break;
                case "049": insertName = "10_473"; break;
            }

        } else if (smiley_type == MyReplyView.SMILEY_ACN) {
            switch (name) {
                case "acn062":
                    insertName = "15_950";
                    break;
                case "acn006":
                    insertName = "15_963";
                    break;
                case "acn025":
                    insertName = "15_964";
                    break;
                case "acn035":
                    insertName = "15_965";
                    break;
                case "acn071":
                    insertName = "15_966";
                    break;
                case "acn017":
                    insertName = "15_968";
                    break;
                case "acn047":
                    insertName = "15_969";
                    break;
                case "acn016":
                    insertName = "15_970";
                    break;
                case "acn009":
                    insertName = "15_971";
                    break;
                case "acn010":
                    insertName = "15_972";
                    break;
                case "acn020":
                    insertName = "15_962";
                    break;
                case "acn072":
                    insertName = "15_961";
                    break;
                case "acn060":
                    insertName = "15_951";
                    break;
                case "acn065":
                    insertName = "15_952";
                    break;
                case "acn070":
                    insertName = "15_953";
                    break;
                case "acn032":
                    insertName = "15_954";
                    break;
                case "acn049":
                    insertName = "15_956";
                    break;
                case "acn013":
                    insertName = "15_957";
                    break;
                case "acn061":
                    insertName = "15_958";
                    break;
                case "acn064":
                    insertName = "15_959";
                    break;
                case "acn093":
                    insertName = "15_973";
                    break;
                case "acn083":
                    insertName = "15_987";
                    break;
                case "acn054":
                    insertName = "15_989";
                    break;
                case "acn069":
                    insertName = "15_990";
                    break;
                case "acn074":
                    insertName = "15_991";
                    break;
                case "acn043":
                    insertName = "15_992";
                    break;
                case "acn039":
                    insertName = "15_993";
                    break;
                case "acn089":
                    insertName = "15_994";
                    break;
                case "acn005":
                    insertName = "15_986";
                    break;
                case "acn018":
                    insertName = "15_985";
                    break;
                case "acn079":
                    insertName = "15_976";
                    break;
                case "acn008":
                    insertName = "15_977";
                    break;
                case "acn022":
                    insertName = "15_982";
                    break;
                case "acn096":
                    insertName = "15_983";
                    break;
                case "acn081":
                    insertName = "15_949";
                    break;
                case "acn055":
                    insertName = "15_901";
                    break;
                case "acn050":
                    insertName = "15_914";
                    break;
                case "acn031":
                    insertName = "15_916";
                    break;
                case "acn059":
                    insertName = "15_918";
                    break;
                case "acn073":
                    insertName = "15_919";
                    break;
                case "acn097":
                    insertName = "15_922";
                    break;
                case "acn068":
                    insertName = "15_913";
                    break;
                case "acn044":
                    insertName = "15_912";
                    break;
                case "acn011":
                    insertName = "15_904";
                    break;
                case "acn087":
                    insertName = "15_905";
                    break;
                case "acn085":
                    insertName = "15_906";
                    break;
                case "acn057":
                    insertName = "15_907";
                    break;
                case "acn090":
                    insertName = "15_909";
                    break;
                case "acn088":
                    insertName = "15_910";
                    break;
                case "acn080":
                    insertName = "15_911";
                    break;
                case "acn004":
                    insertName = "15_924";
                    break;
                case "acn007":
                    insertName = "15_938";
                    break;
                case "acn003":
                    insertName = "15_939";
                    break;
                case "acn029":
                    insertName = "15_940";
                    break;
                case "acn058":
                    insertName = "15_943";
                    break;
                case "acn042":
                    insertName = "15_944";
                    break;
                case "acn067":
                    insertName = "15_945";
                    break;
                case "acn094":
                    insertName = "15_946";
                    break;
                case "acn048":
                    insertName = "15_947";
                    break;
                case "acn066":
                    insertName = "15_936";
                    break;
                case "acn034":
                    insertName = "15_928";
                    break;
                case "acn012":
                    insertName = "15_929";
                    break;
                case "acn086":
                    insertName = "15_930";
                    break;
                case "acn056":
                    insertName = "15_931";
                    break;
                case "acn019":
                    insertName = "15_932";
                    break;
                case "acn015":
                    insertName = "15_933";
                    break;
                case "acn021":
                    insertName = "15_934";
                    break;
                case "acn063":
                    insertName = "15_948";
                    break;
            }
        }
        return insertName;
    }



}
