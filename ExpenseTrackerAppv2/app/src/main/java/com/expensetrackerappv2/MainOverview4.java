package com.expensetrackerappv2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class MainOverview4 extends AppCompatActivity {

    EditText categoryName, categoryExpenses, categoryPieColor;
    Button btnAddCategory, btnGetColors;

    //CHARTS IMPORTANT VARIABLES
    PieChart overallChart3;
    List<PieEntry> pieSpace;
    ArrayList<Integer> colors;

    //dynamic addings
    LinearLayout dynamicLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_overview4);
        init();

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isValidHexCode(categoryPieColor.getText().toString())
                        || categoryName.getText().toString().isEmpty()
                        || categoryExpenses.getText().toString().isEmpty()) {
                    return;
                }

                addValuesToList();
                startChart();

                dynamicLayout.removeAllViews();
                getPieChartValues();
            }
        });
    }

    void init() {
        categoryName = (EditText) findViewById(R.id.etCategory1);
        categoryExpenses = (EditText) findViewById(R.id.etExpenses1);
        categoryPieColor = (EditText) findViewById(R.id.etColor1);
        btnAddCategory = (Button) findViewById(R.id.btnAdd1);
        btnGetColors = (Button) findViewById(R.id.btnGetColors);
        overallChart3 = (PieChart) findViewById(R.id.overallChart3);
        pieSpace = new ArrayList<>();
        colors = new ArrayList<>();

        dynamicLayout = (LinearLayout) findViewById(R.id.dynamics_container);
    }

    void startChart() {
        PieDataSet pieDataSet = new PieDataSet(pieSpace, "Overall Expenses");
        PieData data = new PieData(pieDataSet);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(10f);

        Legend legend = overallChart3.getLegend();
        legend.setEnabled(false);
        overallChart3.setDescription(null);

        overallChart3.setData(data);
        overallChart3.invalidate();
    }

    void addValuesToList() {
        pieSpace.add(new PieEntry(Float.parseFloat(categoryExpenses.getText().toString()),
                categoryName.getText().toString(), categoryPieColor.getText().toString()));
        colors.add(Color.parseColor(categoryPieColor.getText().toString()));
    }

    void getPieChartValues() {
        // Get the PieDataSet object from the chart
        PieDataSet pieDataSet = (PieDataSet) overallChart3.getData().getDataSet();
        float totVal = 0;

        for (int i = 0; i < pieDataSet.getEntryCount(); i++) {
            PieEntry entry = pieDataSet.getEntryForIndex(i);
            //get category value in chart
            totVal += entry.getValue();
        }

        for (int i = 0; i < pieDataSet.getEntryCount(); i++) {
            PieEntry entry = pieDataSet.getEntryForIndex(i);

            // Get the background hex color of the entry
            String hex = String.valueOf(entry.getData());

            //get category name
            String category = entry.getLabel();

            //get category value in chart
            float value = entry.getValue();

            addViewsDynamically(category, String.valueOf(value), (value / totVal) * 100, hex);
        }
    }

    void addViewsDynamically(String catName, String catExp, double catPercent, String color) {
        View view = getLayoutInflater().inflate(R.layout.dynamic_base_layout, null);

        // Access the TextView and ProgressBar elements of the added view
        TextView categoryTextView = view.findViewById(R.id.category);
        TextView expensesTextView = view.findViewById(R.id.categoryExpenses);
        TextView percentTextView = view.findViewById(R.id.progressPercent);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        // Set the values of the TextView and ProgressBar elements
        categoryTextView.setText(catName);
        expensesTextView.setText("P " + catExp);
        percentTextView.setText(String.format("%.2f", catPercent) + "%");
        progressBar.setProgress((int) catPercent);

        //changing progress color
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        LayerDrawable layers = (LayerDrawable) progressDrawable;
        ClipDrawable clipDrawable = (ClipDrawable) layers.findDrawableByLayerId(android.R.id.progress);

        clipDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN));

        // Add the modified view to the dynamic layout
        dynamicLayout.addView(view);
    }

    boolean isValidHexCode(String hexCode) {
        String pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        return hexCode.matches(pattern);
    }

}