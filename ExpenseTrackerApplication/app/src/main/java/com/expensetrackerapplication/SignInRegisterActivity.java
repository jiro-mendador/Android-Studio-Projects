package com.expensetrackerapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expensetrackerapplication.operations.UserOperation;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class SignInRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etFirstName, etLastName, etUsername, etPassword;
    LinearLayout name_layout;
    TextView showPassword, tvWc, tvInstruc;
    ExtendedFloatingActionButton btnSignInUp;
    EditText[] fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register_layout);

        init();
        setListeners();

        UserOperation userOperation = new UserOperation(this);
        userOperation.open();

        if(userOperation.getUserId() == -1 && !userOperation.isAUserStatusActive()) {
            name_layout.setVisibility(View.VISIBLE);
            fields = new EditText[]{
                    etFirstName, etLastName, etUsername, etPassword
            };
            btnSignInUp.setText("Sign Up");
        } else {
            name_layout.setVisibility(View.GONE);
            fields = new EditText[]{
                    etUsername, etPassword
            };
            btnSignInUp.setText("Sign In");
            tvWc.setText("Welcome back");
            tvInstruc.setText("Please enter your credentials to sign in");
        }
        userOperation.close();
    }

    void init() {
        etFirstName = findViewById(R.id.etSignInFirstname);
        etLastName = findViewById(R.id.etSignInLastname);
        etUsername = findViewById(R.id.etSignInUsername);
        etPassword = findViewById(R.id.etSignInPassword);
        name_layout = findViewById(R.id.name_layout);
        showPassword = findViewById(R.id.tvShowHidepassword);
        btnSignInUp = findViewById(R.id.btnSignInUp);
        tvWc = findViewById(R.id.wcBack);
        tvInstruc = findViewById(R.id.instruction);
    }

    void setListeners() {
        btnSignInUp.setOnClickListener(this);
        showPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvShowHidepassword:
                if (showPassword.getText().toString().contains("Show")) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword.setText("Hide password");
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassword.setText("Show password");
                }
                break;
            case R.id.btnSignInUp:

                UserOperation userOperation = new UserOperation(this);
                userOperation.open();

                if(userOperation.getUserId() == -1 && !userOperation.isAUserStatusActive()) {

                    if (etFirstName.getText().toString().isEmpty()) {
                        etFirstName.setError("First name is empty");
                        return;
                    }

                    if (etLastName.getText().toString().isEmpty()) {
                        etLastName.setError("Last name is empty");
                        return;
                    }

                    if (etUsername.getText().toString().isEmpty()) {
                        etUsername.setError("Username is empty");
                        return;
                    }

                    if (etPassword.getText().toString().isEmpty()) {
                        etPassword.setError("Password is empty");
                        return;
                    }

                    signUpUser();
                    userOperation.close();
                } else {

                    if (etUsername.getText().toString().isEmpty()) {
                        etUsername.setError("Username is empty");
                        return;
                    }

                    if (etPassword.getText().toString().isEmpty()) {
                        etPassword.setError("Password is empty");
                        return;
                    }

                    signInUser();
                    userOperation.close();
                }
                break;
        }
    }

    void signUpUser() {
        UserOperation userOperation = new UserOperation(this);
        userOperation.open();

        long userId = userOperation.addUser(etFirstName.getText().toString(), etLastName.getText().toString(),
                etUsername.getText().toString(), etPassword.getText().toString(),"inactive");

        if (userId != -1) {
            Toast.makeText(this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Signed Up Failed", Toast.LENGTH_SHORT).show();
        }
        setToDefaults(2);
        userOperation.close();
        recreate();
    }

    void signInUser() {
        UserOperation userOperation = new UserOperation(this);
        userOperation.open();
        if (userOperation.signInUser(etUsername.getText().toString(),etPassword.getText().toString())) {
            Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
            return;
        }
        userOperation.setCurrentUserInfo();
        setToDefaults(1);
        userOperation.close();
        startActivity(new Intent(SignInRegisterActivity.this,HomeActivity.class));
        finish();
    }

    void setToDefaults(int num) {
        if(num == 1) {
            etUsername.setText("");
            etPassword.setText("");
        } else {
            for(EditText i : fields) {
                i.setText("");
            }
        }
    }
}