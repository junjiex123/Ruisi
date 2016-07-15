package xyz.yluo.ruisiapp.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.VerticalTabLayout;
import xyz.yluo.ruisiapp.adapter.SmileyAdapter;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.utils.ImeUtil;
import xyz.yluo.ruisiapp.utils.PostHandler;

/**
 * Created by free2 on 16-7-14.
 * 回复框 dialog
 */

public class FrageReplyDialog extends DialogFragment implements View.OnClickListener{


    private final int SMILEY_TB = 1;
    private final int SMILEY_LDB = 2;
    private final int SMILEY_ACN = 3;

    public static final int REPLY_LZ =0;
    public static final int REPLY_CZ =1;
    public static final int REPLY_HY =2;

    private int smiley_type = SMILEY_TB;
    private int replyType = REPLY_LZ;
    private replyCompeteCallBack callBack;
    private long lastReplyTime = 0;

    private EditText input;
    private LinearLayout smiley_container;
    private CoordinatorLayout notisfy_view;
    private ImageView btn_send;
    private SmileyAdapter adapter;
    private boolean isEnableTail = false;
    private String[] nameList;
    private String replyUrl;
    private LinearLayout loadingView;


    private List<Drawable> ds = new ArrayList<>();

    public static FrageReplyDialog newInstance(
            String url,int type,long lastreplyTime,boolean isEnableTail,String userName,String info) {
        Bundle args = new Bundle();
        args.putString("replyUrl",url);
        args.putInt("replyType",type);
        args.putBoolean("isEnableTail",isEnableTail);
        args.putString("userName",userName);
        args.putString("info",info);
        args.putLong("lastreplyTime",lastreplyTime);
        FrageReplyDialog fragment = new FrageReplyDialog();
        fragment.setArguments(args);
        return fragment;
    }


