package com.expensetrackerapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.expensetrackerapplication.operations.BudgetOperations;
import com.expensetrackerapplication.operations.CategoryOperations;
import com.expensetrackerapplication.operations.ItemOperations;

public class CategoryEditActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etCategoryName, etExpenses, etBudget;
    Button btnEditSaveCategoryName, btnDeleteCategory, btnEditCategoryBudget, btnBackToCategory;
    LinearLayout expensesListLayout;

    //variables
    boolean editingCategory, editingExpenses, editingBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_items_budget_layout);

        init();
        setListeners();

        if(ReportsActivity.isOnlyViewingExpenses) {
            btnEditCategoryBudget.setVisibility(View.INVISIBLE);
            btnEditSaveCategoryName.setVisibility(View.INVISIBLE);
            btnDeleteCategory.setVisibility(View.INVISIBLE);

            ItemOperations itemOperations = new ItemOperations(this);
            itemOperations.open();

            CategoryOperations categoryOperations = new CategoryOperations(this);
            categoryOperations.open();

            etCategoryName.setText(ReportsActivity.categoryName);
            etExpenses.setText(String.valueOf(itemOperations.getTotalItemExpensesForCategory(ReportsActivity.categoryId)));
            etBudget.setText(String.valueOf(categoryOperations.getCategoryBudget(ReportsActivity.categoryId)));

            getAllExpensesForViewing();

            categoryOperations.close();
            itemOperations.close();
        } else {
            etCategoryName.setText(CategoryActivity.categoryName);
            etExpenses.setText(String.valueOf(CategoryActivity.totalItemExpenses));
            etBudget.setText(String.valueOf(CategoryActivity.categoryBudget));
            getAllExpenses();
        }
    }

    void init() {
        InputFilter[] textFilter = new InputFilter[1];
        textFilter[0] = new InputFilter.LengthFilter(17);

        etCategoryName = (EditText) findViewById(R.id.lblTvCategoryName);
        etExpenses = (EditText) findViewById(R.id.etExpenses);
        etBudget = (EditText) findViewById(R.id.etBudget);
        expensesListLayout = (LinearLayout) findViewById(R.id.layoutCategoryList);
        btnEditSaveCategoryName = (Button) findViewById(R.id.btnEditSaveCategoryName);
        btnDeleteCategory = (Button) findViewById(R.id.btnDeleteCategory);
        btnBackToCategory = (Button) findViewById(R.id.btnBackToCategory);
        btnEditCategoryBudget = (Button) findViewById(R.id.btnEditBudget);

        editingCategory = false;
        editingExpenses = false;
        editingBudget = false;
        etCategoryName.setFilters(textFilter);
    }

    void setListeners() {
        btnEditSaveCategoryName.setOnClickListener(this);
        btnDeleteCategory.setOnClickListener(this);
        btnBackToCategory.setOnClickListener(this);
        btnEditCategoryBudget.setOnClickListener(this);
    }

    void getAllExpenses() {
        if (CategoryActivity.itemNames == null) {
            return;
        }

        expensesListLayout.removeAllViews();

        // Create a wrapper class to hold the previous item ID
        class PreviousItem {
            int id = 0;
            boolean isItemBeingEdited = false;
        }
        final PreviousItem prevItem = new PreviousItem();

        for (int i = 0; i < CategoryActivity.itemNames.size(); i++) {
            View childLayout = getLayoutInflater().inflate(R.layout.expenses_history_layout, expensesListLayout, false);

            TextView tvCategoryName = childLayout.findViewById(R.id.tvHistoryCategoryName);
            EditText etExpenseItemName = childLayout.findViewById(R.id.tvActHistoryItemName);
            EditText etExpenseItemQuantity = childLayout.findViewById(R.id.tvActHistoryItemQuantity);
            TextView tvExpenseItemDate = childLayout.findViewById(R.id.tvActHistoryDate);
            EditText etExpenseItemPrice = childLayout.findViewById(R.id.tvActHistoryAmount);
            Button btnEditExpensesInformation = childLayout.findViewById(R.id.btnEditExpenseItem);

            btnEditExpensesInformation.setVisibility(View.VISIBLE);
            tvCategoryName.setVisibility(View.GONE);

            etExpenseItemName.setText(CategoryActivity.itemNames.get(i));
            etExpenseItemQuantity.setText(String.valueOf(CategoryActivity.itemQuantities.get(i)));
            tvExpenseItemDate.setText(CategoryActivity.itemDateOfExpenses.get(i));
            etExpenseItemPrice.setText(String.valueOf(CategoryActivity.itemPrices.get(i)));

            expensesListLayout.addView(childLayout);

            //set the id of this expense item
            ItemOperations itemOperations = new ItemOperations(CategoryEditActivity.this);
            itemOperations.open();

            String item_name = etExpenseItemName.getText().toString();
            double price = Double.parseDouble(etExpenseItemPrice.getText().toString());
            int quantity = Integer.parseInt(etExpenseItemQuantity.getText().toString());
            String date = tvExpenseItemDate.getText().toString();

            final int itemId = itemOperations.getItemIdByValues(item_name, date,
                    price, quantity, CategoryActivity.categoryID);

            btnEditExpensesInformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to prevent user to edit another item if there is a current item being edited
                    if (itemId != prevItem.id && prevItem.isItemBeingEdited) {
                        Toast.makeText(CategoryEditActivity.this, "Save the current item being edited first", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    etExpenseItemName.setEnabled(true);
                    etExpenseItemPrice.setEnabled(true);
                    etExpenseItemQuantity.setEnabled(true);

                    //some settings to limit edittext characters
                    InputFilter[] quantityFilter = new InputFilter[1];
                    quantityFilter[0] = new InputFilter.LengthFilter(4);

                    InputFilter[] priceFilter = new InputFilter[1];
                    priceFilter[0] = new InputFilter.LengthFilter(8);

                    InputFilter[] textFilter = new InputFilter[1];
                    textFilter[0] = new InputFilter.LengthFilter(15);

                    etExpenseItemName.setFilters(textFilter);
                    etExpenseItemPrice.setFilters(priceFilter);
                    etExpenseItemQuantity.setFilters(quantityFilter);

                    if (etExpenseItemName.getText().toString().isEmpty()) {
                        Toast.makeText(CategoryEditActivity.this, "Item name is empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (etExpenseItemQuantity.getText().toString().isEmpty()) {
                        Toast.makeText(CategoryEditActivity.this, "Item quantity is empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (etExpenseItemPrice.getText().toString().isEmpty()) {
                        Toast.makeText(CategoryEditActivity.this, "Item price is empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (Integer.parseInt(etExpenseItemQuantity.getText().toString()) <= 0 || Double.parseDouble(etExpenseItemPrice.getText().toString()) <= 0) {
                        Toast.makeText(CategoryEditActivity.this, "Item Quantity and price cannot be zero", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ItemOperations itemOperations1 = new ItemOperations(CategoryEditActivity.this);
                    itemOperations1.open();

                    //if the total amount + current price exceeds the budget
                    if (itemOperations1.isBudgetExceeded(CategoryActivity.categoryID,
                            Double.parseDouble(etExpenseItemPrice.getText().toString()) * Integer.parseInt(etExpenseItemQuantity.getText().toString()), itemId)) {
                        Toast.makeText(CategoryEditActivity.this, "Current expense amount will exceed the alloted budget for this category", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editingExpenses) {
                        int id = itemOperations1.updateItem(itemId, etExpenseItemName.getText().toString(), tvExpenseItemDate.getText().toString(),
                                Double.parseDouble(etExpenseItemPrice.getText().toString()),
                                Integer.parseInt(etExpenseItemQuantity.getText().toString()), CategoryActivity.categoryID);

                        if (id != -1) {
                            Toast.makeText(CategoryEditActivity.this, "expenses information updated", Toast.LENGTH_SHORT).show();

                            CategoryOperations categoryOperations = new CategoryOperations(CategoryEditActivity.this);
                            categoryOperations.open();

                            //warn the user if the total expenses are about to exceed
                            float percent = categoryOperations.getCategoryExpensePercentage(CategoryActivity.categoryID);
                            String category = etCategoryName.getText().toString();

                            if(percent >= 85 && percent <= 99) {
                                sendNotification(category+" expenses about to exceed ",
                                        "Total expenses takes "+categoryOperations.getCategoryExpensePercentage(CategoryActivity.categoryID)
                                                +"% of the category budget");
                            } else if(percent == 100) {
                                sendNotification(category+" expenses",
                                        "already takes up the whole category budget");
                            }
                            categoryOperations.close();

                        } else {
                            Toast.makeText(CategoryEditActivity.this, "Failed to update expenses information", Toast.LENGTH_SHORT).show();
                        }
                        etExpenses.setText(String.valueOf(itemOperations1.getTotalItemExpensesForCategory(CategoryActivity.categoryID)));
                        itemOperations1.close();
                    }

                    editingExpenses = !editingExpenses;

                    changeButtonIcon(btnEditExpensesInformation, editingExpenses
                            ? ContextCompat.getDrawable(CategoryEditActivity.this, R.drawable.savechanges)
                            : ContextCompat.getDrawable(CategoryEditActivity.this, R.drawable.edit));

                    etExpenseItemName.setEnabled(editingExpenses);
                    etExpenseItemPrice.setEnabled(editingExpenses);
                    etExpenseItemQuantity.setEnabled(editingExpenses);

                    // Update the flag and previous item ID
                    prevItem.isItemBeingEdited = !prevItem.isItemBeingEdited;
                    prevItem.id = itemId;
                }
            });
            itemOperations.close();
        }
    }

    void getAllExpensesForViewing() {
        ItemOperations itemOperations = new ItemOperations(this);
        itemOperations.open();

        if (itemOperations.getItemNames(ReportsActivity.categoryId) == null) {
            return;
        }
        int id = ReportsActivity.categoryId;

        expensesListLayout.removeAllViews();
        for (int i = 0; i < itemOperations.getItemNames(ReportsActivity.categoryId).size(); i++) {
            View childLayout = getLayoutInflater().inflate(R.layout.expenses_history_layout, expensesListLayout, false);

            TextView tvCategoryName = childLayout.findViewById(R.id.tvHistoryCategoryName);
            EditText etExpenseItemName = childLayout.findViewById(R.id.tvActHistoryItemName);
            EditText etExpenseItemQuantity = childLayout.findViewById(R.id.tvActHistoryItemQuantity);
            TextView tvExpenseItemDate = childLayout.findViewById(R.id.tvActHistoryDate);
            EditText etExpenseItemPrice = childLayout.findViewById(R.id.tvActHistoryAmount);
            Button btnEditExpensesInformation = childLayout.findViewById(R.id.btnEditExpenseItem);

            btnEditExpensesInformation.setVisibility(View.INVISIBLE);
            tvCategoryName.setVisibility(View.GONE);

            etExpenseItemName.setText(itemOperations.getItemNames(id).get(i));
            etExpenseItemQuantity.setText(String.valueOf(itemOperations.getItemQuantities(id).get(i)));
            tvExpenseItemDate.setText(itemOperations.getItemExpenseDates(id).get(i));
            etExpenseItemPrice.setText(String.valueOf(itemOperations.getItemIndividualPrices(id).get(i)));

            expensesListLayout.addView(childLayout);
        }
        itemOperations.close();
    }

    void changeButtonIcon(Button button, Drawable drawable) {
        // Set the drawable to the left of the text (use null for top, right, and bottom drawables)
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    public void sendNotification(String notifTitle, String notifContent) {
        //for sample notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel with unique ID, name, and importance
            String channelId = "channel_id";
            CharSequence channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            // Configure the channel's settings
            channel.setDescription("Channel description");
            // Add additional channel configuration if needed

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        String channelId = "channel_id";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notif)
                .setContentTitle(notifTitle)
                .setContentText(notifContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notificationId = 1;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnEditSaveCategoryName:
                if (etCategoryName.getText().toString().isEmpty()) {
                    etCategoryName.setError("Category name is empty");
                    return;
                }

                if (etBudget.getText().toString().isEmpty()) {
                    etBudget.setError("Budget amount is empty");
                    return;
                }

                if (Double.parseDouble(etBudget.getText().toString()) <= 0) {
                    etBudget.setError("Budget amount cannot be less than or equal zero");
                    return;
                }

                CategoryOperations categoryOperations = new CategoryOperations(this);
                categoryOperations.open();

                BudgetOperations budgetOperations = new BudgetOperations(this);
                budgetOperations.open();


                if (categoryOperations.isCategoryNameDuplicate(etCategoryName.getText().toString(),
                        CategoryActivity.categoryID,budgetOperations.getBudgetIdByDate(BudgetActivity.getCurrentDate()))) {
                    etCategoryName.setError("Please choose another category name");
                    return;
                }

                if (Double.parseDouble(etBudget.getText().toString()) >
                        budgetOperations.getRemainingBudget(budgetOperations.getBudgetIdByDate(BudgetActivity.getCurrentDate()), CategoryActivity.categoryID)) {
                    Toast.makeText(this, "Category budget cannot exceed the overall remaining budget", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editingCategory) {
                    int categoryId = categoryOperations.updateCategory(CategoryActivity.categoryID, etCategoryName.getText().toString(),
                            Double.parseDouble(etBudget.getText().toString()));

                    if (categoryId != -1) {
                        // category added successfully
                        Toast.makeText(this, "category name updated", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to add category
                        Toast.makeText(this, "Failed to updated category name", Toast.LENGTH_SHORT).show();
                    }
                }
                categoryOperations.close();
                budgetOperations.close();

                editingCategory = !editingCategory;

                changeButtonIcon(btnEditSaveCategoryName, editingCategory
                        ? ContextCompat.getDrawable(this, R.drawable.savechanges)
                        : ContextCompat.getDrawable(this, R.drawable.edit));

                etCategoryName.setEnabled(editingCategory);
                break;
            case R.id.btnEditBudget:

                if (etCategoryName.getText().toString().isEmpty()) {
                    etCategoryName.setError("Category name is empty");
                    return;
                }

                if (etBudget.getText().toString().isEmpty()) {
                    etBudget.setError("Budget amount is empty");
                    return;
                }

                if (Double.parseDouble(etBudget.getText().toString()) <= 0) {
                    etBudget.setError("Budget amount cannot be less than or equal zero");
                    return;
                }

                if (Double.parseDouble(etBudget.getText().toString()) < Double.parseDouble(etExpenses.getText().toString())) {
                    etBudget.setError("Budget amount cannot be less than the category total expenses");
                    return;
                }

                CategoryOperations categoryOperations1 = new CategoryOperations(this);
                categoryOperations1.open();

                BudgetOperations budgetOperations1 = new BudgetOperations(this);
                budgetOperations1.open();

                if (categoryOperations1.isCategoryNameDuplicate(etCategoryName.getText().toString(),
                        CategoryActivity.categoryID,budgetOperations1.getBudgetIdByDate(BudgetActivity.getCurrentDate()))) {
                    etCategoryName.setError("Please choose another category name");
                    return;
                }

                if (Double.parseDouble(etBudget.getText().toString()) >
                        budgetOperations1.getRemainingBudget(budgetOperations1.getBudgetIdByDate(BudgetActivity.getCurrentDate()), CategoryActivity.categoryID)) {
                    Toast.makeText(this, "Category budget cannot exceed the overall remaining budget", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editingBudget) {
                    int categoryId = categoryOperations1.updateCategory(CategoryActivity.categoryID, etCategoryName.getText().toString(),
                            Double.parseDouble(etBudget.getText().toString()));

                    if (categoryId != -1) {
                        // category added successfully
                        Toast.makeText(this, "category budget updated", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to add category
                        Toast.makeText(this, "Failed to update category budget", Toast.LENGTH_SHORT).show();
                    }
                }
                categoryOperations1.close();
                budgetOperations1.close();


                editingBudget = !editingBudget;

                changeButtonIcon(btnEditCategoryBudget, editingBudget
                        ? ContextCompat.getDrawable(this, R.drawable.savechanges)
                        : ContextCompat.getDrawable(this, R.drawable.edit));

                etBudget.setEnabled(editingBudget);
                break;
            case R.id.btnDeleteCategory:
                // Create an AlertDialog.Builder instance
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Category");
                builder.setMessage("This category will be deleted permanently, are you sure you want to continue?"); // Set the dialog message

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CategoryOperations categoryOperations = new CategoryOperations(CategoryEditActivity.this);
                        categoryOperations.open();
                        categoryOperations.deleteCategory(CategoryActivity.categoryID);
                        categoryOperations.close();
                        startActivity(new Intent(CategoryEditActivity.this, CategoryActivity.class));
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.btnBackToCategory:
                if(ReportsActivity.isOnlyViewingExpenses) {
                    onPause();
                } else {
                    startActivity(new Intent(CategoryEditActivity.this, CategoryActivity.class));
                }
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        ReportsActivity.isOnlyViewingExpenses = false;
        finish();
    }
}