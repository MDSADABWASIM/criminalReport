package mdsadabwasimcom.criminal;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment{

    public static final String EXTRA_DATE="com.mdsadabwasim.criminal.date";
    public static final String ARG_DATE="date";
    private DatePicker mDatePicker;
    private Button mDatePickerOkButton;

    /*  creating a newInstance method to store the date in form of  an argument */
    public static DatePickerFragment newInstance(Date date){
        Bundle args= new Bundle();
        args.putSerializable(ARG_DATE,date);
        DatePickerFragment fragment= new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //getting the argument back from the fragment arguments
        //get the date that is given by crime.getDate() method .
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        int year= calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View v= LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);
        mDatePicker=v.findViewById(R.id.date_picker);
        //initializing the date in datePicker that we've got from crime.getDate() method.
        mDatePicker.init(year,month,day,null);
        mDatePickerOkButton =v.findViewById(R.id.datePickerOkbutton);
        mDatePickerOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 /* when we type ok button in dialog , after selecting our new date
                     * the it fetches the date and give it back to the crimeFragment to
                      * set the new Date in Crime , using crime.setDate() method.*/
                    int year=mDatePicker.getYear();
                    int month=mDatePicker.getMonth();
                    int day= mDatePicker.getDayOfMonth();
                    Date date = new GregorianCalendar(year,month,day).getTime();
                    sendResult(Activity.RESULT_OK,date);
                }

        });
        return v;
    }


    //send the final selected date back to the crimeFragment.
    private void sendResult(int resultCode, Date date){
        Intent data = new Intent();
        data.putExtra(EXTRA_DATE, date);
        if(getTargetFragment()==null){
            Activity hostingActivity = getActivity();
            hostingActivity.setResult(resultCode, data);
            hostingActivity.finish();
        }else {
            dismiss();
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
        }
    }



}
