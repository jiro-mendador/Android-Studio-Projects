package com.depantenumpairgame;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView scoreText, multiplierText, playerNameText;
    List<Button> buttons;
    List<Button> possiblePairs;
    List<String> numberList;
    List<Button> alreadyPaired;
    int clickCounter;
    boolean cheatClicked;
    int pairsCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        setClickListeners();
        setScoreAndMultiplier(0, 1);
        playerNameText.setText("Player: " + User.getName());
    }

    private void initialize() {
        buttons = new ArrayList<>();
        numberList = new ArrayList<>();
        alreadyPaired = new ArrayList<>();
        possiblePairs = new ArrayList<>();

        playerNameText = findViewById(R.id.tvPlayerName);
        scoreText = findViewById(R.id.tvScore);
        multiplierText = findViewById(R.id.tvMultiplier);

        int[] buttonIds = {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btn10, R.id.btn11, R.id.btn12, R.id.btn13, R.id.btn14, R.id.btn15, R.id.btn16, R.id.btn17, R.id.btn18,
                R.id.btn19, R.id.btn20, R.id.btn21, R.id.btn22, R.id.btn23, R.id.btn24, R.id.btn25, R.id.btn26, R.id.btn27,
                R.id.btn28, R.id.btn29, R.id.btn30, R.id.btn31, R.id.btn32, R.id.btn33, R.id.btn34, R.id.btn35, R.id.btn36,
                R.id.btn37, R.id.btn38, R.id.btn39, R.id.btn40, R.id.btn41, R.id.btn42, R.id.btn43, R.id.btn44, R.id.btn45,
                R.id.btn46, R.id.btn47, R.id.btn48, R.id.btn49, R.id.btn50};

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setTextColor(Color.WHITE);
            buttons.add(button);
        }

        Button cheatButton = findViewById(R.id.btnCheat);
        Button resetButton = findViewById(R.id.btnReset);
        Button exitButton = findViewById(R.id.btnExit);

        buttons.add(cheatButton);
        buttons.add(resetButton);
        buttons.add(exitButton);

        for (Button button : buttons) {
            button.setOnClickListener(this);
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 1; j < 26; j++) {
                numberList.add(String.valueOf(j));
            }
        }
        rearrangeButtonOrder();
    }

    private void setClickListeners() {
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
    }

    private void showOrHideAllButtonsValue() {
        cheatClicked = !cheatClicked;
        for (int i = 0; i < 50; i++) {
            Button button = buttons.get(i);
            if (cheatClicked) {
                button.setText(numberList.get(i));
                button.setEnabled(false);
            } else {
                button.setText(alreadyPaired.contains(button) ? numberList.get(i) : "");
                button.setEnabled(!alreadyPaired.contains(button));
            }
        }
    }

    private void showIndividualButtonValue(int index) {
        Button button = buttons.get(index);
        button.setText(numberList.get(index));
    }

    private void rearrangeButtonOrder() {
        Collections.shuffle(numberList);
        alreadyPaired.clear();
        cheatClicked = true;
        clickCounter = 0;
        showOrHideAllButtonsValue();
        setScoreAndMultiplier(0, 1);
        pairsCounter = 0;
    }

    private void checkForPairs(Button button) {
        int score = Integer.parseInt(scoreText.getText().toString().replaceAll("Score: ", ""));
        int multiplier = Integer.parseInt(multiplierText.getText().toString().replaceAll("Multiplier: ", ""));

        if (clickCounter == 1) {
            possiblePairs = new ArrayList<>();
            possiblePairs.add(button);
            possiblePairs.get(0).setEnabled(false);
        } else if (clickCounter == 2) {
            possiblePairs.add(button);
            possiblePairs.get(1).setEnabled(false);

            if (possiblePairs.get(0).getText().toString().equals(possiblePairs.get(1).getText().toString())) {
                alreadyPaired.add(possiblePairs.get(0));
                alreadyPaired.add(possiblePairs.get(1));

                setScoreAndMultiplier((score + 5) * multiplier, ++multiplier);
                pairsCounter++;

                if (pairsCounter == numberList.size() / 2) {
                    new AlertDialog.Builder(this)
                            .setMessage("Congratulations! You Win!\n" + scoreText.getText().toString())
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alreadyPaired.clear();
                                    rearrangeButtonOrder();
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
                clickCounter = 0;
            } else {
                setScoreAndMultiplier(score, 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (Button button : possiblePairs) {
                            button.setText("");
                            button.setEnabled(true);
                        }
                        clickCounter = 0;
                    }
                }, 500);
            }
        }
    }

    private void setScoreAndMultiplier(int score, int multiplier) {
        scoreText.setText(String.format("Score: %d", score));
        multiplierText.setText(String.format("Multiplier: %d", multiplier));
    }

    @Override
    public void onClick(View view) {
        if (buttons.get(50) == view) {
            showOrHideAllButtonsValue();
        } else if (buttons.get(51) == view) {
            rearrangeButtonOrder();
            buttons.get(51).setText("RESET");
        } else if (buttons.get(52) == view) {
            finish();
        }

        for (int i = 0; i < 50; i++) {
            if (buttons.get(i) == view && clickCounter != 2) {

                if (buttons.get(51).getText().toString().equals("PLAY")) {
                    Toast.makeText(MainActivity.this, "Press Play First...", Toast.LENGTH_SHORT).show();
                    return;
                }

                clickCounter++;
                showIndividualButtonValue(i);
                checkForPairs(buttons.get(i));
            }
        }
    }
}
