package com.expensetrackerapplication.operations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ItemOperations {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ArrayList<String> allExpensesCategories;
    public ArrayList<String> allExpensesItems;
    public ArrayList<Integer> allExpensesQuantities;
    public ArrayList<Double> allExpensesPrices;
    public ArrayList<String> allExpensesDates;

    public ItemOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addItem(String itemName, String dateOfExpense, double itemPrice, int itemQuantity, int categoryId) {

        ContentValues values = new ContentValues();
        values.put("item_name", itemName);
        values.put("date_of_expense", dateOfExpense);
        values.put("item_price", itemPrice);
        values.put("item_quantity", itemQuantity);
        values.put("category_id", categoryId);

        long itemId = database.insert("item_table", null, values);

        return itemId;
    }

    public int updateItem(int itemId, String itemName, String dateOfExpense, double itemPrice, int itemQuantity, int categoryId) {

        ContentValues values = new ContentValues();
        values.put("item_name", itemName);
        values.put("date_of_expense", dateOfExpense);
        values.put("item_price", itemPrice);
        values.put("item_quantity", itemQuantity);
        values.put("category_id", categoryId);

        int rowsAffected = database.update("item_table", values, "item_id = ?", new String[]{String.valueOf(itemId)});

        return rowsAffected;
    }

    public int deleteItem(int itemId) {

        int rowsAffected = database.delete("item_table", "item_id = ?", new String[]{String.valueOf(itemId)});

        return rowsAffected;
    }

    public int getItemIdByValues(String itemName, String dateOfExpense, double itemPrice, int itemQuantity, int categoryId) {

        String[] columns = {"item_id"};
        String selection = "item_name = ? AND date_of_expense = ? AND item_price = ? AND item_quantity = ? AND category_id = ?";
        String[] selectionArgs = {itemName, dateOfExpense, String.valueOf(itemPrice), String.valueOf(itemQuantity), String.valueOf(categoryId)};

        Cursor cursor = database.query("item_table", columns, selection, selectionArgs, null, null, null);
        int itemId = -1;

        if (cursor != null && cursor.moveToFirst()) {
            itemId = cursor.getInt(cursor.getColumnIndexOrThrow("item_id"));
            cursor.close();
        }
        return itemId;
    }

    public double getTotalItemExpensesForCategory(int categoryId) {
        String query = "SELECT SUM(item_price * item_quantity) AS total_expenses " +
                "FROM item_table " +
                "WHERE category_id = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        double totalExpenses = 0;
        if (cursor != null && cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("total_expenses"));
            cursor.close();
        }

        return totalExpenses;
    }

    /*public ArrayList<String> getItemNames(int categoryID) {
        ArrayList<String> itemNames = new ArrayList<>();

        String query = "SELECT item_table.item_name " +
                "FROM item_table " +
                "JOIN category_table ON item_table.category_id = category_table.category_id " +
                "WHERE category_table.category_id = ? " +
                "ORDER BY date_of_expense DESC";
        String[] selectionArgs = {String.valueOf(categoryID)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                itemNames.add(itemName);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return itemNames;
    }*/

    public ArrayList<String> getItemNames(int categoryID) {
        ArrayList<String> itemNames = new ArrayList<>();

        String query = "SELECT item_table.item_name " +
                "FROM item_table " +
                "JOIN category_table ON item_table.category_id = category_table.category_id " +
                "WHERE category_table.category_id = ? " +
                "ORDER BY date_of_expense DESC";
        String[] selectionArgs = {String.valueOf(categoryID)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String itemName = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                        itemNames.add(itemName);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        return itemNames;
    }


    public ArrayList<String> getItemExpenseDates(int categoryID) {
        ArrayList<String> itemExpenseDates = new ArrayList<>();

        String query = "SELECT item_table.date_of_expense " +
                "FROM item_table " +
                "JOIN category_table ON item_table.category_id = category_table.category_id " +
                "WHERE category_table.category_id = ? " +
                "ORDER BY date_of_expense DESC";
        String[] selectionArgs = {String.valueOf(categoryID)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date_of_expense = cursor.getString(cursor.getColumnIndexOrThrow("date_of_expense"));
                //itemExpenseDates.add(getDateFormattedWithDaysName(date_of_expense));
                itemExpenseDates.add(date_of_expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return itemExpenseDates;
    }

    public static String getDateFormattedWithDaysName(String dateString) {
        // Parse the date string into a Date object
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;

        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Format the date in the desired format
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault());
        String formattedDate = outputFormat.format(date);

        return formattedDate;
    }

    public ArrayList<Integer> getItemExpenseQuantities(int categoryID) {
        ArrayList<Integer> itemExpenseDates = new ArrayList<>();

        String query = "SELECT item_table.item_quantity " +
                "FROM item_table " +
                "JOIN category_table ON item_table.category_id = category_table.category_id " +
                "WHERE category_table.category_id = ? " +
                "ORDER BY date_of_expense DESC";
        String[] selectionArgs = {String.valueOf(categoryID)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("item_quantity"));
                itemExpenseDates.add(quantity);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return itemExpenseDates;
    }

    public ArrayList<Integer> getItemQuantities(int categoryID) {
        ArrayList<Integer> itemExpensesQuantities = new ArrayList<>();

        String query = "SELECT item_table.item_quantity " +
                "FROM item_table " +
                "JOIN category_table ON item_table.category_id = category_table.category_id " +
                "WHERE category_table.category_id = ? " +
                "ORDER BY date_of_expense DESC";
        String[] selectionArgs = {String.valueOf(categoryID)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("item_quantity"));
                itemExpensesQuantities.add(quantity);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return itemExpensesQuantities;
    }

    public ArrayList<Double> getItemIndividualPrices(int categoryID) {
        ArrayList<Double> itemExpensePrices = new ArrayList<>();

        String query = "SELECT item_table.item_price " +
                "FROM item_table " +
                "JOIN category_table ON item_table.category_id = category_table.category_id " +
                "WHERE category_table.category_id = ? " +
                "ORDER BY date_of_expense DESC";
        String[] selectionArgs = {String.valueOf(categoryID)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Double price = cursor.getDouble(cursor.getColumnIndexOrThrow("item_price"));
                itemExpensePrices.add(price);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return itemExpensePrices;
    }

    /*public double getOverallExpenses(int budgetId) {
        String query = "SELECT SUM(item_price * item_quantity) AS overall_expenses " +
                "FROM item_table " +
                "INNER JOIN category_table ON item_table.category_id = category_table.category_id " +
                "WHERE category_table.budget_id = ?";
        String[] selectionArgs = {String.valueOf(budgetId)};

        Cursor cursor = database.rawQuery(query, selectionArgs);

        double overallExpenses = 0;
        if (cursor != null && cursor.moveToFirst()) {
            overallExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("overall_expenses"));
            cursor.close();
        }

        return overallExpenses;
    }*/
    public double getOverallExpenses(int budgetId) {
        String query = "SELECT IFNULL(SUM(item_price * item_quantity), 0) AS overall_expenses " +
                "FROM category_table " +
                "LEFT JOIN item_table ON category_table.category_id = item_table.category_id " +
                "WHERE category_table.budget_id = ?";

        String[] selectionArgs = {String.valueOf(budgetId)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        double overallExpenses = 0;
        if (cursor != null && cursor.moveToFirst()) {
            overallExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("overall_expenses"));
            cursor.close();
        }

        return overallExpenses;
    }

    public boolean isBudgetExceeded(int categoryId, double price, int itemId) {
        boolean isExceeded = false;

        String query = "SELECT ((category_table.category_budget < " +
                "(SELECT IFNULL(SUM(item_price * item_quantity), 0) + ? " +
                "FROM item_table " +
                "WHERE category_id = ? AND item_id != ?)) OR " +
                "(category_table.category_budget IS NULL)) as budget_exceeded " +
                "FROM category_table " +
                "WHERE category_id = ?";
        String[] selectionArgs = {String.valueOf(price), String.valueOf(categoryId), String.valueOf(itemId), String.valueOf(categoryId)};

        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            isExceeded = cursor.getInt(cursor.getColumnIndexOrThrow("budget_exceeded")) > 0;
            cursor.close();
        }

        return isExceeded;
    }

    public static String getCurrentDateWithHoursMinSec() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = new Date();
            return dateFormat.format(currentDate);
    }

    public void getAllExpensesInformation(int budgetId) {
        allExpensesCategories = new ArrayList<>();
        allExpensesItems = new ArrayList<>();
        allExpensesQuantities = new ArrayList<>();
        allExpensesPrices = new ArrayList<>();
        allExpensesDates = new ArrayList<>();

        String query = "SELECT category_name, item_name, item_price, item_quantity, date_of_expense " +
                "FROM category_table " +
                "JOIN item_table ON category_table.category_id = item_table.category_id " +
                "WHERE budget_id = ? " +
                "ORDER BY item_table.date_of_expense DESC";
        String[] selectionArgs = {String.valueOf(budgetId)};

        Cursor cursor = database.rawQuery(query, selectionArgs,null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                allExpensesCategories.add(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
                allExpensesItems.add(cursor.getString(cursor.getColumnIndexOrThrow("item_name")));
                allExpensesQuantities.add(cursor.getInt(cursor.getColumnIndexOrThrow("item_quantity")));
                allExpensesPrices.add(cursor.getDouble(cursor.getColumnIndexOrThrow("item_price")));
                allExpensesDates.add(cursor.getString(cursor.getColumnIndexOrThrow("date_of_expense")));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}