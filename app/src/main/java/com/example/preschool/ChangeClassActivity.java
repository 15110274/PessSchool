package com.example.preschool;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChangeClassActivity extends AppCompatActivity {
    private String idClass, idTeacher, current_user_id, nameClass;
    private Bundle bundle;
    private DatabaseReference usersRef;
    private Button btnChangeClass;
    private Spinner spinner;
    private List classList;
    private FirebaseAuth mAuth;
    private ValueEventListener userValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.change_join_class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get Bundle
        bundle = getIntent().getExtras();
        if (bundle != null) {
            idClass = bundle.getString("ID_CLASS");
            idTeacher = bundle.getString("ID_TEACHER");
        }

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();


        spinner = findViewById(R.id.spinner);
        btnChangeClass = findViewById(R.id.btn_change_class);
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        classList=new ArrayList<String>();
        classList.add("Mot");
        classList.add("Hai");
        classList.add("Ba");
        classList.add("Bon");
        classList.add("Nam");

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,classList);
        spinner.setAdapter(adapter);


        btnChangeClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersRef.child(current_user_id).setValue("idclass",idClass);
                usersRef.child(current_user_id).setValue("classname",nameClass);
            }
        });

    }

}
