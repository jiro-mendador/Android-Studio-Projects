package com.mendador.signinupwithsqlite;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterAct extends AppCompatActivity implements View.OnClickListener {

    EditText un, pw;
    Button btnSignUp;
    TextView tvSignIn;

    Database database;
    SQLiteDatabase sqliteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        init();
    }

    void init() {
        un = (EditText) findViewById(R.id.etUname);
        pw = (EditText) findViewById(R.id.etPw);
        btnSignUp = (Button) findViewById(R.id.btnSigUp);
        tvSignIn = (TextView) findViewById(R.id.tvSignIn);

        btnSignUp.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);

        database = new Database(this);
        sqliteDB = database.getWritableDatabase();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSigUp) {
            String username = un.getText().toString();
            String password = pw.getText().toString();

            if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter desired username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            long newRowId = sqliteDB.insert("users", null, values);

            if (newRowId == -1) {
                Toast.makeText(this, "SIGN UP FAILED...", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "SIGNED UP SUCCESSFULLY!!!", Toast.LENGTH_SHORT).show();
            sqliteDB.close();
            showLogin();

        } else if (view.getId() == R.id.tvSignIn) {
            showLogin();
        }
    }

    void showLogin() {
        Intent signInIntent = new Intent(this, LoginAct.class);
        startActivity(signInIntent);
        finish();
    }
}