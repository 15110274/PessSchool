package com.example.pessschool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    EditText txtUsername, txtPass;
    Button btnFogotPass, btnSignIn;
    TextView txtCreateNewAcc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addControlls();
        addEvents();
    }

    private void addEvents() {

        // Click LogIn
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to MainPage
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Click FogotPass
        btnFogotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void addControlls() {
        txtUsername=findViewById(R.id.txtUsername);
        txtPass=findViewById(R.id.txtPass);
        btnFogotPass=findViewById(R.id.btnFogotPass);
        btnSignIn=findViewById(R.id.btnSignIn);
        txtCreateNewAcc=findViewById(R.id.txtCreateNewAcc);

    }

    public void createNewAcc(View view) {
        Intent intent = new Intent(LoginActivity.this,RegisterDemoActivity.class);
        startActivity(intent);
    }
}
