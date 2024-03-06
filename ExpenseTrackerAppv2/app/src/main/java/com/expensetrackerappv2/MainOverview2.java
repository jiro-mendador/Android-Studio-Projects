package com.expensetrackerappv2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainOverview2 extends AppCompatActivity {

    private EditText etCategory, etExpenses, etColor;;
    private Button btnAddCateg;
    private PieChart overviewChart;
    private List<PieEntry> pieData;
    ArrayList<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_overview2);
        init();

        btnAddCateg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addValuesToList();
                runChart();
            }
        });
    }

    void init() {
        overviewChart = (PieChart) findViewById(R.id.overviewChart);
        pieData = new ArrayList<>();
        colors = new ArrayList<>();
        etCategory = (EditText) findViewById(R.id.etCategory);
        etExpenses = (EditText) findViewById(R.id.etExpenses);
        etColor = (EditText) findViewById(R.id.etColor);
        btnAddCateg = (Button) findViewById(R.id.btnAdd);
    }

    void runChart() {
        PieDataSet pieDataSet = new PieDataSet(pieData,"Overall Expenses");
        PieData data = new PieData(pieDataSet);
        //pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(10f);

        Legend legend = overviewChart.getLegend();
        legend.setEnabled(false);
        overviewChart.setDescription(null);

        overviewChart.setData(data);
        overviewChart.invalidate();
    }

    void addValuesToList() {
        pieData.add(new PieEntry(Integer.parseInt(etExpenses.getText().toString()), etCategory.getText().toString()));
        colors.add(Color.parseColor(etColor.getText().toString()));
    }
}
