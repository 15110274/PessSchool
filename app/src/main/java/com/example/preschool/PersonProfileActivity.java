package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.appcenter.ingestion.Ingestion;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.MissingFormatArgumentException;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userClass, userParentof, userBirthDay;
    private CircleImageView userProfileImage;

    private DatabaseReference UsersRef;
    private ValueEventListener UserListener;
    private FirebaseAuth mAuth;

    private String current_user_id, visitUserId, idClass, idTeacher, className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            visitUserId = bundle.getString("VISIT_USER_ID");
        }

        // Khai báo các thành phần giao diện
        addControlls();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        UserListener= UsersRef.child(visitUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                        String myUserName = dataSnapshot.child("username").getValue().toString();
                        String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                        String myBirthday = dataSnapshot.child("birthday").getValue().toString();
                        String myClass = dataSnapshot.child("classname").getValue().toString();
                        String myParentOf = dataSnapshot.child("parentof").getValue().toString();

                        Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfileImage);

                        userName.setText(myUserName);
                        userProfName.setText(myProfileName);
                        userBirthDay.setText("Sinh nhật: " + myBirthday);
                        userClass.setText("Lớp: " + myClass);
                        userParentof.setText("Phụ huynh của bé: " + myParentOf);
                    }catch (Exception e){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addControlls() {
        userProfileImage = findViewById(R.id.person_profile_pic);
        userName = findViewById(R.id.person_username);
        userProfName = findViewById(R.id.person_full_name);
        userParentof = findViewById(R.id.relationship_with_children);
        userBirthDay = findViewById(R.id.person_birthday);
        userClass = findViewById(R.id.person_class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UsersRef.child(current_user_id).removeEventListener(UserListener);
    }
}
