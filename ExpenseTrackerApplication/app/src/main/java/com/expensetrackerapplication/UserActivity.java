package com.expensetrackerapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.expensetrackerapplication.operations.UserOperation;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignOut, btnBack, btnEditSave;
    Intent openActivity;
    TextView showPassword;
    EditText etPassword, etUsername, firstname, lastname;
    ;
    boolean isSavingUserInformation;
    EditText[] fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);

        init();
        setListeners();
        fields = new EditText[]{
                firstname, lastname, etUsername, etPassword
        };
        setToDefaults();
    }

    void init() {
        btnSignOut = (Button) findViewById(R.id.btnSignout);
        btnBack = (Button) findViewById(R.id.btnBack);
        showPassword = (TextView) findViewById(R.id.tvShowpassword);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnEditSave = (Button) findViewById(R.id.btnEditSave);
        firstname = (EditText) findViewById(R.id.tvUserFirstname);
        lastname = (EditText) findViewById(R.id.tvUserLastname);
        isSavingUserInformation = false;
    }

    void setListeners() {
        btnSignOut.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        showPassword.setOnClickListener(this);
        btnEditSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSignout:
                UserOperation user = new UserOperation(this);
                user.open();
                user.signOut();
                user.close();
                openActivity = new Intent(UserActivity.this, HomeActivity.class);
                startActivity(openActivity);
                finish();
                break;
            case R.id.btnBack:
                openActivity = new Intent(UserActivity.this, HomeActivity.class);
                startActivity(openActivity);
                finish();
                break;
            case R.id.tvShowpassword:
                if (showPassword.getText().toString().contains("Show")) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword.setText("Hide password");
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassword.setText("Show password");
                }
                break;
            case R.id.btnEditSave:
                firstname.setEnabled(true);
                lastname.setEnabled(true);
                etUsername.setEnabled(true);
                etPassword.setEnabled(true);

                if (firstname.getText().toString().isEmpty()) {
                    firstname.setError("First name is empty");
                    return;
                }

                if (lastname.getText().toString().isEmpty()) {
                    lastname.setError("Last name is empty");
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

                if (!isSavingUserInformation) {
                    changeButtonIcon(btnEditSave, ContextCompat.getDrawable(UserActivity.this, R.drawable.savechanges));
                } else {
                    changeButtonIcon(btnEditSave, ContextCompat.getDrawable(UserActivity.this, R.drawable.edit));
                }

                // Update the user information
                if (isSavingUserInformation) {
                    UserOperation userOperation = new UserOperation(UserActivity.this);
                    userOperation.open();
                    long userID = userOperation.updateUser(userOperation.getUserId(), firstname.getText().toString(),
                            lastname.getText().toString(), etUsername.getText().toString(), etPassword.getText().toString(), "active");
                    if (userID != -1) {
                        Toast.makeText(UserActivity.this, "User information updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserActivity.this, "Failed to update user information", Toast.LENGTH_SHORT).show();
                    }
                    userOperation.setCurrentUserInfo();
                    setToDefaults();
                    userOperation.close();
                    isSavingUserInformation = false;
                    return;
                }

                isSavingUserInformation = true;
                break;
        }
    }

    void changeButtonIcon(Button button, Drawable drawable) {
        // Set the drawable to the left of the text (use null for top, right, and bottom drawables)
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    void setToDefaults() {
        for (EditText i : fields) {
            i.setEnabled(false);
        }
        firstname.setText(UserOperation.firstName);
        lastname.setText(UserOperation.lastName);
        etUsername.setText(UserOperation.username);
        etPassword.setText(UserOperation.password);
        isSavingUserInformation = false;
    }
}