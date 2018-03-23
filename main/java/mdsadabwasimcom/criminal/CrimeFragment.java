package mdsadabwasimcom.criminal;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class CrimeFragment extends Fragment {

    //to format date and time we define a format type
    // and then use DateFormat (SimpleDateFormat) to format date and time.
    public static final String DATE_FORMAT = "EEE MMM dd yyyy";
    public static final String TIME_FORMAT = "hh:mm a z";

    public static final String ARG_CRIME_ID = "crime_id";
    public static final String DIALOG_DATE = "dialog_date";
    public static final String DIALOG_TIME = "dialog_time";
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_TIME = 1;
    public static final int REQUEST_CONTACT = 2;
    public static final int REQUEST_PHONE_NUMBER = 3;
    public static final int REQUEST_PHOTO = 4;
    private static final String DIALOG_PHOTO = "DialogPhoto";
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    public Button mTimeButton;
    private CheckBox mSolvedCheckbox;
    private CheckBox mRequiresPoliceCheckbox;
    private boolean mIsLargeLayout;
    private Callbacks mCallbacks;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;


    // Required interface for hosting activities


    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }


    /* create a new method newInstance to create a arguments bundle that work as
    savedInstanceState in fragment and put the id in it .
     */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public CrimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrime = new Crime();
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
        //make sure to call this method if you add menu in any fragment and pass  true .
        setHasOptionsMenu(true);

        //get the crime id from arguments that we earlier saved .
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        //get the crime from CrimeLab class using the getCrime and passing the crimeId.
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //set the crime title and convert char to string.
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //initialize our date button, and give it a date through crime class.
        mDateButton = v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsLargeLayout) {
                    FragmentManager fragmentManager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.show(fragmentManager, DIALOG_DATE);
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                } else {
                    Intent intent = DatePickerActivity.newIntent(getContext(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_DATE);
                }

            }
        });

        //set the CheckBox and add an onChecked listener to respond in check.
        mSolvedCheckbox = v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        //set the checkbox for requires police or not.
        mRequiresPoliceCheckbox=v.findViewById(R.id.crime_police_Requires);
        mRequiresPoliceCheckbox.setChecked(mCrime.isRequiresPolice());
        mRequiresPoliceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean Checked) {
                mCrime.setRequiresPolice(Checked);
                updateCrime();
            }
        });

        //set the time in the time button.
        mTimeButton = v.findViewById(R.id.crime_time);
        mTimeButton.setEnabled(true);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment time_dialog = TimePickerFragment.newInstance(mCrime.getTime());
                time_dialog.show(fragmentManager, DIALOG_TIME);
                time_dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
            }
        });

        //Set the report Button function
        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .setText(getCrimeReport())
                        .getIntent();
                startActivity(shareIntent);
            }
        });
        //create a pickIntent before the anonymous class
        // cause we want to use it in another place as well.
        final Intent pickIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //choose the suspect of crime using suspect button.
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PHONE_NUMBER);
                } else {
                    startActivityForResult(pickIntent, REQUEST_CONTACT);
                }
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText("Suspect is"+" "+mCrime.getSuspect());
            mSuspectButton.setContentDescription("suspect is "+mCrime.getSuspect());
        }
        //check whether or not tha phone has contact app , using packageManager
        //if there's no contact app then disable the mSuspect Button.
        final PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        //call the suspect phone number .
        mCallSuspectButton = v.findViewById(R.id.call_suspect);
        String call = getString(R.string.call_suspect, mCrime.getSuspect());
        mCallSuspectButton.setText(call);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check permission , if not allowed request it.,
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PHONE_NUMBER);
                } else {

                    Uri phoneNumber = Uri.parse("tel:" + mCrime.getPhoneNumber());
                    Intent intent = new Intent(Intent.ACTION_DIAL, phoneNumber);
                    startActivity(intent);

                }
            }
        });
        if (mCrime.getPhoneNumber() != null) {
            mCallSuspectButton.setText(getString(R.string.call) +" "+ mCrime.getPhoneNumber());
            mCallSuspectButton.setContentDescription("call the suspect");
        }

        mPhotoButton = v.findViewById(R.id.crime_camera_button);
        final Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImageIntent.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we're giving file to intent in the form of URI to save the photos.
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "mdsadabwasim.android.criminal.fileProvider", mPhotoFile);
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                //check if there's any camera app in device if yes,
                // then assign all to write on that file.

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImageIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImageIntent, REQUEST_PHOTO);

            }
        });
        mPhotoView = v.findViewById(R.id.crime_photo);
        /* We're using viewTreeObserver to detect whether layout completes or not
        * , we can attach viewTreeObserver to any view and add multiple listener to it*/
        ViewTreeObserver observer = mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView();
            }
        });
        //setting onClickListener to zoomIn the photo of suspect.
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                PhotoViewer dialog = PhotoViewer.newInstance(mPhotoFile);
                dialog.show(manager, DIALOG_PHOTO);
            }
        });

        return v;
    }


    //get the permission result.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_NUMBER: {
                //if request is canceled the array will be empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "permission  granted , get phone numbers", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getActivity(), "permission  denied can't get phone numbers", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();

        }
        if (requestCode == REQUEST_TIME) {
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setTime(time);
            updateTime();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Cursor cursor = null;
            try {

                // getData() method will have the Content Uri of the selected contact
                Uri uri = data.getData();
                cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

                if (cursor.moveToFirst()) {


                    String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getActivity().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null, null);
                        phones.moveToFirst();

                        //get the phone number
                        String number = phones.getString(phones.getColumnIndex("data1"));
                        mCrime.setPhoneNumber(number);
                        mCallSuspectButton.setText(number);

                    }
                    //get the person name
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //set the suspect name, in data class and button text.
                    mCrime.setSuspect(name);
                    mSuspectButton.setText(name);
                    mCrime.setContactId(Long.parseLong(id));

                }

            } finally {
                cursor.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "mdsadabwasim.android.criminal.fileProvider", mPhotoFile);
            //revoke the permission that we grant .
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
        updateCrime();
    }
    /*here we format the time based on our defined format type
    * and then assign it to our date and time buttons.*/

    private void updateTime() {
        DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
        mTimeButton.setText(timeFormat.format(mCrime.getTime()));
    }

    private void updateDate() {
        Locale l = Locale.getDefault();
        DateFormat dateFormat;
        if (l== new Locale("es", "ES")){
            dateFormat = new SimpleDateFormat(DATE_FORMAT,new Locale("es", "ES"));

        }else{
            dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        }
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    //onAttach and onDetach for initializing and closing our callback.
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

    /*
    creating the separate menu for this fragment.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.second_menu_file, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                updateCrime();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = android.text.format.DateFormat.format(
                dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);

        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }


        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription("photo is not set");
        } else {
            //for accessibility announcement.
            mPhotoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPhotoView.announceForAccessibility("suspect photo is taken");
                }
            },1000);
            mPhotoView.setContentDescription("photo is set");
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}

