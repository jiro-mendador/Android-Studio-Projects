package com.expensetrackerapplication;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.expensetrackerapplication.operations.ItemOperations;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.zxing.Result;

public class ScannerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST_CODE = 101;
    private CodeScanner codeScanner;
    CodeScannerView scannerView;
    private EditText etItemName, etItemPrice;
    TextView tvItemQuantity;
    Button btnMinus, btnAdd;
    ExtendedFloatingActionButton btnAddExpense;
    int quantity;
    String scannerOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_layout);

        init();
        addListeners();

        setUpPermissions();
        codeScanner();
    }

    void init() {
        InputFilter[] textFilter = new InputFilter[1];
        textFilter[0] = new InputFilter.LengthFilter(15);

        scannerView = findViewById(R.id.scannerView);

        etItemName = findViewById(R.id.etExpenseItemName);
        etItemPrice = findViewById(R.id.etAddExpense);
        btnMinus = (Button) findViewById(R.id.btnExpenseItemMinus);
        btnAdd = (Button) findViewById(R.id.btnExpenseItemPlus);
        tvItemQuantity = findViewById(R.id.tvExpenseItemQuantity);
        btnAddExpense = findViewById(R.id.btnAddExpense);

        quantity = 1;

        etItemName.setFilters(textFilter);
        scannerOutput = "";
    }

    void addListeners() {
        btnMinus.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnAddExpense.setOnClickListener(this);
    }

    void setToDefault() {
        etItemName.setText("");
        etItemPrice.setText("");
        tvItemQuantity.setText("1");
        quantity = 1;
        scannerOutput = "";
    }

    private void codeScanner() {
        codeScanner = new CodeScanner(ScannerActivity.this, scannerView);
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);

        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannerOutput = result.getText();
                        if(!processScannerOutput(scannerOutput)) {
                            // If the scanner output doesn't match the expected format, display an error message
                            Toast.makeText(ScannerActivity.this, "Invalid scanner output", Toast.LENGTH_SHORT).show();
                            setToDefault();
                        }
                    }
                });
            }
        });

        codeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Throwable thrown) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ScannerActivity.this, "Camera initialization error "+thrown.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    private void setUpPermissions() {
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Success
                } else {
                    Toast.makeText(this, "Camera permissions needed to use this feature!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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

                if (etItemName.getText().toString().isEmpty()) {
                    etItemName.setError("Expense Item Name is empty");
                    return;
                }

                if (etItemPrice.getText().toString().isEmpty()) {
                    etItemPrice.setError("Expense Item Amount is empty");
                    return;
                }

                if (Double.parseDouble(etItemPrice.getText().toString()) <= 0) {
                    etItemPrice.setError("Put an expense amount greater than zero");
                    return;
                }

                ItemOperations itemOperations = new ItemOperations(this);
                itemOperations.open();

                //if the total amount + current price exceeds the budget
                if(itemOperations.isBudgetExceeded(ExpenseActivity.categoryID,
                        Double.parseDouble(etItemPrice.getText().toString())*quantity,0)) {
                    etItemPrice.setError("Current expense amount will exceed the alloted budget for this category");
                    return;
                }

                long itemId = itemOperations.addItem(etItemName.getText().toString(),
                        itemOperations.getCurrentDateWithHoursMinSec(),Double.parseDouble(etItemPrice.getText().toString()),
                        quantity, ExpenseActivity.categoryID);

                if (itemId != -1) {
                    // Budget added successfully
                    Toast.makeText(this, "New Expense Item added", Toast.LENGTH_SHORT).show();
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
    private boolean processScannerOutput(String scannerOutput) {
        // Check if the scanner output matches the expected format
        if (scannerOutput.contains(":") && !scannerOutput.contains("/")) {
            String[] parts = scannerOutput.split(":");
            if (parts.length == 2) {
                String itemName = parts[0].trim();
                String itemPrice = parts[1].trim();

                // Trim the peso sign from the item price if present
                if (itemPrice.startsWith("P")) {
                    itemPrice = itemPrice.substring(1).trim();
                }

                if (itemPrice.matches(".*[A-Za-z].*")) {
                    return false;
                }

                // Set the item name and item price in the respective EditText fields
                etItemName.setText(itemName);
                etItemPrice.setText(itemPrice);
                return true;
            }
        }
        return false; // Failed to process the scanner output
    }
}

