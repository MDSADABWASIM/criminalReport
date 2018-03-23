package mdsadabwasimcom.criminal;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Date;


public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "com.mdsadabwasim.criminal.time";


    public static final String ARG_TIME = "time";

    TimePicker mTimePicker;
    int hour1;
    int minute1;



    public static TimePickerFragment newInstance(Date time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       final Date time = (Date) getArguments().getSerializable(ARG_TIME);
       Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(time);
     int   hour = mCalendar.get(Calendar.HOUR_OF_DAY);
    int  minute = mCalendar.get(Calendar.MINUTE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = v.findViewById(R.id.time_picker);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Time of crime:")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    hour1 = mTimePicker.getHour();
                                    minute1 = mTimePicker.getMinute();
                                } else {
                                  hour1 = mTimePicker.getCurrentHour();
                                    minute1 = mTimePicker.getCurrentMinute();
                                }
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(time);
                                calendar.set(Calendar.HOUR_OF_DAY, hour1);
                                calendar.set(Calendar.MINUTE, minute1);
                                Date time = calendar.getTime();
                                sendResult(Activity.RESULT_OK, time);

                            }
                        }
                )
                .create();
    }


    private void sendResult(int resultCode,Date time) {

            if(getTargetFragment()==null){
                return;
            }
            Intent intent = new Intent();
         intent.putExtra(EXTRA_TIME,time);
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);

    }
}