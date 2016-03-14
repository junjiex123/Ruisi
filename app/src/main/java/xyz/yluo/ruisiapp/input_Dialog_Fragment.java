package xyz.yluo.ruisiapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * Created by free2 on 16-3-14.
 *
 */
public class input_Dialog_Fragment extends DialogFragment{

    private EditText username;
    private EditText password;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.inputdialog, null));
        builder.setTitle("登陆");

        // Add action buttons
        builder.setPositiveButton("登陆", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                username = (EditText) getDialog().findViewById(R.id.username);
                password = (EditText) getDialog().findViewById(R.id.password);

                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(user!=""&&pass!=""){
                    // sign in the user ...
                    loginDialogListener.onDialogPositiveClick(input_Dialog_Fragment.this,user,pass);
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                loginDialogListener.onDialogNegativeClick(input_Dialog_Fragment.this);
                input_Dialog_Fragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    public interface LoginDialogListener {
        void onDialogPositiveClick(DialogFragment dialog,String username,String password);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    LoginDialogListener loginDialogListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            loginDialogListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
