package com.zybooks.thierrytran_eventtrackingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eventtracking.db";
    private static final int DATABASE_VERSION = 1;

    // User table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Events table
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_EVENT_NAME = "event_name";
    public static final String COLUMN_USER_ID_FK = "user_id"; // Foreign key to users table

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Create events table
        String createEventsTable = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EVENT_NAME + " TEXT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createEventsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    // Add a new user to the database
    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // Check if a user exists with the given username and password
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // Get the user ID for a given username and password
    public long getUserId(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID));
            cursor.close();
            return userId;
        } else {
            return -1; // User not found
        }
    }

    // Add a new event for a specific user
    public long addEvent(String eventName, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, eventName);
        values.put(COLUMN_USER_ID_FK, userId);
        return db.insert(TABLE_EVENTS, null, values);
    }

    // Retrieve all events for a specific user
    public Cursor getAllEvents(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EVENTS,
                null,
                COLUMN_USER_ID_FK + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);
    }

    // Delete an event
    public int deleteEvent(long eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENTS,
                COLUMN_EVENT_ID + "=?",
                new String[]{String.valueOf(eventId)});
    }

    // Update an event
    public int updateEvent(long eventId, String eventName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, eventName);
        return db.update(TABLE_EVENTS, values, COLUMN_EVENT_ID + "=?", new String[]{String.valueOf(eventId)});
    }

}
