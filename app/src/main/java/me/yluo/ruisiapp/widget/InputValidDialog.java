package me.yluo.ruisiapp.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.myhttp.HttpUtil;
import me.yluo.ruisiapp.myhttp.ResponseHandler;
import me.yluo.ruisiapp.utils.UrlUtils;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 输入验证码
 */
public class InputValidDialog extends DialogFragment {
    private EditText input;
    private GifImageView gifImageView;
    private ProgressBar progressBar;
    private TextView statusView;
    private String hash = "";
    private String imageUrl = "";
    private OnInputValidListener dialogListener;


    public static InputValidDialog newInstance(OnInputValidListener var, String hash, String imgUrl) {
        InputValidDialog frag = new InputValidDialog();
        frag.hash = hash;
        frag.imageUrl = imgUrl;
        frag.dialogListener = var;
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_input_valid, null);
        builder.setView(view);

        builder.setTitle("输入验证码");
        builder.setCancelable(false);

        input = view.findViewById(R.id.value);
        gifImageView = view.findViewById(R.id.gifview);
        progressBar = view.findViewById(R.id.progress);
        statusView = view.findViewById(R.id.status);
        statusView.setVisibility(View.GONE);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String value = input.getText().toString().trim();
                if (value.length() == 4) {
                    checkValid(hash, value);
                }
            }
        });


        view.findViewById(R.id.btn_ok).setOnClickListener(view1 -> {
            if (checkInput()) {
                dialogListener.onInputFinish(hash, input.getText().toString().trim());
                InputValidDialog.this.getDialog().cancel();
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(view12 -> dismiss());
        loadImage(hash, imageUrl);

        gifImageView.setOnClickListener(view13 -> changeValid());
        return builder.create();
    }

    private void changeValid() {
        input.setText(null);
        hash = "S" + new Random().nextInt(999);
        loadImage(hash, imageUrl); //misc.php?mod=seccode&update=27663&idhash=SszZ1&mobile=2
    }

    // 检查验证码
    private void checkValid(String hash, String value) {
        HttpUtil.get("misc.php?mod=seccode&action=check&inajax=1&modid=member::logging&idhash=" + hash + "&secverify=" + value, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                // err
                //<?xml version="1.0" encoding="utf-8"?>
                //<root><![CDATA[invalid]]></root>

                // yes
                //<?xml version="1.0" encoding="utf-8"?>
                //<root><![CDATA[succeed]]></root>

                statusView.setVisibility(View.VISIBLE);
                String res = new String(response);
                Log.v("check", res);
                if (res.contains("succeed")) {
                    statusView.setText("正确");
                    statusView.setTextColor(ContextCompat.getColor(getActivity(), R.color.green_light));
                    dialogListener.onInputFinish(hash, value);
                } else {
                    statusView.setText("错误");
                    statusView.setTextColor(ContextCompat.getColor(getActivity(), R.color.orange_light));
                }
            }
        });
    }

    private void loadImage(String hash, String url) {
        if (!url.contains(hash)) {
            int start = url.indexOf("idhash") + 7;
            int end = url.indexOf("&", start);
            if (end < 0) end = url.length();
            url = url.replace(url.substring(start, end), hash);
        }

        //misc.php?mod=seccode&update=27663&idhash=SszZ1&mobile=2
        Log.v("===", HttpUtil.getStore(getActivity()).getCookie());
        HttpUtil.getClient().addHeader("Referer", App.getBaseUrl() + UrlUtils.getLoginUrl());
        HttpUtil.get(url, new ResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                gifImageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(byte[] response) {
                try {
                    GifDrawable drawable = new GifDrawable(response);
                    gifImageView.setImageDrawable(drawable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                HttpUtil.getClient().removeHeader("Referer");
                progressBar.setVisibility(View.GONE);
                gifImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean checkInput() {
        String str = input.getText().toString().trim();
        if (str.length() < 3) {
            input.setError("字数不够");
            return false;
        }
        return true;
    }

    public interface OnInputValidListener {
        void onInputFinish(String hash, String value);
    }
}
