package me.yluo.ruisiapp.activity;

import android.app.TimePickerDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import me.yluo.ruisiapp.App;
import me.yluo.ruisiapp.R;

public class NightModeActivity extends BaseActivity {

    private View startView, endView;
    private TextView startText, endText, startLabel, endLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_mode);

        initToolBar(true, "夜间模式设置");
        CheckBox auto = (CheckBox) findViewById(R.id.auto_dark_mode);
        boolean isAuto = App.isAutoDarkMode(this);
        auto.setChecked(isAuto);

        startView = findViewById(R.id.start_time);
        endView = findViewById(R.id.end_time);

        startText = (TextView) findViewById(R.id.start_time_text);
        endText = (TextView) findViewById(R.id.end_time_text);
        startLabel = (TextView) findViewById(R.id.start_time_label);
        endLabel = (TextView) findViewById(R.id.end_time_label);

        startText.setText(App.getDarkModeTime(this)[0] + ":00");
        endText.setText(App.getDarkModeTime(this)[1] + ":00");

        updateUi(isAuto);

        auto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            App.setAutoDarkMode(this, isChecked);
            updateUi(isChecked);
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

    private void updateUi(boolean isAuto) {
        startView.setEnabled(isAuto);
        endView.setEnabled(isAuto);

        if (isAuto) {
            startLabel.setTextColor(ContextCompat.getColor(this, R.color.text_color_pri));
            endLabel.setTextColor(ContextCompat.getColor(this, R.color.text_color_pri));
        } else {
            startLabel.setTextColor(ContextCompat.getColor(this, R.color.colorDisableHintIcon));
            endLabel.setTextColor(ContextCompat.getColor(this, R.color.colorDisableHintIcon));
        }
    }
}
