package com.expensetrackerapplication.operations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SavingsOperations {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public SavingsOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addSavings(double savingsAmount, String date) {
        ContentValues values = new ContentValues();
        values.put("savings_amount", savingsAmount);
        values.put("savings_date_updated", date);

        long savingsId = database.insert("savings_table", null, values);

        return savingsId;
    }

    public int updateSavings(int savingsId, double savingsAmount, String date) {
        ContentValues values = new ContentValues();
        values.put("savings_amount", savingsAmount);
        values.put("savings_date_updated", date);

        int rowsAffected = database.update("savings_table", values, "savings_id = ?", new String[]{String.valueOf(savingsId)});

        return rowsAffected;
    }

    public int getSavingsId() {
        String[] columns = {"savings_id"};

        Cursor cursor = database.query("savings_table", columns, null, null, null, null, null);
        int savingsId = -1;

        if (cursor != null && cursor.moveToFirst()) {
            savingsId = cursor.getInt(cursor.getColumnIndexOrThrow("savings_id"));
            cursor.close();
        }

        return savingsId;
    }

    public double getSavingsAmount(int savingsId) {
        String[] columns = {"savings_amount"};
        String selection = "savings_id = ?";
        String[] selectionArgs = {String.valueOf(savingsId)};

        Cursor cursor = database.query("savings_table", columns, selection, selectionArgs, null, null, null);
        double savingsAmount = 0;

        if (cursor != null && cursor.moveToFirst()) {
            savingsAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("savings_amount"));
            cursor.close();
        }

        return savingsAmount;
    }

    public String getSavingsDateUpdated(int savingsId) {
        String[] columns = {"savings_date_updated"};
        String selection = "savings_id = ?";
        String[] selectionArgs = {String.valueOf(savingsId)};

        Cursor cursor = database.query("savings_table", columns, selection, selectionArgs, null, null, null);
        String date = "";

        if (cursor != null && cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndexOrThrow("savings_date_updated"));
            cursor.close();
        }

        return date;
    }

    public double getAllSavings() {
        String[] columns = {"savings_amount"};

        Cursor cursor = database.query("savings_table", columns, null, null, null, null, null);
        double savingsAmount = 0;

        if (cursor != null && cursor.moveToFirst()) {
            savingsAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("savings_amount"));
            cursor.close();
        }

        return savingsAmount;
    }
}
