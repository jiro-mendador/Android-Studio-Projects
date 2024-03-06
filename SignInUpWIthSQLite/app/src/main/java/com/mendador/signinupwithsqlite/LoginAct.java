package com.mendador.signinupwithsqlite;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginAct extends AppCompatActivity implements View.OnClickListener {

    EditText un,pw;
    Button btnSignIn;
    TextView tvSignUp;

    Database database;
    SQLiteDatabase sqliteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        init();
    }

    void init() {
        un = (EditText) findViewById(R.id.etUname);
        pw = (EditText) findViewById(R.id.etPw);
        btnSignIn = (Button) findViewById(R.id.btnSigIn);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);

        btnSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);

        database = new Database(this);
        sqliteDB = database.getWritableDatabase();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnSigIn) {
            String username = un.getText().toString();
            String password = pw.getText().toString();

            if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] projection = {
                    "id",
                    "username",
                    "password"
            };

            String selection = "username = ? AND password = ?";
            String[] selectionArgs = {
                    username,
                    password
            };

            Cursor cursor = sqliteDB.query(
                    "users",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            boolean userExists = cursor.moveToFirst();

            if(!userExists) {
                Toast.makeText(this, "INCORRECT CREDENTIALS", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(cursor.getString(cursor.getColumnIndexOrThrow("username")));

            Toast.makeText(this, "SIGNED IN SUCCESSFULLY!!!", Toast.LENGTH_SHORT).show();
            cursor.close();
            sqliteDB.close();

            Intent homePageIntent = new Intent(this,MainActivity.class);
            startActivity(homePageIntent);
            finish();

        } else if(view.getId() == R.id.tvSignUp) {
            Intent signUpIntent = new Intent(this,RegisterAct.class);
            startActivity(signUpIntent);
            finish();
        }
    }
}