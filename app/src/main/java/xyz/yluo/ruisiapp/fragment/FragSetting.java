package xyz.yluo.ruisiapp.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.PostActivity;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.DataManager;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.IntentUtils;

/**
 * Created by free2 on 16-7-18.
 * 设置页面
 */

public class FragSetting extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    //小尾巴string
    private EditTextPreference setting_user_tail;
    //论坛地址
    private ListPreference setting_forums_url;
    private SharedPreferences sharedPreferences;
    private Preference about_this, clear_cache, open_sourse,exit_login;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        setting_user_tail = (EditTextPreference) findPreference("setting_user_tail");
        setting_forums_url = (ListPreference) findPreference("setting_forums_url");
        about_this = findPreference("about_this");
        open_sourse = findPreference("open_sourse");
        clear_cache = findPreference("clean_cache");
        exit_login = findPreference("exit_login");
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        boolean b = sharedPreferences.getBoolean("setting_show_tail", false);
        setting_user_tail.setEnabled(b);
        setting_user_tail.setSummary(sharedPreferences.getString("setting_user_tail", "无小尾巴"));
        setting_forums_url.setSummary(App.IS_SCHOOL_NET?"当前网络校园网，点击切换":"当前网络校外网，点击切换");
        setting_forums_url.setValue(App.IS_SCHOOL_NET?"1":"2");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        if(!App.ISLOGIN(getActivity())){
            ((PreferenceGroup)findPreference("group_other")).
                    removePreference(findPreference("exit_login"));//这是删除 二级
        }

        exit_login.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity()).
                        setTitle("退出登录").
                        setMessage("你确定要注销吗？").
                        setPositiveButton("注销", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataManager.cleanApplicationData(getActivity());
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .setCancelable(true)
                        .create()
                        .show();
                return true;
            }
        });

        PackageManager manager;
        PackageInfo info = null;
        manager = getActivity().getPackageManager();
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int version_code = 1;
        String version_name = "1.0";
        if (info != null) {
            version_code = info.versionCode;
            version_name = info.versionName;
        }
        about_this.setSummary("当前版本" + version_name+"  version code:"+version_code);

        //[2016年6月9日更新][code:25]睿思手机客户端
        final int finalversion_code = version_code;
        about_this.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Toast.makeText(getActivity(), "正在检查更新", Toast.LENGTH_SHORT).show();
                            HttpUtil.get(getActivity(), App.CHECK_UPDATE_URL, new ResponseHandler() {
                                @Override
                                public void onSuccess(byte[] response) {
                                    String res = new String(response);
                                    int ih = res.indexOf("keywords");
                                    int h_start = res.indexOf('\"', ih + 15);
                                    int h_end = res.indexOf('\"', h_start + 1);
                                    String title = res.substring(h_start + 1, h_end);
                                    if (title.contains("code")) {
                                        int st = title.indexOf("code");
                                        int code = GetId.getNumber(title.substring(st));
                                        Log.e("code", code + " " + finalversion_code);
                                        if(code>finalversion_code){
                                            new AlertDialog.Builder(getActivity()).
                                                    setTitle("检测到新版本！！").
                                                    setMessage("你确定要注销吗？").
                                                    setPositiveButton("查看", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            PostActivity.open(getActivity(),App.CHECK_UPDATE_URL,"谁用了FREEDOM");
                                                        }
                                                    })
                                                    .setNegativeButton("取消",null)
                                                    .setCancelable(true)
                                                    .create()
                                                    .show();

                                        }else{
                                            Toast.makeText(getActivity(),"暂无更新",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            return  true;
                }
        });

        open_sourse.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                IntentUtils.openBroswer(getActivity(), "https://github.com/freedom10086/Ruisi");
                return false;
            }
        });
        clear_cache.setSummary("缓存大小：" + DataManager.getTotalCacheSize(getActivity()));
        clear_cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DataManager.cleanApplicationData(getActivity());

                Toast.makeText(getActivity(), "缓存清理成功!请重新登陆", Toast.LENGTH_SHORT).show();
                clear_cache.setSummary("缓存大小：" + DataManager.getTotalCacheSize(getActivity()));
                return false;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "setting_forums_url":
                switch (sharedPreferences.getString("setting_forums_url", "2")) {
                    case "1":
                        setting_forums_url.setSummary("当前网络校园网，点击切换");
                        Toast.makeText(getActivity(),"切换到校园网!",Toast.LENGTH_SHORT).show();
                        App.IS_SCHOOL_NET = true;
                        break;
                    case "2":
                        setting_forums_url.setSummary("当前网络校外网，点击切换");
                        Toast.makeText(getActivity(),"切换到外网!",Toast.LENGTH_SHORT).show();
                        App.IS_SCHOOL_NET = false;
                        break;
                }

                break;
            case "setting_show_tail":
                boolean b = sharedPreferences.getBoolean("setting_show_tail", false);
                setting_user_tail.setEnabled(b);
                setting_user_tail.setSummary(sharedPreferences.getString("setting_user_tail", "无小尾巴"));
                break;
            case "setting_user_tail":
                setting_user_tail.setSummary(sharedPreferences.getString("setting_user_tail", "无小尾巴"));
                break;
            case "setting_hide_zhidin":
                boolean bbbb = sharedPreferences.getBoolean("setting_hide_zhidin",true);
                Toast.makeText(getActivity(),bbbb?"帖子列表不显示置顶帖":"帖子列表显示置顶帖",
                        Toast.LENGTH_SHORT).show();
                break;
            case "setting_show_plain":
                bbbb = sharedPreferences.getBoolean("setting_show_plain",false);
                Toast.makeText(getActivity(),bbbb?"文章显示模式：简洁":"文章显示模式：默认",
                        Toast.LENGTH_SHORT).show();
                break;
            case "setting_dark_mode":
                bbbb = sharedPreferences.getBoolean("setting_dark_mode",false);
                if(bbbb){
                    Toast.makeText(getActivity(),"成功切换到夜间模式，重启软件生效"
                            ,Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}