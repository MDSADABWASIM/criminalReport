package mdsadabwasimcom.criminal;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity{
    private static final String EXTRA_CRIME_DATE ="com.mdsadabwasim.criminal.crime_date";

    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, DatePickerActivity.class);
        intent.putExtra(EXTRA_CRIME_DATE, date);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_CRIME_DATE);
        return DatePickerFragment.newInstance(date);
    }
}
