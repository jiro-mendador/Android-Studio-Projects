package com.mendador.animations;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MainActivity extends AppCompatActivity {

    Switch switchTog;
    RelativeLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchTog = (Switch) findViewById(R.id.switchTog);
        parent = (RelativeLayout) findViewById(R.id.parentLayout);

        switchTog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchTog.isChecked()) {

                    parent.setBackgroundColor(Color.BLACK);
                    YoYo.with(Techniques.FadeIn)
                            .duration(500)
                            .playOn(parent);

                } else {
                    parent.setBackgroundColor(Color.WHITE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(500)
                            .playOn(parent);
                }
            }
        });
    }
}