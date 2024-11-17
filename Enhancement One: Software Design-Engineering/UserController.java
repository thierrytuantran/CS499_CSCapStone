package com.zybooks.thierrytran_eventtrackingapp;

import android.content.Context;

public class UserController {
    private DatabaseHelper databaseHelper;

    public UserController(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public boolean authenticateUser(String username, String password) {
        return databaseHelper.checkUser(username, password);
    }

    public long getUserId(String username, String password) {
        return databaseHelper.getUserId(username, password);
    }
}
