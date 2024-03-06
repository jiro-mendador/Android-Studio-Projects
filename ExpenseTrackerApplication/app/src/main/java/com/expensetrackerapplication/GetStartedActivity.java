package com.expensetrackerapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class GetStartedActivity extends AppCompatActivity {
    public static boolean isFirstTimeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started_layout);
        ExtendedFloatingActionButton btnStart = findViewById(R.id.btnGetStarted);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFirstTimeUser) {
                    openSignInSignUpActivity();
                } else {
                    openMainActivity();
                }
            }
        });
    }

    void openMainActivity() {
        startActivity(new Intent(GetStartedActivity.this,HomeActivity.class));
    }

    void openSignInSignUpActivity() {
        startActivity(new Intent(GetStartedActivity.this,SignInRegisterActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}