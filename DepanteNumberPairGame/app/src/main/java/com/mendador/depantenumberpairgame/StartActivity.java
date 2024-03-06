package com.mendador.depantenumberpairgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    EditText playerNameInput;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_layout);

        initialize();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (playerNameInput.getText().toString().isEmpty()) {
                    return;
                }

                User user = new User(playerNameInput.getText().toString());
                Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();

            }
        });
    }

    void initialize() {
        playerNameInput = findViewById(R.id.etPlayerName);
        saveButton = findViewById(R.id.btnSave);
    }

}
