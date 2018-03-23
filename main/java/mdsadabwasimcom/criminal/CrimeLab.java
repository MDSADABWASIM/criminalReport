package mdsadabwasimcom.criminal;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mdsadabwasimcom.criminal.database.CrimeBaseHelper;
import mdsadabwasimcom.criminal.database.CrimeCursorWrapper;
import mdsadabwasimcom.criminal.database.CrimeDbSchema;
import mdsadabwasimcom.criminal.database.CrimeDbSchema.CrimeTable;

/* it's  a singleton class, a singleton exists as longs as the application
stays in the memory, so it stores the crime data available throughout any lifecycle
changes in our activity and fragments.
 */

public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;
    /*to create a singleton class we make a private constructor
    and a get() method,if the instance already exists, then get()
    return the instance , if not then , get() will call the
    constructor to create it.
     */


    //our singleton get method .
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    //we have a private method which means other class can't call its constructor.
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public File getPhotoFile(Crime crime){
        File filesDir=mContext.getFilesDir();
        return new File(filesDir,crime.getPhotoFilename());
    }


    //getCrimes method to return the list of crimes.
    public List<Crime> getCrimes() {

        List<Crime> crimes = new ArrayList<>();
       /*we pass null to whereClause  and whereArgs to select all columns */
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }
        return crimes;
    }


    //getCrime method that returns the crime with the given ID.
    public Crime getCrime(UUID id) {
        //here we pass whereClause and argument to select only one value.
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " =?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();

        } finally {
            cursor.close();
        }
    }

    //Create the required content values.
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.TIME, crime.getTime().getTime());
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());
        values.put(CrimeTable.Cols.CALL,crime.getPhoneNumber());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.CALL_POLICE,crime.isRequiresPolice() ? 1 : 0);
        values.put(CrimeTable.Cols.CONTACT_ID,Long.toString(crime.getContactId()));
        return values;
    }

    //update the crime List we use it in our fragments to update crime.
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + "=?",
                new String[]{uuidString});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeTable.NAME,
                null,//columns - null selects all columns.
                whereClause,
                whereArgs,
                null,//groupBy
                null,//having
                null//orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }

    public void addCrime(Crime crime) {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void deleteCrime(Crime crime) {
       mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID +"=?",
               new String[] {crime.getId().toString()});

    }

}
