package com.asseco.assecoform.model;

/**
 * Created by mjelic on 14.06.16..
 */
public class WebContentHashTableInfo {
    // table name
    public static final String TABLE_NAME = "webcontenthash";
    // column names (for table "WebContentHash")
    public static final String COLUMN_URL = "url"; // URL is an ID - Primary key
    public static final String COLUMN_MD5HASH = "md5Hash"; // MD5 hash

    // queries
    public static final String CREATE_TABLE_QUERY =
            "create table " + TABLE_NAME + "(" +
                    COLUMN_URL + " text primary key, " +
                    COLUMN_MD5HASH + " text not null " +
                    ");";

}
