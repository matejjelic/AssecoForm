package com.asseco.assecoform.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mjelic on 14.06.16..
 */
public class WebContentHashSqliteHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "SqliteHelper";
    private static final String DATABASE_NAME = DatabaseUtils.DB_NAME;
    private static final int DATABASE_VERSION = DatabaseUtils.DB_VERSION;

    public WebContentHashSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Creates the database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WebContentHashTableInfo.CREATE_TABLE_QUERY);
    }

    /**
     * Deletes old version of the database, and creates new.
     * DESTROYS ALL DATA!!!
     *
     * @param db         database
     * @param oldVersion old db version
     * @param newVersion new db version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // log the upgrade
        Log.w(LOG_TAG, "Upgrading database " + DATABASE_NAME + " from version " + oldVersion + " to " + newVersion + ". This will destroy all data from the database");
        // drop table
        db.execSQL("DROP TABLE IF EXISTS " + WebContentHashTableInfo.TABLE_NAME);
        // create new table
        onCreate(db);
    }
}
