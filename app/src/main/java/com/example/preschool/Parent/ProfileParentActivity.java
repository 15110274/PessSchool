package com.example.preschool.Parent;


import android.os.Bundle;

import com.example.preschool.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_parent);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle(R.string.parent_profile);
    }
}
