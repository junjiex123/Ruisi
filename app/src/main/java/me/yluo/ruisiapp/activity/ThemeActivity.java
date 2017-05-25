package me.yluo.ruisiapp.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.R;
import me.yluo.ruisiapp.widget.MyCircleView;

/**
 * 默认主题0
 * 夜间主题1
 * ...
 */
public class ThemeActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final int THEME_DEFAULT = 0;
    public static final int THEME_NIGHT = 1;

    private int[] colors = new int[]{
            0xd12121, 0x1e1e1e, 0xf44836, 0xf2821e, 0x7bb736, 0x16c24b,
            0x16a8c2, 0x2b86e3, 0x3f51b5, 0x9c27b0, 0xcc268f, 0x39c5bb
    };

    private int[] themeIds = new int[]{
            THEME_DEFAULT, THEME_NIGHT, R.style.AppTheme_2,
            R.style.AppTheme_3, R.style.AppTheme_4, R.style.AppTheme_5,
            R.style.AppTheme_6, R.style.AppTheme_7, R.style.AppTheme_8,
            R.style.AppTheme_9, R.style.AppTheme_10, R.style.AppTheme_11,
    };

    private String[] names = new String[]{
            "默认", "黑色", "橘红", "橘黄", "草绿", "翠绿",
            "青色", "天蓝", "蓝色", "紫色", "紫红", "初音"
    };


    private int currentSelect = 0;
    private int currentTheme = THEME_DEFAULT;

    private ColorAdapter adpter;
    private View startView, endView, nightViews;
    private TextView startText, endText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        initToolBar(true, "主题设置");
        addToolbarMenu(R.drawable.ic_done_black_24dp).setOnClickListener(v -> {
            App.setCustomTheme(this, themeIds[currentSelect]);
            finish();
        });
        GridView gridView = (GridView) findViewById(R.id.commons_colors);
        adpter = new ColorAdapter();
        gridView.setAdapter(adpter);
        gridView.setOnItemClickListener(this);


        CheckBox auto = (CheckBox) findViewById(R.id.auto_dark_mode);
        boolean isAuto = App.isAutoDarkMode(this);
        auto.setChecked(isAuto);

        startView = findViewById(R.id.start_time);
        endView = findViewById(R.id.end_time);
        nightViews = findViewById(R.id.night_views);
        startText = (TextView) findViewById(R.id.start_time_text);
        endText = (TextView) findViewById(R.id.end_time_text);

        currentTheme = App.getCustomTheme(this);
        currentSelect = getSelect();

        if (currentSelect == THEME_NIGHT) {
            nightViews.setVisibility(View.GONE);
        } else {
            nightViews.setVisibility(View.VISIBLE);
        }


        startText.setText(App.getDarkModeTime(this)[0] + ":00");
        endText.setText(App.getDarkModeTime(this)[1] + ":00");

        auto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            App.setAutoDarkMode(this, isChecked);
        });

        startView.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                startText.setText(hourOfDay + ":00");
                App.setDarkModeTime(this, true, hourOfDay);
            }, App.getDarkModeTime(this)[0], 0, true).show();
        });

        endView.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                endText.setText(hourOfDay + ":00");
                App.setDarkModeTime(this, false, hourOfDay);
            }, App.getDarkModeTime(this)[1], 0, true).show();
        });
    }


    public int getSelect() {
        for (int i = 0; i < themeIds.length; i++) {
            if (currentTheme == themeIds[i]) {
                return i;
            }
        }

        return 0;
    }

    private void changeTheme(int position) {
        if (themeIds[position] == THEME_NIGHT) {
            nightViews.setVisibility(View.GONE);
        } else {
            nightViews.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentSelect == position) return;
        currentSelect = position;
        adpter.notifyDataSetChanged();
        changeTheme(position);
    }

    private class ColorAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return colors.length;
        }

        @Override
        public Object getItem(int position) {
            return colors[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_color, null);
            MyCircleView circleView = (MyCircleView) convertView.findViewById(R.id.color);
            circleView.setColor(colors[position]);
            circleView.setSelect(position == currentSelect);
            ((TextView) convertView.findViewById(R.id.name)).setText(names[position]);
            return convertView;
        }
    }
}
