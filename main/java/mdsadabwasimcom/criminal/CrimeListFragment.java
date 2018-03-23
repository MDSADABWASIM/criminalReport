package mdsadabwasimcom.criminal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    public int savedPosition;
    private boolean mSubtitleVisible;
    public static final String SAVED_CURSOR_POSITION = "savedPosition";
    public static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    public CrimeLab crimeLab;
    private Callbacks mCallbacks;
    public Crime crime;
    private TextView mEmptyText;
    private Button mEmptyButton;
    Button mContactPoliceButton;


    public interface Callbacks {
        void onCrimeSelected(Crime crime);

        void onCrimeSwiped(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mEmptyButton = view.findViewById(R.id.empty_button);
        mEmptyText = view.findViewById(R.id.empty_text);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            savedPosition = savedInstanceState.getInt(SAVED_CURSOR_POSITION);
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        mCrimeRecyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mCrimeRecyclerView.setLayoutManager(linearLayoutManager);
        setCrimeRecyclerViewItemTouchListener();

        updateUI();

        return view;
    }

    //function to delete items while swiped  right.
    private void setCrimeRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchListener = new
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    //this is for drag&drop feature so we left it.
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final int pos = viewHolder.getAdapterPosition();
                        Crime crime = mAdapter.getCrimes().get(pos);
                        if (direction == ItemTouchHelper.RIGHT) {
                            mCallbacks.onCrimeSwiped(crime);
                            updateUI();
                        }
                    }

                };
        ItemTouchHelper iteItemTouchHelper = new ItemTouchHelper(itemTouchListener);
        iteItemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);
    }


    private abstract class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int layout) {
            super(inflater.inflate(layout, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
            mContactPoliceButton = itemView.findViewById(R.id.CallPoliceButton);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText((mCrime.getTitle()));
            mDateTextView.setText(formatDate(mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
            mContactPoliceButton
                    .setVisibility(crime.isRequiresPolice() ? View.VISIBLE:View.GONE);
            mContactPoliceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri phoneNumber = Uri.parse("tel:" + 100);
                    Intent intent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View view) {
            //for accessibility
            if (mCrime.isSolved()){
            view.setContentDescription("crime is about"+mCrime.getTitle()
                    +"and it is solved");
            }else {
                view.setContentDescription("crime is about"
                        +mCrime.getTitle()+"and it is unsolved");
                    }
            mCallbacks.onCrimeSelected(mCrime);
            savedPosition = getAdapterPosition();
        }
    }



    private class SeriousCrimeHolder extends CrimeHolder {
        public SeriousCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent , R.layout.list_serious_crime);
        }

        @Override
        public void bind(Crime crime) {
            //calling the super class bind method to set its layout
            //title and date text view.
            super.bind(crime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {

            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                return new SeriousCrimeHolder(layoutInflater, parent);

        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public List<Crime> getCrimes() {
            return mCrimes;
        }


        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }

    //to format the date in form of friday,4 february 2018
    private String formatDate(Date date) {
        java.text.DateFormat dateFormat = java.text.DateFormat
                .getDateInstance(java.text.DateFormat.FULL);
        return dateFormat.format(date);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_CURSOR_POSITION, savedPosition);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    //update the ui using crimeLab class.
    public void updateUI() {
        final CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        updateEmptyRecycler(crimes);

        // if fragment exist then don't create a new one and
        /* use notifyItemChanged method to apply changes
         only on the items that values are changed.*/
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);


        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged(); //add this otherwise recyclerView will crash while deleting data.
            mAdapter.notifyItemChanged(savedPosition);

        }
        updateSubtitle();
    }


    /*
    This method is used when there is no crime in the crimeList to view.we call it under
    updateUI method.
     */
    private void updateEmptyRecycler(List<Crime> crimes) {
        if (crimes.isEmpty()) {
            mCrimeRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
            mEmptyButton.setVisibility(View.VISIBLE);
        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);
            mEmptyButton.setVisibility(View.GONE);
        }
        mEmptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
            }
        });
    }

    /*
    This method is used to inflate the menu for the activity or fragment.
     */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        //here we are defining the menu title through code.
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }

    /*
    what we want to do if some menu item is clicked, we define that here.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();//invalidateOptionsMenu is used to redraw the menu  UI.
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /*
    we call invalidateOptionsMenu() method to redraw the menu , then this method is called
    to give the menu instructions what to do next.
     */
    private void updateSubtitle() {
        crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        //we take the appcompatActivity to set the actionbar subtitle.
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);

    }


}