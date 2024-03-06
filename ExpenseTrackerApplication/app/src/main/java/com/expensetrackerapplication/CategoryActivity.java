package com.expensetrackerapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.expensetrackerapplication.operations.BudgetOperations;
import com.expensetrackerapplication.operations.CategoryOperations;
import com.expensetrackerapplication.operations.DatabaseHelper;
import com.expensetrackerapplication.operations.ItemOperations;
import com.expensetrackerapplication.operations.SavingsOperations;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Date;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvBudgetAlreadySet, tvRemBudgetForThisPeriod;
    ExtendedFloatingActionButton btnAddCategory;
    EditText etCategoryBudgetAmount, etCategoryName;
    LinearLayout categoryListLayout;

    //for CategoryEditClass
    static String categoryName;
    static double categoryBudget;
    static double totalItemExpenses;
    static ArrayList<String> itemNames;
    static ArrayList<String> itemDateOfExpenses;
    static ArrayList<Double> itemPrices;
    static ArrayList<Integer> itemQuantities;
    static ArrayList<Integer> itemExpensesID;
    static int categoryID;
    boolean usingSavings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_layout);

        init();
        setListeners();

        setRemainingBudgetTextView();
        addCategorylists();
    }

    void init() {
        InputFilter[] textFilter = new InputFilter[1];
        textFilter[0] = new InputFilter.LengthFilter(17);

        tvBudgetAlreadySet = (TextView) findViewById(R.id.lblTvRemBud);
        tvRemBudgetForThisPeriod = (TextView) findViewById(R.id.lblRemBud);
        btnAddCategory = (ExtendedFloatingActionButton) findViewById(R.id.btnAddCategory);
        etCategoryBudgetAmount = (EditText) findViewById(R.id.etAddCategorybudget);
        etCategoryName = (EditText) findViewById(R.id.etAddCategoryName);
        etCategoryName.setFilters(textFilter);

        categoryListLayout = (LinearLayout) findViewById(R.id.layoutCategoryList);
        itemNames = new ArrayList<>();
        itemPrices = new ArrayList<>();
        itemQuantities = new ArrayList<>();
        itemDateOfExpenses = new ArrayList<>();
        itemExpensesID = new ArrayList<>();
        categoryID = 0;
        usingSavings = false;
    }

    void setListeners() {
        btnAddCategory.setOnClickListener(this);
    }

    void setRemainingBudgetTextView() {
        BudgetOperations budgetOperations1 = new BudgetOperations(this);
        // Open the database connection
        budgetOperations1.open();
        //to set the value of the textViews in the upper right corner
        if (!budgetOperations1.isBudgetSetForDate(BudgetActivity.getCurrentDate())) {
            tvBudgetAlreadySet.setText("There's no budget set for this period...");
            tvRemBudgetForThisPeriod.setVisibility(View.GONE);
        } else {
            tvRemBudgetForThisPeriod.setText("P " + String.valueOf(
                    budgetOperations1.getRemainingBudget(
                            budgetOperations1.getBudgetIdByDate(BudgetActivity.getCurrentDate()), 0)));
        }
        budgetOperations1.close();
    }

    void addCategorylists() {
        categoryListLayout.removeAllViews();
        //access database to get all the category in this budget period
        CategoryOperations categoryOperations1 = new CategoryOperations(this);
        categoryOperations1.open();

        BudgetOperations budgetOperations2 = new BudgetOperations(this);
        budgetOperations2.open();


        int budgetId = budgetOperations2.getBudgetIdByDate(BudgetActivity.getCurrentDate());
        ArrayList<String> categoryNames = categoryOperations1.getCategoryName(budgetId);
        ArrayList<Integer> categoryPercentages = categoryOperations1.getCategoryPercentagesInBudget(budgetId);


        for (int i = 0; i < categoryNames.size(); i++) {
            View childLayout = getLayoutInflater().inflate(R.layout.category_list_with_bar_layout, categoryListLayout, false);
            TextView tvCategoryName = childLayout.findViewById(R.id.tvCatListCatName);
            ProgressBar pbPercentBar = childLayout.findViewById(R.id.categoryPercentage);

            tvCategoryName.setText(categoryNames.get(i));
            pbPercentBar.setProgress(categoryPercentages.get(i));

            final int j = i;
            childLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CategoryOperations categoryOperations4 = new CategoryOperations(CategoryActivity.this);
                    categoryOperations4.open();

                    ItemOperations itemOperations = new ItemOperations(CategoryActivity.this);
                    itemOperations.open();

                    categoryID = categoryOperations4.getCategoryIdByBudgetAndName(budgetId, categoryNames.get(j));

                    categoryName = categoryNames.get(j);
                    categoryBudget = categoryOperations4.getCategoryBudget(categoryID);
                    totalItemExpenses = itemOperations.getTotalItemExpensesForCategory(categoryID);

                    //loops for all item expenses for this category
                    for (int i = 0; i < itemOperations.getItemNames(categoryID).size(); i++) {
                        itemNames.add(itemOperations.getItemNames(categoryID).get(i));
                        itemDateOfExpenses.add(itemOperations.getItemExpenseDates(categoryID).get(i));
                        itemQuantities.add(itemOperations.getItemQuantities(categoryID).get(i));
                        itemPrices.add(itemOperations.getItemIndividualPrices(categoryID).get(i));
                    }

                    itemOperations.close();
                    categoryOperations4.close();

                    Intent openActivity = new Intent(CategoryActivity.this, CategoryEditActivity.class);
                    startActivity(openActivity);
                }
            });
            categoryListLayout.addView(childLayout);
        }
        budgetOperations2.close();
        categoryOperations1.close();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnAddCategory:
                double savingsUpdatedAmount = 0;
                BudgetOperations budgetOperations3 = new BudgetOperations(this);
                budgetOperations3.open();

                SavingsOperations savingsOperations = new SavingsOperations(this);
                savingsOperations.open();

                //condition to know if there's already budget set on this budget period
                if (!budgetOperations3.isBudgetSetForDate(BudgetActivity.getCurrentDate())) {
                    Toast.makeText(this, "Set an overall budget first on this budget period", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etCategoryBudgetAmount.getText().toString().isEmpty()) {
                    etCategoryBudgetAmount.setError("Add a category budget");
                    return;
                }

                if (etCategoryName.getText().toString().isEmpty()) {
                    etCategoryName.setError("Add a category name");
                    return;
                }
                //check if remaining budget is sufficient in current amount
                if (Double.parseDouble(etCategoryBudgetAmount.getText().toString()) >
                        budgetOperations3.getRemainingBudget(budgetOperations3.getBudgetIdByDate(BudgetActivity.getCurrentDate()), 0)) {

                    //check if savings amount is sufficient in current amount
                    if (savingsOperations.getSavingsAmount(savingsOperations.getSavingsId()) > 0 &&
                            savingsOperations.getSavingsAmount(savingsOperations.getSavingsId()) >= Double.parseDouble(etCategoryBudgetAmount.getText().toString())) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Insufficient Budget");
                        builder.setMessage("Budget will be adjusted using the savings, do you want to continue?"); // Set the dialog message

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                CategoryOperations categoryOperations2 = new CategoryOperations(CategoryActivity.this);
                                categoryOperations2.open();

                                if (categoryOperations2.isCategoryNameDuplicate(etCategoryName.getText().toString(), 0,
                                        budgetOperations3.getBudgetIdByDate(BudgetActivity.getCurrentDate()))) {
                                    etCategoryName.setError("Please choose another category name");
                                    return;
                                }

                                useSavings(Double.parseDouble(etCategoryBudgetAmount.getText().toString()));
                                long categoryId = categoryOperations2.addCategory(etCategoryName.getText().toString(),
                                        Double.parseDouble(etCategoryBudgetAmount.getText().toString()),
                                        budgetOperations3.getBudgetIdByDate(BudgetActivity.getCurrentDate()));

                                if (categoryId != -1) {
                                    // category added successfully
                                    if (usingSavings) {
                                        useSavings(Double.parseDouble(etCategoryBudgetAmount.getText().toString()));
                                        Toast.makeText(CategoryActivity.this, "New category added and savings updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CategoryActivity.this, "New category added", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Failed to add category
                                    Toast.makeText(CategoryActivity.this, "Failed to add new category", Toast.LENGTH_SHORT).show();
                                }


                                //to see the updated remaining budget amount
                                setRemainingBudgetTextView();

                                //add the new categoriess
                                addCategorylists();

                                // Close the database connection
                                budgetOperations3.close();
                                categoryOperations2.close();

                                etCategoryBudgetAmount.setText("");
                                etCategoryName.setText("");

                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                return;
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return;

                    } else {
                        Toast.makeText(this, "Category budget cannot exceed the overall remaining budget", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                CategoryOperations categoryOperations2 = new CategoryOperations(this);
                categoryOperations2.open();

                if (categoryOperations2.isCategoryNameDuplicate(etCategoryName.getText().toString(), 0,
                        budgetOperations3.getBudgetIdByDate(BudgetActivity.getCurrentDate()))) {
                    etCategoryName.setError("Please choose another category name");
                    return;
                }

                long categoryId = categoryOperations2.addCategory(etCategoryName.getText().toString(),
                        Double.parseDouble(etCategoryBudgetAmount.getText().toString()),
                        budgetOperations3.getBudgetIdByDate(BudgetActivity.getCurrentDate()));

                if (categoryId != -1) {
                    // category added successfully
                    if (usingSavings) {
                        useSavings(Double.parseDouble(etCategoryBudgetAmount.getText().toString()));
                        Toast.makeText(this, "New category added and savings updated :" + categoryId, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "New category added", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Failed to add category
                    Toast.makeText(this, "Failed to add new category", Toast.LENGTH_SHORT).show();
                }


                //to see the updated remaining budget amount
                setRemainingBudgetTextView();

                //add the new categoriess
                addCategorylists();

                // Close the database connection
                budgetOperations3.close();
                categoryOperations2.close();

                etCategoryBudgetAmount.setText("");
                etCategoryName.setText("");
                break;
        }
    }

    void useSavings(double currentCategoryBudget) {
        BudgetOperations budgetOperations = new BudgetOperations(this);
        budgetOperations.open();

        SavingsOperations savingsOperations = new SavingsOperations(this);
        savingsOperations.open();

        int budgetId = budgetOperations.getBudgetIdByDate(BudgetActivity.getCurrentDate());
        String start = budgetOperations.getBudgetStartDate(budgetId);
        String type = budgetOperations.getBudgetType(budgetId);
        String end = BudgetActivity.getBudgetEndDate(type,start);

        double currentSavingsAmount = savingsOperations.getSavingsAmount(savingsOperations.getSavingsId());
        double currentBudgetAmount = budgetOperations.getBudgetSetForThisPeriod(BudgetActivity.getCurrentDate());

        double remainingBudget = budgetOperations.getRemainingBudget(budgetOperations.getBudgetIdByDate(BudgetActivity.getCurrentDate()), 0);

        double savingsUpdatedAmount = currentSavingsAmount - (currentCategoryBudget - remainingBudget);
        double budgetUpdatedAmount = currentBudgetAmount + (currentCategoryBudget - remainingBudget);

        budgetOperations.updateBudget(budgetId,type,start,end,budgetUpdatedAmount);
        savingsOperations.updateSavings(savingsOperations.getSavingsId(), savingsUpdatedAmount, BudgetActivity.getCurrentDate());

        savingsOperations.close();
        budgetOperations.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}