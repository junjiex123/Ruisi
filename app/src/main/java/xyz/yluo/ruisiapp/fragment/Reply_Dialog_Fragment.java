package xyz.yluo.ruisiapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-14.
 *
 */
public class Reply_Dialog_Fragment extends DialogFragment{

    private EditText content;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.activity_article_reply_dialog, null));
        builder.setTitle("回复");

        // Add action buttons
        builder.setPositiveButton("发表", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                content = (EditText) getDialog().findViewById(R.id.reply_content);

                String text = content.getText().toString();
                // sign in the user ...
                dialogListener.onDialogSendClick(Reply_Dialog_Fragment.this,text);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogListener.onDialogCancelClick(Reply_Dialog_Fragment.this);
                Reply_Dialog_Fragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    public interface ReplyDialogListener {
        void onDialogSendClick(DialogFragment dialog, String text);
        void onDialogCancelClick(DialogFragment dialog);
    }

    ReplyDialogListener dialogListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialogListener = (ReplyDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
