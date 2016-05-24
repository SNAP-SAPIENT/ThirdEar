package com.snap.thirdear.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hrajal on 5/19/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

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
    public static final String TABLE_ONE_COL_10 = "ALERT_TEXT";
    public static final String TABLE_ONE_COL_11 = "NAME";

    public static final String TABLE_TWO_NAME = "TRIGGERS";
    public static final String TABLE_TWO_COL_1 = "_ID";
    public static final String TABLE_TWO_COL_2 = TABLE_ONE_NAME + "_ID";
    public static final String TABLE_TWO_COL_3 = "TYPE";
    public static final String TABLE_TWO_COL_4 = "TRIGGER_TEXT";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DataBaseHelper onCreate", "Creating DB tables");
        db.execSQL(groupsTableCreateQuery());
        db.execSQL(triggersTableCreateQuery());
        Log.d("DataBaseHelper onCreate", "Done creating DB tables");
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
        query.append(" INTEGER NOT NULL, ");
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
        query.append(" INTEGER NOT NULL, ");
        query.append(TABLE_ONE_COL_10);
        query.append(" INTEGER NOT NULL, ");
        query.append(TABLE_ONE_COL_11);
        query.append(" TEXT NOT NULL");
        query.append(");");
        Log.d("DataBaseHelper", query.toString());
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
        query.append(" ) REFERENCES ");
        query.append(TABLE_ONE_NAME);
        query.append(" (");
        query.append(TABLE_ONE_COL_1);
        query.append(" )");
        query.append(" );");
        Log.d("DataBaseHelper", query.toString());
        return query.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //when we have a version tht needs DB Upgrade we need to implement it with out loosing data in DB
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONE_NAME);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWO_NAME);
        // onCreate(db);
    }

    public void dropAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ONE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWO_NAME);
    }

    public void createAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    public long addGroup(Groups groups, SQLiteDatabase db) {
        ContentValues groupValues = new ContentValues();
        groupValues.put(TABLE_ONE_COL_2, groups.getIconUrl());
        groupValues.put(TABLE_ONE_COL_3, groups.getEnabled());
        groupValues.put(TABLE_ONE_COL_4, groups.getPhoneVibrate());
        groupValues.put(TABLE_ONE_COL_5, groups.getPhoneLight());
        groupValues.put(TABLE_ONE_COL_6, groups.getPhoneAudio());
        groupValues.put(TABLE_ONE_COL_7, groups.getLight());
        groupValues.put(TABLE_ONE_COL_8, groups.getBtReceiver());
        groupValues.put(TABLE_ONE_COL_9, groups.getWearableDevice());
        groupValues.put(TABLE_ONE_COL_10, groups.getAlertText());
        groupValues.put(TABLE_ONE_COL_11, groups.getName());
        long rowId = db.insert(TABLE_ONE_NAME, null, groupValues);
        return rowId;
    }

    public long addTrigger(Trigger trigger, SQLiteDatabase db) {
        ContentValues triggerValues = new ContentValues();
        triggerValues.put(TABLE_TWO_COL_2, trigger.getGroupsId());
        triggerValues.put(TABLE_TWO_COL_3, trigger.getType());
        triggerValues.put(TABLE_TWO_COL_4, trigger.getTriggerText());
        long rowId = db.insert(TABLE_TWO_NAME, null, triggerValues);
        return rowId;
    }

    public void loadData() {
        Log.d("DataBaseHelper loadData", "inserting data...");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            //first group
            Groups groupOne = new Groups("alert.png", 1, 1, 1, 1, 1, 1, 1, "call for assistance detected", "Help");
            long groupOneId = addGroup(groupOne, db);
            Trigger triggerOne = new Trigger(groupOneId, "WORDS", "help");
            addTrigger(triggerOne, db);
            //group two
            Groups groupTwo = new Groups("alert.png", 1, 1, 1, 1, 1, 1, 1, "greeting detected", "Greeting");
            long groupTwoId = addGroup(groupTwo, db);
            Trigger triggerTwo = new Trigger(groupTwoId, "WORDS", "hello");
            addTrigger(triggerTwo, db);

            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }
        Log.d("DataBaseHelper loadData", "done inserting data...");
    }

    public Trigger getTriggerByText(String text) {
        Log.d("TriggerByText text ", text);
        Trigger trigger = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String fullText = "%" + text + "%";
        String sentence = DatabaseUtils.sqlEscapeString(fullText);
        //try to match full sentence
        trigger = queryTriggerTable(sentence, db);
        //if no match found try each word in the sentence
        if (null == trigger) {
            String[] wordArray = text.split(" ");
            for (String word : wordArray) {
                String processedWord = DatabaseUtils.sqlEscapeString("%" + word + "%");
                trigger = queryTriggerTable(processedWord, db);
                //stop on first match
                if (null != trigger) {
                    break;
                }
            }
        }
        if(null == trigger)
            Log.d("TriggerByText result ", "trigger is null");
        else
            Log.d("TriggerByText result ", trigger.toString());
        return trigger;
    }

    private Trigger queryTriggerTable(String searchText, SQLiteDatabase db) {
        Trigger trigger = null;
        StringBuilder selectQuery = new StringBuilder().append("SELECT ")
                .append(TABLE_TWO_COL_1).append(",").append(TABLE_TWO_COL_2)
                .append(",").append(TABLE_TWO_COL_3).append(",").append(TABLE_TWO_COL_4)
                .append(" FROM ").append(TABLE_TWO_NAME).append(" WHERE ")
                .append(TABLE_TWO_COL_4).append(" like ").append(searchText);
        Log.d("TriggerByText Query ", selectQuery.toString());
        Cursor cursor = db.rawQuery(selectQuery.toString(), null);
        if (cursor != null && cursor.moveToFirst()) {
            trigger = new Trigger();
            trigger.set_id(cursor.getLong(0));
            trigger.setGroupsId(cursor.getInt(1));
            trigger.setType(cursor.getString(2));
            trigger.setTriggerText(cursor.getString(3));
        }
        return trigger;
    }

    public Groups getGroup(long groupId) {
        Log.d("getGroup ", "id "+ groupId);
        Groups group = null;
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder selectQuery = new StringBuilder().append("SELECT ")
                .append(TABLE_ONE_COL_1).append(", ").append(TABLE_ONE_COL_2).append(", ")
                .append(TABLE_ONE_COL_3).append(", ").append(TABLE_ONE_COL_4).append(", ")
                .append(TABLE_ONE_COL_5).append(", ").append(TABLE_ONE_COL_6).append(", ")
                .append(TABLE_ONE_COL_7).append(", ").append(TABLE_ONE_COL_8).append(", ")
                .append(TABLE_ONE_COL_9).append(", ").append(TABLE_ONE_COL_10).append(", ")
                .append(TABLE_ONE_COL_11).append(" FROM ").append(TABLE_ONE_NAME)
                .append(" WHERE ").append(TABLE_ONE_COL_1).append(" = ").append(groupId);
        Log.d("getGroup Query ", selectQuery.toString());
        Cursor cursor = db.rawQuery(selectQuery.toString(), null);
        if (cursor != null && cursor.moveToFirst()) {
            group = new Groups();
            group.set_id(cursor.getLong(0));
            group.setIconUrl(cursor.getString(1));
            group.setEnabled(cursor.getInt(2));
            group.setPhoneVibrate(cursor.getInt(3));
            group.setPhoneLight(cursor.getInt(4));
            group.setPhoneAudio(cursor.getInt(5));
            group.setLight(cursor.getInt(6));
            group.setBtReceiver(cursor.getInt(7));
            group.setWearableDevice(cursor.getInt(8));
            group.setAlertText(cursor.getString(9));
            group.setName(cursor.getString(10));
            Log.d("getGroup result ", group.toString());
        }
        return group;

    }
}
