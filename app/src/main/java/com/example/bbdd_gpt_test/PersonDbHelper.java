package com.example.bbdd_gpt_test;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersonDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "person.db";
    private static final int DATABASE_VERSION = 1;

    public PersonDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE =
                "CREATE TABLE " + PersonContract.PersonEntry.TABLE_NAME + " (" +
                        PersonContract.PersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PersonContract.PersonEntry.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                        PersonContract.PersonEntry.COLUMN_NAME_AGE + " INTEGER NOT NULL, " +
                        PersonContract.PersonEntry.COLUMN_NAME_HEIGHT + " REAL NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PersonContract.PersonEntry.TABLE_NAME);
        onCreate(db);
    }
}