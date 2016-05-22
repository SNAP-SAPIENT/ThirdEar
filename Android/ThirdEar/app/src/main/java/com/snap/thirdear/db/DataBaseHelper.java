package com.snap.thirdear.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hrajal on 5/19/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "ThirdEar.db";
    public static final int VERSION = 1;

    public static final String TABLE_ONE_NAME = "GROUPS";
    public static final String TABLE_ONE_COL_1 = "_ID";
    public static final String TABLE_ONE_COL_2 = "ICON_URL";
    //not using this for this version. THis is to disable a group
    public static final String TABLE_ONE_COL_3 = "ENABLED";
    //0 means disabled 1 means enabled
    public static final String TABLE_ONE_COL_4 = "PHONE_VIBRATE";
    //0 means disabled 1 means enabled
    public static final String TABLE_ONE_COL_5 = "PHONE_LIGHT";
    //0 means disabled 1 means enabled
    public static final String TABLE_ONE_COL_6 = "PHONE_AUDIO";
    //0 means disabled 1 means enabled
    public static final String TABLE_ONE_COL_7 = "LIGHT";
    public static final String TABLE_ONE_COL_8 = "BT_RECEIVER";
    //0 means disabled 1 means enabled
    public static final String TABLE_ONE_COL_9 = "WEARABLE_DEVICE";

    public static final String TABLE_TWO_NAME = "TRIGGERS";
    public static final String TABLE_TWO_COL_1 = "_ID";
    public static final String TABLE_TWO_COL_2 = TABLE_ONE_NAME + "_ID";
    public static final String TABLE_TWO_COL_3 = "TYPE";
    public static final String TABLE_TWO_COL_4 = "TRIGGER_TEXT";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DataBaseHelper onCreate","Creating DB tables");
        db.execSQL(groupsTableCreateQuery());
        db.execSQL(triggersTableCreateQuery());
        Log.d("DataBaseHelper onCreate","Done creating DB tables");
        loadData(db);
    }

    private void loadData(SQLiteDatabase db) {
        Log.d("DataBaseHelper loadData","inserting data...");
        try
        {
            db.beginTransaction();

            /*for each record in the
            list

                if (line represent a valid entry)
                {
                    db.insert(SOME_TABLE, null, SOME_VALUE);
                }
            }*/

            db.setTransactionSuccessful();
        }
        catch (SQLException e) {}
        finally
        {
            db.endTransaction();
        }
        db.execSQL("INSERT INTO " + TABLE_ONE_NAME + "");
        Log.d("DataBaseHelper loadData","done inserting data...");
    }


    private String groupsTableCreateQuery() {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ");
        query.append(TABLE_ONE_NAME);
        query.append("(");
        query.append(TABLE_ONE_COL_1);
        query.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        query.append(TABLE_ONE_COL_2);
        query.append(" TEXT  NOT NULL, ");
        query.append(TABLE_ONE_COL_3);
        query.append("INTEGER, ");
        query.append(TABLE_ONE_COL_4);
        query.append(" INTEGER  NOT NULL, ");
        query.append(TABLE_ONE_COL_5);
        query.append(" INTEGER  NOT NULL, ");
        query.append(TABLE_ONE_COL_6);
        query.append(" INTEGER  NOT NULL, ");
        query.append(TABLE_ONE_COL_7);
        query.append(" INTEGER  NOT NULL, ");
        query.append(TABLE_ONE_COL_8);
        query.append(" INTEGER  NOT NULL, ");
        query.append(TABLE_ONE_COL_9);
        query.append("INTEGER NOT NULL");
        query.append(");");
        return query.toString();
    }

    private String triggersTableCreateQuery() {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ");
        query.append(TABLE_TWO_NAME);
        query.append("(");
        query.append(TABLE_TWO_COL_1);
        query.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        query.append(TABLE_TWO_COL_2);
        query.append(" INTEGER  NOT NULL, ");
        query.append(TABLE_TWO_COL_3);
        query.append(" TEXT  NOT NULL, ");
        query.append(TABLE_TWO_COL_4);
        query.append(" TEXT, ");
        query.append("FOREIGN KEY(");
        query.append(TABLE_TWO_COL_2);
        query.append(") REFERENCES ");
        query.append(TABLE_ONE_NAME);
        query.append("(");
        query.append(TABLE_ONE_COL_1);
        query.append(")");
        query.append(");");
        return query.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //when we have a version tht needs DB Upgrade we need to implement it with out loosing data in DB
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONE_NAME);
        //onCreate(db);
    }
}
