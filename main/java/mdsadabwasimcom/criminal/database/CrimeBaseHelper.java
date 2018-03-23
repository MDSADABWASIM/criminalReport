package mdsadabwasimcom.criminal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import mdsadabwasimcom.criminal.database.CrimeDbSchema.CrimeTable;
/*
here we define the base helper class that's a sqlite helper
that will help us to create database table and later upgrade it.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "crimeBase.db";
    private static final int VERSION = 1;

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL("create table "+ CrimeTable.NAME +"(" +
    "_id integer primary key autoincrement," +
            CrimeTable.Cols.UUID +","+
            CrimeTable.Cols.TITLE +"," +
            CrimeTable.Cols.DATE +"," +
            CrimeTable.Cols.TIME + ","+
            CrimeTable.Cols.SOLVED + ","+
            CrimeTable.Cols.SUSPECT +"," +
            CrimeTable.Cols.CALL + ","+
            CrimeTable.Cols.CALL_POLICE +","+
            CrimeTable.Cols.CONTACT_ID+     ")"
    );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
