package xyz.yluo.ruisiapp.fragment;

import android.app.Activity;
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
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.PostActivity;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.DataManager;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.IntentUtils;
import xyz.yluo.ruisiapp.widget.MyDoubleTimePicker;

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
    private Preference aboutThis, clearCache, openSourse, exit_login, setDarkModeTime, setAutoDarkMode;
    private MyDoubleTimePicker timePickerDialog;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        setting_user_tail = (EditTextPreference) findPreference("setting_user_tail");
        setting_forums_url = (ListPreference) findPreference("setting_forums_url");
        aboutThis = findPreference("about_this");
        openSourse = findPreference("open_sourse");
        clearCache = findPreference("clean_cache");
        exit_login = findPreference("exit_login");
        setAutoDarkMode = findPreference("setting_auto_dark_mode");
        setDarkModeTime = findPreference("setting_auto_dark_mode_time");
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        boolean b = sharedPreferences.getBoolean("setting_show_tail", false);
        setting_user_tail.setEnabled(b);
        b = sharedPreferences.getBoolean("setting_dark_mode", false);
        setDarkModeTime.setEnabled(!b && sharedPreferences.getBoolean("setting_auto_dark_mode", true));
        setAutoDarkMode.setEnabled(!b);
        setting_user_tail.setSummary(sharedPreferences.getString("setting_user_tail", "无小尾巴"));
        setting_forums_url.setSummary(App.IS_SCHOOL_NET ? "当前网络校园网，点击切换" : "当前网络校外网，点击切换");
        setting_forums_url.setValue(App.IS_SCHOOL_NET ? "1" : "2");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        timePickerDialog = new MyDoubleTimePicker(getActivity(), null, 22, 0, 6, 0);


        if (!App.ISLOGIN(getActivity())) {
            ((PreferenceGroup) findPreference("group_other")).
                    removePreference(findPreference("exit_login"));//这是删除 二级
        }

        setDarkModeTime.setOnPreferenceClickListener(preference -> {
            timePickerDialog.show();
            return false;
        });

        exit_login.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getActivity()).
                    setTitle("退出登录").
                    setMessage("你确定要注销吗？").
                    setPositiveButton("注销", (dialog, which) -> {
                        DataManager.cleanApplicationData(getActivity());
                        getActivity().finish();
                    })
                    .setNegativeButton("取消", null)
                    .setCancelable(true)
                    .create()
                    .show();
            return true;
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

        aboutThis.setSummary("当前版本" + version_name + "  version code:" + version_code);

        //[2016年6月9日更新][code:25]睿思手机客户端
        //更新逻辑 检查睿思帖子标题 比对版本号
        final int finalversion_code = version_code;
        aboutThis.setOnPreferenceClickListener(
                preference -> {
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
                                if (code > finalversion_code) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putLong(App.CHECK_UPDATE_KEY, System.currentTimeMillis());
                                    editor.apply();
                                    new AlertDialog.Builder(getActivity()).
                                            setTitle("检测到新版本").
                                            setMessage(title).
                                            setPositiveButton("查看", (dialog, which) -> PostActivity.open(getActivity(), App.CHECK_UPDATE_URL, "谁用了FREEDOM"))
                                            .setNegativeButton("取消", null)
                                            .setCancelable(true)
                                            .create()
                                            .show();

                                } else {
                                    Toast.makeText(getActivity(), "暂无更新", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    return true;
                });

        openSourse.setOnPreferenceClickListener(preference -> {
            IntentUtils.openBroswer(getActivity(), "https://github.com/freedom10086/Ruisi");
            return false;
        });
        clearCache.setSummary("缓存大小：" + DataManager.getTotalCacheSize(getActivity()));
        clearCache.setOnPreferenceClickListener(preference -> {
            DataManager.cleanApplicationData(getActivity());

            Toast.makeText(getActivity(), "缓存清理成功!请重新登陆", Toast.LENGTH_SHORT).show();
            clearCache.setSummary("缓存大小：" + DataManager.getTotalCacheSize(getActivity()));
            return false;
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "setting_forums_url":
                switch (sharedPreferences.getString("setting_forums_url", "2")) {
                    case "1":
                        setting_forums_url.setSummary("当前网络校园网，点击切换");
                        Toast.makeText(getActivity(), "切换到校园网!", Toast.LENGTH_SHORT).show();
                        App.IS_SCHOOL_NET = true;
                        break;
                    case "2":
                        setting_forums_url.setSummary("当前网络校外网，点击切换");
                        Toast.makeText(getActivity(), "切换到外网!", Toast.LENGTH_SHORT).show();
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
                break;
            case "setting_show_plain":
                boolean bbbb = sharedPreferences.getBoolean("setting_show_plain", false);
                Toast.makeText(getActivity(), bbbb ? "文章显示模式：简洁" : "文章显示模式：默认",
                        Toast.LENGTH_SHORT).show();
                break;
            case "setting_dark_mode":
                bbbb = sharedPreferences.getBoolean("setting_dark_mode", false);
                if (bbbb) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(getActivity(), "切换到夜间模式", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                setDarkModeTime.setEnabled(!bbbb && sharedPreferences.getBoolean("setting_auto_dark_mode", true));
                setAutoDarkMode.setEnabled(!bbbb);
                Activity a = getActivity();
                if (a != null) a.onContentChanged();
                break;
            case "setting_auto_dark_mode":
                bbbb = sharedPreferences.getBoolean("setting_auto_dark_mode", true);
                setDarkModeTime.setEnabled(bbbb);
                break;
        }
    }
}