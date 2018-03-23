package mdsadabwasimcom.criminal;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.UUID;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeFragment.Callbacks, CrimeListFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        //phone
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            //tablets
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onCrimeSwiped(Crime crime) {

        if (findViewById(R.id.detail_fragment_container) == null) { // phone
            CrimeLab.get(CrimeListActivity.this).deleteCrime(crime);
            CrimeLab.get(CrimeListActivity.this).updateCrime(crime);
            onCrimeUpdated(crime);
        } else { // tablet

            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .remove(fm.findFragmentById(R.id.detail_fragment_container))
                    .commit();
        }

    }
}