package com.snap.thirdear.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public static final long DB_TRUE = 1;
    public static final long DB_FALSE = 0;


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
            List<Trigger> triggers = new ArrayList<>();
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Fire sensor"));
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Flood sensor"));
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Earthquake sensor"));
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Smoke sensor"));
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Extreme alerts"));
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Severe alerts"));
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "AMBER Alert"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Fire"));
            insertGroupAndTrigger(db, new Groups("emergency_alert", 1, 1, 1, 1, 1, 1, 1, "Disaster alert", "Disaster"), triggers);
            triggers.clear();
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Front door open"));
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Home Security Integration"));
            triggers.add(new Trigger(Trigger.TYPE.SENSOR, "Safe"));
            insertGroupAndTrigger(db, new Groups("security_alert", 1, 1, 1, 1, 1, 1, 1, "Security alert", "Security"), triggers);
            triggers.clear();
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Help"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Mommy"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Daddy"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Dad"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "sissy"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Big boy"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Lexi"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Jenny"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Andy"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Stop"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "ouch"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Blood"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Bleeding"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Boo boo"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Boo"));
            insertGroupAndTrigger(db, new Groups("alert_injury", 1, 1, 1, 1, 1, 1, 1, "Injury alert", "Injury"), triggers);
            triggers.clear();
            triggers.add(new Trigger(Trigger.TYPE.SOUND, "Glass breaking"));
            insertGroupAndTrigger(db, new Groups("alert", 1, 1, 1, 1, 1, 1, 1, "Breakage alert", "Breakage"), triggers);
            triggers.clear();
            triggers.add(new Trigger(Trigger.TYPE.SOUND, "Door Bell"));
            triggers.add(new Trigger(Trigger.TYPE.SOUND, "Knocking on door"));
            triggers.add(new Trigger(Trigger.TYPE.SOUND, "Dryer finished"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Notification"));
            insertGroupAndTrigger(db, new Groups("alert", 1, 1, 1, 1, 1, 1, 1, "Notification alert", "General Notification"), triggers);
            triggers.clear();
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Food"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Candy"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Fun"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Good ear"));
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Grandma"));
            insertGroupAndTrigger(db, new Groups("alert", 1, 1, 1, 1, 1, 1, 1, "Alerts added by you", "Added By Me"), triggers);
            triggers.clear();
            triggers.add(new Trigger(Trigger.TYPE.WORDS, "Noise"));
            insertGroupAndTrigger(db, new Groups("alert", 1, 1, 1, 1, 1, 1, 1, "Noise level alert", "Noise Level"), triggers);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }
        Log.d("DataBaseHelper loadData", "done inserting data...");
    }

    private void insertGroupAndTrigger(SQLiteDatabase db, Groups group, List<Trigger> triggers) {
        long groupId = addGroup(group, db);
        for (Trigger t:triggers) {
            t.setGroupsId(groupId);
            addTrigger(t, db);
        }
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
                String processedWord = DatabaseUtils.sqlEscapeString(word);
                trigger = queryTriggerTable(processedWord, db);
                //stop on first match
                if (null != trigger) {
                    trigger.setMatchingWord(word);
                    break;
                }
            }
        }else{
            trigger.setMatchingWord(text);
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
                .append(TABLE_TWO_COL_3).append(" = \"").append(Trigger.TYPE.WORDS).append("\"")
                .append(" AND ").append(TABLE_TWO_COL_4).append(" like ").append(searchText);
        Log.d("TriggerByText Query ", selectQuery.toString());
        Cursor cursor = db.rawQuery(selectQuery.toString(), null);
        if (cursor != null && cursor.moveToFirst()) {
            trigger = setTriggerVarsFromCursor(cursor);
        }
        return trigger;
    }

    @NonNull
    private Trigger setTriggerVarsFromCursor(Cursor cursor) {
        Trigger trigger;
        trigger = new Trigger();
        trigger.set_id(cursor.getLong(0));
        trigger.setGroupsId(cursor.getInt(1));
        trigger.setType(cursor.getString(2));
        trigger.setTriggerText(cursor.getString(3));
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
            group = setGroupVarsFromCursor(cursor);
            Log.d("getGroup result ", group.toString());
        }
        return group;

    }

    @NonNull
    private Groups setGroupVarsFromCursor(Cursor cursor) {
        Groups group;
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
        return group;
    }

    public ArrayList<Groups> getAllGroups() {
        ArrayList<Groups> groups = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder selectQuery = new StringBuilder().append("SELECT ")
                .append(TABLE_ONE_COL_1).append(", ").append(TABLE_ONE_COL_2).append(", ")
                .append(TABLE_ONE_COL_3).append(", ").append(TABLE_ONE_COL_4).append(", ")
                .append(TABLE_ONE_COL_5).append(", ").append(TABLE_ONE_COL_6).append(", ")
                .append(TABLE_ONE_COL_7).append(", ").append(TABLE_ONE_COL_8).append(", ")
                .append(TABLE_ONE_COL_9).append(", ").append(TABLE_ONE_COL_10).append(", ")
                .append(TABLE_ONE_COL_11).append(" FROM ").append(TABLE_ONE_NAME);
        Log.d("getGroup Query ", selectQuery.toString());
        Cursor cursor = db.rawQuery(selectQuery.toString(), null);
        if (cursor.moveToFirst()) {
            do {
                Groups group = setGroupVarsFromCursor(cursor);
                groups.add(group);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return groups;
    }

    public ArrayList<Trigger> getAllTriggersForGroup(long id) {
        ArrayList<Trigger> triggers = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder selectQuery = new StringBuilder().append("SELECT ")
                .append(TABLE_TWO_COL_1).append(",").append(TABLE_TWO_COL_2)
                .append(",").append(TABLE_TWO_COL_3).append(",").append(TABLE_TWO_COL_4)
                .append(" FROM ").append(TABLE_TWO_NAME).append(" WHERE ")
                .append(TABLE_TWO_COL_2).append(" = ").append(id);
        Log.d("TriggerByText Query ", selectQuery.toString());
        Cursor cursor = db.rawQuery(selectQuery.toString(), null);
        if (cursor.moveToFirst()) {
            do {
                Trigger trigger = setTriggerVarsFromCursor(cursor);
                triggers.add(trigger);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return triggers;
    }

    public void insertTrigger(long id, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        Trigger triggerTwo = new Trigger(id, Trigger.TYPE.WORDS, text);
        Long result = addTrigger(triggerTwo, db);
        Log.d("insertTrigger", "_ID= " + result);
    }

    public int deleteTrigger(String triggerText) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TWO_NAME, TABLE_TWO_COL_4 + " = ?",new String[]{triggerText});
        Log.d("deleteTrigger", "result= " + result);
        return result;
    }

    public int toggleAlertMode(String columnName, long columnValue, String groupId) {
        Log.d("toggleAlertMode", "columnName= " + columnName);
        Log.d("toggleAlertMode", "columnValue= " + columnValue);
        Log.d("toggleAlertMode", "groupId= " + groupId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, columnValue);
        int result = db.update(TABLE_ONE_NAME, contentValues, TABLE_ONE_COL_1 + " = ?",new String[]{groupId});
        Log.d("toggleAlertMode", "result= " + result);
        return result;
    }

    public int toggleAlertMode(String groupId) {
        Log.d("toggleAlertMode", "groupId= " + groupId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_ONE_COL_4, DB_TRUE);
        contentValues.put(TABLE_ONE_COL_5, DB_TRUE);
        contentValues.put(TABLE_ONE_COL_6, DB_TRUE);
        contentValues.put(TABLE_ONE_COL_7, DB_TRUE);
        contentValues.put(TABLE_ONE_COL_8, DB_TRUE);
        contentValues.put(TABLE_ONE_COL_9, DB_TRUE);
        int result = db.update(TABLE_ONE_NAME, contentValues, TABLE_ONE_COL_1 + " = ?",new String[]{groupId});
        Log.d("toggleAlertMode", "result= " + result);
        return result;
    }
}
