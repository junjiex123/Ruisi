package xyz.yluo.ruisiapp.View;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-14.
 * 回复层主dialog
 */
public class ReplyDialog extends DialogFragment{

    private EditText content;
    private long lasttime = 0;

    private String title = "回复";
    private String url = "";
    private String reply_ref_text;
    private ReplyDialogListener dialogListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.reply_dialog,null);
        builder.setView(view);
        builder.setTitle(title);

        content = (EditText) view.findViewById(R.id.reply_content);
        ArrowTextView reply_ref = (ArrowTextView) view.findViewById(R.id.reply_ref);
        if(reply_ref_text==null){
            reply_ref.setVisibility(View.GONE);
        }else {
            reply_ref.setText(reply_ref_text);
        }
        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        TextView btn_send = (TextView) view.findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkTime()){
                    String text = content.getText().toString();
                    int len = 0;
                    try {
                        len = text.getBytes("UTF-8").length;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(len==0) {
                        content.setError("你还没写内容呢");
                    }else {
                        if(len<13){
                            int need = 15-len;
                            for(int i=0;i<need;i++){
                                text+=" ";
                            }
                        }
                        dialogListener.onDialogSendClick(ReplyDialog.this,url,text);
                        ReplyDialog.this.getDialog().cancel();
                    }
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder.create();
    }

    public interface ReplyDialogListener {
        void onDialogSendClick(DialogFragment dialog, String url,String text);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogListener = (ReplyDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private boolean checkTime(){
        if(System.currentTimeMillis()-lasttime>14500){
            return  true;
        }else{
            content.setError("还没到15s呢");
            return false;
        }
    }

    public void setReply_ref(String reply_ref_text) {
        this.reply_ref_text = reply_ref_text;
    }
}
