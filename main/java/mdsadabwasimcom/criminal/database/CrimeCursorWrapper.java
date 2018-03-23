package mdsadabwasimcom.criminal.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import mdsadabwasimcom.criminal.Crime;
import mdsadabwasimcom.criminal.database.CrimeDbSchema.CrimeTable;


public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){
        String uuidString= getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title= getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date= getLong(getColumnIndex(CrimeTable.Cols.DATE));
        long time= getLong(getColumnIndex(CrimeTable.Cols.TIME));
        String suspect=getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String call=getString(getColumnIndex(CrimeTable.Cols.CALL));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int isRequiresPolice=getInt(getColumnIndex(CrimeTable.Cols.CALL_POLICE));
        long contact_id = getLong(getColumnIndex(CrimeTable.Cols.CONTACT_ID));

        Crime crime=new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setTime(new Date(time));
        crime.setSuspect(suspect);
        crime.setPhoneNumber(call);
        crime.setSolved(isSolved !=0);
        crime.setRequiresPolice(isRequiresPolice !=0);
        crime.setContactId(contact_id);
        return crime;
    }
}

