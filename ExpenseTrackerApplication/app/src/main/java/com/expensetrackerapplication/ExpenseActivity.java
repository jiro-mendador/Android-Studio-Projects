package com.expensetrackerapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.expensetrackerapplication.operations.BudgetOperations;
import com.expensetrackerapplication.operations.CategoryOperations;
import com.expensetrackerapplication.operations.ItemOperations;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout categoryButtonsListLayout;
    TextView tvNoCategory, tvItemQuantity;
    Button btnMinus, btnAdd;
    EditText etExpenseItemName, etExpenseItemAmount;
    ExtendedFloatingActionButton btnAddExpense;

    //variables
    ArrayList<ExtendedFloatingActionButton> catButtons;
    public static int categoryID, quantity;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_layout);

        init();
        setListeners();
        addCategorylists();
    }

    void init() {
        InputFilter[] textFilter = new InputFilter[1];
        textFilter[0] = new InputFilter.LengthFilter(15);

        categoryButtonsListLayout = (LinearLayout) findViewById(R.id.categoryExpenseListLayout);
        tvNoCategory = (TextView) findViewById(R.id.lvlTvNoCategory);
        btnMinus = (Button) findViewById(R.id.btnExpenseItemMinus);
        btnAdd = (Button) findViewById(R.id.btnExpenseItemPlus);
        tvItemQuantity = (TextView) findViewById(R.id.tvExpenseItemQuantity);
        etExpenseItemName = (EditText) findViewById(R.id.etExpenseItemName);
        etExpenseItemAmount = (EditText) findViewById(R.id.etAddExpense);
        btnAddExpense = (ExtendedFloatingActionButton) findViewById(R.id.btnAddExpense);

        quantity = 1;
        categoryID = 0;
        etExpenseItemName.setFilters(textFilter);
        categoryName = "";
    }

    void setListeners() {
        btnMinus.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnAddExpense.setOnClickListener(this);
    }

    void addCategorylists() {
        for (int i = categoryButtonsListLayout.getChildCount() - 1; i >= 0; i--) {
            View childView = categoryButtonsListLayout.getChildAt(i);
            if (!(childView instanceof TextView)) {
                categoryButtonsListLayout.removeViewAt(i);
            }
        }

        //access database to get all the category in this budget period
        CategoryOperations categoryOperations1 = new CategoryOperations(this);
        categoryOperations1.open();

        BudgetOperations budgetOperations1 = new BudgetOperations(this);
        budgetOperations1.open();

        int budgetId = budgetOperations1.getBudgetIdByDate(BudgetActivity.getCurrentDate());
        ArrayList<String> categoryNames = categoryOperations1.getCategoryName(budgetId);
        ArrayList<Integer> categoryPercentages = categoryOperations1.getCategoryPercentagesInBudget(budgetId);

        if (categoryNames.size() == 0) {
            tvNoCategory.setVisibility(View.VISIBLE);
        } else {
            tvNoCategory.setVisibility(View.GONE);
            catButtons = new ArrayList<>();
        }

        for (int i = 0; i < categoryNames.size(); i++) {
            View childLayout = getLayoutInflater().inflate(R.layout.category_buttons_layout, categoryButtonsListLayout, false);
            ExtendedFloatingActionButton btnCatName = (ExtendedFloatingActionButton) childLayout.findViewById(R.id.btnCategoryExpense);

            btnCatName.setText(categoryNames.get(i));
            catButtons.add(btnCatName);

            final int j = i;
            btnCatName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ExpenseActivity.this,"Expense will be added in "+categoryNames.get(j), Toast.LENGTH_SHORT).show();
                    categoryName = categoryNames.get(j);
                    CategoryOperations categoryOperations4 = new CategoryOperations(ExpenseActivity.this);
                    categoryOperations4.open();

                    categoryID = categoryOperations4.getCategoryIdByBudgetAndName(budgetId, categoryNames.get(j));
                    categoryOperations4.close();

                    changeCategoryButtonsColor(btnCatName);
                }
            });
            categoryButtonsListLayout.addView(childLayout);
        }
        budgetOperations1.close();
        categoryOperations1.close();
    }

    void changeCategoryButtonsColor(ExtendedFloatingActionButton btnPressed) {
        for (ExtendedFloatingActionButton i : catButtons) {
            i.setBackgroundColor(
                    i.equals(btnPressed) ? ContextCompat.getColor(this, R.color.lighter)
                            : ContextCompat.getColor(this, R.color.darkest));
            i.setTextColor(
                    i.equals(btnPressed) ? ContextCompat.getColor(this, R.color.darkest)
                            : ContextCompat.getColor(this, R.color.lighter));
        }
    }

    void setToDefault() {
        etExpenseItemName.setText("");
        etExpenseItemAmount.setText("");
        tvItemQuantity.setText("1");
        quantity = 1;
        categoryName = "";
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
            case R.id.btnExpenseItemMinus:
                quantity = quantity > 1 ? quantity - 1 : quantity;
                break;
            case R.id.btnExpenseItemPlus:
                quantity++;
                break;
            case R.id.btnAddExpense:

                if (categoryID == 0) {
                    Toast.makeText(this, "Choose a category above", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (etExpenseItemName.getText().toString().isEmpty()) {
                    etExpenseItemName.setError("Expense Item Name is empty");
                    return;
                }


                if (etExpenseItemAmount.getText().toString().isEmpty()) {
                    etExpenseItemAmount.setError("Expense Item Amount is empty");
                    return;
                }

                if (Double.parseDouble(etExpenseItemAmount.getText().toString()) <= 0) {
                    etExpenseItemAmount.setError("Put an expense amount greater than zero");
                    return;
                }

                ItemOperations itemOperations = new ItemOperations(this);
                itemOperations.open();

                CategoryOperations categoryOperations = new CategoryOperations(this);
                categoryOperations.open();

                //if the total amount + current price exceeds the budget
                if(itemOperations.isBudgetExceeded(categoryID,
                        Double.parseDouble(etExpenseItemAmount.getText().toString())*quantity,0)) {
                    etExpenseItemAmount.setError("Current expense amount will exceed the alloted budget for this category");
                    return;
                }

                long itemId = itemOperations.addItem(etExpenseItemName.getText().toString(),
                        itemOperations.getCurrentDateWithHoursMinSec(),Double.parseDouble(etExpenseItemAmount.getText().toString()),
                        quantity,categoryID);

                if (itemId != -1) {
                    // Budget added successfully
                    Toast.makeText(this, "New Expense Item added", Toast.LENGTH_SHORT).show();

                    //warn the user if the total expenses are about to exceed
                    float percent = categoryOperations.getCategoryExpensePercentage(categoryID);
                    if(percent >= 85 && percent <= 99) {
                        sendNotification(categoryName+" expenses about to exceed ",
                                "Total expenses takes "+categoryOperations.getCategoryExpensePercentage(categoryID)
                                        +"% of the category budget");
                    } else if(categoryOperations.getCategoryExpensePercentage(categoryID) == 100) {
                        sendNotification(categoryName+" expenses",
                                "already takes up the whole category budget");
                    }
                    categoryOperations.close();

                } else {
                    // Failed to add budget
                    Toast.makeText(this, "Failed to add new Expense Item", Toast.LENGTH_SHORT).show();
                }

                setToDefault();
                itemOperations.close();
                break;
        }
        tvItemQuantity.setText(String.valueOf(quantity));
    }
}