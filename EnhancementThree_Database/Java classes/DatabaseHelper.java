package com.zybooks.thierrytran_eventtrackingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    public static final String COLUMN_USER_ID_FK = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT)";
            db.execSQL(createUsersTable);

            String createEventsTable = "CREATE TABLE " + TABLE_EVENTS + " (" +
                    COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EVENT_NAME + " TEXT, " +
                    COLUMN_USER_ID_FK + " INTEGER, " +
                    "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
            db.execSQL(createEventsTable);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    // Add a new user
    public long addUser(String username, String password) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, sanitizeInput(username));
            values.put(COLUMN_PASSWORD, password);
            return db.insert(TABLE_USERS, null, values);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error adding user: " + e.getMessage());
            return -1;
        }
    }

    // Check if user exists
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                    new String[]{username, password},
                    null, null, null);
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Add a new event
    public long addEvent(String eventName, long userId) {
        if (isDuplicateEvent(eventName, userId)) {
            Log.w("DatabaseHelper", "Duplicate event name detected.");
            return -1; // Indicate failure for duplicate event
        }
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_EVENT_NAME, sanitizeInput(eventName));
            values.put(COLUMN_USER_ID_FK, userId);
            return db.insert(TABLE_EVENTS, null, values);
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error adding event: " + e.getMessage());
            return -1;
        }
    }

    // Check for duplicate events
    private boolean isDuplicateEvent(String eventName, long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_EVENTS,
                    null,
                    COLUMN_EVENT_NAME + "=? AND " + COLUMN_USER_ID_FK + "=?",
                    new String[]{eventName, String.valueOf(userId)},
                    null, null, null);
            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Sanitize input
    private String sanitizeInput(String input) {
        return input.trim().replaceAll("[^a-zA-Z0-9 ]", ""); // Allow only alphanumeric and spaces
    }

    // Update an event by ID
    public int updateEvent(long eventId, String eventName) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_EVENT_NAME, sanitizeInput(eventName));
            return db.update(TABLE_EVENTS, values, COLUMN_EVENT_ID + "=?", new String[]{String.valueOf(eventId)});
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error updating event: " + e.getMessage());
            return 0; // Indicate failure
        }
    }

    // Delete an event by ID
    public int deleteEvent(long eventId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(TABLE_EVENTS, COLUMN_EVENT_ID + "=?", new String[]{String.valueOf(eventId)});
        } catch (SQLException e) {
            Log.e("DatabaseHelper", "Error deleting event: " + e.getMessage());
            return 0; // Indicate failure
        }
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


}
