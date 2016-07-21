package xyz.yluo.ruisiapp.fragment;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import xyz.yluo.ruisiapp.Config;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.NewVersionDialog;
import xyz.yluo.ruisiapp.httpUtil.AsyncHttpClient;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.DataCleanManager;
import xyz.yluo.ruisiapp.utils.RequestOpenBrowser;

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
    private Preference about_this, clear_cache, open_sourse;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        setting_user_tail = (EditTextPreference) findPreference("setting_user_tail");
        setting_forums_url = (ListPreference) findPreference("setting_forums_url");
        about_this = findPreference("about_this");
        open_sourse = findPreference("open_sourse");
        clear_cache = findPreference("clean_cache");

        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        boolean b = sharedPreferences.getBoolean("setting_show_tail", false);
        setting_user_tail.setEnabled(b);
        setting_user_tail.setSummary(sharedPreferences.getString("setting_user_tail", "无小尾巴"));
        setting_forums_url.setSummary(Config.IS_SCHOOL_NET?"当前网络校园网，点击切换":"当前网络校外网，点击切换");


        Log.i("is show tail", "" + sharedPreferences.getBoolean("setting_show_tail", false));
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
        about_this.setSummary("当前版本" + version_name);

        final int finalVersion_code = version_code;
        about_this.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(), "正在检查更新", Toast.LENGTH_SHORT).show();
                AsyncHttpClient client = new AsyncHttpClient();
                client.get("http://xidianrs.cn/version.json", new ResponseHandler() {
                    @Override
                    public void onSuccess(byte[] response) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(new String(response));
                            int get_code = jsonObject.getInt("version_code");
                            if (get_code > finalVersion_code) {
                                String get_name = jsonObject.getString("version_name");
                                JSONArray resultJsonArray = jsonObject.getJSONArray("des");
                                String info = "";
                                for (int i = 0; i < resultJsonArray.length(); i++) {
                                    info += (i + 1) + ":" + resultJsonArray.getJSONObject(i).getString("info");
                                    if (i != resultJsonArray.length() - 1) {
                                        info += "\n";
                                    }
                                }
                                NewVersionDialog dialog = new NewVersionDialog();
                                dialog.setCode(get_name);
                                dialog.setMessage(info);
                                dialog.show(getFragmentManager(), "new");
                            } else {
                                Toast.makeText(getActivity(), "暂无更新", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Toast.makeText(getActivity(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        });

        open_sourse.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                RequestOpenBrowser.openBroswer(getActivity(), "https://github.com/freedom10086/Ruisi");
                return false;
            }
        });
        clear_cache.setSummary("缓存大小：" + DataCleanManager.getTotalCacheSize(getActivity()));
        clear_cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DataCleanManager.cleanApplicationData(getActivity());

                Toast.makeText(getActivity(), "缓存清理成功!请重新登陆", Toast.LENGTH_SHORT).show();
                clear_cache.setSummary("缓存大小：" + DataCleanManager.getTotalCacheSize(getActivity()));
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
                        Config.IS_SCHOOL_NET = true;
                        break;
                    case "2":
                        setting_forums_url.setSummary("当前网络校外网，点击切换");
                        Config.IS_SCHOOL_NET = false;
                        break;
                }
                Toast.makeText(getActivity(),"切换网络成功!",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),bbbb?"帖子列表不显示置顶帖":"帖子列表显示置顶帖",Toast.LENGTH_SHORT).show();
                break;
            case "setting_show_plain":
                bbbb = sharedPreferences.getBoolean("setting_show_plain",false);
                Toast.makeText(getActivity(),bbbb?"文章显示模式：简洁":"文章显示模式：默认",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}