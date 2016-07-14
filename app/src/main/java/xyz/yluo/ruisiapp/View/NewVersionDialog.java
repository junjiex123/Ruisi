package xyz.yluo.ruisiapp.View;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import xyz.yluo.ruisiapp.utils.RequestOpenBrowser;

/**
 * Created by free2 on 16-3-20.
 * 检测到新版本提示
 */
public class NewVersionDialog extends DialogFragment {

    private String code = "1.0";
    private String message = "";

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("检测到新版本：" + code);
        builder.setMessage(message)
                .setPositiveButton("去下载", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RequestOpenBrowser.openBroswer(getActivity(), "http://xidianrs.cn/ruisiapp.apk");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
