package com.expensetrackerapplication.operations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UserOperation {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public static String firstName;
    public static String lastName;
    public static String username;
    public static String password;

    public UserOperation(String fName, String lName, String uName, String pw) {
        firstName = fName;
        lastName = lName;
        username = uName;
        password = pw;
    }

    public UserOperation(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addUser(String firstName, String lastName, String username, String password, String status) {
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("username", username);
        values.put("password", password);
        values.put("status", status);

        return database.insert("user_table", null, values);
    }

    public int updateUser(int userId, String firstName, String lastName, String username, String password, String status) {
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("username", username);
        values.put("password", password);
        values.put("status", status);

        String whereClause = "user_id = ?";
        String[] whereArgs = {String.valueOf(userId)};

        return database.update("user_table", values, whereClause, whereArgs);
    }

    public int deleteUser(int userId) {
        String whereClause = "user_id = ?";
        String[] whereArgs = {String.valueOf(userId)};

        return database.delete("user_table", whereClause, whereArgs);
    }

    public boolean signInUser(String username, String password) {
        String[] columns = {"user_id"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = database.query("user_table", columns, selection, selectionArgs, null, null, null);
        boolean success = false;

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            cursor.close();

            // Update user status to "active"
            ContentValues values = new ContentValues();
            values.put("status", "active");
            String whereClause = "user_id = ?";
            String[] whereArgs = {String.valueOf(userId)};
            int rowsAffected = database.update("user_table", values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                success = true;
            }
        }
        return success;
    }

    public boolean isAUserStatusActive() {
        String[] columns = {"user_id"};
        String selection = "status = ?";
        String[] selectionArgs = {"active"};
        Cursor cursor = database.query("user_table", columns, selection, selectionArgs, null, null, null);
        boolean hasActiveUser = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        return hasActiveUser;
    }

    public void setCurrentUserInfo() {
        String[] columns = {"first_name", "last_name", "username", "password"};
        String selection = "status = ?";
        String[] selectionArgs = {"active"};

        Cursor cursor = database.query("user_table", columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public int getUserId() {
        String[] columns = {"user_id"};
        Cursor cursor = database.query("user_table", columns, null, null, null, null, null);
        int userId = -1;

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            cursor.close();
        }
        return userId;
    }

    public int signOut() {
        int userId = getUserId();
        ContentValues values = new ContentValues();
        values.put("status", "inactive");

        int rowsAffected = database.update("user_table", values, "user_id = ?", new String[]{String.valueOf(userId)});

        return rowsAffected;
    }
}

