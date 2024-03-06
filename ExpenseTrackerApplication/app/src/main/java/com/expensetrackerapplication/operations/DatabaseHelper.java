package com.expensetrackerapplication.operations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the budget_table
        db.execSQL("CREATE TABLE IF NOT EXISTS budget_table (" +
                "budget_id INTEGER PRIMARY KEY, budget_type TEXT, budget_date_start DATE, " +
                "budget_date_end DATE, budget_amount REAL)");

        // Create the category_table
        db.execSQL("CREATE TABLE IF NOT EXISTS category_table (" +
                "category_id INTEGER PRIMARY KEY, category_name TEXT, category_budget REAL, " +
                "budget_id INTEGER, FOREIGN KEY (budget_id) REFERENCES budget_table (budget_id))");

        // Create the item_table
        db.execSQL("CREATE TABLE IF NOT EXISTS item_table (" +
                "item_id INTEGER PRIMARY KEY, item_name TEXT, date_of_expense DATE, item_price REAL, item_quantity INTEGER, " +
                "category_id INTEGER, FOREIGN KEY (category_id) REFERENCES category_table (category_id))");

        // Create the savings_table
        db.execSQL("CREATE TABLE IF NOT EXISTS savings_table (savings_id INTEGER PRIMARY KEY, savings_amount REAL, savings_date_updated DATE);");

        // Create the user_table
        db.execSQL("CREATE TABLE IF NOT EXISTS user_table (user_id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT, username TEXT, password TEXT, status TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
