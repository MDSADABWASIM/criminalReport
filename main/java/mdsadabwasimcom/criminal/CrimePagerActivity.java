package mdsadabwasimcom.criminal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks{
    ViewPager mViewPager;
    Button mFirstButton;
    Button mLastButton;
    Fragment fragment;

    Crime crime;
    private List<Crime> mCrimes;
    private static final String EXTRA_CRIME_ID="com.mdsadabwasim.criminal.crime_id";



    public static Intent newIntent(Context context, UUID crimeId){
        Intent intent= new Intent(context,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        mViewPager= (ViewPager) findViewById(R.id.crime_view_pager);
        //set the onPageChangeListener to enable/disable the buttons.
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
             mFirstButton.setEnabled(position+positionOffset >0);
             mLastButton.setEnabled(position+positionOffset < mCrimes.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mCrimes=CrimeLab.get(this).getCrimes();
        mFirstButton = (Button) findViewById(R.id.First_view_button);
        mLastButton= (Button) findViewById(R.id.Last_view_button);
        mFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });
        mLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getAdapter().getCount());
            }
        });

        FragmentManager fragmentManager= getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
             crime= mCrimes.get(position);
                fragment=CrimeFragment.newInstance(crime.getId());

                return fragment;

            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });


        //get the crimeId
        UUID crimeId= (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);


        for (int i=0;i<mCrimes.size();i++){
            if (mCrimes.get(i).getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
