package xyz.yluo.ruisiapp.View;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import xyz.yluo.ruisiapp.Config;
import xyz.yluo.ruisiapp.R;

/**
 * Created by free2 on 16-3-14.
 * 回复层主dialog
 */
public class ChangeNetDialog extends DialogFragment {

    private boolean is_school_net = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_net_dialog, null);
        builder.setView(view);
        builder.setTitle("切换网络");
        //content = (EditText) view.findViewById(R.id.reply_content);
        final TextView net_school = (TextView) view.findViewById(R.id.net_school);
        final TextView net_out = (TextView) view.findViewById(R.id.net_out);

        net_school.setVisibility(is_school_net ? View.VISIBLE : View.INVISIBLE);
        net_out.setVisibility(is_school_net ? View.INVISIBLE : View.VISIBLE);
        TextView btn_ok = (TextView) view.findViewById(R.id.btn_ok);

        view.findViewById(R.id.school_net_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.IS_SCHOOL_NET = true;
                net_school.setVisibility(View.VISIBLE);
                net_out.setVisibility(View.INVISIBLE);
            }
        });

        view.findViewById(R.id.out_net_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config.IS_SCHOOL_NET = false;
                net_school.setVisibility(View.INVISIBLE);
                net_out.setVisibility(View.VISIBLE);
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialogListener.onDialogSendClick(ChangeNetDialog.this,url,text);
                ChangeNetDialog.this.getDialog().cancel();

            }
        });


        return builder.create();
    }

    public void setNetType(boolean is_school_net) {
        this.is_school_net = is_school_net;
    }
}
