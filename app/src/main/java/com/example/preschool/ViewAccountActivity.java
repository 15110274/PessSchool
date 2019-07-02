package com.example.preschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAccountActivity extends AppCompatActivity {
    private TextView userName, userProfName,userClass,userParentof,userBirthDay,userRole,userEmail;
    private CircleImageView userProfileImage;

    private Button EditButton;

    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        mAuth=FirebaseAuth.getInstance();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        userProfileImage=findViewById(R.id.person_profile_pic);
        userName=findViewById(R.id.person_username);
        userEmail=findViewById(R.id.email);
        userProfName=findViewById(R.id.person_full_name);
        userParentof=findViewById(R.id.relationship_with_children);
        userBirthDay=findViewById(R.id.person_birthday);
        userClass=findViewById(R.id.person_class);
        userRole=findViewById(R.id.role);
        EditButton=findViewById(R.id.edit_account);
        UsersRef.child(getIntent().getStringExtra("USER_ID")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("role")){
                    String myRole=dataSnapshot.child("role").getValue().toString();
                    if(myRole.equals("Admin")){
                        userClass.setVisibility(View.GONE);
                        userParentof.setVisibility(View.GONE);
                    }
                    else{
                        String myClass = dataSnapshot.child("classname").getValue().toString();
                        userClass.setText("Lớp: " + myClass);
                    }
                    userRole.setText("Role: "+myRole);
                }
                String myEmail=dataSnapshot.child("email").getValue().toString();
                userEmail.setText("Email: "+myEmail);
                if(dataSnapshot.hasChild("profileimage")){
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(myProfileImage).placeholder(R.drawable.ic_person_black_50dp).into(userProfileImage);
                }
                if(dataSnapshot.hasChild("fullname")){
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    userProfName.setText(myProfileName);
                }
                if(dataSnapshot.hasChild("username")){
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    userName.setText(myUserName);
                }
                if(dataSnapshot.hasChild("birthday")){
                    String myDOB = dataSnapshot.child("birthday").getValue().toString();
                    userBirthDay.setText(myDOB);
                }
                if(dataSnapshot.hasChild("parentof")){
                    String myParentOf = dataSnapshot.child("parentof").getValue().toString();
                    userParentof.setText(myParentOf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewAccountActivity.this, EditAccountActivity.class);
                intent.putExtra("USER_ID",getIntent().getStringExtra("USER_ID"));
                startActivity(intent);
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ViewAccountActivity.this, ManageUserActivity.class);
        startActivity(intent);
    }
}
