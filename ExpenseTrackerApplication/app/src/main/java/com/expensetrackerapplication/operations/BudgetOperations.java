package com.expensetrackerapplication.operations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BudgetOperations {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public BudgetOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addBudget(String budgetType, String startDate, String endDate, double amount) {
        ContentValues values = new ContentValues();
        values.put("budget_type", budgetType);
        values.put("budget_date_start", startDate);
        values.put("budget_date_end", endDate);
        values.put("budget_amount", amount);

        return database.insert("budget_table", null, values);
    }

    public int updateBudget(int budgetId, String budgetType, String startDate, String endDate, double amount) {
        ContentValues values = new ContentValues();
        values.put("budget_type", budgetType);
        values.put("budget_date_start", startDate);
        values.put("budget_date_end", endDate);
        values.put("budget_amount", amount);

        String whereClause = "budget_id=?";
        String[] whereArgs = {String.valueOf(budgetId)};

        return database.update("budget_table", values, whereClause, whereArgs);
    }

    public int deleteBudget(int budgetId) {
        String whereClause = "budget_id=?";
        String[] whereArgs = {String.valueOf(budgetId)};

        return database.delete("budget_table", whereClause, whereArgs);
    }

    public int getBudgetIdByDate(String budgetDate) {
        String[] columns = {"budget_id"};
        String selection = "budget_date_start <= ? AND budget_date_end >= ?";
        String[] selectionArgs = {budgetDate, budgetDate};

        Cursor cursor = database.query("budget_table", columns, selection, selectionArgs, null, null, null);
        int budgetId = -1;

        if (cursor != null && cursor.moveToFirst()) {
            budgetId = cursor.getInt(cursor.getColumnIndexOrThrow("budget_id"));
        }

        if (cursor != null) {
            cursor.close();
        }

        return budgetId;
    }

    public double getRemainingBudget(int budgetId, int categoryId) {
        String query = "SELECT (budget_table.budget_amount - COALESCE((SELECT SUM(category_budget) " +
                "FROM category_table WHERE budget_id = ? AND category_id != ?), 0)) AS remaining_budget " +
                "FROM budget_table " +
                "WHERE budget_id = ?";
        String[] selectionArgs = {String.valueOf(budgetId), String.valueOf(categoryId), String.valueOf(budgetId)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        double remainingBudget = 0;
        if (cursor != null && cursor.moveToFirst()) {
            remainingBudget = cursor.getDouble(cursor.getColumnIndexOrThrow("remaining_budget"));
            cursor.close();
        }
        return remainingBudget;
    }

    public boolean isUpdatedAmountValid(double updatedAmount, int budgetId) {
        boolean isValid = true;
        String query = "SELECT SUM(category_table.category_budget) AS sum " +
                "FROM budget_table " +
                "LEFT JOIN category_table ON budget_table.budget_id = category_table.budget_id " +
                "WHERE budget_table.budget_id = ?";
        String[] selectionArgs = {String.valueOf(budgetId)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        double sum = 0;
        if (cursor != null && cursor.moveToFirst()) {
            sum = cursor.getDouble(cursor.getColumnIndexOrThrow("sum"));
            if(updatedAmount - sum < 0){
                isValid = false;
            }
            cursor.close();
        }
        return isValid;
    }

    public double getBudgetSetForThisPeriod(String budgetDate) {
        String[] columns = {"budget_amount"};
        String selection = "budget_date_start <= ? AND budget_date_end >= ?";
        String[] selectionArgs = {budgetDate, budgetDate};

        Cursor cursor = database.query("budget_table", columns, selection, selectionArgs, null, null, null);
        double budgetSet = 0;

        if (cursor != null && cursor.moveToFirst()) {
            budgetSet = cursor.getDouble(cursor.getColumnIndexOrThrow("budget_amount"));
        }

        if (cursor != null) {
            cursor.close();
        }

        return budgetSet;
    }

    public String getBudgetStartDate(int budgetId) {
        String[] columns = {"budget_date_start"};
        String selection = "budget_id = ?";
        String[] selectionArgs = {String.valueOf(budgetId)};

        Cursor cursor = database.query("budget_table", columns, selection, selectionArgs, null, null, null);
        String budgetDateStart = "";

        if (cursor != null && cursor.moveToFirst()) {
            budgetDateStart = cursor.getString(cursor.getColumnIndexOrThrow("budget_date_start"));
        }

        if (cursor != null) {
            cursor.close();
        }

        return budgetDateStart;
    }

    public String getBudgetType(int budgetId) {
        String[] columns = {"budget_type"};
        String selection = "budget_id = ?";
        String[] selectionArgs = {String.valueOf(budgetId)};

        Cursor cursor = database.query("budget_table", columns, selection, selectionArgs, null, null, null);
        String budgetType = "";

        if (cursor != null && cursor.moveToFirst()) {
            budgetType = cursor.getString(cursor.getColumnIndexOrThrow("budget_type"));
        }

        if (cursor != null) {
            cursor.close();
        }

        return budgetType;
    }

    public boolean isBudgetSetForDate(String currentDate) {
        String[] columns = {"budget_id"};
        String selection = "budget_date_start <= ? AND budget_date_end >= ?";
        String[] selectionArgs = {currentDate, currentDate};

        Cursor cursor = database.query("budget_table", columns, selection, selectionArgs, null, null, null);

        boolean isBudgetSet = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }

        return isBudgetSet;
    }

    public int getPreviousBudgetID(String currentDate) {
        String query = "SELECT budget_id FROM budget_table WHERE budget_date_end < ? ORDER BY budget_date_end DESC LIMIT 1";
        String[] selectionArgs = { currentDate };

        Cursor cursor = database.rawQuery(query, selectionArgs);
        int budgetID = -1;

        if (cursor.moveToFirst() && cursor != null) {
            budgetID = cursor.getInt(0);
            cursor.close();
        }

        return budgetID;
    }

    /*public boolean checkBudgetPeriodEnd() {
        // Get yesterday's date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();

        // Format the date as yyyy-MM-dd
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterdayString = dateFormat.format(yesterday);

        // Query to check if yesterday's date is the end of a budget period
        String query = "SELECT COUNT(*) FROM budget_table WHERE budget_date_end = ?";
        String[] selectionArgs = {yesterdayString};

        Cursor cursor = database.rawQuery(query, selectionArgs);
        int count = 0;

        if (cursor.moveToFirst() && cursor != null) {
            count = cursor.getInt(0);
            cursor.close();
        }

        // Return true if yesterday's date is the end of a budget period
        return count > 0;
    }*/

    public boolean checkBudgetPeriodEnd(int prevBudgetId) {
        // Get current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        // Query to get the budget period details
        String query = "SELECT budget_date_end " +
                "FROM budget_table " +
                "WHERE budget_id = ?";

        String[] selectionArgs = {String.valueOf(prevBudgetId)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            String budgetEndDate = cursor.getString(cursor.getColumnIndexOrThrow("budget_date_end"));

            if (currentDate.compareTo(budgetEndDate) >= 0 && !isBudgetSetForDate(currentDate)) {
                // Budget period has ended and the next budget period has not been set
                cursor.close();
                return true;
            }
        }

        cursor.close();
        return false;
    }



}
