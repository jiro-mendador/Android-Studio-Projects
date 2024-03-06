package com.expensetrackerapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.expensetrackerapplication.operations.BudgetOperations;
import com.expensetrackerapplication.operations.CategoryOperations;
import com.expensetrackerapplication.operations.ItemOperations;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    ExtendedFloatingActionButton btnStartDate, btnEndDate;
    DatePickerDialog dateFilterStart, dateFilterEnd;
    LinearLayout category_list_layout;
    TextView tvNoCategory, tvTotalBudget, tvTotalExpenses;
    public static boolean isOnlyViewingExpenses;
    public static String categoryName;
    public static int categoryId;

    //CHARTS IMPORTANT VARIABLES
    PieChart overallExpensesChart;
    ArrayList<PieEntry> pieSpace;
    ArrayList<Integer> colors;
    //for dates
    final Calendar calendar = Calendar.getInstance();
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_reports_layout);

        init();
        setListeners();

        btnStartDate.setText(getCurrentMonthDates()[0]);
        btnEndDate.setText(getCurrentMonthDates()[1]);
        addCategorylists();
        setTopCategoriesAndOthers();
        startChart();
    }

    void init() {
        // Get current date
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        btnStartDate = (ExtendedFloatingActionButton) findViewById(R.id.btnStartDate);
        btnEndDate = (ExtendedFloatingActionButton) findViewById(R.id.btnEndDate);
        category_list_layout = findViewById(R.id.CategoryExpenseListLayout);
        tvNoCategory = findViewById(R.id.lvlTvNoCategories);
        tvTotalBudget = findViewById(R.id.totalBudget);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        isOnlyViewingExpenses = false;

        //chart
        overallExpensesChart = findViewById(R.id.overallReportsChart);
        pieSpace = new ArrayList<>();
        colors = new ArrayList<>();
        colors.add(Color.parseColor("#EA34AB"));
        colors.add(Color.parseColor("#4A7DF0"));
        colors.add(Color.parseColor("#61D37C"));
        colors.add(Color.parseColor("#FFDF72"));
        colors.add(Color.parseColor("#FD6B78"));
        colors.add(Color.parseColor("#191B27"));
    }

    void startChart() {
        PieDataSet pieDataSet = new PieDataSet(pieSpace, "Overall Expenses");
        PieData data = new PieData(pieDataSet);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(20f);

        Legend legend = overallExpensesChart.getLegend();
        legend.setEnabled(false);
        overallExpensesChart.setDescription(null);

        // Set hole color and size
        overallExpensesChart.setHoleColor(Color.parseColor("#292C2D"));

        overallExpensesChart.setData(data);
        overallExpensesChart.invalidate();
    }

    void setTopCategoriesAndOthers() {
        int counter = 0;
        CategoryOperations categoryOperations = new CategoryOperations(this);
        categoryOperations.open();
        ArrayList<CategoryOperations.Category> categories = categoryOperations.getTopCategoriesAndOthers(
                btnStartDate.getText().toString(), btnEndDate.getText().toString());

        // Iterate over the categories and print the category name and total expenses
        for (CategoryOperations.Category category : categories) {
            addValuesToList(category.categoryTotalExpenses,category.categoryName,colors.get(counter));
            counter++;
        }

        float others = (float) (categoryOperations.getTotalExpensesInRange(btnStartDate.getText().toString(),btnEndDate.getText().toString()) - categoryOperations.totalExpensesOfTop5Categories);
        if(others > 0) {
            addValuesToList(others,"Others",colors.get(counter));
        }

        categoryOperations.close();
    }
    void addValuesToList(float amount, String catName, int color) {
        pieSpace.add(new PieEntry(amount,catName,color));
    }

    void addCategorylists() {
        for (int i = category_list_layout.getChildCount() - 1; i >= 0; i--) {
            View childView = category_list_layout.getChildAt(i);
            if (!(childView instanceof TextView)) {
                category_list_layout.removeViewAt(i);
            }
        }

        //access database to get all the category in this budget period
        CategoryOperations categoryOperations = new CategoryOperations(this);
        categoryOperations.open();

        String start = btnStartDate.getText().toString();
        String end = btnEndDate.getText().toString();
        String expense = String.valueOf(categoryOperations.getTotalExpensesInRange(start,end));
        String budget = String.valueOf(categoryOperations.getTotalBudgetInRange(start,end));

        categoryOperations.getCategoriesInRange(start,end); // to set the categories
        tvTotalExpenses.setText(expense);
        tvTotalBudget.setText(budget);

        ArrayList<String> categoryNames = categoryOperations.categoryNames;
        ArrayList<Integer> categoriesID = categoryOperations.categoriesID;

        if (categoryNames.size() == 0) {
            tvNoCategory.setVisibility(View.VISIBLE);
        } else {
            tvNoCategory.setVisibility(View.GONE);
        }

        for (int i = 0; i < categoryNames.size(); i++) {
            View childLayout = getLayoutInflater().inflate(R.layout.category_buttons_layout, category_list_layout, false);
            ExtendedFloatingActionButton btnCatName = (ExtendedFloatingActionButton) childLayout.findViewById(R.id.btnCategoryExpense);

            btnCatName.setText(categoryNames.get(i));

            final int j = i;
            btnCatName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ReportsActivity.this, "Viewing history in " + categoryNames.get(j), Toast.LENGTH_SHORT).show();
                    isOnlyViewingExpenses = true;
                    categoryName = categoryNames.get(j);
                    categoryId = categoriesID.get(j);
                    startActivity(new Intent(ReportsActivity.this, CategoryEditActivity.class));
                }
            });
            category_list_layout.addView(childLayout);
        }
        categoryOperations.close();
    }

    public String[] getCurrentMonthDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the month
        Date startDate = calendar.getTime(); // Get the start date

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // Set to the last day of the month
        Date endDate = calendar.getTime(); // Get the end date

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startDateString = dateFormat.format(startDate); // Format start date as string
        String endDateString = dateFormat.format(endDate); // Format end date as string

        return new String[]{startDateString, endDateString};
    }


    void setListeners() {
        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnStartDate:
                // Create date picker dialog
                dateFilterStart = new DatePickerDialog(ReportsActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, // Dialog style
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Format day and month with leading zeros if necessary
                                String day = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                                String month = ((monthOfYear + 1) < 10) ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);

                                // Do something with the selected date
                                // For example, display it in a TextView
                                String selectedDate = year + "-" + month + "-" + day;
                                btnStartDate.setText(selectedDate);
                                addCategorylists();

                                pieSpace.clear();
                                setTopCategoriesAndOthers();
                                startChart();
                            }
                        }, year, month, day);
                dateFilterStart.show();

                // Customize date picker dialog
                dateFilterStart.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Set transparent background
                dateFilterStart.getDatePicker().setBackgroundColor(ContextCompat.getColor(ReportsActivity.this, R.color.lighter)); // Set background color
                dateFilterStart.show();
                break;

            case R.id.btnEndDate:
                // Create date picker dialog
                dateFilterEnd = new DatePickerDialog(ReportsActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, // Dialog style
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Format day and month with leading zeros if necessary
                                String day = (dayOfMonth < 10) ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                                String month = ((monthOfYear + 1) < 10) ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);

                                // Do something with the selected date
                                // For example, display it in a TextView
                                String selectedDate = year + "-" + month + "-" + day;
                                btnEndDate.setText(selectedDate);
                                addCategorylists();

                                pieSpace.clear();
                                setTopCategoriesAndOthers();
                                startChart();
                            }
                        }, year, month, day);
                dateFilterEnd.show();

                // Customize date picker dialog
                dateFilterEnd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Set transparent background
                dateFilterEnd.getDatePicker().setBackgroundColor(ContextCompat.getColor(ReportsActivity.this, R.color.lighter)); // Set background color
                dateFilterEnd.show();
                break;
        }

    }
}