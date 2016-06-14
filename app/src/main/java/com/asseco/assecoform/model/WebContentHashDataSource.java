package com.asseco.assecoform.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Data access object.
 * Handles db connection for accessing and modifying the data, and converts the db objects to Java objects.
 * <p/>
 * Created by mjelic on 14.06.16..
 */
public class WebContentHashDataSource {
    private SQLiteDatabase database;
    private WebContentHashSqliteHelper dbHelper;
    private String[] allColumns = {
            WebContentHashTableInfo.COLUMN_URL,
            WebContentHashTableInfo.COLUMN_MD5HASH
    };

    public WebContentHashDataSource(Context context) {
        dbHelper = new WebContentHashSqliteHelper(context);
    }

    /**
     * Opens the database connection for writing (inserting).
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the DB connection.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Insert
     */
    public WebContentHash insertWebContentHash(WebContentHash hash) throws SQLException {
        open();
        ContentValues contentValues = new ContentValues();
        WebContentHash result = null;

        if (hash != null) {
            // prepare data
            if (hash.getUrl() != null && !hash.getUrl().isEmpty()) {
                contentValues.put(WebContentHashTableInfo.COLUMN_URL, hash.getUrl());
            }
            if (hash.getMd5Hash() != null && !hash.getMd5Hash().isEmpty()) {
                contentValues.put(WebContentHashTableInfo.COLUMN_MD5HASH, hash.getMd5Hash());
            }

            // insert data
            Long insertId = database.insert(WebContentHashTableInfo.TABLE_NAME, null, contentValues);
            Cursor cursor = database.query(WebContentHashTableInfo.TABLE_NAME, allColumns, WebContentHashTableInfo.COLUMN_URL + " = " + insertId, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                result = cursorToWebsiteContentHash(cursor);
                cursor.close();
                close();
            }

        } else {
            result = null;
        }

        return result;
    }

    /**
     * Select where url = ?
     * */
    /**
     * SELECT ALL
     *
     * @return
     * @throws SQLException
     */
    public String getHashForUrl(String url) throws SQLException {
        open();
        WebContentHash hash = null;
        String result = null;
//        String[] columns = {WebContentHashTableInfo.COLUMN_MD5HASH};

        Cursor cursor = database.query(WebContentHashTableInfo.TABLE_NAME, allColumns, WebContentHashTableInfo.COLUMN_URL + " = '" + url + "'", null, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                hash = cursorToWebsiteContentHash(cursor);
                cursor.moveToNext();
            }

            cursor.close();
        }

        if (hash != null) {
            result = hash.getMd5Hash();
        }

        close();
        return result;
    }


    private WebContentHash cursorToWebsiteContentHash(Cursor cursor) {
        WebContentHash hash = new WebContentHash();
        hash.setUrl(cursor.getString(0));
        hash.setMd5Hash(cursor.getString(1));

        return hash;
    }


}
