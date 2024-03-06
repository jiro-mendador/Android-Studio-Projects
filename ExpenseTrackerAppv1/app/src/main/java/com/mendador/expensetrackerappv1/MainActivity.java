package com.mendador.expensetrackerappv1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button addCategory, addBudget, addExpense;
    Spinner categoryList;
    ArrayList<String> categories;
    EditText etCategory, etBudget, etExpense;
    String selectedCategory;
    TableLayout dataTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        addListeners();

        categoryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void init() {
        addCategory = (Button) findViewById(R.id.btnAddCategory);
        addBudget = (Button) findViewById(R.id.btnAddBudget);
        addExpense = (Button) findViewById(R.id.btnAddExpense);
        etCategory = (EditText) findViewById(R.id.etAddCategory);
        etBudget = (EditText) findViewById(R.id.etAddBudget);
        etExpense = (EditText) findViewById(R.id.etAddExpense);
        categoryList = (Spinner) findViewById(R.id.categoryList);
        dataTable = (TableLayout) findViewById(R.id.dataTable);

        categories = new ArrayList<>();
        addCategoryListItems();
    }

    void addListeners() {
        addCategory.setOnClickListener(this);
        addBudget.setOnClickListener(this);
        addExpense.setOnClickListener(this);
    }

    void addCategoryListItems() {
        //array adapter for dropdown list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryList.setAdapter(adapter);
    }

    void addRowOnTable(String cat, String bud, String exp) {
        TableRow newRow = new TableRow(this);

        TextView category = new TextView(this);
        TextView budget = new TextView(this);
        TextView expense = new TextView(this);
        TextView remaining = new TextView(this);

        category.setText(cat);
        budget.setText(bud);
        expense.setText(exp);
        remaining.setText(String.valueOf(Double.parseDouble(bud)-Double.parseDouble(exp)));

        newRow.addView(category);
        newRow.addView(budget);
        newRow.addView(expense);
        newRow.addView(remaining);

        dataTable.addView(newRow);
    }

    void updateRowOnTable(String cat, String exp) {

        for (int i = 0; i < dataTable.getChildCount(); i++) {
            View view = dataTable.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;

                // Find the TextView that contains the search string
                TextView nameTextView = (TextView) row.getChildAt(0);
                if (nameTextView.getText().toString().equals(cat)) {
                    // Update the TextViews in the row with the new values
                    TextView budget = (TextView) row.getChildAt(1);
                    TextView expense = (TextView) row.getChildAt(2);
                    TextView remaining = (TextView) row.getChildAt(3);

                    expense.setText(String.valueOf(Double.parseDouble(expense.getText().toString())+Double.parseDouble(exp)));
                    remaining.setText(String.valueOf(Double.parseDouble(budget.getText().toString())
                            -Double.parseDouble(exp)));
                    break; // Exit the loop once the row has been updated
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddCategory:
                categories.add(etCategory.getText().toString());
                addCategoryListItems();
                etCategory.setText("");
                break;
            case R.id.btnAddBudget:
                addRowOnTable(selectedCategory,etBudget.getText().toString(),"0");
                etBudget.setText("");
                break;
            case R.id.btnAddExpense:
                updateRowOnTable(selectedCategory,etExpense.getText().toString());
                etExpense.setText("");
                break;
            default:
        }
    }
}