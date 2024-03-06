package com.expensetrackerapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.expensetrackerapplication.operations.BudgetOperations;
import com.expensetrackerapplication.operations.DatabaseHelper;
import com.expensetrackerapplication.operations.SavingsOperations;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BudgetActivity extends AppCompatActivity implements View.OnClickListener {

    ExtendedFloatingActionButton[] btnBudgetPeriodType;
    ExtendedFloatingActionButton btnAddBudget;
    Button btnEditBudgetSet, btnSaveBudgetChanges;
    EditText etBudgetAmount, etBudgetSetAmount;
    RelativeLayout budgetSetlayout;
    TextView currentSavings;

    //variables
    String budgetType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget_layout);

        init();
        setListeners();
        setBudgetForThisPeriod();

        SavingsOperations savingsOperations = new SavingsOperations(this);
        savingsOperations.open();
        currentSavings.setText("+ P "+savingsOperations.getSavingsAmount(savingsOperations.getSavingsId())+" Savings");
        savingsOperations.close();
    }
    void init() {
        btnBudgetPeriodType = new ExtendedFloatingActionButton[3];
        btnBudgetPeriodType[0] = (ExtendedFloatingActionButton) findViewById(R.id.btnWeeklyBudget);
        btnBudgetPeriodType[1] = (ExtendedFloatingActionButton) findViewById(R.id.btnMonthlyBudget);
        btnBudgetPeriodType[2] = (ExtendedFloatingActionButton) findViewById(R.id.btnYearlyBudget);
        btnAddBudget = (ExtendedFloatingActionButton) findViewById(R.id.btnAddBudget);
        etBudgetAmount = (EditText) findViewById(R.id.etAddBudget);
        budgetSetlayout = (RelativeLayout) findViewById(R.id.budgetBtnsLayout);
        etBudgetSetAmount = (EditText) findViewById(R.id.etBudgetSet);
        btnEditBudgetSet = (Button) findViewById(R.id.btnEditBudget);
        btnSaveBudgetChanges = (Button) findViewById(R.id.btnSaveAllChanges);
        currentSavings = findViewById(R.id.lblTvSavings);

        //variables initialization
        budgetType = "";

    }

    void setListeners() {
        btnBudgetPeriodType[0].setOnClickListener(this);
        btnBudgetPeriodType[1].setOnClickListener(this);
        btnBudgetPeriodType[2].setOnClickListener(this);
        btnAddBudget.setOnClickListener(this);
        btnEditBudgetSet.setOnClickListener(this);
        btnSaveBudgetChanges.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnWeeklyBudget:
                changeButtonColor(btnBudgetPeriodType[0]);
                budgetType = "weekly";
                break;
            case R.id.btnMonthlyBudget:
                changeButtonColor(btnBudgetPeriodType[1]);
                budgetType = "monthly";
                break;
            case R.id.btnYearlyBudget:
                changeButtonColor(btnBudgetPeriodType[2]);
                budgetType = "yearly";
                break;
            case R.id.btnAddBudget:

                if (budgetType.isEmpty()) {
                    Toast.makeText(this, "Choose a budget type for this period above", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etBudgetAmount.getText().toString().isEmpty()) {
                    etBudgetAmount.setError("Budget amount is empty");
                    return;
                }

                if (Double.parseDouble(etBudgetAmount.getText().toString()) <= 0) {
                    etBudgetAmount.setError("Set an amount greater than zero");
                    return;
                }

                BudgetOperations budgetOperations2 = new BudgetOperations(this);
                // Open the database connection
                budgetOperations2.open();

                //condition to know if there's already budget set on this budget period
                if (budgetOperations2.isBudgetSetForDate(getCurrentDate())) {
                    Toast.makeText(this, "A budget has already been set for this budget period", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add a new budget
                long addBudgetId = budgetOperations2.addBudget(budgetType, getCurrentDate(), getBudgetEndDate(budgetType, getCurrentDate()),
                        Double.parseDouble(etBudgetAmount.getText().toString()));

                if (addBudgetId != -1) {
                    // Budget added successfully
                    Toast.makeText(this, "New budget added", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed to add budget
                    Toast.makeText(this, "Failed to add new budget", Toast.LENGTH_SHORT).show();
                }

                setBudgetForThisPeriod();

                // Close the database connection
                budgetOperations2.close();
                setToDefault();
                break;
            case R.id.btnEditBudget:
                etBudgetSetAmount.setEnabled(true);
                break;
            case R.id.btnSaveAllChanges:
                if(!etBudgetSetAmount.isEnabled()) {
                    return;
                }

                if(budgetType.isEmpty()) {
                    Toast.makeText(this, "Choose an updated budget type for this period above", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etBudgetSetAmount.getText().toString().isEmpty()) {
                    etBudgetSetAmount.setError("Set an updated budget amount");
                    return;
                }

                if (Double.parseDouble(etBudgetSetAmount.getText().toString()) <= 0) {
                    etBudgetSetAmount.setError("Set an updated amount greater than zero");
                    return;
                }

                BudgetOperations budgetOperations3 = new BudgetOperations(this);
                budgetOperations3.open();

                if (!budgetOperations3.isUpdatedAmountValid(Double.parseDouble(etBudgetSetAmount.getText().toString()),
                        budgetOperations3.getBudgetIdByDate(getCurrentDate()))) {
                    etBudgetSetAmount.setError("Updated amount conflicts with your previous expenses");
                    return;
                }

                long updateBudgetId = budgetOperations3.updateBudget(
                        budgetOperations3.getBudgetIdByDate(getCurrentDate()),
                        budgetType,
                        budgetOperations3.getBudgetStartDate(budgetOperations3.getBudgetIdByDate(getCurrentDate())),
                        getBudgetEndDate(budgetType,budgetOperations3.getBudgetStartDate(budgetOperations3.getBudgetIdByDate(getCurrentDate()))),
                        Double.parseDouble(etBudgetSetAmount.getText().toString())
                );
                if (updateBudgetId != -1) {
                    // Budget added successfully
                    Toast.makeText(this, "budget updated", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed to add budget
                    Toast.makeText(this, "Failed to update budget", Toast.LENGTH_SHORT).show();
                }

                etBudgetSetAmount.setEnabled(false);
                setBudgetForThisPeriod();
                budgetOperations3.close();
                break;
        }

    }

    void setBudgetForThisPeriod() {
        BudgetOperations budgetOperations1 = new BudgetOperations(this);
        // Open the database connection
        budgetOperations1.open();
        //condition to know if there's already budget set on this budget period
        if (budgetOperations1.isBudgetSetForDate(getCurrentDate())) {
            budgetSetlayout.setVisibility(View.VISIBLE);
            etBudgetSetAmount.setText(String.valueOf(budgetOperations1.getBudgetSetForThisPeriod(getCurrentDate())));
        } else {
            budgetSetlayout.setVisibility(View.GONE);
        }
        budgetOperations1.close();
    }
    void changeButtonColor(ExtendedFloatingActionButton btnPressed) {
        for (ExtendedFloatingActionButton i : btnBudgetPeriodType) {
            i.setBackgroundColor(
                    i.equals(btnPressed) ? ContextCompat.getColor(this, R.color.darkest)
                            : ContextCompat.getColor(this, R.color.darker));
        }
    }

    void setToDefault() {
        budgetType = "";
        etBudgetAmount.setText("");
        for (ExtendedFloatingActionButton i : btnBudgetPeriodType) {
            i.setBackgroundColor(ContextCompat.getColor(this, R.color.darker));
        }
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    public static String getBudgetEndDate(String budgetType, String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        try {
            //String currentDate = getCurrentDate();
            Date date = dateFormat.parse(currentDate);
            calendar.setTime(date);

            if (budgetType.equalsIgnoreCase("weekly")) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            } else if (budgetType.equalsIgnoreCase("monthly")) {
                calendar.add(Calendar.MONTH, 1);
            } else if (budgetType.equalsIgnoreCase("yearly")) {
                calendar.add(Calendar.YEAR, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateFormat.format(calendar.getTime());
    }
}