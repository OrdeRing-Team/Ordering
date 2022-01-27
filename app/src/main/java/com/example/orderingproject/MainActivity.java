package com.example.orderingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int n = 0;

        if(n == 0) setContentView(R.layout.activity_login);
        else setContentView(R.layout.activity_main);
        String Phone = ((EditText) findViewById(R.id.editTextPhone)).getText().toString();
        if(Phone.length() > 10) setContentView(R.layout.activity_main);
    }
}