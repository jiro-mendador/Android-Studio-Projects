package com.expensetrackerapplication.operations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.expensetrackerapplication.CategoryActivity;
import com.expensetrackerapplication.CategoryEditActivity;

import java.util.ArrayList;

public class CategoryOperations {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ArrayList<String> categoryNames = new ArrayList<>();
    public ArrayList<Integer> categoriesID = new ArrayList<>();

    //for chart
    public static class Category {
        public String categoryName;
        public float categoryTotalExpenses;

        Category(String name, float amount) {
            categoryName = name;
            categoryTotalExpenses = amount;
        }
    }
    public float totalExpensesOfTop5Categories = 0;

    public CategoryOperations(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addCategory(String categoryName, double categoryBudget, int budgetId) {
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);
        values.put("category_budget", categoryBudget);
        values.put("budget_id", budgetId);

        return database.insert("category_table", null, values);
    }

    public int updateCategory(int categoryId, String categoryName, double categoryBudget) {
        ContentValues values = new ContentValues();
        values.put("category_name", categoryName);
        values.put("category_budget", categoryBudget);

        String whereClause = "category_id=?";
        String[] whereArgs = {String.valueOf(categoryId)};

        return database.update("category_table", values, whereClause, whereArgs);
    }

    public int deleteCategory(int categoryId) {
        String whereClause = "category_id=?";
        String[] whereArgs = {String.valueOf(categoryId)};

        // Delete the category
        int rowsAffected = database.delete("category_table", whereClause, whereArgs);

        // Delete the items associated with the category
        database.delete("item_table", whereClause, whereArgs);

        return rowsAffected;
    }

    public int getCategoryIdByBudgetAndName(int budgetId, String categoryName) {
        String[] columns = {"category_id"};
        String selection = "budget_id=? AND category_name=?";
        String[] selectionArgs = {String.valueOf(budgetId), categoryName};

        Cursor cursor = database.query("category_table", columns, selection, selectionArgs, null, null, null);
        int categoryId = -1;

        if (cursor.moveToFirst()) {
            categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
        }

        cursor.close();
        return categoryId;
    }

    public ArrayList<String> getCategoryName(int budgetId) {
        String query = "SELECT category_name " +
                "FROM category_table " +
                "LEFT JOIN budget_table ON budget_table.budget_id = category_table.budget_id " +
                "WHERE budget_table.budget_id = ?";
        String[] selectionArgs = {String.valueOf(budgetId)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        ArrayList<String> categoryNames = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                categoryNames.add(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categoryNames;
    }

    public double getOverallRemainingBudget(int budgetId) {
        String query = "SELECT COALESCE((SELECT budget_amount FROM budget_table WHERE budget_id = ?), 0) - " +
                "COALESCE((SELECT SUM(item_price * item_quantity) FROM item_table WHERE category_id IN " +
                "(SELECT category_id FROM category_table WHERE budget_id = ?)), 0) AS overall_remaining_budget";

        String[] selectionArgs = {String.valueOf(budgetId), String.valueOf(budgetId)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        double overall_remaining_budget = 0;
        if (cursor != null && cursor.moveToFirst()) {
            overall_remaining_budget = cursor.getDouble(cursor.getColumnIndexOrThrow("overall_remaining_budget"));
            cursor.close();
        }
        return overall_remaining_budget;
    }
    public double getCategoryBudget(int categoryId) {
        String[] columns = {"category_budget"};
        String selection = "category_id = ?";
        String[] selectionArgs = {String.valueOf(categoryId)};

        Cursor cursor = database.query("category_table", columns, selection, selectionArgs, null, null, null);
        double categoryBudget = 0;

        if (cursor.moveToFirst()) {
            categoryBudget = cursor.getInt(cursor.getColumnIndexOrThrow("category_budget"));
        }
        cursor.close();

        return categoryBudget;
    }
    public boolean isCategoryNameDuplicate(String catName, int category_id, int budget_id) {
        String[] columns = {"category_name"};
        String selection = "category_name = ? AND category_id != ? AND b.budget_id = ?";
        String[] selectionArgs = {catName, String.valueOf(category_id), String.valueOf(budget_id)};

        String tableName = "category_table c INNER JOIN budget_table b ON c.budget_id = b.budget_id";
        Cursor cursor = database.query(tableName, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndexOrThrow("category_name")) != null;
        }
        cursor.close();

        return false;
    }

    public ArrayList<Integer> getCategoryPercentagesInBudget(int budgetId) {
        ArrayList<Integer> percentages = new ArrayList<>();

        String query = "SELECT IFNULL(expense_percentage, 0) AS expense_percentage " +
                "FROM (SELECT category_table.category_id, ROUND((SUM(item_price * item_quantity) / category_budget) * 100) AS expense_percentage " +
                "FROM category_table " +
                "LEFT JOIN item_table ON category_table.category_id = item_table.category_id " +
                "WHERE category_table.budget_id = ? " +
                "GROUP BY category_table.category_id) AS subquery";
        String[] selectionArgs = {String.valueOf(budgetId)};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int expensePercentage = cursor.getInt(cursor.getColumnIndexOrThrow("expense_percentage"));
                percentages.add(expensePercentage);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return percentages;
    }

    public void getCategoriesInRange(String startDate,String endDate) {
        categoryNames = new ArrayList<>();
        categoriesID = new ArrayList<>();

        String query = "SELECT category_id, category_name FROM category_table WHERE budget_id IN " +
                "(SELECT budget_id FROM budget_table WHERE budget_date_start >= ? AND budget_date_end <= ?)";

        String[] selectionArgs = {startDate, endDate};

        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                categoryNames.add(categoryName);
                categoriesID.add(categoryId);
            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    public double getTotalExpensesInRange(String startDate, String endDate) {
        double totalExpenses = 0;

        String query = "SELECT SUM(i.item_price * i.item_quantity) AS total_expenses " +
                "FROM item_table i " +
                "JOIN category_table c ON c.category_id = i.category_id " +
                "JOIN budget_table b ON c.budget_id = b.budget_id " +
                "WHERE b.budget_date_start >= ? AND b.budget_date_end <= ?";

        String[] selectionArgs = {startDate, endDate};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("total_expenses"));
            cursor.close();
        }

        return totalExpenses;
    }

    public double getTotalBudgetInRange(String startDate, String endDate) {
        double totalBudget = 0;

        String query = "SELECT SUM(c.category_budget) AS total_budget " +
                "FROM category_table c " +
                "JOIN budget_table b ON c.budget_id = b.budget_id " +
                "WHERE b.budget_date_start >= ? AND b.budget_date_end <= ?";

        String[] selectionArgs = {startDate, endDate};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            totalBudget = cursor.getDouble(cursor.getColumnIndexOrThrow("total_budget"));
            cursor.close();
        }

        return totalBudget;
    }

