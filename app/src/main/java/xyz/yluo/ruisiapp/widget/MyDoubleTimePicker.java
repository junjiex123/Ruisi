package xyz.yluo.ruisiapp.widget;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import xyz.yluo.ruisiapp.R;


public class MyDoubleTimePicker extends AlertDialog implements DialogInterface.OnClickListener,
        TimePicker.OnTimeChangedListener {

    private static final String HOUR_START = "hour_start";
    private static final String MINUTE_START = "minute_start";
    private static final String HOUR_END = "hour_end";
    private static final String MINUTE_END = "minute_end";

    private final TimePicker mTimePickerStart, mTimePickerEnd;
    private final TimePickerDialog.OnTimeSetListener mTimeSetListener;

    private final int mInitialHourOfDayStart,mInitialHourOfDayEnd;
    private final int mInitialMinuteStart,mInitialMinuteEnd;


    public interface OnTimeSetListener {
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }


    public MyDoubleTimePicker(Context context, TimePickerDialog.OnTimeSetListener listener,
                              int hourOfDayStrt, int minuteStart,
                              int hourOfDayEnd, int minuteEnd) {
        super(context);

        mTimeSetListener = listener;
        mInitialHourOfDayStart = hourOfDayStrt;
        mInitialMinuteStart = minuteStart;
        mInitialHourOfDayEnd = hourOfDayEnd;
        mInitialMinuteEnd = minuteEnd;

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.my_double_time_picker, null);
        setView(view);
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        mTimePickerStart = (TimePicker) view.findViewById(R.id.time_start);
        mTimePickerEnd = (TimePicker) view.findViewById(R.id.time_end);

        mTimePickerStart.setIs24HourView(true);
        mTimePickerEnd.setIs24HourView(true);

        mTimePickerStart.setCurrentHour(mInitialHourOfDayStart);
        mTimePickerEnd.setCurrentHour(mInitialHourOfDayEnd);
        mTimePickerStart.setCurrentMinute(mInitialMinuteStart);
        mTimePickerEnd.setCurrentMinute(mInitialMinuteEnd);
        mTimePickerStart.setOnTimeChangedListener(this);
        mTimePickerEnd.setOnTimeChangedListener(this);
    }


    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        /* do nothing */
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mTimeSetListener != null) {
                    mTimeSetListener.onTimeSet(mTimePickerStart, mTimePickerStart.getCurrentHour(), mTimePickerStart.getCurrentMinute());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }


    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR_START, mTimePickerStart.getCurrentHour());
        state.putInt(MINUTE_START, mTimePickerStart.getCurrentMinute());
        state.putInt(HOUR_END, mTimePickerEnd.getCurrentHour());
        state.putInt(MINUTE_END, mTimePickerEnd.getCurrentMinute());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int hourStart = savedInstanceState.getInt(HOUR_START);
        final int minuteStart = savedInstanceState.getInt(MINUTE_START);

        final int hourEnd = savedInstanceState.getInt(HOUR_END);
        final int minuteEnd = savedInstanceState.getInt(MINUTE_END);

        mTimePickerStart.setCurrentHour(hourStart);
        mTimePickerStart.setCurrentMinute(minuteStart);

        mTimePickerEnd.setCurrentHour(hourEnd);
        mTimePickerEnd.setCurrentMinute(minuteEnd);
    }
}
