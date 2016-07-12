package xyz.yluo.ruisiapp.View;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;

/**
 * Created by free2 on 16-3-20.
 * 是否要退出登录
 */
public class ExitLoginDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("你要退出登录吗？？？")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //删除cookie
                        HttpUtil.exit();
                        PublicData.ISLOGIN = false;
                        PublicData.USER_NAME = "";
                        PublicData.USER_UID = "";

                        SharedPreferences perUserInfo = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = perUserInfo.edit();
                        editor.clear();
                        editor.apply();

                        getActivity().finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
