package com.expensetrackerappv2;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class MainOverview3 extends AppCompatActivity {
    private PieChart overviewChart;
    private List<PieEntry> pieData;
    ArrayList<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_overview3);
        init();
        addValuesToList();
        runChart();
        setProgressBars();
    }

    void init() {
        overviewChart = (PieChart) findViewById(R.id.overallChart2);
        pieData = new ArrayList<>();
        colors = new ArrayList<>();
    }

    void runChart() {
        PieDataSet pieDataSet = new PieDataSet(pieData, "Overall Expenses");
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
        pieData.add(new PieEntry(100, "Category 1",Color.YELLOW));
        pieData.add(new PieEntry(250, "Category 2",Color.BLUE));
        pieData.add(new PieEntry(300, "Category 3",Color.GREEN));
        pieData.add(new PieEntry(400, "Category 4",Color.RED));
        pieData.add(new PieEntry(500, "Category 5",Color.MAGENTA));

        colors.add(Color.YELLOW);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.MAGENTA);
    }

    void setProgressBars() {
        //getting progress bar and text
        ProgressBar bar1 = (ProgressBar) findViewById(R.id.progress_bar1);
        TextView progress1 = (TextView) findViewById(R.id.progressPercent1);
        TextView category1 = (TextView) findViewById(R.id.category1);
        TextView expenses1 = (TextView) findViewById(R.id.categoryExpenses1);

        ProgressBar bar2 = (ProgressBar) findViewById(R.id.progress_bar2);
        TextView progress2 = (TextView) findViewById(R.id.progressPercent2);
        TextView category2 = (TextView) findViewById(R.id.category2);
        TextView expenses2 = (TextView) findViewById(R.id.categoryExpenses2);

        // Get the PieDataSet object from the chart
        PieDataSet pieDataSet = (PieDataSet) overviewChart.getData().getDataSet();

        //get the total to convert it to percentage
        double total = 0;

        // Iterate through each entry in the PieDataSet object to just get the total value for later
        for (int i = 0; i < pieDataSet.getEntryCount(); i++) {
            PieEntry entry = pieDataSet.getEntryForIndex(i);
            float value = entry.getValue();
            total += value;
        }

        for (int i = 0; i < pieDataSet.getEntryCount(); i++) {
            PieEntry entry = pieDataSet.getEntryForIndex(i);

            // Get the background color of the entry
            int backgroundColor = entry.getData() instanceof Integer ? (int) entry.getData() : Color.TRANSPARENT;
            String hexColor = String.format("#%06X", (0xFFFFFF & backgroundColor)); // Convert int color to hex string

            // Get the category (label) and value of the entry
            String category = entry.getLabel();
            float value = entry.getValue();

            // Do something with the data
            if (i == 0) {
                bar1.setProgress((int) (value / total * 100)); //formula in getting the percentage based on total value
                progress1.setText(bar1.getProgress() + "%");
                category1.setText(category);
                expenses1.setText(String.valueOf(value));

                //changing progress color
                Drawable progressDrawable = bar1.getProgressDrawable().mutate();
                LayerDrawable layers = (LayerDrawable) progressDrawable;
                ClipDrawable clipDrawable = (ClipDrawable) layers.findDrawableByLayerId(android.R.id.progress);

                clipDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(hexColor), PorterDuff.Mode.SRC_IN));
            } else if (i == 1) {
                //setting its value
                bar2.setProgress((int) (value / total * 100));
                progress2.setText(bar2.getProgress() + "%");

                category2.setText(category);
                expenses2.setText(String.valueOf(value));

                //changing progress color
                Drawable progressDrawable = bar2.getProgressDrawable().mutate();
                LayerDrawable layers = (LayerDrawable) progressDrawable;
                ClipDrawable clipDrawable = (ClipDrawable) layers.findDrawableByLayerId(android.R.id.progress);

                clipDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(hexColor), PorterDuff.Mode.SRC_IN));
            }
        }
    }
}
