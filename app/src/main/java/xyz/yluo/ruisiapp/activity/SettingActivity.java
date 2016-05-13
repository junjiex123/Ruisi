package xyz.yluo.ruisiapp.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.NewVersionDialog;
import xyz.yluo.ruisiapp.httpUtil.AsyncHttpClient;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.RequestOpenBrowser;

/**
 * Created by free2 on 16-3-6.
 * 设置activity 待完善
 *
 */
public class SettingActivity extends PreferenceActivity {

    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        ActionBar actionBar = getDelegate().getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("设置");
        }

    }

    public static class MyPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        //小尾巴
        private EditTextPreference setting_user_tail;
        //论坛地址
        private ListPreference setting_forums_url;

        private SharedPreferences sharedPreferences;

        private Preference about_this;
        private Preference open_sourse;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting);

            setting_user_tail = (EditTextPreference) findPreference("setting_user_tail");
            setting_forums_url = (ListPreference) findPreference("setting_forums_url");
            about_this = findPreference("about_this");
            open_sourse = findPreference("open_sourse");

            sharedPreferences = getPreferenceScreen().getSharedPreferences();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

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
            if(info!=null){
                version_code = info.versionCode;
                version_name =  info.versionName;
            }
            about_this.setSummary("当前版本"+version_name);

            final int finalVersion_code = version_code;
            about_this.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(),"正在检查更新",Toast.LENGTH_SHORT).show();
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get("http://xidianrs.cn/version.json", new ResponseHandler() {
                        @Override
                        public void onSuccess(byte[] response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(new String(response));
                                int get_code = jsonObject.getInt("version_code");
                                if(get_code> finalVersion_code){
                                    String get_name = jsonObject.getString("version_name");
                                    JSONArray resultJsonArray = jsonObject.getJSONArray("des");
                                    String info = "";
                                    for(int i=0;i<resultJsonArray.length();i++){
                                        info+=(i+1)+":"+resultJsonArray.getJSONObject(i).getString("info");
                                        if(i!=resultJsonArray.length()-1){
                                            info+="\n";
                                        }
                                    }
                                    NewVersionDialog dialog = new NewVersionDialog();
                                    dialog.setCode(get_name);
                                    dialog.setMessage(info);
                                    dialog.show(getFragmentManager(),"new");
                                }else{
                                    Toast.makeText(getActivity(),"暂无更新",Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(Throwable e) {
                            Toast.makeText(getActivity(),"连接服务器失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                    return false;
                }
            });

            open_sourse.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    RequestOpenBrowser.openBroswer(getActivity(),"https://github.com/freedom10086/Ruisi");
                    return false;
                }
            });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "setting_forums_url":
                    switch (sharedPreferences.getString("setting_forums_url", "0")) {
                        case "0":
                            setting_forums_url.setSummary("自动切换");
                            break;
                        case "1":
                            setting_forums_url.setSummary("内网:http://rs.xidian.edu.cn/");
                            break;
                        case "2":
                            setting_forums_url.setSummary("外网:http://bbs.rs.xidian.me/");
                            break;
                    }

                    break;
                case "setting_user_tail":
                    setting_user_tail.setSummary(sharedPreferences.getString("setting_user_tail", "无小尾巴"));
                    break;
                case "setting_show_zhidin":
                    PublicData.ISSHOW_ZHIDIN = sharedPreferences.getBoolean("setting_show_zhidin", false);
                    break;
                case "setting_show_plain":
                    PublicData.ISSHOW_PLAIN = sharedPreferences.getBoolean("setting_show_plain", true);
                    break;
            }
        }
    }


    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
