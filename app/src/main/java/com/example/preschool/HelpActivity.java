package com.example.preschool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class HelpActivity extends AppCompatActivity {

    private ExpandableListView expandableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.help);

        expandableTextView = findViewById(R.id.eTv);
        FragHelp adapter = new FragHelp(HelpActivity.this);
        expandableTextView.setAdapter(adapter);


    }
}