    public ArrayList<Category> getTopCategoriesAndOthers(String startDate, String endDate) {
        ArrayList<Category> categories = new ArrayList<>();

        // Query to get the top 5 categories with the most expenses
        String topCategoriesQuery = "SELECT c.category_name, SUM(i.item_price * i.item_quantity) AS total_expenses " +
                "FROM category_table c " +
                "JOIN item_table i ON c.category_id = i.category_id " +
                "JOIN budget_table b ON c.budget_id = b.budget_id " +
                "WHERE b.budget_date_start >= ? AND b.budget_date_end <= ? " +
                "GROUP BY c.category_id " +
                "ORDER BY total_expenses DESC " +
                "LIMIT 5";

        String[] selectionArgs = {startDate, endDate};
        Cursor cursor = database.rawQuery(topCategoriesQuery, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));
                float totalExpenses = cursor.getFloat(cursor.getColumnIndexOrThrow("total_expenses"));
                Category category = new Category(categoryName, totalExpenses);
                categories.add(category);
                totalExpensesOfTop5Categories += totalExpenses;
            } while (cursor.moveToNext());

            cursor.close();
        }
        return categories;
    }

    public float getCategoryExpensePercentage(int categoryId) {
        float expensePercentage = 0;
        // Retrieve the category budget
        String budgetQuery = "SELECT category_budget FROM category_table WHERE category_id = ?";
        String[] budgetSelectionArgs = {String.valueOf(categoryId)};
        Cursor budgetCursor = database.rawQuery(budgetQuery, budgetSelectionArgs);

        if (budgetCursor != null && budgetCursor.moveToFirst()) {
            float categoryBudget = budgetCursor.getFloat(budgetCursor.getColumnIndexOrThrow("category_budget"));
            budgetCursor.close();

            // Retrieve the total expenses for the category
            String expensesQuery = "SELECT SUM(item_price * item_quantity) AS total_expenses FROM item_table WHERE category_id = ?";
            String[] expensesSelectionArgs = {String.valueOf(categoryId)};
            Cursor expensesCursor = database.rawQuery(expensesQuery, expensesSelectionArgs);

            if (expensesCursor != null && expensesCursor.moveToFirst()) {
                float totalExpenses = expensesCursor.getFloat(expensesCursor.getColumnIndexOrThrow("total_expenses"));
                expensesCursor.close();

                // Calculate the expense percentage
                if (categoryBudget > 0) {
                    expensePercentage = (totalExpenses / categoryBudget) * 100;
                }
            }
        }
        return expensePercentage;
    }
}
