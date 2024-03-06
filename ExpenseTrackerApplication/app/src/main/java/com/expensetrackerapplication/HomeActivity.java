package com.expensetrackerapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.expensetrackerapplication.operations.BudgetOperations;
import com.expensetrackerapplication.operations.CategoryOperations;
import com.expensetrackerapplication.operations.DatabaseHelper;
import com.expensetrackerapplication.operations.ItemOperations;
import com.expensetrackerapplication.operations.SavingsOperations;
import com.expensetrackerapplication.operations.UserOperation;
import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton btnCategory, btnBudget, btnExpense, btnReports;
    TextView tvUserFirstLastName, tvTotalExpenses,
            tvTotalRemainingBudget, tvTotalSavings;
    Intent openActivity;
    CircleImageView userImage;
    LinearLayout history_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper dbHelper = new DatabaseHelper(HomeActivity.this); // to create the database at the start of the application
        dbHelper.getWritableDatabase();
        dbHelper.close();

        // to know if the get started will be displayed or the homescreen
        UserOperation userOperation = new UserOperation(this);
        userOperation.open();

        if (userOperation.getUserId() == -1 || !userOperation.isAUserStatusActive()) {
            setContentView(R.layout.get_started_layout);
            ExtendedFloatingActionButton btnStart = findViewById(R.id.btnGetStarted);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this,SignInRegisterActivity.class));
                    userOperation.close();
                    finish();
                }
            });
        } else {
            //display the homescreen instead
            setContentView(R.layout.home_layout);
            init();
            setListeners();

            userOperation.setCurrentUserInfo();
            tvUserFirstLastName.setText(UserOperation.firstName+" "+UserOperation.lastName);

            Stetho.initializeWithDefaults(this); //for debugging db
            setOverAllTotals();
            userOperation.close();
        }
    }

    void init() {
        btnCategory = (FloatingActionButton) findViewById(R.id.btnCategory);
        btnBudget = (FloatingActionButton) findViewById(R.id.btnBudget);
        btnExpense = (FloatingActionButton) findViewById(R.id.btnAddExpense);
        btnReports = (FloatingActionButton) findViewById(R.id.btnReports);
        userImage = (CircleImageView) findViewById(R.id.ivUserImg);
        tvTotalExpenses = (TextView) findViewById(R.id.totalExpenses);
        tvUserFirstLastName = (TextView) findViewById(R.id.tvUserName);
        tvTotalRemainingBudget = (TextView) findViewById(R.id.RemainingBudget);
        tvTotalSavings = (TextView) findViewById(R.id.tvTotalSavings);
        history_layout = (LinearLayout) findViewById(R.id.actitvity_history_layout);
    }

    void setListeners() {
        btnCategory.setOnClickListener(this);
        btnBudget.setOnClickListener(this);
        btnExpense.setOnClickListener(this);
        btnReports.setOnClickListener(this);
        userImage.setOnClickListener(this);
    }

    void isBudgetPeriodEnded() {
        BudgetOperations forThread = new BudgetOperations(HomeActivity.this);
        forThread.open();
        if (forThread.checkBudgetPeriodEnd(forThread.getPreviousBudgetID(BudgetActivity.getCurrentDate()))) {
            //Toast.makeText(this, "true check", Toast.LENGTH_SHORT).show();
            addUpdateSavings();
        }
        forThread.close();
    }
    private void addUpdateSavings() {
        //Toast.makeText(this, "sa update na", Toast.LENGTH_SHORT).show();
        SavingsOperations savingsOperations = new SavingsOperations(HomeActivity.this);
        savingsOperations.open();

        BudgetOperations budgetOperations = new BudgetOperations(HomeActivity.this);
        budgetOperations.open();

        CategoryOperations categoryOperations = new CategoryOperations(HomeActivity.this);
        categoryOperations.open();

        int prevBudgetID = budgetOperations.getPreviousBudgetID(BudgetActivity.getCurrentDate());
        int currentBudgetId = budgetOperations.getBudgetIdByDate(BudgetActivity.getCurrentDate());
        int savingsId = savingsOperations.getSavingsId();

        double savings = categoryOperations.getOverallRemainingBudget(prevBudgetID);

        if (savingsId == -1) {
            //Toast.makeText(this, "pasok sa if", Toast.LENGTH_SHORT).show();
            savingsOperations.addSavings(savings, BudgetActivity.getCurrentDate());
            Toast.makeText(HomeActivity.this ,"Remaining budget from previous " +
                    "budget period has been added to your savings", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "pasok sa else", Toast.LENGTH_SHORT).show();
            if (!savingsOperations.getSavingsDateUpdated(savingsId).equals(BudgetActivity.getCurrentDate())
                    && currentBudgetId == -1) {
                //Toast.makeText(this, "pasok sa if else", Toast.LENGTH_SHORT).show();
                Toast.makeText(HomeActivity.this ,"Remaining budget from previous " +
                        "budget period has been added to your savings", Toast.LENGTH_SHORT).show();
                savings += savingsOperations.getSavingsAmount(savingsId);
                savingsOperations.updateSavings(savingsId, savings, BudgetActivity.getCurrentDate());
            }
        }

        savingsOperations.close();
        budgetOperations.close();
        categoryOperations.close();
    }

    void setOverAllTotals() {
        //check the budget period end
        isBudgetPeriodEnded();

        //get histories of expenses
        getAllExpenses();

        ItemOperations itemOperations = new ItemOperations(HomeActivity.this);
        itemOperations.open();

        BudgetOperations budgetOperations = new BudgetOperations(HomeActivity.this);
        budgetOperations.open();

        CategoryOperations categoryOperations = new CategoryOperations(HomeActivity.this);
        categoryOperations.open();

        SavingsOperations savingsOperations = new SavingsOperations(HomeActivity.this);
        savingsOperations.open();

        int budgetID = budgetOperations.getBudgetIdByDate(BudgetActivity.getCurrentDate());
        tvTotalExpenses.setText("P " + String.valueOf(itemOperations.getOverallExpenses(budgetID)));
        tvTotalRemainingBudget.setText("P " + String.valueOf(categoryOperations.getOverallRemainingBudget(budgetID)));
        tvTotalSavings.setText("P " + String.valueOf(savingsOperations.getAllSavings()));

        savingsOperations.close();
        categoryOperations.close();
        itemOperations.close();
        budgetOperations.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCategory:
                openActivity = new Intent(HomeActivity.this, CategoryActivity.class);
                break;
            case R.id.btnBudget:
                openActivity = new Intent(HomeActivity.this, BudgetActivity.class);
                break;
            case R.id.btnAddExpense:
                openActivity = new Intent(HomeActivity.this, ExpenseActivity.class);
                break;
            case R.id.btnReports:
                openActivity = new Intent(HomeActivity.this, ReportsActivity.class);
                break;
            case R.id.ivUserImg:
                openActivity = new Intent(HomeActivity.this, UserActivity.class);
                startActivity(openActivity);
                finish();
                openActivity = new Intent(HomeActivity.this, HomeActivity.class);
                return;
        }
        startActivity(openActivity);
    }
    void getAllExpenses() {
        ItemOperations itemOperations = new ItemOperations(HomeActivity.this);
        itemOperations.open();

        BudgetOperations budgetOperations = new BudgetOperations(HomeActivity.this);
        budgetOperations.open();

        itemOperations.getAllExpensesInformation(budgetOperations.getBudgetIdByDate(BudgetActivity.getCurrentDate())); // to populate the arrayLists in ItemOperations class
        history_layout.removeAllViews();

        for (int i = 0; i < itemOperations.allExpensesCategories.size(); i++) {
            View childLayout = getLayoutInflater().inflate(R.layout.expenses_history_layout, history_layout, false);

            TextView tvCategoryName = childLayout.findViewById(R.id.tvHistoryCategoryName);
            EditText etExpenseItemName = childLayout.findViewById(R.id.tvActHistoryItemName);
            EditText etExpenseItemQuantity = childLayout.findViewById(R.id.tvActHistoryItemQuantity);
            TextView tvExpenseItemDate = childLayout.findViewById(R.id.tvActHistoryDate);
            EditText etExpenseItemPrice = childLayout.findViewById(R.id.tvActHistoryAmount);
            Button btnEditExpensesInformation = childLayout.findViewById(R.id.btnEditExpenseItem);
            btnEditExpensesInformation.setVisibility(View.INVISIBLE);

            tvCategoryName.setText(itemOperations.allExpensesCategories.get(i));
            etExpenseItemName.setText(itemOperations.allExpensesItems.get(i));
            etExpenseItemQuantity.setText(String.valueOf(itemOperations.allExpensesQuantities.get(i)));
            tvExpenseItemDate.setText(itemOperations.allExpensesDates.get(i));
            etExpenseItemPrice.setText(String.valueOf(itemOperations.allExpensesPrices.get(i)));

            history_layout.addView(childLayout);
        }
        itemOperations.close();
        budgetOperations.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserOperation userOperation = new UserOperation(this);
        userOperation.open();
        if(userOperation.getUserId() != -1 && userOperation.isAUserStatusActive()) {
            setOverAllTotals();
        }
        userOperation.close();
    }
}