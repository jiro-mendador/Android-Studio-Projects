package com.expensetrackerappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class MainOverview extends AppCompatActivity {

    private PieChart overallChart;
    private EditText etCategory, etExpenses, etColor;
    private Button btnAddCateg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_overview);
        init();
        btnAddCateg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateChart();
            }
        });
    }

    private void updateChart() {
        overallChart.addPieSlice(new PieModel( etCategory.getText().toString(),
                Float.parseFloat(etExpenses.getText().toString()), Color.parseColor(etColor.getText().toString())));
        overallChart.startAnimation();
    }

    void init() {
        etCategory = (EditText) findViewById(R.id.etCategory);
        etExpenses = (EditText) findViewById(R.id.etExpenses);
        etColor = (EditText) findViewById(R.id.etColor);
        btnAddCateg = (Button) findViewById(R.id.btnAdd);
        overallChart = (PieChart) findViewById(R.id.overallChart);
    }
}