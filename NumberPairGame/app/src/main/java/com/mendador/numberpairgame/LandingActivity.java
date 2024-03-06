package com.mendador.numberpairgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    EditText playerName;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_layout);

        init();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(playerName.getText().toString().isEmpty()) {
                    return;
                }

                Player player = new Player(playerName.getText().toString());
                Intent mainIntent = new Intent(LandingActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();

            }
        });
    }

    void init() {
        playerName = (EditText) findViewById(R.id.etPlayerName);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

}