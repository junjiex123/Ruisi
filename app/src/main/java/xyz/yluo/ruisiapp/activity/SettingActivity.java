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

import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.utils.AsyncHttpCilentUtil;

/**
 * Created by free2 on 16-3-6.
 *
 */
public class SettingActivity extends PreferenceActivity {

    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

        // Show the Up button in the action bar.
        ActionBar actionBar = getDelegate().getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("设置");
        }

    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

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

            about_this.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Toast.makeText(getActivity(),"正在检查更新",Toast.LENGTH_SHORT).show();
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

                    AsyncHttpCilentUtil.get(getActivity(), "", null, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

                        }

                        @Override
                        public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                    return false;
                }
            });

            open_sourse.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(),"被电击2",Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("setting_forums_url")){
                switch (sharedPreferences.getString("setting_forums_url","0")){
                    case "0":
                        setting_forums_url.setSummary("自动");
                        break;
                    case "1":
                        setting_forums_url.setSummary("内网:http://rs.xidian.edu.cn/");
                        break;
                    case "2":
                        setting_forums_url.setSummary("外网:http://bbs.rs.xidian.me/");
                        break;
                }

            }else if(key.equals("setting_user_tail")){
                setting_user_tail.setSummary(sharedPreferences.getString("setting_user_tail","无小尾巴"));
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
