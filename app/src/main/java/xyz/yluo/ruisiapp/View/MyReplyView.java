package xyz.yluo.ruisiapp.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SmileyAdapter;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.ImageUtils;
import xyz.yluo.ruisiapp.utils.ImeUtil;
import xyz.yluo.ruisiapp.utils.PostHandler;

/**
 * Created by free2 on 16-7-14.
 * 回复框 dialog
 */

public class MyReplyView extends DialogFragment implements View.OnClickListener {
    public static final int SMILEY_TB = 1;
    public static final int SMILEY_ALI = 2;
    public static final int SMILEY_ACN = 3;

    public static final int REPLY_LZ = 0;
    public static final int REPLY_CZ = 1;

    private int smiley_type = SMILEY_TB;
    private int replyType = REPLY_LZ;
    private replyCompeteCallBack callBack;
    private long lastReplyTime = 0;

    private EditText input;
    private LinearLayout smiley_container;
    private CoordinatorLayout notisfy_view;
    private Button btn_send;
    private SmileyAdapter adapter;
    private boolean isEnableTail = false;
    private String[] nameList;
    private String replyUrl;
    private LinearLayout loadingView;
    //chat时代表touid
    private String info = "";
    private String userName;


    private List<Drawable> ds = new ArrayList<>();

    public static MyReplyView newInstance(
            String url, int type, long lastreplyTime, boolean isEnableTail, String userName, String info) {
        Bundle args = new Bundle();
        args.putString("replyUrl", url);
        args.putInt("replyType", type);
        args.putBoolean("isEnableTail", isEnableTail);
        args.putString("userName", userName);
        args.putString("info", info);
        args.putLong("lastreplyTime", lastreplyTime);
        MyReplyView fragment = new MyReplyView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            replyUrl = bundle.getString("replyUrl");
            replyType = bundle.getInt("replyType", REPLY_LZ);
            isEnableTail = bundle.getBoolean("isEnableTail", false);
            userName = bundle.getString("userName", "回复");
            lastReplyTime = bundle.getLong("lastreplyTime", 0);
            info = bundle.getString("info", "0");
        }
    }

    ////onCreateDialog>>onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_reply_view, null);
        VerticalTabLayout tabLayout = (VerticalTabLayout) v.findViewById(R.id.smiley_tab);
        input = (EditText) v.findViewById(R.id.input_aera);
        input.setHint(userName);
        RecyclerView smiley_listv = (RecyclerView) v.findViewById(R.id.smiley_list);
        smiley_container = (LinearLayout) v.findViewById(R.id.smileys_container);
        smiley_container.setVisibility(View.GONE);
        loadingView = (LinearLayout) v.findViewById(R.id.loading_view);
        loadingView.setVisibility(View.GONE);
        notisfy_view = (CoordinatorLayout) v.findViewById(R.id.notisfy_view);
        btn_send = (Button) v.findViewById(R.id.action_send);
        btn_send.setOnClickListener(this);
        input.setOnClickListener(this);
        hideSmiley();

        tabLayout.setOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {

            }

            @Override
            public void onTabSelectedChanged(int i) {
                Log.i("tab check", "id " + i);
                switch (i) {
                    case 0:
                        smiley_type = SMILEY_TB;
                        break;
                    case 1:
                        smiley_type = SMILEY_ALI;
                        break;
                    case 2:
                        smiley_type = SMILEY_ACN;
                        break;
                }
                changeSmiley();
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(input.getText())) {
                    btn_send.setEnabled(true);
                } else {
                    btn_send.setEnabled(false);
                }
            }
        });
        v.findViewById(R.id.btn_smiley).setOnClickListener(this);
        ds = getSmileys();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(
                getActivity(), 4, LinearLayoutManager.HORIZONTAL, false);
        adapter = new SmileyAdapter((v1, position) -> insertSmiley(position), ds);
        smiley_listv.setLayoutManager(layoutManager);
        smiley_listv.setAdapter(adapter);
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i("MyReplyView", "onCreateDialog");
        Dialog dialog = new Dialog(getActivity(), R.style.replyBarDialogStyle);
        dialog.setContentView(R.layout.my_reply_view);
        dialog.setCanceledOnTouchOutside(true);

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        input.requestFocus();
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_send:
                hideSmiley();
                send_click();
                break;
            case R.id.btn_smiley:
                if (smiley_container.getVisibility() == View.VISIBLE) {
                    smiley_container.setVisibility(View.GONE);
                } else {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getView() != null)
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    smiley_container.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.input_aera:
                hideSmiley();
                break;
            default:
                break;
        }
    }

    private void send_click() {
        String text = input.getText().toString();
        int len = 0;
        try {
            len = text.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (len == 0) {
            //input.setError("你还没写内容呢!");
            Snackbar.make(notisfy_view, "你还没写内容呢", Snackbar.LENGTH_LONG).show();
        } else {
            //时间检测
            if (!checkTime()) {
                return;
            }
            //添加小尾巴
            if (isEnableTail) {
                SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (shp.getBoolean("setting_show_tail", false)) {
                    String texttail = shp.getString("setting_user_tail", "无尾巴").trim();
                    if (!texttail.equals("无尾巴")) {
                        texttail = "     " + texttail;
                        text += texttail;
                    }
                }

            }

            //字数补齐补丁
            if (len < 13) {
                int need = 14 - len;
                for (int i = 0; i < need; i++) {
                    text += " ";
                }
            }

            loadingView.setVisibility(View.VISIBLE);
            ImeUtil.hide_ime(getDialog().getWindow());
            switch (replyType) {
                case REPLY_LZ:
                    replyLz(text);
                    break;
                case REPLY_CZ:
                    replyCz(text);
                    break;
            }

        }
    }


    //回复楼主
    private void replyLz(final String res) {
        Map<String, String> params = new HashMap<>();
        params.put("message", res);
        HttpUtil.post(getActivity(), replyUrl + "&handlekey=fastpost&loc=1&inajax=1", params, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                handleReply(true, res);
            }

            @Override
            public void onFailure(Throwable e) {
                handleReply(false, "");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadingView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 回复层主
     */
    private void replyCz(final String txt) {
        HttpUtil.get(getActivity(), replyUrl, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                Document document = Jsoup.parse(new String(response));
                Elements els = document.select("#postform");
                String formhash = els.select("input[name=formhash]").attr("value");
                String posttime = els.select("input[name=posttime]").attr("value");
                String noticeauthor = els.select("input[name=noticeauthor]").attr("value");
                String noticetrimstr = els.select("input[name=noticetrimstr]").attr("value");
                String reppid = els.select("input[name=reppid]").attr("value");
                String reppost = els.select("input[name=reppost]").attr("value");
                String noticeauthormsg = els.select("input[name=noticeauthormsg]").attr("value");
                String postUrl = els.attr("action");
                Map<String, String> params = new HashMap<>();
                params.put("formhash", formhash);
                params.put("posttime", posttime);
                params.put("noticeauthor", noticeauthor);
                params.put("noticetrimstr", noticetrimstr);
                params.put("reppid", reppid);
                params.put("reppost", reppost);
                params.put("noticeauthormsg", noticeauthormsg);
                params.put("replysubmit", "yes");
                params.put("message", txt);

                HttpUtil.post(getActivity(), postUrl, params, new ResponseHandler() {
                    @Override
                    public void onSuccess(byte[] response) {
                        String res = new String(response);
                        handleReply(true, res + "层主");
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                        handleReply(false, "");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loadingView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }


    /**
     * 回复楼主
     */
    private void handleReply(boolean isok, String res) {
        if (isok) {
            if (res.contains("成功") || res.contains("层主")) {
                Toast.makeText(getActivity(), "回复发表成功", Toast.LENGTH_SHORT).show();
                sendCallBack(Activity.RESULT_OK, input.getText().toString());
                input.setText("");
                dismiss();
            } else if (res.contains("您两次发表间隔")) {
                Toast.makeText(getActivity(), "您两次发表间隔太短了......", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "由于未知原因发表失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkTime() {
        if (System.currentTimeMillis() - lastReplyTime > 15000) {
            return true;
        } else {
            Snackbar.make(notisfy_view, "还没到15s呢，再等等吧！", Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    private void insertSmiley(int position) {
        if (position > nameList.length) {
            return;
        }
        String name = nameList[position].split("\\.")[0];
        String insertName = ImageUtils.getSmileyName(smiley_type, name);
        PostHandler handler = new PostHandler(input);
        handler.insertSmiley("{:" + insertName + ":}", ds.get(position));
    }

    private void changeSmiley() {
        ds.clear();
        ds = getSmileys();
        adapter.notifyDataSetChanged();
    }

    private boolean hideSmiley() {
        if (smiley_container.getVisibility() == View.VISIBLE) {
            smiley_container.setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    private List<Drawable> getSmileys() {
        String smiley_dir = "static/image/smiley/";
        if (smiley_type == SMILEY_TB) {
            smiley_dir += "tieba";
        } else if (smiley_type == SMILEY_ALI) {
            smiley_dir += "ali";
        } else if (smiley_type == SMILEY_ACN) {
            smiley_dir += "acn";
        }

        try {
            nameList = getActivity().getAssets().list(smiley_dir);
            for (String temp : nameList) {
                Drawable d = Drawable.createFromPath("file:///android_asset/" + smiley_dir + temp);
                ds.add(d);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public interface replyCompeteCallBack {
        void onReplyFinish(int status, String info);
    }

    public void setCallBack(replyCompeteCallBack callBack) {
        this.callBack = callBack;
    }

    private void sendCallBack(int status, String info) {
        if (callBack != null) {
            callBack.onReplyFinish(status, info);
        }
    }

}
