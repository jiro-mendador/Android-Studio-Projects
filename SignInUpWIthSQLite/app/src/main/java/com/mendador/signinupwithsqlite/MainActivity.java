package com.mendador.signinupwithsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView tvUsername;
    Button btnAddCateg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        init();
        tvUsername.setText("SIGNED IN AS " + User.getUsername());

        btnAddCateg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditTextAndButtonToContainer("REMOVE CATEGORY", "Enter Category");
            }
        });
    }

    void init() {
        btnAddCateg = (Button) findViewById(R.id.btnAddCateg);
        tvUsername = (TextView) findViewById(R.id.tvSignedInUsername);
    }

    private void addEditTextAndButtonToContainer(String buttonText, String textHint) {
        LinearLayout linearLayout = findViewById(R.id.holderLayout);
        List<EditText> allEditTexts = getAllEditTextsInLayout(linearLayout);

        if(allEditTexts.get(allEditTexts.size()-1).getText().toString().isEmpty()) {
            return;
        }

        Button button = new Button(this);
        button.setText(buttonText);

        EditText editText = new EditText(this);
        editText.setHint(textHint);

        if (button.getParent() != null) {
            ((ViewGroup) button.getParent()).removeView(button);
        } else if (editText.getParent() != null) {
            ((ViewGroup) editText.getParent()).removeView(editText);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup) button.getParent()).removeView(button);
                ((ViewGroup) editText.getParent()).removeView(editText);
            }
        });

        LinearLayout layout = new LinearLayout(this);
        // Set the width and height of the LinearLayout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // width
                LinearLayout.LayoutParams.WRAP_CONTENT  // height
        );
        layout.setLayoutParams(layoutParams);
        layout.setWeightSum(2);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // width
                LinearLayout.LayoutParams.WRAP_CONTENT  // height
        );
        layoutParams.weight = 1;

        layout.addView(editText,layoutParams);
        layout.addView(button, layoutParams);

        linearLayout.addView(layout);
    }

    private List<EditText> getAllEditTextsInLayout(ViewGroup layout) {
        List<EditText> editTexts = new ArrayList<>();

        // Iterate over all child views of the layout
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);

            // If the child is an EditText, add it to the list
            if (child instanceof EditText) {
                editTexts.add((EditText) child);
            }

            // If the child is a ViewGroup (e.g. LinearLayout), recurse into it to find its EditText children
            if (child instanceof ViewGroup) {
                editTexts.addAll(getAllEditTextsInLayout((ViewGroup) child));
            }
        }

        return editTexts;
    }

}