    ////onCreateDialog>>onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("replyDialog","====init====");
        View v = inflater.inflate(R.layout.reply_view, null);
        VerticalTabLayout tabLayout = (VerticalTabLayout) v.findViewById(R.id.smiley_tab);
        input = (EditText) v.findViewById(R.id.input_aera);
        RecyclerView smiley_listv = (RecyclerView) v.findViewById(R.id.smiley_list);
        smiley_container = (LinearLayout) v.findViewById(R.id.smileys_container);
        loadingView = (LinearLayout) v.findViewById(R.id.loading_view);
        loadingView.setVisibility(View.GONE);
        notisfy_view = (CoordinatorLayout) v.findViewById(R.id.notisfy_view);
        btn_send = (ImageView) v.findViewById(R.id.action_send);
        btn_send.setOnClickListener(this);
        input.setOnClickListener(this);
        hideSmiley();

        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            replyUrl = bundle.getString("replyUrl");
            replyType = bundle.getInt("replyType",REPLY_LZ);
            isEnableTail = bundle.getBoolean("isEnableTail",false);
            input.setHint(bundle.getString("userName","回复"));
            lastReplyTime = bundle.getLong("lastreplyTime",0);
            Log.i("type is", "==" + replyType + "==");
        }

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
                       smiley_type = SMILEY_LDB;
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
                    btn_send.setImageResource(R.drawable.ic_menu_send_active);
                } else {
                    btn_send.setImageResource(R.drawable.ic_menu_send);
                }
            }
        });
        v.findViewById(R.id.action_smiley).setOnClickListener(this);
        ds = getSmileys();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.HORIZONTAL, false);
        adapter = new SmileyAdapter(new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                insertSmiley(position);
            }
        }, ds);
        smiley_listv.setLayoutManager(layoutManager);
        smiley_listv.setAdapter(adapter);
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i("FrageReplyDialog","onCreateDialog");
        Dialog dialog = new Dialog(getActivity(), R.style.replyBarDialogStyle);
        dialog.setContentView(R.layout.reply_view);
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
        Log.i("reply dialog", ">>>onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        input.requestFocus();
        if(getDialog().getWindow()!=null){
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
            case R.id.action_smiley:
                if (smiley_container.getVisibility() == View.VISIBLE) {
                    smiley_container.setVisibility(View.GONE);
                } else {
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
            Snackbar.make(notisfy_view, "你还没写内容呢", Snackbar.LENGTH_LONG)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                        }
                    }).show();
        } else {
            //时间检测
            if(!checkTime()){
                return;
            }
            //添加小尾巴
            if(isEnableTail){
                SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if(shp.getBoolean("setting_show_tail", false)){
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
            switch (replyType){
                case REPLY_LZ:
                    replyLz(text);
                    break;
                case REPLY_CZ:
                    replyCz(text);
                    break;
                case REPLY_HY:
                    replyHy(text);
                    break;
            }

        }
    }

    private void replyLz(final String res){
        Map<String, String> params = new HashMap<>();
        params.put("formhash", PublicData.FORMHASH);
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

    private void replyCz(final String txt){
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

    private void replyHy(final String txt){

    }


    private void handleReply(boolean isok, String res) {
        if (isok) {
            Log.i("reply resoult",res);
            if (res.contains("成功") || res.contains("层主")) {
                Toast.makeText(getActivity(), "回复发表成功", Toast.LENGTH_SHORT).show();
                sendCallBack(Activity.RESULT_OK,"test");
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
            Snackbar.make(notisfy_view, "还没到15s呢，再等等吧！", Snackbar.LENGTH_LONG)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                        }
                    }).show();
            return false;
        }
    }

    private void insertSmiley(int position) {
        if (position > nameList.length) {
            return;
        }
        String name = nameList[position].split("\\.")[0];
        String insertName = "";

        Log.i("click smiley name", "name " + name);
        if (smiley_type == SMILEY_TB) {
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
        } else if (smiley_type == SMILEY_LDB) {
            switch (name) {
                case "ldb033":
                    insertName = "14_860";
                    break;
                case "ldb074":
                    insertName = "14_871";
                    break;
                case "ldb028":
                    insertName = "14_872";
                    break;
                case "ldb079":
                    insertName = "14_873";
                    break;
                case "ldb038":
                    insertName = "14_874";
                    break;
                case "ldb061":
                    insertName = "14_875";
                    break;
                case "ldb036":
                    insertName = "14_876";
                    break;
                case "ldb062":
                    insertName = "14_877";
                    break;
                case "ldb023":
                    insertName = "14_878";
                    break;
                case "ldb007":
                    insertName = "14_870";
                    break;
                case "ldb052":
                    insertName = "14_869";
                    break;
                case "ldb050":
                    insertName = "14_861";
                    break;


                case "ldb032":
                    insertName = "14_862";
                    break;
                case "ldb016":
                    insertName = "14_863";
                    break;
                case "ldb082":
                    insertName = "14_864";
                    break;
                case "ldb039":
                    insertName = "14_865";
                    break;
                case "ldb043":
                    insertName = "14_866";
                    break;
                case "ldb019":
                    insertName = "14_867";
                    break;
                case "ldb060":
                    insertName = "14_868";
                    break;
                case "ldb046":
                    insertName = "14_879";
                    break;
                case "ldb002":
                    insertName = "14_880";
                    break;
                case "ldb055":
                    insertName = "14_881";
                    break;
                case "ldb080":
                    insertName = "14_882";
                    break;
                case "ldb073":
                    insertName = "14_883";
                    break;


                case "ldb040":
                    insertName = "14_894";
                    break;
                case "ldb020":
                    insertName = "14_895";
                    break;
                case "ldb008":
                    insertName = "14_896";
                    break;
                case "ldb024":
                    insertName = "14_897";
                    break;
                case "ldb042":
                    insertName = "14_898";
                    break;
                case "ldb018":
                    insertName = "14_899";
                    break;

                case "ldb034":
                    insertName = "14_891";
                    break;
                case "ldb041":
                    insertName = "14_890";
                    break;
                case "ldb083":
                    insertName = "14_882";
                    break;
                case "ldb067":
                    insertName = "14_883";
                    break;
                case "ldb025":
                    insertName = "14_884";
                    break;
                case "ldb063":
                    insertName = "14_885";
                    break;


                case "ldb078":
                    insertName = "14_886";
                    break;
                case "ldb065":
                    insertName = "14_887";
                    break;
                case "ldb004":
                    insertName = "14_888";
                    break;
                case "ldb081":
                    insertName = "14_889";
                    break;
                case "ldb006":
                    insertName = "14_900";
                    break;
                case "ldb026":
                    insertName = "14_859";
                    break;
                case "ldb017":
                    insertName = "14_818";
                    break;
                case "ldb011":
                    insertName = "14_829";
                    break;
                case "ldb014":
                    insertName = "14_830";
                    break;
                case "ldb070":
                    insertName = "14_831";
                    break;
                case "ldb056":
                    insertName = "14_832";
                    break;
                case "ldb021":
                    insertName = "14_833";
                    break;


                case "ldb010":
                    insertName = "14_834";
                    break;
                case "ldb045":
                    insertName = "14_835";
                    break;
                case "ldb076":
                    insertName = "14_836";
                    break;
                case "ldb005":
                    insertName = "14_828";
                    break;
                case "ldb069":
                    insertName = "14_827";
                    break;
                case "ldb030":
                    insertName = "14_819";
                    break;
                case "ldb071":
                    insertName = "14_820";
                    break;
                case "ldb044":
                    insertName = "14_821";
                    break;
                case "ldb027":
                    insertName = "14_822";
                    break;
                case "ldb013":
                    insertName = "14_823";
                    break;
                case "ldb015":
                    insertName = "14_824";
                    break;
                case "ldb072":
                    insertName = "14_825";
                    break;


                case "ldb054":
                    insertName = "14_826";
                    break;
                case "ldb049":
                    insertName = "14_837";
                    break;
                case "ldb068":
                    insertName = "14_838";
                    break;
                case "ldb059":
                    insertName = "14_839";
                    break;
                case "ldb075":
                    insertName = "14_850";
                    break;
                case "ldb031":
                    insertName = "14_851";
                    break;
                case "ldb064":
                    insertName = "14_852";
                    break;
                case "ldb037":
                    insertName = "14_853";
                    break;
                case "ldb003":
                    insertName = "14_854";
                    break;
                case "ldb012":
                    insertName = "14_855";
                    break;
                case "ldb035":
                    insertName = "14_856";
                    break;
                case "ldb022":
                    insertName = "14_857";
                    break;


                case "ldb001":
                    insertName = "14_849";
                    break;
                case "ldb057":
                    insertName = "14_848";
                    break;
                case "ldb048":
                    insertName = "14_840";
                    break;
                case "ldb077":
                    insertName = "14_841";
                    break;
                case "ldb009":
                    insertName = "14_842";
                    break;
                case "ldb053":
                    insertName = "14_843";
                    break;
                case "ldb029":
                    insertName = "14_844";
                    break;
                case "ldb047":
                    insertName = "14_845";
                    break;
                case "ldb066":
                    insertName = "14_846";
                    break;
                case "ldb051":
                    insertName = "14_847";
                    break;
                case "ldb058":
                    insertName = "14_858";
                    break;

            }
        } else if (smiley_type == SMILEY_ACN) {
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
                case "acn037":
                    insertName = "15_967";
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
                case "acn078":
                    insertName = "15_955";
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
                case "acn024":
                    insertName = "15_960";
                    break;
                case "acn093":
                    insertName = "15_973";
                    break;

                case "acn036":
                    insertName = "15_974";
                    break;
                case "acn083":
                    insertName = "15_987";
                    break;
                case "acn095":
                    insertName = "15_988";
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
                case "acn045":
                    insertName = "15_995";
                    break;
                case "acn030":
                    insertName = "15_996";
                    break;
                case "acn005":
                    insertName = "15_986";
                    break;

                case "acn018":
                    insertName = "15_985";
                    break;
                case "acn076":
                    insertName = "15_975";
                    break;
                case "acn079":
                    insertName = "15_976";
                    break;
                case "acn008":
                    insertName = "15_977";
                    break;
                case "acn033":
                    insertName = "15_978";
                    break;
                case "acn077":
                    insertName = "15_979";
                    break;
                case "acn014":
                    insertName = "15_980";
                    break;
                case "acn001":
                    insertName = "15_981";
                    break;
                case "acn022":
                    insertName = "15_982";
                    break;
                case "acn096":
                    insertName = "15_983";
                    break;
                case "acn026":
                    insertName = "15_984";
                    break;
                case "acn084":
                    insertName = "15_997";
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
                case "acn091":
                    insertName = "15_915";
                    break;
                case "acn031":
                    insertName = "15_916";
                    break;
                case "acn002":
                    insertName = "15_917";
                    break;
                case "acn059":
                    insertName = "15_918";
                    break;
                case "acn073":
                    insertName = "15_919";
                    break;
                case "acn075":
                    insertName = "15_920";
                    break;
                case "acn046":
                    insertName = "15_921";
                    break;
                case "acn097":
                    insertName = "15_922";
                    break;
                case "acn038":
                    insertName = "15_923";
                    break;


                case "acn068":
                    insertName = "15_913";
                    break;
                case "acn044":
                    insertName = "15_912";
                    break;
                case "acn092":
                    insertName = "15_902";
                    break;
                case "acn028":
                    insertName = "15_903";
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
                case "acn052":
                    insertName = "15_908";
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
                case "acn053":
                    insertName = "15_925";
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
                case "acn040":
                    insertName = "15_941";
                    break;
                case "acn023":
                    insertName = "15_942";
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


                case "acn027":
                    insertName = "15_937";
                    break;
                case "acn066":
                    insertName = "15_936";
                    break;
                case "acn082":
                    insertName = "15_926";
                    break;
                case "acn051":
                    insertName = "15_927";
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
                case "acn041":
                    insertName = "15_935";
                    break;
                case "acn063":
                    insertName = "15_948";
                    break;
            }
        }


        PostHandler handler = new PostHandler(input);
        handler.insertSmiley("{:" + insertName + ":}", ds.get(position));
    }

    private void changeSmiley() {
        Log.i("smiley type", "type " + smiley_type);
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
        } else if (smiley_type == SMILEY_LDB) {
            smiley_dir += "lindab";
        } else if (smiley_type == SMILEY_ACN) {
            smiley_dir += "acn";
        }

        try {
            nameList = getActivity().getAssets().list(smiley_dir);
            for (String temp : nameList) {
                InputStream in = getActivity().getAssets().open(smiley_dir + "/" + temp);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                Drawable d = new BitmapDrawable(getActivity().getResources(), bitmap);
                d.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                ds.add(d);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public interface replyCompeteCallBack{
        void onReplyFinish(int status,String info);
    }

    public void setCallBack(replyCompeteCallBack callBack){
        this.callBack = callBack;
    }

    private void sendCallBack(int status,String info){
        if(callBack!=null){
            callBack.onReplyFinish(status,"test");
        }
    }

}
