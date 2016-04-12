package xyz.yluo.ruisiapp.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import xyz.yluo.ruisiapp.MyPublicData;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;

/**
 * Created by free2 on 16-3-20.
 * 是否要登陆
 */
public class ExitLoginDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("你要退出登录吗？？？")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HttpUtil.exit();
                        MyPublicData.ISLOGIN = false;
                        MyPublicData.USER_NAME = "";
                        MyPublicData.USER_UID = "";
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
